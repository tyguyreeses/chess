package ui.repls;

import chess.ChessGame;
import chess.ChessGame.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.ResponseException;
import model.*;
import ui.clients.GameClient;
import ui.websocket.GameHandler;
import ui.websocket.WebsocketFacade;
import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationServerMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

public class GameUI implements GameHandler {
    private final GameClient client;
    private final TeamColor color;
    private GameData gameData;
    private final WebsocketFacade websocket;
    private final Gson gson = new Gson();

    public GameUI(AuthData authData, TeamColor color, GameData gameData, int port) {
        this.gameData = gameData;
        this.color = color;
        try {
            this.websocket = new WebsocketFacade(port);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        client = new GameClient(authData, gameData, color, websocket);
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
                case LOAD_GAME -> {
                    this.gameData = ((LoadGameMessage) message).game;
                    client.drawBoard(color);
                }
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
        System.out.println(SET_TEXT_COLOR_RED + message.message);
    }

    private void printError(ErrorServerMessage message) {
        System.out.println(SET_TEXT_COLOR_RED + message.errorMessage);

    }

    public void run() {
        System.out.printf("Successfully joined \"" + gameData.gameName() + '"');
        try {
            client.drawBoard(color);
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
        Scanner scanner = new Scanner(System.in);
        Object result = "";
        while (!result.equals("Successfully left game")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                if (result != "") {
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(SET_TEXT_COLOR_RED + msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}
