package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class SqlDataAccess implements DataAccess {

    public SqlDataAccess() {
        configureDatabase();
    }

    public void clearData() throws ResponseException {
        String[] statements = {"DELETE FROM authTokens", "DELETE FROM games", "DELETE FROM users"};
        for (String statement : statements) {
            executeUpdate(statement);
        }
    }

    public UserData getUser(String username) throws ResponseException {
        String statement = "SELECT username, password, email FROM users WHERE username=?";
        return (UserData) retrieveData(statement, username, "userData");
    }

    public int createUser(UserData ud) throws ResponseException {
        // check if userData is valid
        if (ud.username() == null || ud.password() == null || ud.email() == null) {
            throw new ResponseException(400, "Error: unauthorized");
        }
        // check if username in database
        if (getUser(ud.username()) != null) {
            throw new ResponseException(403, "Error: already taken");
        }
        // add user into users, encrypting password before storage
        String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        String password = BCrypt.hashpw(ud.password(), BCrypt.gensalt());
        return executeUpdate(statement, ud.username(), password, ud.email());
    }

    public AuthData getAuth(String authToken) throws ResponseException {
        String statement = "SELECT username, authToken FROM authTokens WHERE authToken=?";
        return (AuthData) retrieveData(statement, authToken, "authData");
    }

    public String createAuth(String username) throws ResponseException {
        String statement = "INSERT INTO authTokens (username, authToken) VALUES (?, ?)";
        String auth = UUID.randomUUID().toString();
        executeUpdate(statement, username, auth);
        return auth;
    }

    public int removeAuth(String authToken) throws ResponseException {
        String statement = "DELETE FROM authTokens WHERE authToken=?";
        return executeUpdate(statement, authToken);
    }

    public int createGame(String gameName) throws ResponseException {
        String insertStatement  = "INSERT INTO games (gameName, chessGame) VALUES (?, ?)";
        ChessGame chessGame = new ChessGame();
        return executeUpdate(insertStatement, gameName, chessGame);
    }

    public GameData getGame(int gameID) throws ResponseException {
        String statement = "SELECT * FROM games WHERE id=?";
        return (GameData) retrieveData(statement, gameID, "gameData");
    }

    // suppress the unchecked warning since retrieveData will only
    // return a Map with the specific expected parameter
    @SuppressWarnings("unchecked")
    public Map<Integer, GameData> getGames() throws ResponseException {
        String statement = "SELECT * FROM games ORDER BY id";
        return (Map<Integer, GameData>) retrieveData(statement, null, "gameMap");
    }

    public void updateGame(GameData gd) throws DataAccessException, ResponseException {
        int id = gd.gameID();
        if (getGame(id) == null) {
            throw new DataAccessException("Error: gameID doesn't exist in the database");
        }
        String statement = "UPDATE games SET whiteUsername=?, blackUsername=?, gameName=?, chessGame=? WHERE id = ?";
        executeUpdate(statement, gd.whiteUsername(), gd.blackUsername(), gd.gameName(), gd.game(), id);
    }

    private Object retrieveData(String statement, Object param, String expected) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                if (param != null) { switch (param) {
                    case String p -> ps.setString(1, p);
                    case Integer p -> ps.setInt(1, p);
                    default -> throw new ResponseException(500, "Param type incompatible: " + param);
                }
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readData(rs, expected);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to retrieve data: %s", e.getMessage()));
        }
        return null;
    }

    private Object readData(ResultSet rs, String expected) throws SQLException {
        return switch (expected) {
            case "userData" -> readUserData(rs);
            case "authData" -> readAuthData(rs);
            case "gameData" -> readGameData(rs);
            case "gameMap" -> readGameMap(rs);
            case null, default -> null;
        };
    }

    private AuthData readAuthData(ResultSet rs) throws SQLException {
        String username = rs.getString("username");
        String authToken = rs.getString("authToken");
        return new AuthData(authToken, username);
    }

    /**
     * returns user data without decrypting password
     */
    private UserData readUserData(ResultSet rs) throws SQLException {
        String username = rs.getString("username");
        String password = rs.getString("password");
        String email = rs.getString("email");
        return new UserData(username, password, email);
    }

    private GameData readGameData(ResultSet rs) throws SQLException {
        Gson gson = new Gson();
        int id = rs.getInt("id");
        String wu = rs.getString("whiteUsername");
        String bu = rs.getString("blackUsername");
        String gn = rs.getString("gameName");
        ChessGame cg = gson.fromJson(rs.getString("chessGame"), ChessGame.class);
        return new GameData(id, wu, bu, gn, cg);
    }

    private Map<Integer, GameData> readGameMap (ResultSet rs) throws SQLException {
        Map<Integer, GameData> gameMap = new LinkedHashMap<>();
        do {
            int id = rs.getInt("id");
            gameMap.put(id, readGameData(rs));
        } while (rs.next());
        return gameMap;
    }

    private int executeUpdate(String statement, Object... params) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                Gson gson = new Gson(); // for converting GameData to json
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case ChessGame p -> ps.setString(i + 1, gson.toJson(p));
                        case null -> ps.setNull(i + 1, NULL);
                        default -> throw new ResponseException(500, "Param type incompatible: " + param);
                    }
                }
                return rowsOrID(ps, statement);
            }
        } catch (SQLException e) {
            throw new ResponseException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private int rowsOrID(PreparedStatement ps, String statement) throws SQLException {
        // calculate affected rows
        int affectedRows = ps.executeUpdate();
        // if INSERT, return generated id
        if (statement.trim().toUpperCase().startsWith("INSERT")) {
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        // otherwise just return affected rows
        return affectedRows;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL UNIQUE,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL UNIQUE,
              PRIMARY KEY (id)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS games (
              `id` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `chessGame` text NOT NULL,
              PRIMARY KEY (id),
              INDEX (whiteUsername),
              INDEX (blackUsername),
              CONSTRAINT fk_games_white FOREIGN KEY (whiteUsername) REFERENCES users(username) ON DELETE CASCADE,
              CONSTRAINT fk_games_black FOREIGN KEY (blackUsername) REFERENCES users(username) ON DELETE CASCADE
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS authTokens (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `authToken` varchar(256) NOT NULL,
              PRIMARY KEY (id)
            )
            """
    };

    private void configureDatabase() {
        try {
            DatabaseManager.createDatabase();
            try (Connection conn = DatabaseManager.getConnection()) {
                for (String statement : createStatements) {
                    try (PreparedStatement ps = conn.prepareStatement(statement)) {
                        ps.executeUpdate();
                    }
                }
            } catch (SQLException ex) {
                throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
            }
        } catch (Exception e) {
            System.err.println("Error configuring database: " + e.getMessage());
        }
    }
}
