package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import java.sql.SQLException;
import java.util.Map;

public class SqlDataAccess implements DataAccess {

    public SqlDataAccess() {
        try {
            configureDatabase();
        } catch (ResponseException e) {
            System.err.println("Error configuring database: " + e.getMessage());
        }
    }

    public void clearData() throws ResponseException {

    }

    public UserData getUser(String username) {
        return null;
    }

    public void createUser(UserData userData) throws ResponseException {

    }

    public AuthData getAuth(String authToken) {
        return null;
    }

    public String createAuth(String username) {
        return "";
    }

    public void removeAuth(String authToken) throws DataAccessException {

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

    public void updateGame(GameData gameData) throws DataAccessException {

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


    private void configureDatabase() throws ResponseException {
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
    }
}
