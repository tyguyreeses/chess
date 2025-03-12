package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {

    private MemoryDataAccess memoryDataAccess;

    @BeforeEach
    public void setUp() {
        memoryDataAccess = new MemoryDataAccess();
    }

    @AfterEach
    public void tearDown() {
        try {
            memoryDataAccess.clearData();
        } catch (ResponseException ignored) {
        }
    }

    @Test
    public void testCreateUserSuccess() throws ResponseException {
        // Arrange
        UserData user = new UserData("testUser", "testPassword", "testEmail");

        // Act
        memoryDataAccess.createUser(user);

        // Assert
        assertNotNull(memoryDataAccess.getUser("testUser"), "User should be successfully created");
    }

    @Test
    public void testCreateUserFailureDuplicate() throws ResponseException {
        // Arrange
        UserData user = new UserData("testUser", "testPassword", "testEmail");
        memoryDataAccess.createUser(user);

        // Act & Assert
        assertThrows(ResponseException.class, () -> memoryDataAccess.createUser(user),
                "Creating a duplicate user should throw a ResponseException");
    }

    @Test
    public void testCreateUserFailureNullPassword() {
        // Arrange
        UserData user = new UserData("testUser", null, "testEmail");

        // Act & Assert
        assertThrows(ResponseException.class, () -> memoryDataAccess.createUser(user),
                "Creating a user with a null password should throw a ResponseException");
    }

    @Test
    public void testCreateAuthSuccess() {
        // Arrange
        String username = "testUser";

        // Act
        String authToken = memoryDataAccess.createAuth(username);
        AuthData authData = memoryDataAccess.getAuth(authToken);

        // Assert
        assertNotNull(authData, "AuthData should be created successfully");
        assertEquals(username, authData.username(), "Auth username should match");
    }

    @Test
    public void testRemoveAuthSuccess() throws DataAccessException {
        // Arrange
        String username = "testUser";
        String authToken = memoryDataAccess.createAuth(username);

        // Act
        memoryDataAccess.removeAuth(authToken);

        // Assert
        assertNull(memoryDataAccess.getAuth(authToken), "Auth token should be removed successfully");
    }

    @Test
    public void testRemoveAuthFailure() {
        // Act & Assert
        assertThrows(DataAccessException.class, () -> memoryDataAccess.removeAuth("invalidToken"),
                "Removing a non-existent auth token should throw a DataAccessException");
    }

    @Test
    public void testCreateGameSuccess() {
        // Arrange
        String gameName = "Test Game";

        // Act
        int gameId = memoryDataAccess.createGame(gameName);
        GameData gameData = memoryDataAccess.getGame(gameId);

        // Assert
        assertNotNull(gameData, "Game should be created successfully");
        assertEquals(gameName, gameData.gameName(), "Game name should match");
    }

    @Test
    public void testGetNonExistentGame() {
        // Act
        GameData gameData = memoryDataAccess.getGame(9999);

        // Assert
        assertNull(gameData, "Getting a non-existent game should return null");
    }

    @Test
    public void testUpdateGameSuccess() throws DataAccessException {
        // Arrange
        int gameId = memoryDataAccess.createGame("Test Game");
        GameData updatedGame = new GameData(gameId, "playerWhite", "playerBlack", "Updated Game", new ChessGame());

        // Act
        memoryDataAccess.updateGame(updatedGame);
        GameData retrievedGame = memoryDataAccess.getGame(gameId);

        // Assert
        assertNotNull(retrievedGame, "Game should exist after update");
        assertEquals("Updated Game", retrievedGame.gameName(), "Game name should be updated");
    }

    @Test
    public void testUpdateGameFailure() {
        // Arrange
        GameData nonExistentGame = new GameData(9999, "playerWhite", "playerBlack", "Non-Existent Game", new ChessGame());

        // Act & Assert
        assertThrows(DataAccessException.class, () -> memoryDataAccess.updateGame(nonExistentGame),
                "Updating a non-existent game should throw a DataAccessException");
    }

    @Test
    public void testClearData() throws ResponseException {
        // Arrange
        memoryDataAccess.createUser(new UserData("user1", "password1", "email1"));
        memoryDataAccess.createAuth("user1");
        memoryDataAccess.createGame("Game 1");

        // Act
        memoryDataAccess.clearData();

        // Assert
        assertTrue(memoryDataAccess.users.isEmpty(), "Users should be cleared");
        assertTrue(memoryDataAccess.authTokens.isEmpty(), "Auth tokens should be cleared");
        assertTrue(memoryDataAccess.games.isEmpty(), "Games should be cleared");
    }
}

