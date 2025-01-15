package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] squares = new ChessPiece[9][9];

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()][position.getColumn()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()][position.getColumn()];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // clear initial empty squares
        for (int i = 3; i < 6; i++) {
            for (int j = 0; j < 9; j++) {
                squares[i][j] = null;
            }
        }
        // list of back row pieces
        ArrayList<ChessPiece.PieceType> backRow = new ArrayList<>();
        backRow.add(ChessPiece.PieceType.ROOK); // filler object
        backRow.add(ChessPiece.PieceType.ROOK);
        backRow.add(ChessPiece.PieceType.KNIGHT);
        backRow.add(ChessPiece.PieceType.BISHOP);
        backRow.add(ChessPiece.PieceType.QUEEN);
        backRow.add(ChessPiece.PieceType.KING);
        backRow.add(ChessPiece.PieceType.BISHOP);
        backRow.add(ChessPiece.PieceType.KNIGHT);
        backRow.add(ChessPiece.PieceType.ROOK);

        // add white pieces
        for (int col = 1; col < 9; col++) {
            ChessPiece piece = new ChessPiece(ChessGame.TeamColor.WHITE, backRow.get(col));
            ChessPiece pawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            // adds the back row piece
            ChessPosition position = new ChessPosition(1,col);
            addPiece(position, piece);
            // adds the pawn
            ChessPosition pawnPos = new ChessPosition(2, col);
            addPiece(pawnPos, pawn);
        }
        // add black pieces
        for (int col = 1; col < 9; col++) {
            ChessPiece piece = new ChessPiece(ChessGame.TeamColor.BLACK, backRow.get(col));
            ChessPiece pawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            // adds the back row piece
            ChessPosition position = new ChessPosition(8,col);
            addPiece(position, piece);
            // adds the pawn
            ChessPosition pawnPos = new ChessPosition(7, col);
            addPiece(pawnPos, pawn);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }
}
