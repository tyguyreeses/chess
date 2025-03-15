package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

public class SqlDataAccessTests {
    private SqlDataAccess db;
    protected Connection conn;

    @BeforeEach
    public void setup() {
        this.db = new SqlDataAccess();
        try {
            conn = DatabaseManager.getConnection();
        } catch (ResponseException e) {
            System.out.println("Couldn't set up the test: " + e.getMessage());
        }
    }

    @AfterEach
    public void tearDown() throws SQLException, ResponseException {
        db.clearData();
        if (conn != null) {
            conn.close();
        }
    }

    @Test
    public void testConfigureDatabaseSuccess() throws SQLException {

        // Check if the 'users' table exists
        String checkUsersTable = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'users' AND " +
                "table_schema = 'chess'";
        try (PreparedStatement statement = conn.prepareStatement(checkUsersTable);
             ResultSet rs = statement.executeQuery()) {
                rs.next();
                int count = rs.getInt(1);
                assertEquals(1, count, "Users table should exist.");
             }

        // Check if the 'games' table exists
        String checkGamesTable = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'games'";
        try (PreparedStatement statement = conn.prepareStatement(checkGamesTable);
             ResultSet rs = statement.executeQuery()) {
                rs.next();
                int count = rs.getInt(1);
                assertEquals(1, count, "Games table should exist.");
             }

        // Check if the 'authTokens' table exists
        String checkAuthTokensTable = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'authTokens'";
        try (PreparedStatement statement = conn.prepareStatement(checkAuthTokensTable);
             ResultSet rs = statement.executeQuery()) {
                rs.next();
                int count = rs.getInt(1);
                assertEquals(1, count, "AuthTokens table should exist.");
             }
    }

    /**
     * register Tests
     */
    @Nested
    class RegisterTests {
        @Test
        public void testRegisterUserAdded() throws ResponseException {
            UserData newUser = new UserData("newUser", "123", "test@mail.com");
            db.createUser(newUser);
            assertNotNull(db.getUser("newUser"));
        }

        @Test
        public void testRegisterUserMultipleTimes() {
            UserData newUser = new UserData("testuser", "123", "test@mail.com");
            ResponseException ex = assertThrows(ResponseException.class, () -> db.createUser(newUser));
            assertEquals(403, ex.statusCode());
        }

        @Test
        public void testRegisterUserBadUsername() {
            UserData newUser = new UserData(null, "valid", "valid");
            ResponseException ex = assertThrows(ResponseException.class, () -> db.createUser(newUser));
            assertEquals(400, ex.statusCode());
        }
        @Test
        public void testRegisterUserInvalidEmail() {
            UserData newUser = new UserData("invalidEmailUser", "password123", null);
            ResponseException ex = assertThrows(ResponseException.class, () -> db.createUser(newUser));
            assertEquals(400, ex.statusCode());
        }

        @Test
        public void testRegisterUserInvalidPassword() {
            UserData newUser = new UserData("invalidEmailUser", null, "valid");
            ResponseException ex = assertThrows(ResponseException.class, () -> db.createUser(newUser));
            assertEquals(400, ex.statusCode());
        }
    }

    /**
     * getUser Tests
     */
    @Nested
    class GetUserTests {
        @Test
        public void testGetUserSuccess() throws ResponseException {
            UserData expected = new UserData("testUser", "testPassword", "testEmail");
            UserData actual = db.getUser("testUser");
            assertEquals(expected, actual);
        }

        @Test
        public void testGetUserNotExist() throws ResponseException {
            assertNull(db.getUser("badUser"));
        }

        @Test
        public void testGetUserBadRequest() {
            assertThrows(ResponseException.class, () -> db.getUser(null));
        }
    }

    /**
     * auth Tests
     */
    @Nested
    class AuthTests {
        @Test
        public void testCreateAuth() throws ResponseException {
            UserData ud = new UserData("authTest", "authPassword", "auth@mail.com");
            db.createUser(ud);
            String auth = db.createAuth("authTest");
            AuthData authData = new AuthData(auth, "authTest");
            assertEquals(authData, db.getAuth(auth));
        }

        @Test
        public void testRemoveAuth() throws ResponseException {
            UserData ud = new UserData("authTest", "authPassword", "auth@mail.com");
            db.createUser(ud);
            String auth = db.createAuth("authTest");
            db.removeAuth(auth);
            assertNull(db.getAuth(auth));
        }

        @Test
        public void testRemoveAuthNotExist() throws ResponseException {
            assertEquals(0, db.removeAuth("invalidToken"));
        }

        @Test
        public void testAuthInvalidToken() throws ResponseException {
            assertNull(db.getAuth("invalidToken"));
        }
    }

    @Nested
    class GameTests {
        @Test
        public void testCreateGameSuccess() throws ResponseException {
            int id = db.createGame("name");
            assertNotNull(db.getGame(id));
        }

        @Test
        public void testCreateGameInvalidID() throws ResponseException {
            assertNull(db.getGame(-1));
        }

        @Test
        public void testAddAndGetMultipleGames() throws ResponseException {
            db.createGame("1");
            int id2 = db.createGame("2");
            int id3 = db.createGame("3");
            assertEquals(3, db.getGames().size());
            assertNotNull(db.getGame(id2));
            assertNotNull(db.getGame(id3));
        }
        @Test
        public void testListGamesNoGames() throws ResponseException {
            assertNull(db.getGames());
        }
        @Test
        public void testUpdateGame() throws ResponseException, InvalidMoveException, DataAccessException {
            int id = db.createGame("name");
            ChessMove move = new ChessMove(new ChessPosition(2,2),new ChessPosition(4,2),null);
            ChessGame game = new ChessGame();
            game.makeMove(move);
            GameData gd = new GameData(id, null, null, "name", game);
            db.updateGame(gd);
            assertEquals(db.getGame(id).game(), game);
        }
        @Test
        public void testUpdateGameWithNullValues() throws ResponseException, InvalidMoveException, DataAccessException {
            int id = db.createGame("name");
            ChessMove move = new ChessMove(new ChessPosition(2, 2), new ChessPosition(4, 2), null);
            ChessGame game = new ChessGame();
            game.makeMove(move);
            GameData gd = new GameData(id, null, null, "name", game);  // Null for game name
            db.updateGame(gd);
            GameData updatedGame = db.getGame(id);
            assertEquals("name", updatedGame.gameName());
            assertEquals(updatedGame.game(), game);
        }
        @Test
        public void testUpdateGameNoChange() throws ResponseException, DataAccessException {
            int id = db.createGame("name");
            ChessGame game = new ChessGame();
            GameData gd = new GameData(id, null, null, "name", game);
            db.updateGame(gd);
            assertEquals(db.getGame(id).game(), game);
        }
    }

    @Test
    public void testClearData() throws ResponseException {
        UserData newUser = new UserData("newUser", "123", "test@mail.com");
        db.createUser(newUser);
        assertNotNull(db.getUser("newUser"));
        db.clearData();
        assertNull(db.getUser("newUser"));
    }

    @Test
    public void testGamesTableExistsAfterClear() throws SQLException, ResponseException {
        db.clearData();
        String checkGamesTable = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'games'";
        try (PreparedStatement statement = conn.prepareStatement(checkGamesTable);
             ResultSet rs = statement.executeQuery()) {
            rs.next();
            int count = rs.getInt(1);
            assertEquals(1, count, "Games table should exist after clearing data.");
        }
    }
}

