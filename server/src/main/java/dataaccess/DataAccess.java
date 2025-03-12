package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.*;

public interface DataAccess {
    void clearData() throws ResponseException;

    /**
     * returns userData if user exists, otherwise null
     */
    UserData getUser(String username);

    /**
     * creates a new user if user isn't already in the database
     */
    void createUser(UserData userData) throws ResponseException;

    AuthData getAuth(String authToken);

    String createAuth(String username);

    /**
     * removes an authToken from the database, throws an error if authToken doesn't exist
     */
    void removeAuth(String authToken) throws DataAccessException;

    /**
     * creates a new game given a username and a password, creating an ID using a PriorityQueue
     */
    int createGame(String gameName);

    /**
     * finds game by gameID
     */
    GameData getGame(int gameID);

    /**
     * returns the Map of games stored by ID
     */
    Map<Integer, GameData> getGames();

    /**
     * replaces gameData by gameID
     */
    void updateGame(GameData gameData) throws DataAccessException;

}
