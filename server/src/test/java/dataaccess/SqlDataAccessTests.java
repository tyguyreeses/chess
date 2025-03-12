package dataaccess;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

public class SqlDataAccessTests {
    private SqlDataAccess sqlDataAccess;

    @BeforeEach
    public void setup() {
        this.sqlDataAccess = new SqlDataAccess();
    }

    @Test
    public void testConfigureDatabaseSuccess() throws SQLException, ResponseException {
        // After configureDatabase() runs, check if the tables were created
        try (Connection conn = DatabaseManager.getConnection()) {
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
    }
}
