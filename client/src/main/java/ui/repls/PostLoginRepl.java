package ui.repls;

import ui.ServerFacade;
import ui.clients.PostLoginClient;

import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

public class PostLoginRepl {
        private final PostLoginClient client;

        public PostLoginRepl(ServerFacade serverFacade) {
            client = new PostLoginClient(serverFacade);
        }

        public void run() {
            System.out.println();
            System.out.print(client.help());

            Scanner scanner = new Scanner(System.in);
            var result = "";
            while (!result.equals("quit")) {
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