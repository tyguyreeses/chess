package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.*;
import services.Service;
import spark.*;

public class Server {

    Gson gson = new Gson();
    Service service = new Service();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clearData);
        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::loginUser);
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
        res.status(ex.StatusCode());
        res.body(ex.toJson());
    }

    private Object clearData(Request req, Response res) throws ResponseException {
        service.clearData();
        res.status(204);
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

}
