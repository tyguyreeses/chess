package dataaccess;

import exception.ResponseException;
import org.junit.jupiter.api.*;

import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

public class SqlDataAccessTests {
    private SqlDataAccess sqlDataAccess;
    protected Connection conn;

    @BeforeEach
    public void setup() {
        this.sqlDataAccess = new SqlDataAccess();
        try {
            conn = DatabaseManager.getConnection();
            addTestUser();
        } catch (ResponseException e) {
            System.out.println("Couldn't set up the test: " + e.getMessage());
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
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




    public void addTestUser() throws ResponseException {
        String insertUser = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        try (PreparedStatement statement = conn.prepareStatement(insertUser)) {
            // Set parameters for username and password
            statement.setString(1, "testuser");
            statement.setString(2, "testpassword");
            statement.setString(3, "testemail");

            // Execute the insert statement and get the result
            int rowsAffected = statement.executeUpdate();

            // Assert that the insertion affected 1 row
            assertEquals(1, rowsAffected, "User should be inserted into the table.");
        } catch (Exception e) {
            throw new ResponseException(500, "Couldn't add test user: " + e.getMessage());
        }
    }
}
