package ui.clients;

import chess.ChessGame.TeamColor;
import exception.ResponseException;
import model.*;
import ui.ServerFacade;

import java.util.Arrays;

public class PostLoginClient {
    private final ServerFacade facade;
    private final AuthData authData;


    public PostLoginClient(ServerFacade serverFacade, AuthData authData) {
        this.facade = serverFacade;
        this.authData = authData;

    }

    public Object eval(String input) {
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
            return facade.listGames(authData.authToken()).toString();
    }

    public String create(String... params) throws ResponseException {
        if (params.length == 1) {
            int gameID = facade.createGame(authData.authToken(), params[0]);
            return String.format("Created game %s, ID = %d", params[0] ,gameID);
        }
        throw new ResponseException(400, "Expected format: <GAME NAME>");
    }

    public TeamColor join(String... params) throws ResponseException {
        if (params.length == 2) {
            TeamColor color = params[1].equalsIgnoreCase("white") ? TeamColor.WHITE : TeamColor.BLACK;
            try {
                int gameID = Integer.parseInt(params[0]);
                facade.joinGame(authData.authToken(), color, gameID);
                return color;
            } catch (NumberFormatException e) {
                throw new ResponseException(400, "Expected format: <GAME ID> <COLOR>");
            }
        }
        throw new ResponseException(400, "Expected format: <GAME ID> <COLOR>");
    }

    public String watch(String... params) throws ResponseException {
        if (params.length == 1) {
            try {
                int gameID = Integer.parseInt(params[0]);
                return String.format("Join game unimplemented, but format is correct: ID = %d", gameID);
            } catch (NumberFormatException e) {
                throw new ResponseException(400, "Expected format: <GAME ID>");
            }
        }
        throw new ResponseException(400, "Expected format: <GAME ID>");
    }

    public String logout() throws ResponseException {
        facade.logoutUser(authData.authToken());
        return "";
    }

    public String help() {
        return """
                Options:
                List current games: "l", "list"
                Create a new game: "c", "create" <GAME NAME>
                Join a game: "j", "join" <GAME ID> <COLOR>
                Watch a game: "w", "watch" <GAME ID>
                Logout: "logout"
                """;
    }
}
