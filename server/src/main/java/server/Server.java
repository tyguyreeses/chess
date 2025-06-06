package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.DataAccess;
import dataaccess.SqlDataAccess;
import exception.ResponseException;
import handlers.JoinRequest;
import model.*;
import services.Service;
import spark.*;
import websocket.WebSocketHandler;

import java.util.Map;

public class Server {

    Gson gson = new Gson();
    public DataAccess dataAccess = new SqlDataAccess();
    public Service service = new Service(dataAccess);
    public WebSocketHandler wsHandler = new WebSocketHandler(service);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", wsHandler);

        // Register your endpoints and handle exceptions here.
        Spark.get("/hello", this::hello);
        Spark.delete("/db", this::clearData);
        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.post("/game", this::createGame);
        Spark.get("/game", this::listGames);
        Spark.put("/game", this::joinGame);
        Spark.exception(ResponseException.class, this::exceptionHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.statusCode());
        res.body(ex.toJson());
    }

    private Object hello(Request req, Response res) throws ResponseException {
         String message = "You've connected to the service!";
         return gson.toJson(message);
    }

    private Object clearData(Request req, Response res) throws ResponseException {
        service.clearData();
        return "";
    }

    private Object registerUser(Request req, Response res) throws ResponseException {
        UserData userData = gson.fromJson(req.body(), UserData.class);
        AuthData authData = service.registerUser(userData);
        return gson.toJson(authData);
    }

    private Object loginUser(Request req, Response res) throws ResponseException {
        UserData userData = gson.fromJson(req.body(), UserData.class);
        AuthData authData = service.loginUser(userData.username(), userData.password());
        return gson.toJson(authData);
    }

    private Object logoutUser(Request req, Response res) throws ResponseException {
        String authToken = req.headers("authorization");
        service.logoutUser(authToken);
        return "";
    }

    private Object createGame(Request req, Response res) throws ResponseException {
        String authToken = req.headers("authorization");
        JsonObject jsonObject = gson.fromJson(req.body(), JsonObject.class);
        String gameName = jsonObject.get("gameName").getAsString();
        return gson.toJson(Map.of("gameID", service.createGame(authToken, gameName)));
    }

    private Object joinGame(Request req, Response res) throws ResponseException {
        String authToken = req.headers("authorization");
        JoinRequest joinRequest = gson.fromJson(req.body(), JoinRequest.class);
        service.joinGame(authToken, joinRequest.playerColor(), joinRequest.gameID());
        return "";
    }

    private Object listGames(Request req, Response res) throws ResponseException {
        String authToken = req.headers("authorization");
        return gson.toJson(Map.of("games", service.listGames(authToken)));
    }

}
