package ui.clients;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.*;
import ui.BoardPrinter;
import ui.websocket.WebsocketFacade;

public class GameClient {
    private GameData gameData;
    private final AuthData authData;
    private final WebsocketFacade websocket;
    private final ChessGame.TeamColor color;
    private final Gson gson = new Gson();

    public GameClient(AuthData authData, GameData gameData, ChessGame.TeamColor color, WebsocketFacade websocket) {
        this.gameData = gameData;
        this.color = color;
        this.authData = authData;
        this.websocket = websocket;
    }

    public Object eval(String input) {
        try {
            String[] tokens = input.split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
//            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd.toLowerCase()) {
                case "hl", "highlight" -> null; // not yet implemented
                case "m", "move" -> null; // not yet implemented
                case "r", "redraw" -> drawBoard(color);
                case "res", "resign" -> null; // not yet implemented
                case "leave" -> "Successfully left game";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String drawBoard(ChessGame.TeamColor color) throws ResponseException {
        new BoardPrinter(gameData, color).printBoard();
        return "";
    }

    public String help() {
        return """
            Options:
            Highlight legal moves: "hl", "highlight" <POSITION> (e.g. f5)
            Make a move: "m", "move" <SOURCE> <DESTINATION> <OPTIONAL PROMOTION> (e.g. f5 e4 q)
            Redraw board: "r", "redraw"
            Resign from game: "res", "resign"
            Leave game: "leave"
            """;
    }
}
