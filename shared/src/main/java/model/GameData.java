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

    public GameData withUpdatedGame(ChessGame game) {
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    @Override
    public String toString() {
        return String.format("Game Name: %s     White: %s     Black: %s", gameName, whiteUsername, blackUsername);
    }
}
