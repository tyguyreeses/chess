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

    ChessPiece[][] squares = new ChessPiece[9][9];

    public ChessBoard() {

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

    @Override
    public String toString() {
        return Arrays.toString(squares);
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
        // empty initial squares
        for (int row = 3; row < 7; row++) {
            for (int col = 1; col<=8; col++) {
                squares[row][col] = null;
            }
        }

        // set up back row piece order
        ArrayList<ChessPiece.PieceType> backRow = new ArrayList<>();
        backRow.add(ChessPiece.PieceType.ROOK); // filler
        backRow.add(ChessPiece.PieceType.ROOK);
        backRow.add(ChessPiece.PieceType.KNIGHT);
        backRow.add(ChessPiece.PieceType.BISHOP);
        backRow.add(ChessPiece.PieceType.QUEEN);
        backRow.add(ChessPiece.PieceType.KING);
        backRow.add(ChessPiece.PieceType.BISHOP);
        backRow.add(ChessPiece.PieceType.KNIGHT);
        backRow.add(ChessPiece.PieceType.ROOK);

        // add white pieces
        for(int col=1; col<=8; col++) {
            // add back row piece and the pawn
            squares[1][col] = new ChessPiece(ChessGame.TeamColor.WHITE, backRow.get(col));
            squares[2][col] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        }

        // add black pieces
        for(int col=1; col<=8; col++) {
            // add back row piece and the pawn
            squares[8][col] = new ChessPiece(ChessGame.TeamColor.BLACK, backRow.get(col));
            squares[7][col] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }

        backRow.add(ChessPiece.PieceType.ROOK);
    }
}
