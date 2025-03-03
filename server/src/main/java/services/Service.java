package services;

import chess.ChessGame.TeamColor;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import exception.ResponseException;
import model.*;

import java.util.Collection;
import java.util.Objects;

public class Service {
    public final DataAccess dataAccess = new DataAccess();

    public void clearData() throws ResponseException {
        dataAccess.clearData();
    }

     public AuthData registerUser(UserData userData) throws ResponseException {
         dataAccess.createUser(userData);
         String authToken = dataAccess.createAuth(userData.username());
         return dataAccess.getAuth(authToken);
    }


    public AuthData loginUser(String username, String password) throws ResponseException {
        UserData userData = dataAccess.getUser(username);
        if (userData != null && Objects.equals(userData.password(), password)) {
            // return success response and an authToken
            String authToken = dataAccess.createAuth(userData.username());
            return new AuthData(authToken, username);
        } else {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }

    public void logoutUser(String authToken) throws ResponseException {
        try {
            AuthData authData = dataAccess.getAuth(authToken);
            if (authData != null) {
                dataAccess.removeAuth(authToken);
            } else {
                throw new ResponseException(401, "Error: unauthorized");
            }
        } catch (ResponseException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public Collection<GameData> listGames(String authToken) throws ResponseException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData != null) {
            return dataAccess.getGames().values();
        } else {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }
//
//    public Res createGame(String authToken, String gameName) {
//        try {
//            AuthData authData = dataAccess.getAuth(authToken);
//            if (authData != null) {
//                int gameID = dataAccess.createGame(authData.username(), gameName);
//                return new Res(200, gameID);
//            } else {
//                return new Res(401, "Error: unauthorized");
//            }
//        } catch (Exception e) {
//            return new Res(500, "Error: " + e.getMessage());
//        }
//    }
//
//    public Res joinGame(String authToken, TeamColor playerColor, int gameID) {
//        try {
//            GameData gameData = dataAccess.getGame(gameID);
//            AuthData authData = dataAccess.getAuth(authToken);
//
//            // if authToken empty or gameData isn't found or playerColor empty
//            if (authToken == null || gameData == null || playerColor == null) {
//                return new Res(400, "Error: bad request");
//
//            // if not a valid authToken
//            } else if (authData == null) {
//                return new Res(401, "Error: unauthorized");
//            }
//
//            // if requested color already taken
//            if ((playerColor == TeamColor.WHITE && gameData.whiteUsername() != null) ||
//                    (playerColor == TeamColor.BLACK && gameData.blackUsername() != null)) {
//                return new Res(403, "Error: already taken");
//            }
//
//            // if none of the previous errors triggered, update the game
//            String username = authData.username();
//            gameData = playerColor == TeamColor.WHITE ? gameData.withWhiteUser(username) : gameData.withBlackUser(username);
//            dataAccess.updateGame(gameData);
//            return new Res(200);
//
//        } catch (Exception e) {
//            return new Res(500, "Error: " + e.getMessage());
//        }
//    }
//
//    // inner class to represent response data
//    public record Res(int status, String message, String username, String authToken, int gameID, Map<Integer, GameData> games) {
//        // Convenience constructors for different response types
//        public Res(int status) {
//            this(status,null,null,null, -1,null);
//        }
//
//        public Res(int status, String message) {
//            this(status, message,null,null,-1,null);
//        }
//
//        public Res(int status, String username, String authToken) {
//            this(status,null, username, authToken,-1,null);
//        }
//
//        public Res(int status, Map<Integer, GameData> games) {
//            this(status,null,null,null,-1, games);
//        }
//
//        public Res(int status, int gameID) {
//            this(status,null,null,null, gameID, null);
//
//        }
//    }
}

