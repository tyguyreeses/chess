package ui.repls;

import model.AuthData;
import ui.ServerFacade;
import ui.clients.PreLoginClient;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class PreLoginRepl {
    private final PreLoginClient client;
    private final ServerFacade facade;

    public PreLoginRepl(ServerFacade serverFacade) {
        client = new PreLoginClient(serverFacade);
        facade = serverFacade;
    }

    public void run() {
        System.out.println();
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        Object result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                if (result instanceof AuthData) {
                    new PostLoginRepl(facade, (AuthData) result).run();
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
        System.out.print("\n" + RESET_TEXT_COLOR + "Chess Login >>> " + SET_TEXT_COLOR_GREEN);
    }
}

