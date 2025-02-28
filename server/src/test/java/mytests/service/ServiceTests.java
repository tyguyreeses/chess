package mytests.service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.Service;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
    private Service service;

    @BeforeEach
    public void setUp() {
        service = new Service();

        // reset database
        service.clearData();
    }

    @Test
    public void testRegisterUser_Success() {
        UserData userData = new UserData("testUser", "password123", "test@example.com");

        Service.Response response = service.registerUser(userData);

        assertEquals(200, response.status());
        assertEquals("testUser", response.username());
        assertNotNull(response.authToken());

        // Verify user was actually stored in DataAccess
        assertNotNull(service.dataAccess.getUser("testUser"));
    }

    @Test
    public void testRegisterUser_MissingFields() {
        UserData userData = new UserData(null, "password123", "test@example.com");

        Service.Response response = service.registerUser(userData);

        assertEquals(400, response.status());
        assertEquals("Error: bad request", response.message());

        // Ensure no user was created
        assertNull(service.dataAccess.getUser(null));
    }

    @Test
    public void testRegisterUser_UsernameTaken() {
        UserData userData = new UserData("testUser", "password123", "test@example.com");

        try {
            service.dataAccess.createUser(userData);  // Prepopulate database with the same username
        } catch (DataAccessException e) {
            fail("Setup failed: could not create initial user");
        }

        Service.Response response = service.registerUser(userData);

        assertEquals(403, response.status());
        assertEquals("Error: already taken", response.message());
    }

    @Test
    public void testClearData_Success() {
        // Add a user to ensure there's data to clear
        UserData user = new UserData("testUser", "password123", "test@example.com");
        try {
            service.dataAccess.createUser(user);
        } catch (DataAccessException e) {
            fail("Setup failed: could not create initial user");
        }

        // Verify user exists before clearing
        assertNotNull(service.dataAccess.getUser("testUser"));

        // Call clearData
        Service.Response response = service.clearData();

        // Verify response
        assertEquals(200, response.status());

        // Verify data is cleared
        assertNull(service.dataAccess.getUser("testUser"));
    }

    @Test
    public void testLoginUser_Success() {
        UserData user = new UserData("testUser", "password123", "test@example.com");
        try {
            service.dataAccess.createUser(user);
        } catch (DataAccessException e) {
            fail("Setup failed: could not create initial user");
        }

        // Attempt login
        Service.Response response = service.loginUser("testUser", "password123");

        assertEquals(200, response.status());
        assertEquals("testUser", response.username());
        assertNotNull(response.authToken());
    }

    @Test
    public void testLoginUser_WrongPassword() {
        UserData user = new UserData("testUser", "password123", "test@example.com");
        try {
            service.dataAccess.createUser(user);
        } catch (DataAccessException e) {
            fail("Setup failed: could not create initial user");
        }

        // Attempt login with wrong password
        Service.Response response = service.loginUser("testUser", "wrongPassword");

        assertEquals(401, response.status());
        assertEquals("Error: unauthorized", response.message());
    }

    @Test
    public void testLoginUser_NonExistentUser() {
        // Attempt login with a user that doesn't exist
        Service.Response response = service.loginUser("nonExistentUser", "password");

        assertEquals(401, response.status());
        assertEquals("Error: unauthorized", response.message());
    }

    @Test
    public void testLogoutUser_Success() {
        // Register a user and generate an auth token
        UserData user = new UserData("testUser", "password123", "test@example.com");
        try {
            service.dataAccess.createUser(user);
            String authToken = service.dataAccess.createAuth(user.username());

            // Ensure auth token exists before logging out
            assertNotNull(service.dataAccess.getAuth(authToken));

            // Attempt to log out
            Service.Response response = service.logoutUser(authToken);

            // Verify response
            assertEquals(200, response.status());

            // Ensure auth token is removed
            assertNull(service.dataAccess.getAuth(authToken));
        } catch (DataAccessException e) {
            fail("Setup failed: could not create initial user or auth token");
        }
    }

    @Test
    public void testLogoutUser_InvalidAuthToken() {
        // Attempt to log out with a non-existent auth token
        Service.Response response = service.logoutUser("invalidToken");

        assertEquals(401, response.status());
        assertEquals("Error: unauthorized", response.message());
    }
}
