package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.*;

public interface DataAccessInterface {
    public void clearData() throws ResponseException;

    /**
     * returns userData if user exists, otherwise null
     */
    public UserData getUser(String username);

    /**
     * creates a new user if user isn't already in the database
     */
    public void createUser(UserData userData) throws ResponseException;

    public AuthData getAuth(String authToken);

    public String createAuth(String username);

    /**
     * removes an authToken from the database, throws an error if authToken doesn't exist
     */
    public void removeAuth(String authToken) throws DataAccessException;

    /**
     * creates a new game given a username and a password, creating an ID using a PriorityQueue
     */
    public int createGame(String gameName);

    /**
     * finds game by gameID
     */
    public GameData getGame(int gameID);

    /**
     * returns the Map of games stored by ID
     */
    public Map<Integer, GameData> getGames();

    /**
     * replaces gameData by gameID
     */
    public void updateGame(GameData gameData) throws DataAccessException;

}
