package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public GameData withWhiteUser(String username) {
        return new GameData(gameID, username, blackUsername, gameName, game);
    }
    public GameData withBlackUser(String username) {
        return new GameData(gameID, whiteUsername, username, gameName, game);
    }
    public GameData withNewData(ChessGame chessGame) {
        return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
    }
}
