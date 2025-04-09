package ui.websocket;

import chess.ChessGame;

public interface GameHandler {
    public void updateGame(ChessGame game);
    public void printMessage(String message);
}
