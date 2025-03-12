package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Map;

public class SqlDataAccess implements DataAccess {

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
}
