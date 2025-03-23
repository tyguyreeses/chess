package ui.clients;


import exception.ResponseException;
import model.UserData;
import ui.ServerFacade;
import

import java.util.Arrays;

public class PreLoginClient {
    private final ServerFacade facade;


    public PreLoginClient(int port) {
        new Server().run(port);

        facade = new ServerFacade(port);
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd.toLowerCase()) {
                case "l", "login" -> login(params);
                case "r", "register" -> register(params);
                case "q", "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            facade.loginUser(username, password);
            return String.format("Signed in as %s", username);
        }
        throw new ResponseException(400, "Expected format: <USERNAME> <PASSWORD>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            facade.registerUser(new UserData(params[0], params[1], params[2]));
            return String.format("Signed in as %s", params[0]);
        }
        throw new ResponseException(400, "Expected format: <USERNAME> <PASSWORD> <EMAIL>");
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
