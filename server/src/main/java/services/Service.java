package services;

import chess.ChessGame.TeamColor;
import dataaccess.DataAccess;
import exception.ResponseException;
import model.*;
import org.mindrot.jbcrypt.BCrypt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class Service {

    public final DataAccess dataAccess;

    public Service(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

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
        if (userData != null && BCrypt.checkpw(password, userData.password())) {
            // return success response and an authToken if it doesn't exist
            String authToken = dataAccess.createAuth(userData.username());
            return new AuthData(authToken, username);
        } else {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }

    public void logoutUser(String authToken) throws ResponseException {
        try {
            validateAuthData(authToken);
            dataAccess.removeAuth(authToken);
        } catch (ResponseException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public Collection<GameData> listGames(String authToken) throws ResponseException {
        validateAuthData(authToken);
        Map<Integer, GameData> games = dataAccess.getGames();
        return games != null ? games.values() : new ArrayList<>();
    }

    public Integer createGame(String authToken, String gameName) throws ResponseException {
        try {
            validateAuthData(authToken);
            return dataAccess.createGame(gameName);
        } catch (ResponseException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    public void joinGame(String authToken, TeamColor playerColor, int gameID) throws ResponseException {
        try {
            GameData gameData = dataAccess.getGame(gameID);
            // if authToken empty or gameData isn't found or playerColor empty
            if (authToken == null || gameData == null || playerColor == null) {
                throw new ResponseException(400, "Error: bad request");
            }
            validateAuthData(authToken);
            AuthData authData = dataAccess.getAuth(authToken);

            // if requested color already taken
            if ((playerColor == TeamColor.WHITE && gameData.whiteUsername() != null) ||
                    (playerColor == TeamColor.BLACK && gameData.blackUsername() != null)) {
                throw new ResponseException(403, "Error: already taken");
            }

            // if none of the previous errors triggered, update the game
            String username = authData.username();
            gameData = playerColor == TeamColor.WHITE ? gameData.withWhiteUser(username) : gameData.withBlackUser(username);
            dataAccess.updateGame(gameData);

        } catch (ResponseException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    private void validateAuthData(String authToken) throws ResponseException {
        if (dataAccess.getAuth(authToken) == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }
}

