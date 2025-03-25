package ui.repls;

import chess.ChessGame;
import chess.ChessGame.*;
import exception.ResponseException;
import model.*;
import ui.clients.GameClient;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

public class GameRepl {
    private final GameClient client;
    private final TeamColor color;
    private final GameData gameData = new GameData(0,"test", "test","test", new ChessGame());

    public GameRepl(TeamColor color) {
        client = new GameClient(gameData, color);
        this.color = color;
    }

    public void run() {
        System.out.printf("Successfully joined \"" + gameData.gameName());
        try {
            client.drawBoard(color);
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
        Scanner scanner = new Scanner(System.in);
        Object result = "";
        while (!result.equals("leave")) {
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
