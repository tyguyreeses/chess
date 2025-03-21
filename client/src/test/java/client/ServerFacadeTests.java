package client;

import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;


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
    public void testRegisterUser() throws ResponseException {
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
}
