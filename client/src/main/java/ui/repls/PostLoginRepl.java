package ui.repls;

import chess.ChessGame;
import model.*;
import ui.ServerFacade;
import ui.clients.PostLoginClient;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PostLoginRepl {
        private final PostLoginClient client;
        private final AuthData authData;

        public PostLoginRepl(ServerFacade serverFacade, AuthData authData) {
            client = new PostLoginClient(serverFacade, authData);
            this.authData = authData;
        }

        public void run() {
            System.out.printf("Successfully logged in as " + authData.username());
            System.out.print("\n" + SET_TEXT_COLOR_BLUE + client.help());
            Scanner scanner = new Scanner(System.in);
            Object result = "";
            while (!result.equals("logout")) {
                printPrompt();
                String line = scanner.nextLine();

                try {
                    result = client.eval(line);
                    if (result instanceof ChessGame.TeamColor) {
                        new GameRepl((ChessGame.TeamColor) result).run();
                    } else {
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