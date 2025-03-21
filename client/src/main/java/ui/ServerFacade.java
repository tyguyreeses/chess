package ui;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import exception.ResponseException;
import model.*;

import java.io.*;
import java.net.*;
import java.util.Collection;
import java.util.List;

public class ServerFacade {

    final String serverUrl;

    public ServerFacade(int port) { serverUrl = "http://localhost:" + port; }

    public void clearData() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    public AuthData registerUser(UserData userData) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, userData, AuthData.class, null);
    }

    public AuthData loginUser(String username, String password) throws ResponseException {
        var path = "/session";
        UserData userData = new UserData(username, password, null);
        return this.makeRequest("POST", path, userData, AuthData.class, null);
    }

    public void logoutUser(String authToken) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null, authToken);
    }

    public int createGame(String authToken, String gameName) throws ResponseException {
        var path = "/game";
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("gameName", gameName);

        var response = this.makeRequest("POST", path, requestBody, JsonObject.class, authToken);
        return response.get("gameID").getAsInt();
    }

    public Collection<GameData> listGames(String authToken) throws ResponseException {
        var path = "/game";
        GameData[] gameArray = this.makeRequest("GET", path, null, GameData[].class, authToken);
        return List.of(gameArray);
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws ResponseException {
        var path = "/game";
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("playerColor", playerColor);
        requestBody.addProperty("gameID", gameID);

        this.makeRequest("PUT", path, requestBody, null, authToken);
    }


    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.addRequestProperty("authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        if (responseClass == null) {
            return null;
        }
        try (InputStream respBody = http.getInputStream()) {
            if (respBody == null) {
                return null;
            }
            InputStreamReader reader = new InputStreamReader(respBody);
            return new Gson().fromJson(reader, responseClass);
        }
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }



}
