package ui.repls;

import chess.ChessGame.*;
import model.*;
import ui.ServerFacade;
import ui.clients.GameClient;

import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

public class GameRepl {
    private final GameClient client;
    private final GameData gameData;
    private final TeamColor color;

    public GameRepl(JoinGameResponse jgr) {
        gameData = jgr.gameData();
        color = jgr.color();
        client = new GameClient(gameData, color);
    }

    public void run() {
        System.out.printf("Successfully joined \"" + gameData.gameName() + "\" as " + color);
        System.out.print("\n" + SET_TEXT_COLOR_BLUE + client.help());
        Scanner scanner = new Scanner(System.in);
        Object result = "";
        while (!result.equals("logout")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
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
