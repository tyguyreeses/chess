package dataaccess;
import java.util.*;

import model.*;

public class DataAccess {
    private final Map<String, UserData> users = new HashMap<>(); // simulates a database with username: UserData
    private final Map<String, AuthData> authTokens = new HashMap<>(); // simulates database with authToken: AuthData
    private final Map<Integer, GameData> games = new HashMap<>(); // simulates database with gameID: GameData


    /**
     * resets all stored data
     */
    public void clear() {
        users.clear();
        authTokens.clear();
        games.clear();
    }

    /**
     * returns username if user exists, otherwise null
     */
    public UserData getUser(String username) {
        return users.get(username);
    }

    /**
     * creates a new user if user isn't already in the database
     */
    public void createUser(UserData userData) throws DataAccessException {
        if (getUser(userData.username()) != null) {
            throw new DataAccessException("Error: User already exists in the database");
        }
        users.put(userData.username(), userData); // Adds the username and password
    }

    public AuthData getAuth(String authToken){
        return authTokens.get(authToken);
    }

    public String createAuth(String username) {
        String authToken = UUID.randomUUID().toString(); // creates an authToken
        AuthData authData = new AuthData(authToken, username);
        authTokens.put(authToken, authData);
        return authToken;
    }

    /**
     * removes an authToken from the database, throws an error if authToken doesn't exist
     */
    public void removeAuth(String authToken) throws DataAccessException {
        if (getAuth(authToken) == null) {
            throw new DataAccessException("Error: AuthToken doesn't exist in the database");
        }
        authTokens.remove(authToken);
    }

    /**
     * creates a new game by GameData if gameID isn't already in the database
     */
    public int createGame(GameData gameData) throws DataAccessException{
        if (getGame(gameData.gameID()) != null) {
            throw new DataAccessException("Error: gameID already exists in the database");
        }
        games.put(gameData.gameID(), gameData);
        return gameData.gameID();
    }

    /**
     * finds game by gameID
     */
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    /**
     * returns the Map of games stored by ID
     */
    public Map<Integer, GameData> getGames() {
        return games;
    }

    /**
     * replaces gameData by gameID
     */
    public void updateGame(GameData gameData) throws DataAccessException {
        int gameID = gameData.gameID();
        if (getGame(gameID) == null) {
            throw new DataAccessException("Error: gameID doesn't exist in the database");
        }
        games.replace(gameID, gameData);
    }

}