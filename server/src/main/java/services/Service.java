package services;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import jdk.jshell.spi.ExecutionControl;
import model.*;
import java.util.Map;
import java.util.Objects;

public class Service {
    public final DataAccess dataAccess = new DataAccess();

    /** takes UserData as input and uses the dataAccess class to register the user.
     *  If successful, return username and the created auth token.
     *   400 if bad request,
     *   403 if username already taken,
     *   500 if another error,
     */
    public Response registerUser(UserData userData) {
        try {
            if (userData.username() == null || userData.password() == null || userData.email() == null) {
                return new Response(400, "Error: bad request");
            }
            // create new user, throws DataAccessException if username in use
            dataAccess.createUser(userData);
            // create new AuthData
            String authToken = dataAccess.createAuth(userData.username());
            // return success response
            return new Response(200, userData.username(), authToken);
        } catch (DataAccessException e) {
            return new Response(403, "Error: already taken");
        } catch (Exception e) {
            return new Response(500, "Error: " + e.getMessage());
        }
    }

    public Response clearData() {
        try {
            dataAccess.clearData();
            return new Response(200);
        } catch (Exception e) {
            return new Response(500, "Error: " + e.getMessage());
        }
    }

    public Response loginUser(String username, String password) {
        try {
            // if user exists and password matches (ideally would be encrypted somehow)
            UserData userData = dataAccess.getUser(username);
            if (userData != null && Objects.equals(userData.password(), password)) {
                // return success response and an authToken
                String authToken = dataAccess.createAuth(userData.username());
                return new Response(200, userData.username(), authToken);
            } else {
                return new Response(401, "Error: unauthorized");
            }
        } catch (Exception e) {
            return new Response(500, "Error: " + e.getMessage());
        }
    }

    public Response logoutUser(String authToken) {
        try {
            AuthData authData = dataAccess.getAuth(authToken);
            if (authData != null) {
                dataAccess.removeAuth(authToken);
                return new Response(200);
            } else {
                return new Response(401, "Error: unauthorized");
            }
        } catch (Exception e) {
            return new Response(500, "Error: " + e.getMessage());
        }
    }

    public Response listGames(String authToken) {
        try {
            AuthData authData = dataAccess.getAuth(authToken);
            if (authData != null) {
                return new Response(200, dataAccess.getGames());
            } else {
                return new Response(401, "Error: unauthorized");
            }
        } catch (Exception e) {
            return new Response(500, "Error: " + e.getMessage());
        }
    }

    public Response createGame(String authToken, String gameName) {
        try {
            AuthData authData = dataAccess.getAuth(authToken);
            if (authData != null) {
                int gameID = dataAccess.createGame(authData.username(), gameName);
                return new Response(200, gameID);
            } else {
                return new Response(401, "Error: unauthorized");
            }
        } catch (Exception e) {
            return new Response(500, "Error: " + e.getMessage());
        }
    }

    public Response joinGame(String authToken, ChessGame.TeamColor playerColor, int gameID) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("Error: not yet implemented");
    }

    // inner class to represent response data
    public record Response(int status, String message, String username, String authToken, int gameID, Map<Integer, GameData> games) {
        // Convenience constructors for different response types
        public Response(int status) {
            this(status,null,null,null, -1,null);
        }

        public Response(int status, String message) {
            this(status, message,null,null,-1,null);
        }

        public Response(int status, String username, String authToken) {
            this(status,null, username, authToken,-1,null);
        }

        public Response(int status, Map<Integer, GameData> games) {
            this(status,null,null,null,-1, games);
        }

        public Response(int status, int gameID) {
            this(status,null,null,null, gameID, null);

        }
    }
}

