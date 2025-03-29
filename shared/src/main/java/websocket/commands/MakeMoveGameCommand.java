package websocket.commands;

import chess.ChessMove;

public class MakeMoveGameCommand extends UserGameCommand {

    private final ChessMove move;

    public MakeMoveGameCommand(String authToken, Integer gameID, ChessMove move) {
        super(CommandType.CONNECT, authToken, gameID);
        this.move = move;
    }

    public ChessMove getMove() {
        return this.move;
    }
}
