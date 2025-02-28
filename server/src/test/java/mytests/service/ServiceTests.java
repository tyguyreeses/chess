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
}
