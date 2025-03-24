import chess.*;

import ui.ServerFacade;
import ui.repls.PreLoginRepl;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        ServerFacade serverFacade = new ServerFacade(8080);
        new PreLoginRepl(serverFacade).run();
    }
}