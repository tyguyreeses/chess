package ui.clients;

import chess.ChessGame;
import exception.ResponseException;
import model.*;
import ui.BoardPrinter;

import java.util.Arrays;

public class GameClient {
    private final GameData gameData;
    private final ChessGame.TeamColor color;

    public GameClient(GameData gameData, ChessGame.TeamColor color) {
        this.gameData = gameData;
        this.color = color;
    }

    public Object eval(String input) {
        try {
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd.toLowerCase()) {
                case "r", "redraw" -> drawBoard(color);
                case "leave" -> "leave";
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
            Change color scheme: "c", "colors" <COLOR NUMBER>
            Resign from game: "res", "resign"
            Leave game: "leave"
            """;
    }
}
