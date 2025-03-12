package dataaccess;

import exception.ResponseException;
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

    @Test
    public void testRegisterUserAdded() throws ResponseException {
        UserData newUser = new UserData("newUser", "123", "test@mail.com");
        db.createUser(newUser);
        assertNotNull(db.getUser("newUser"));
    }

    @Test
    public void testRegisterUserAlreadyTaken() {
        UserData newUser = new UserData("testuser", "123", "test@mail.com");
        ResponseException ex = assertThrows(ResponseException.class, () -> db.createUser(newUser));
        assertEquals(400, ex.statusCode());
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

