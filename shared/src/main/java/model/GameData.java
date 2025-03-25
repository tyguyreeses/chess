package model;

import chess.ChessGame;

/**
 * @param gameID
 * @param whiteUsername
 * @param blackUsername
 * @param gameName
 * @param game
 */
public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public GameData withWhiteUser(String username) {
        return new GameData(gameID, username, blackUsername, gameName, game);
    }
    public GameData withBlackUser(String username) {
        return new GameData(gameID, whiteUsername, username, gameName, game);
    }
    @Override
    public String toString() {
        return String.format("%d. Game Name: %s\tWhite: %s\tBlack: %s", gameID, gameName, whiteUsername, blackUsername);
    }
}
