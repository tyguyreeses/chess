package ui.clients;


import exception.ResponseException;
import model.*;
import ui.ServerFacade;
import java.util.Arrays;

public class PreLoginClient {
    private final ServerFacade facade;


    public PreLoginClient(ServerFacade serverFacade) {
        this.facade = serverFacade;
    }

    public Object eval(String input) {
        try {
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd.toLowerCase()) {
                case "l", "login" -> login(params);
                case "r", "register" -> register(params);
                case "hello" -> hello();
                case "q", "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public AuthData login(String... params) throws ResponseException {
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            return facade.loginUser(username, password);
        }
        throw new ResponseException(400, "Expected format: <USERNAME> <PASSWORD>");
    }

    public AuthData register(String... params) throws ResponseException {
        if (params.length == 3) {
            return facade.registerUser(new UserData(params[0], params[1], params[2]));
        }
        throw new ResponseException(400, "Expected format: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String hello() throws ResponseException {
        return facade.hello();
    }

    public String help() {
        return """
                Options:
                Login as existing user: "l", "login" <USERNAME> <PASSWORD>
                Register a new user: "r", "register" <USERNAME> <PASSWORD> <EMAIL>
                Exit the program: "q", "quit"
                View commands: "h", "help"
                """;
    }
}
