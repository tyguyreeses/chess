package client;

import chess.ChessGame;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void cleanUp() throws ResponseException {facade.clearData();}

    @Test
    public void testClearData() {
        Assertions.assertDoesNotThrow(() -> facade.clearData());
    }

    @Test
    public void testRegisterUserSuccess() throws ResponseException {
        AuthData expected = new AuthData(null, "testUser");
        UserData user = new UserData("testUser", "password", "email");
        AuthData actual = facade.registerUser(user);
        Assertions.assertEquals(expected.username(), actual.username());
    }

    @Test
    public void testRegisterUserTwice() throws ResponseException {
        UserData user = new UserData("testUser", "password", "email");
        facade.registerUser(user);
        try {
            facade.registerUser(user);
        } catch (ResponseException e) {
            Assertions.assertEquals(403, e.statusCode());
        }
    }

    @Test
    public void testRegisterUserInvalidRequest() {
        UserData user = new UserData(null, null, null);
        try {
            facade.registerUser(user);
            Assertions.fail();
        } catch (ResponseException e) {
            Assertions.assertEquals(400, e.statusCode());
        }
    }

    @Test
    public void testLoginUserSuccess() throws ResponseException {
        UserData user = new UserData("testUser", "password", "email");
        facade.registerUser(user);
        Assertions.assertDoesNotThrow(() -> facade.loginUser(user.username(), user.password()));
        Assertions.assertNotNull(facade.loginUser(user.username(), user.password()));
    }

    @Test
    public void testLoginUserNotExist() {
        Assertions.assertThrows(ResponseException.class, () -> facade.loginUser("invalidUser", "invalidPassword"));
    }

    @Test
    public void testLoginUserWrongPassword() throws ResponseException {
        UserData user = new UserData("testUser", "password", "email");
        facade.registerUser(user);
        Assertions.assertThrows(ResponseException.class, () -> facade.loginUser(user.username(), "invalidPassword"));
    }

    @Test
    public void testLogoutUserSuccess() throws ResponseException {
        UserData user = new UserData("testUser", "password", "email");
        facade.registerUser(user);
        AuthData auth = facade.loginUser(user.username(), user.password());
        Assertions.assertDoesNotThrow(() -> facade.logoutUser(auth.authToken()));
    }

    @Test
    public void testLogoutUserInvalidAuth() {
        try {
            facade.logoutUser("invalidToken");
            Assertions.fail();
        } catch (ResponseException e) {
            Assertions.assertEquals(401, e.statusCode());
        }
    }

    @Test
    public void testCreateGameSuccess() throws ResponseException {
        AuthData auth = facade.registerUser(new UserData("testUser", "password", "email"));
        int gameId = facade.createGame(auth.authToken(), "Test Game");

        Assertions.assertTrue(gameId > 0);
    }

    @Test
    public void testCreateGameInvalidAuth() {
        try {
            facade.createGame("invalidToken", "Test Game");
            Assertions.fail();
        } catch (ResponseException e) {
            Assertions.assertEquals(401, e.statusCode());
        }
    }

    @Test
    public void testJoinGameSuccess() throws ResponseException {
        AuthData auth = facade.registerUser(new UserData("testUser", "password", "email"));
        int gameID = facade.createGame(auth.authToken(), "Test Game");

        Assertions.assertDoesNotThrow(() -> facade.joinGame(auth.authToken(), ChessGame.TeamColor.WHITE, gameID));
    }

    @Test
    public void testJoinGameInvalidAuth() {
        try {
            facade.joinGame("invalidToken", ChessGame.TeamColor.WHITE, 1);
            Assertions.fail();
        } catch (ResponseException e) {
            Assertions.assertEquals(400, e.statusCode());
        }
    }

    @Test
    public void testJoinGameInvalidGameID() throws ResponseException {
        AuthData auth = facade.registerUser(new UserData("testUser", "password", "email"));

        try {
            facade.joinGame(auth.authToken(), ChessGame.TeamColor.WHITE, -1); // Invalid game ID
            Assertions.fail();
        } catch (ResponseException e) {
            Assertions.assertEquals(400, e.statusCode());
        }
    }

    @Test
    public void testListOneGameSuccess() throws ResponseException {
        UserData user = new UserData("testUser", "password", "email");
        String auth = facade.registerUser(user).authToken();
        Collection<GameData> gamesBefore = facade.listGames(auth);

        Assertions.assertTrue(gamesBefore.isEmpty());
        int gameId = facade.createGame(auth, "Test Game");

        Collection<GameData> gamesAfter = facade.listGames(auth);
        Assertions.assertNotNull(gamesAfter);
        Assertions.assertEquals(1, gamesAfter.size());

        List<GameData> gamesList = new ArrayList<>(gamesAfter);
        Assertions.assertEquals(gameId, gamesList.getFirst().gameID());
    }

    @Test
    public void testListNoGames() throws ResponseException {
        UserData user = new UserData("testUser", "password", "email");
        String auth = facade.registerUser(user).authToken();
        Assertions.assertTrue(facade.listGames(auth).isEmpty());
    }

    @Test
    public void testListGamesInvalidAuth() {
        try {
            facade.listGames("invalidToken");
            Assertions.fail();
        } catch (ResponseException e) {
            Assertions.assertEquals(401, e.statusCode());
        }
    }

}
