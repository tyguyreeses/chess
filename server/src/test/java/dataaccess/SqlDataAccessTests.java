package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
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
    public void testConfigureDatabaseSuccess() throws SQLException, ResponseException {

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
        public void testRegisterUserMultipleTimes() throws ResponseException {
            UserData newUser = new UserData("testuser", "123", "test@mail.com");
            ResponseException ex = assertThrows(ResponseException.class, () -> db.createUser(newUser));
            assertEquals(403, ex.statusCode());
        }

        @Test
        public void testRegisterUserBadRequest() {
            UserData newUser = new UserData("", "", null);
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
    }

    @Test
    public void testClearData() throws ResponseException {
        UserData newUser = new UserData("newUser", "123", "test@mail.com");
        db.createUser(newUser);
        assertNotNull(db.getUser("newUser"));
        db.clearData();
        assertNull(db.getUser("newUser"));
    }
}

