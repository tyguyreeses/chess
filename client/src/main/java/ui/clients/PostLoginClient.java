package ui.clients;

import chess.ChessGame.TeamColor;
import exception.ResponseException;
import model.*;
import ui.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

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
                case "o", "observe" -> observe(params);
                case "logout" -> logout();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String list() throws ResponseException {
        Collection<GameData> games = facade.listGames(authData.authToken());
        if (games == null || games.isEmpty()) {
            return "No games available.";
        }
        StringBuilder sb = new StringBuilder("Available Games:\n");
        int count = 1;
        for (GameData game : games) {
            sb.append(count).append(".  ").append(game).append("\n");
            count += 1;
        }
        return sb.toString();
    }

    public String create(String... params) throws ResponseException {
        if (params.length == 1) {
            facade.createGame(authData.authToken(), params[0]);
            return String.format("Created game %s", params[0]);
        }
        throw new ResponseException(400, "Expected format: <GAME NAME>");
    }

    public TeamColor join(String... params) throws ResponseException {
        if (params.length == 2) {
            int gameID = getGameID(params[0]);
            TeamColor color = getTeamColor(params[1]);
            try {
                facade.joinGame(authData.authToken(), color, gameID);
            } catch (ResponseException e) {
                throw new ResponseException(400, e.getMessage());
            }
            return color;
        }
        throw new ResponseException(400, "Expected format: <GAME ID> <COLOR>");
    }

    public TeamColor observe(String... params) throws ResponseException {
        if (params.length == 1) {
            int gameID = getGameID(params[0]);
            return TeamColor.WHITE; // always observe from white perspective
        }
        throw new ResponseException(400, "Expected format: <GAME ID>");
    }

    private int getGameID(String index) throws ResponseException {
        try {
            int gameIndex = Integer.parseInt(index) - 1;
            ArrayList<GameData> games = (ArrayList<GameData>) facade.listGames(authData.authToken());
            return games.get(gameIndex).gameID();
        } catch (Exception e) {
            throw new ResponseException(400, "Entered ID doesn't exist");
        }
    }

    private TeamColor getTeamColor(String color) throws ResponseException {
        if (color.equalsIgnoreCase("white")) {
            return TeamColor.WHITE;
        } else if (color.equalsIgnoreCase("black")) {
            return TeamColor.BLACK;
        } else {
            throw new ResponseException(400, "Expected color as either 'white' or 'black");
        }
    }

    public String logout() throws ResponseException {
        facade.logoutUser(authData.authToken());
        return "Successfully logged out";
    }

    public String help() {
        return """
                Options:
                List current games: "l", "list"
                Create a new game: "c", "create" <GAME NAME>
                Join a game: "j", "join" <GAME ID> <COLOR>
                Observe a game: "o", "observe" <GAME ID>
                Logout: "logout"
                """;
    }
}
