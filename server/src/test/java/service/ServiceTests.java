package service;

import chess.ChessGame.TeamColor;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import exception.ResponseException;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.Service;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTests {
    private Service service;
    private DataAccess dataAccess;

    @BeforeEach
    void setUp() throws ResponseException {
        this.dataAccess = new MemoryDataAccess();
        this.service = new Service(dataAccess);
        service.clearData();
    }

    @Test
    void clearDataSuccess() throws ResponseException {
        UserData user = new UserData("testUser", "password", "email@test.com");
        service.registerUser(user);
        assertNotNull(dataAccess.getUser("testUser"));
        service.clearData();
        assertNull(dataAccess.getUser("testUser"));
    }

    @Test
    void registerUserSuccess() throws ResponseException {
        UserData user = new UserData("testUser", "password", "email@test.com");
        AuthData auth = service.registerUser(user);
        assertNotNull(auth);
        assertEquals("testUser", auth.username());
    }

    @Test
    void registerUserAlreadyTaken() {
        UserData user = new UserData("testUser", "password", "email@test.com");
        assertDoesNotThrow(() -> service.registerUser(user));
        ResponseException exception = assertThrows(ResponseException.class, () -> service.registerUser(user));
        assertEquals(403, exception.statusCode());
    }

    @Test
    void registerUserEmptyPassword() {
        UserData user = new UserData("testUser", null, "email@test.com");
        ResponseException exception = assertThrows(ResponseException.class, () -> service.registerUser(user));
        assertEquals(400, exception.statusCode());
    }

    @Test
    void registerUserEmptyUsername() {
        UserData user = new UserData(null, "testPassword", "email@test.com");
        ResponseException exception = assertThrows(ResponseException.class, () -> service.registerUser(user));
        assertEquals(400, exception.statusCode());
    }

    @Test
    void loginUserSuccess() throws ResponseException {
        UserData user = new UserData("testUser", "password", "email@test.com");
        service.registerUser(user);
        AuthData auth = service.loginUser("testUser", "password");
        assertNotNull(auth);
        assertEquals("testUser", auth.username());
    }

    @Test
    void loginUserWrongPassword() throws ResponseException {
        UserData user = new UserData("testUser", "password", "email@test.com");
        service.registerUser(user);
        ResponseException exception = assertThrows(ResponseException.class, () -> service.loginUser("testUser", "wrongPass"));
        assertEquals(401, exception.statusCode());
    }

    @Test
    void loginUserNonexistentUser() {
        ResponseException exception = assertThrows(ResponseException.class, () -> service.loginUser("nonUser", "password"));
        assertEquals(401, exception.statusCode());
    }

    @Test
    void logoutUserSuccess() throws ResponseException {
        UserData user = new UserData("testUser", "password", "email@test.com");
        AuthData auth = service.registerUser(user);
        service.logoutUser(auth.authToken());
        assertNull(dataAccess.getAuth(auth.authToken()));
    }

    @Test
    void logoutUserInvalidToken() {
        ResponseException exception = assertThrows(ResponseException.class, () -> service.logoutUser("invalidToken"));
        assertEquals(401, exception.statusCode());
    }

    @Test
    void logoutUserTwice() throws ResponseException {
        UserData user = new UserData("testUser", "password", "email@test.com");
        AuthData auth = service.registerUser(user);
        service.logoutUser(auth.authToken());
        ResponseException exception = assertThrows(ResponseException.class, () -> service.logoutUser(auth.authToken()));
        assertEquals(401, exception.statusCode());
    }

    @Test
    void listGamesSuccess() throws ResponseException {
        UserData user = new UserData("testUser", "password", "email@test.com");
        AuthData auth = service.registerUser(user);
        service.createGame(auth.authToken(), "Chess Match");
        Collection<GameData> games = service.listGames(auth.authToken());
        assertFalse(games.isEmpty());
        assertEquals(1, games.size());
    }

    @Test
    void listGamesInvalidToken() {
        ResponseException exception = assertThrows(ResponseException.class, () -> service.listGames("invalidToken"));
        assertEquals(401, exception.statusCode());
    }

    @Test
    void listGamesEmpty() throws ResponseException {
        UserData user = new UserData("testUser", "password", "email@test.com");
        AuthData auth = service.registerUser(user);
        Collection<GameData> games = service.listGames(auth.authToken());
        assertNotNull(games);
        assertTrue(games.isEmpty());
    }

    @Test
    void createGameSuccess() throws ResponseException {
        UserData user = new UserData("testUser", "password", "email@test.com");
        AuthData auth = service.registerUser(user);
        Integer gameId = service.createGame(auth.authToken(), "Game1");
        assertNotNull(gameId);
        assertEquals("Game1", dataAccess.getGame(gameId).gameName());
    }

    @Test
    void createGameInvalidToken() {
        ResponseException exception = assertThrows(ResponseException.class, () -> service.createGame("invalidToken", "Game1"));
        assertEquals(401, exception.statusCode());
    }

    @Test
    void joinGameWhiteSuccess() throws ResponseException {
        UserData user = new UserData("testUser", "password", "email@test.com");
        AuthData auth = service.registerUser(user);
        Integer gameId = service.createGame(auth.authToken(), "Chess Match");
        service.joinGame(auth.authToken(), TeamColor.WHITE, gameId);
        assertEquals("testUser", dataAccess.getGame(gameId).whiteUsername());
    }

    @Test
    void joinGameBlackSuccess() throws ResponseException {
        UserData user = new UserData("testUser", "password", "email@test.com");
        AuthData auth = service.registerUser(user);
        Integer gameId = service.createGame(auth.authToken(), "Chess Match");
        service.joinGame(auth.authToken(), TeamColor.BLACK, gameId);
        assertEquals("testUser", dataAccess.getGame(gameId).blackUsername());
    }

    @Test
    void joinGameInvalidToken() {
        ResponseException exception = assertThrows(ResponseException.class, () -> service.joinGame("invalidToken", TeamColor.WHITE, 1));
        assertEquals(400, exception.statusCode());
    }

    @Test
    void joinGameAlreadyTakenColor() throws ResponseException {
        UserData user1 = new UserData("player1", "pass", "email1@test.com");
        UserData user2 = new UserData("player2", "pass", "email2@test.com");
        AuthData auth1 = service.registerUser(user1);
        AuthData auth2 = service.registerUser(user2);
        Integer gameId = service.createGame(auth1.authToken(), "Chess Match");
        service.joinGame(auth1.authToken(), TeamColor.WHITE, gameId);
        ResponseException exception = assertThrows(ResponseException.class, () -> service.joinGame(auth2.authToken(), TeamColor.WHITE, gameId));
        assertEquals(403, exception.statusCode());
    }

    @Test
    void joinGameInvalidGameID() throws ResponseException {
        UserData user = new UserData("testUser", "password", "email@test.com");
        AuthData auth = service.registerUser(user);
        ResponseException exception = assertThrows(ResponseException.class, () -> service.joinGame(auth.authToken(), TeamColor.WHITE, 999));
        assertEquals(400, exception.statusCode());
    }

    @Test
    void joinGameNullPlayerColor() throws ResponseException {
        UserData user = new UserData("testUser", "password", "email@test.com");
        AuthData auth = service.registerUser(user);
        Integer gameId = service.createGame(auth.authToken(), "Chess Match");
        ResponseException exception = assertThrows(ResponseException.class, () -> service.joinGame(auth.authToken(), null, gameId));
        assertEquals(400, exception.statusCode());
    }
}