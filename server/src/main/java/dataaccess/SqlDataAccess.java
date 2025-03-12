package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import java.sql.SQLException;
import java.util.Map;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SqlDataAccess implements DataAccess {

    public SqlDataAccess() {
        configureDatabase();
    }

    public int clearData() throws ResponseException {
        return 0;
    }

    public UserData getUser(String username) {
        return null;
    }

    public int createUser(UserData userData) throws ResponseException {
        return 0;
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

    private int executeUpdate(String statement, Object... params) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
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
            }
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
        } catch (Exception e) {
            System.err.println("Error configuring database: " + e.getMessage());
        }
    }
}
