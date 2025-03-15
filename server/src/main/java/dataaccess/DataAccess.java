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
    UserData getUser(String username) throws ResponseException;

    /**
     * creates a new user if user isn't already in the database
     *
     * @return
     */
    int createUser(UserData userData) throws ResponseException;

    AuthData getAuth(String authToken) throws ResponseException;

    String createAuth(String username) throws ResponseException;

    /**
     * removes an authToken from the database, throws an error if authToken doesn't exist
     *
     * @return
     */
    int removeAuth(String authToken) throws DataAccessException, ResponseException;

    /**
     * creates a new game given a username and a password, returning a generated id
     */
    int createGame(String gameName) throws ResponseException;

    /**
     * finds game by gameID
     */
    GameData getGame(int gameID) throws ResponseException;

    /**
     * returns the Map of games stored by ID
     */
    Map<Integer, GameData> getGames() throws ResponseException;

    /**
     * replaces gameData by gameID
     */
    void updateGame(GameData gameData) throws DataAccessException, ResponseException;

}
