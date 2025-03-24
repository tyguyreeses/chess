package ui.clients;

import exception.ResponseException;
import model.UserData;
import ui.ServerFacade;

import java.util.Arrays;

public class PostLoginClient {
    private final ServerFacade facade;
    private final String username;


    public PostLoginClient(ServerFacade serverFacade, String username) {
        this.facade = serverFacade;
        this.username = username;

    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd.toLowerCase()) {
                case "l", "list" -> list();
                case "c", "create" -> create(params);
                case "j", "join" -> join(params);
                case "w", "watch" -> watch(params);
                case "logout" -> logout();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String list() throws ResponseException {
            return facade.listGames();
    }

    public String create(String... params) throws ResponseException {
        if (params.length == 3) {
            facade.registerUser(new UserData(params[0], params[1], params[2]));
            return String.format("Signed in as %s", params[0]);
        }
        throw new ResponseException(400, "Expected format: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String help() {
        return """
                Options:
                List current games: "l", "list"
                Create a new game: "c", "create" <GAME NAME>
                Join a game: "j", "join" <GAME ID> <COLOR>
                Watch a game: "w", "watch" <GAME ID>
                Logout: "logout"
                View commands: "h", "help"
                """;
    }
}
