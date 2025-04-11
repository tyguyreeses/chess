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
            websocket = new WebsocketFacade(port, this);
            // send connect message to server
            websocket.connect(authData.authToken(), gameData.gameID(), websocket.session);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        client = new GameClient(authData, gameData, color, websocket);
    }

    @Override
    public void updateGame(ChessGame game) {
        gameData = gameData.withUpdatedGame(game);
        client.gameData = gameData;
    }

    @Override
    public void printMessage(String message) {
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
        ServerMessage.ServerMessageType messageType = ServerMessage.ServerMessageType.valueOf(jsonObject.get("serverMessageType").getAsString());
        try {
            switch (messageType) {
                case LOAD_GAME -> {
                    LoadGameMessage lgm = gson.fromJson(message, LoadGameMessage.class);
                    this.gameData = lgm.game;
                    client.gameData = lgm.game;
                    client.drawBoard(color);
                    printPrompt();
                }
                case NOTIFICATION -> {
                    NotificationServerMessage nsm = gson.fromJson(message, NotificationServerMessage.class);
                    printNotification(nsm);
                }
                case ERROR -> {
                    ErrorServerMessage esm = gson.fromJson(message, ErrorServerMessage.class);
                    printError(esm);
                }
            }
        } catch (ResponseException e) {
            System.out.println(SET_TEXT_COLOR_RED + e.getMessage());
        }
    }

    private void printNotification(NotificationServerMessage message) {
        System.out.println(SET_TEXT_COLOR_BLUE + message.message);
        printPrompt();
    }

    private void printError(ErrorServerMessage message) {
        System.out.println(SET_TEXT_COLOR_RED + message.errorMessage);
        printPrompt();

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
