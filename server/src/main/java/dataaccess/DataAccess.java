package dataaccess;
import java.util.*;

import model.*;

public class DataAccess {
    private final Map<String, UserData> users = new HashMap<>(); // simulates a database with username: UserData
    private final Map<String, AuthData> authTokens = new HashMap<>(); // simulates database with authToken: AuthData
    private final Map<Integer, GameData> games = new HashMap<>(); // simulates database with gameID: GameData


    // resets all stored data
    public void clear() {
        users.clear();
        authTokens.clear();
        games.clear();
    }

    // returns username if user exists, otherwise null
    public UserData getUser(String username) {
        return users.get(username);
    }

    // creates a new user if user isn't already in the database
    public void createUser(UserData userData) throws DataAccessException {
        if (getUser(userData.username()) == null) {
            throw new DataAccessException("Error: User already exists");
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

    // removes an authToken from the database, throws an error if authToken doesn't exist, might remove later
    public void removeAuth(String authToken) throws DataAccessException {
        if (getAuth(authToken) == null) {
            throw new DataAccessException("Error: AuthToken doesn't exist in the database");
        }
        authTokens.remove(authToken);
    }

}