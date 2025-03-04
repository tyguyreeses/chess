package mytests.dataaccess;

import chess.*;
import dataaccess.*;
import exception.ResponseException;
import model.*;
import org.junit.jupiter.api.*;  // import JUnit 5 annotations
import static org.junit.jupiter.api.Assertions.*;  // import assertion methods

public class DataAccessTests {

    private DataAccess dataAccess;

    // this runs before each test method
    @BeforeEach
    public void setUp() {
        // set up the initial state for each test
        dataAccess = new DataAccess();
        dataAccess.authTokens.put("test", new AuthData("test","test"));
    }

    // this runs after each test method
    @AfterEach
    public void tearDown() throws ResponseException {
        // clean up any state after each test
        dataAccess.clearData();
    }

    @Test
    public void testCreateUser_Success() throws DataAccessException {
        UserData user = new UserData("user1", "password123", "hi@gmail.com");
        dataAccess.createUser(user);
        assertEquals(user, dataAccess.getUser("user1"));
    }

    // test method
    @Test
    public void testCreateUser_UserAlreadyExists() {
        UserData user = new UserData("user1", "password123", "hi@gmail.com");
        try {
            dataAccess.createUser(user);
            dataAccess.createUser(user); // try to create the same user again
            fail("Expected DataAccessException to be thrown");
        } catch (DataAccessException e) {
            assertEquals("Error: User already exists in the database", e.getMessage());
        }
    }

    // test createAuth and getAuth methods
    @Test
    public void testCreateAuth_Success() {
        UserData user = new UserData("user1", "password123", "hi@gmail.com");
        try {
            dataAccess.createUser(user);
            String authToken = dataAccess.createAuth("user1");
            AuthData authData = dataAccess.getAuth(authToken);
            assertNotNull(authData);
            assertEquals("user1", authData.username());
        } catch (DataAccessException e) {
            fail("DataAccessException should not be thrown");
        }
    }

    @Test
    public void testRemoveAuth_Success() {
        UserData user = new UserData("user1", "password123", "hi@gmail.com");
        try {
            dataAccess.createUser(user);
            String authToken = dataAccess.createAuth("user1");
            dataAccess.removeAuth(authToken); // remove the auth token
            assertNull(dataAccess.getAuth(authToken)); // verify it's removed
        } catch (DataAccessException e) {
            fail("DataAccessException should not be thrown");
        }
    }

    @Test
    public void testRemoveAuth_AuthTokenNotExist() {
        try {
            dataAccess.removeAuth("nonexistentAuthToken");
            fail("Expected DataAccessException to be thrown");
        } catch (DataAccessException e) {
            assertEquals("Error: AuthToken doesn't exist in the database", e.getMessage());
        }
    }

    // test createGame and getGame methods
    @Test
    public void testCreateGame_Success() throws DataAccessException {
        // Test the creation of a game and the auto-generated gameID
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(1, "player1", null, "name", game);

        // Create the game and capture the returned gameID
        int gameID = dataAccess.createGame("player1", "name");

        // Validate the gameID is valid and the game was created successfully
        assertEquals(gameData.gameID(), gameID);
        assertEquals(gameData, dataAccess.getGame(gameID));
    }

    @Test
    public void testCreateGame_NewGameID() throws DataAccessException {
        // Test that new game IDs are generated sequentially
        ChessGame game1 = new ChessGame();
        dataAccess.createGame("player1", "game1");

        // Next game should get a new ID, which should be 2
        ChessGame game2 = new ChessGame();
        int newGameID = dataAccess.createGame("player2", "game2");

        assertEquals(2, newGameID);  // The second game should get ID 2
    }

    // tests for updating a game
    @Test
    public void testUpdateGame_Success() throws InvalidMoveException, DataAccessException {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(1, "player1", "player2", "name", game);
        dataAccess.createGame("player1", "name");

        // Simulate a move
        game.makeMove(new ChessMove(new ChessPosition(2, 7), new ChessPosition(4, 7), null));

        // Create an updated game data object
        GameData updatedGame = new GameData(1, "player1", "player2", "name", game);
        dataAccess.updateGame(updatedGame);

        // Assert that the game was updated correctly
        assertEquals(updatedGame, dataAccess.getGame(1));
    }

    @Test
    public void testUpdateGame_GameNotExist() {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(1, "player1", "player2","name", game);
        try {
            dataAccess.updateGame(gameData); // trying to update a game that doesn't exist
            fail("Expected DataAccessException to be thrown");
        } catch (DataAccessException e) {
            assertEquals("Error: gameID doesn't exist in the database", e.getMessage());
        }
    }

    // test clear method
    @Test
    public void testClear() {
        UserData user = new UserData("user1", "password123", "hi@gmail.com");
        try {
            dataAccess.createUser(user);
            dataAccess.clearData(); // Clear the data
            assertNull(dataAccess.getUser("user1")); // verify user is cleared
        } catch (DataAccessException | ResponseException e) {
            fail("DataAccessException should not be thrown");
        }
    }

    // Another test method
    @Test
    public void anotherTest() {
        // You can have multiple test methods, each testing different functionality
    }
}