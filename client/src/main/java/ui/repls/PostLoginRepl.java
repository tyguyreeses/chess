package ui.repls;

import model.*;
import ui.ServerFacade;
import ui.clients.PostLoginClient;

import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

public class PostLoginRepl {
        private final PostLoginClient client;
        private final AuthData authData;

        public PostLoginRepl(ServerFacade serverFacade, AuthData authData) {
            client = new PostLoginClient(serverFacade, authData);
            this.authData = authData;
        }

        public void run() {
            System.out.println();
            System.out.print(String.format("Successfully logged in as &s", authData.username()));

            Scanner scanner = new Scanner(System.in);
            String result = "";
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