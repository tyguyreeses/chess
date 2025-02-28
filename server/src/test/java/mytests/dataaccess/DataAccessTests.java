package mytests.dataaccess;

import chess.*;
import dataaccess.*;
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
    }

    // this runs after each test method
    @AfterEach
    public void tearDown() {
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
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(1, "player1", "player2","name", game);
        int gameID = dataAccess.createGame(gameData);
        assertEquals(gameData.gameID(), gameID);
        assertEquals(gameData, dataAccess.getGame(gameID));
    }

    @Test
    public void testCreateGame_GameAlreadyExists() {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(1, "player1", "player2", "name", game);
        try {
            dataAccess.createGame(gameData);
            dataAccess.createGame(gameData); // Try to create the same game again
            fail("Expected DataAccessException to be thrown");
        } catch (DataAccessException e) {
            assertEquals("Error: gameID already exists in the database", e.getMessage());
        }
    }

    // test updateGame method
    @Test
    public void testUpdateGame_Success() throws InvalidMoveException, DataAccessException {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(1, "player1", "player2","name", game);
        dataAccess.createGame(gameData);
        game.makeMove(new ChessMove(new ChessPosition(2, 7), new ChessPosition(4, 7), null));
        GameData updatedGame = new GameData(1, "player1", "player2","name", game);
        dataAccess.updateGame(updatedGame);
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
        } catch (DataAccessException e) {
            fail("DataAccessException should not be thrown");
        }
    }

    // Another test method
    @Test
    public void anotherTest() {
        // You can have multiple test methods, each testing different functionality
    }
}