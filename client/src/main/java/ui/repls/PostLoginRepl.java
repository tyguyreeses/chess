package ui.repls;

import exception.ResponseException;
import model.*;
import ui.ServerFacade;
import ui.clients.myPair;
import ui.clients.PostLoginClient;

import java.util.List;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PostLoginRepl {
        private final PostLoginClient client;
        private final AuthData authData;
        private final ServerFacade facade;

        public PostLoginRepl(ServerFacade serverFacade, AuthData authData) {
            client = new PostLoginClient(serverFacade, authData);
            this.authData = authData;
            this.facade = serverFacade;
        }

        public void run() {
            System.out.printf("Successfully logged in as " + authData.username());
            System.out.print("\n" + SET_TEXT_COLOR_BLUE + client.help());
            Scanner scanner = new Scanner(System.in);
            Object result = "";
            while (!result.equals("Successfully logged out")) {
                printPrompt();
                String line = scanner.nextLine();

                try {
                    result = client.eval(line);
                    if (result instanceof myPair) {
                        int gameID = ((myPair) result).gameID();
                        GameData gameData = retrieveGameData(gameID);
                        // run the gameREPL
                        new GameUI(authData, ((myPair) result).color(), gameData, facade.port).run();
                    } else {
                        System.out.print(SET_TEXT_COLOR_BLUE + result);
                    }
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.print(SET_TEXT_COLOR_RED + msg + "\n");
                }
            }
            System.out.println();
        }

        private void printPrompt() {
            System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
        }

        private GameData retrieveGameData(int gameID) throws ResponseException {
            List<GameData> gameList = facade.listGames(authData.authToken());
            for (GameData game : gameList) {
                if (game.gameID() == gameID) {
                    return game;
                }
            }
            throw new ResponseException(500, "Unable to retrieve game");
        }
}