package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SqlDataAccess implements DataAccess {

    public SqlDataAccess() {
        configureDatabase();
    }

    public int clearData() throws ResponseException {
        String[] statements = {"TRUNCATE users", "TRUNCATE games", "TRUNCATE authTokens"};
        try (var conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                for (String statement : statements) {
                    executeUpdate(conn, statement);
                }
                conn.commit();
                return 1;
            } catch (ResponseException e) {
                conn.rollback();
            }
        } catch (SQLException e) {
            throw new ResponseException(500, e.getMessage());
        }
        return 0;
    }

    public UserData getUser(String username) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, authToken FROM authTokens WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUserData(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    private UserData readUserData(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, password, email);
    }

    public int createUser(UserData ud) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // check if userData is valid
                if (ud.username() == null || ud.password() == null || ud.email() == null) {
                    throw new ResponseException(400, "Error: unauthorized");
                }
                // check if username in database
                if (getUser(ud.username()) != null) {
                    throw new ResponseException(403, "Error: already taken");
                }
                // add user into users
                String statement1 = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
                String password = BCrypt.hashpw(ud.password(), BCrypt.gensalt());
                executeUpdate(conn, statement1, ud.username(), password, ud.email());
                // create authToken and add to authTokens
                String statement2 = "INSERT INTO authTokens (username, authToken) VALUES (?, ?)";
                String auth = UUID.randomUUID().toString();
                executeUpdate(conn, statement2, ud.username(), auth);
                return 1;
            } catch (ResponseException e) {
                conn.rollback();
                throw new ResponseException(500, e.getMessage());
            }
        }  catch (SQLException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public AuthData getAuth(String authToken) {
        return null;
    }

    public String createAuth(String username) {
        return "";
    }

    public int removeAuth(String authToken) throws DataAccessException {
        return 0;
    }

    public int createGame(String gameName) {
        return 0;
    }

    public GameData getGame(int gameID) {
        return null;
    }

    public Map<Integer, GameData> getGames() {
        return Map.of();
    }

    public int updateGame(GameData gameData) throws DataAccessException {
        return 0;
    }

    private int executeUpdate(Connection conn, String statement, Object... params) throws ResponseException {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case GameData p -> ps.setString(i + 1, p.toString());
                        case null -> ps.setNull(i + 1, NULL);
                        default -> throw new ResponseException(500, "Param type incompatible: " + param);
                    }
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            } catch (SQLException e) {
                throw new ResponseException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
            }
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
              `whiteUsername` varchar(256) NOT NULL,
              `blackUsername` varchar(256) NOT NULL,
              `gameName` varchar(256) NOT NULL,
              `chessGame` text NOT NULL,
              PRIMARY KEY (id),
              INDEX (whiteUsername),
              INDEX (blackUsername),
              CONSTRAINT fk_games_white FOREIGN KEY (whiteUsername) REFERENCES users(username) ON DELETE RESTRICT,
              CONSTRAINT fk_games_black FOREIGN KEY (blackUsername) REFERENCES users(username) ON DELETE RESTRICT
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS authTokens (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL UNIQUE,
              `authToken` varchar(256) NOT NULL UNIQUE,
              PRIMARY KEY (id),
              CONSTRAINT fk_authTokens_user FOREIGN KEY (username) REFERENCES users(username) ON DELETE RESTRICT
            )
            """
    };

    private void configureDatabase() {
        try {
            DatabaseManager.createDatabase();
            try (var conn = DatabaseManager.getConnection()) {
                for (var statement : createStatements) {
                    try (var preparedStatement = conn.prepareStatement(statement)) {
                        preparedStatement.executeUpdate();
                    }
                }
            } catch (SQLException ex) {
                throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
            }
//            addTestUser();
        } catch (Exception e) {
            System.err.println("Error configuring database: " + e.getMessage());
        }
    }

    public void addTestUser() throws ResponseException {
        String insertUser = "INSERT IGNORE INTO users (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement(insertUser)) {
                // Set parameters for username and password
                statement.setString(1, "testuser");
                statement.setString(2, "testpassword");
                statement.setString(3, "testemail");

                // Execute the insert statement and get the result
                int rowsAffected = statement.executeUpdate();

                // Assert that the insertion affected 1 row
                assertEquals(1, rowsAffected, "User should be inserted into the table.");
            }
        } catch (Exception e) {
            throw new ResponseException(500, "Couldn't add test user: " + e.getMessage());
        }
    }
}
