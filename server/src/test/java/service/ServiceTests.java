package java.service;

import chess.ChessGame;
import exception.ResponseException;
import model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Collection;
import services.Service;

public class ServiceTests {

    private Service service;

    @BeforeEach
    public void setUp() {
        // Set up the initial state for each test
        service = new Service();
        service.dataAccess.authTokens.put("test", new AuthData("test","test"));

    }

    @AfterEach
    public void tearDown() {
        // Clean up any state after each test
        // If needed, clear data from the database or reset configurations
        try {
            service.clearData();
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testRegisterUser() throws ResponseException {
        // Arrange
        UserData user = new UserData("testUser", "testPassword", "testEmail");

        // Act
        AuthData authData = service.registerUser(user);

        // Assert
        assertNotNull(authData, "AuthData should not be null");
        assertEquals("testUser", authData.username(), "Username should match");
    }

    @Test
    public void testLoginUser_Success() throws ResponseException {
        // Arrange
        UserData user = new UserData("testUser", "testPassword", "testEmail");
        service.registerUser(user);

        // Act
        AuthData authData = service.loginUser("testUser", "testPassword");

        // Assert
        assertNotNull(authData, "AuthData should not be null");
        assertEquals("testUser", authData.username(), "Username should match");
    }

    @Test
    public void testLoginUser_Failure_InvalidPassword() {
        // Arrange
        UserData user = new UserData("testUser", "testPassword", "testEmail");
        try {
            service.registerUser(user);
        } catch (ResponseException e) {
            fail("User registration should not fail");
        }

        // Act & Assert
        assertThrows(ResponseException.class, () -> service.loginUser("testUser", "wrongPassword"),
                "Logging in with a wrong password should throw a ResponseException");
    }

    @Test
    public void testCreateGame() throws ResponseException {
        // Arrange
        String gameName = "Test Game";

        // Act
        Integer gameId = service.createGame("test", gameName);

        // Assert
        assertNotNull(gameId, "Game ID should not be null");
    }

    @Test
    public void testJoinGame_Success() throws ResponseException {
        // Arrange
        Integer gameId = service.createGame("test", "Test Game");

        // Act
        service.joinGame("test", ChessGame.TeamColor.WHITE, gameId);

        // Assert
        GameData gameData = service.dataAccess.getGame(gameId);
        assertNotNull(gameData, "GameData should not be null");
        assertEquals("test", gameData.whiteUsername(), "Username should match for white team");
    }

    @Test
    public void testJoinGame_AlreadyTaken() throws ResponseException {
        // Arrange
        Integer gameId = service.createGame("test", "Test Game");
        service.joinGame("test", ChessGame.TeamColor.WHITE, gameId);

        // Act & Assert
        assertThrows(ResponseException.class, () -> service.joinGame("test", ChessGame.TeamColor.WHITE, gameId),
                "Joining a game with an already taken color should throw a 403 error");
    }

    @Test
    public void testListGames() throws ResponseException {
        // Arrange
        service.createGame("test", "Test Game");

        // Act
        Collection<GameData> games = service.listGames("test");

        // Assert
        assertNotNull(games, "Games list should not be null");
        assertFalse(games.isEmpty(), "Games list should not be empty");
    }
}
