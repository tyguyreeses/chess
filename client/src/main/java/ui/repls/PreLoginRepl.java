package ui.repls;

import ui.ServerFacade;
import ui.clients.PreLoginClient;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class PreLoginRepl {
    private final PreLoginClient client;

    public PreLoginRepl(int port) {
        client = new PreLoginClient(port);
        System.out.println("Started test HTTP server on " + port);
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

