package ui.clients;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.ResponseException;
import model.*;
import ui.BoardPrinter;
import ui.websocket.GameHandler;
import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationServerMessage;
import websocket.messages.ServerMessage;
import static ui.EscapeSequences.*;

public class GameUI implements GameHandler {
    private GameData gameData;
    private final ChessGame.TeamColor color;
    private final Gson gson = new Gson();

    public GameUI(GameData gameData, ChessGame.TeamColor color) {
        this.gameData = gameData;
        this.color = color;
    }

    @Override
    public void updateGame(ChessGame game) {
        this.gameData = gameData.withUpdatedGame(game);
    }

    @Override
    public void printMessage(String jsonMessage) {
        try {
            ServerMessage message = processMessage(jsonMessage);
            switch (message.getServerMessageType()) {
                case LOAD_GAME -> drawBoard(color);
                case NOTIFICATION -> printNotification((NotificationServerMessage) message);
                case ERROR -> printError((ErrorServerMessage) message);
            }
        } catch (ResponseException e) {
            System.out.println(SET_TEXT_COLOR_RED + e.getMessage());
        }
    }

    private ServerMessage processMessage(String message) throws ResponseException {
        // determine message type
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
        ServerMessage.ServerMessageType messageType = ServerMessage.ServerMessageType.valueOf(jsonObject.get("messageType").getAsString());
        try {
            // call the appropriate message handler
            return switch (messageType) {
                case LOAD_GAME -> gson.fromJson(message, LoadGameMessage.class);
                case NOTIFICATION -> gson.fromJson(message, NotificationServerMessage.class);
                case ERROR -> gson.fromJson(message, ErrorServerMessage.class);
            };
        } catch (Exception e) {
            throw new ResponseException(500, "Unable to process incoming message");
        }
    }

    private void printNotification(NotificationServerMessage message) {

    }

    private void printError(ErrorServerMessage message) {

    }

    public Object eval(String input) {
        try {
            String[] tokens = input.split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
//            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd.toLowerCase()) {
                case "hl", "highlight" -> null;
                case "m", "move" -> null;
                case "r", "redraw" -> drawBoard(color);
                case "res", "resign" -> null;
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
