package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable{

    ChessPiece[][] squares = new ChessPiece[9][9];
    ChessPosition whiteKingPos = null;
    ChessPosition blackKingPos = null;

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
        StringBuilder sb = new StringBuilder();
        for (int i = 8; i > 0; i--) {
            sb.append("|");
            for (int j = 1; j < squares[i].length; j++) {
                if (squares[i][j] != null) {
                    sb.append(squares[i][j]);
                } else {
                    sb.append(" ");
                }
                if (j < squares[i].length - 1) {
                    sb.append("|"); // Tab between elements in a row
                }
            }
            sb.append("|\n"); // New line after each row
        }
        return sb.toString();
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

    public void movePiece(ChessMove move) {
        ChessPosition startPos = move.getStartPosition();
        // add the piece to the end position
        this.addPiece(move.getEndPosition(), getPiece(startPos));
        // remove the piece from starting position
        this.addPiece(startPos, null);
    }

    public boolean isInCheck(ChessGame.TeamColor teamColor) {
        // store king position
        ChessPosition kingPos = teamColor == ChessGame.TeamColor.WHITE ? whiteKingPos : blackKingPos;

        // iterate over whole board
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                // if there is a piece
                if (getPiece(pos) != null) {
                    // calculate valid moves
                    Collection<ChessMove> moves = getPiece(pos).pieceMoves(this, pos);
                    // check if a piece is threatening the king
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().getRow() == kingPos.getRow() && move.getEndPosition().getColumn() == kingPos.getColumn()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
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
        whiteKingPos = new ChessPosition(1, 5);

        // add black pieces
        for(int col=1; col<=8; col++) {
            // add back row piece and the pawn
            squares[8][col] = new ChessPiece(ChessGame.TeamColor.BLACK, backRow.get(col));
            squares[7][col] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }
        blackKingPos = new ChessPosition(8, 5);

        backRow.add(ChessPiece.PieceType.ROOK);
    }

    @Override
    public ChessBoard clone() {
        try {
            ChessBoard clone = (ChessBoard) super.clone();

            // make deepCopy of squares
            ChessPiece[][] cloneSquares = new ChessPiece[9][9];
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col<=8; col++) {
                    ChessPiece ogPiece = squares[row][col];
                    cloneSquares[row][col] = (ogPiece != null) ? ogPiece.clone() : null;
                }
            }
            clone.squares = cloneSquares;

            // make deep copy of king positions
            ChessPosition cloneWKP = whiteKingPos != null ? new ChessPosition(whiteKingPos.getRow(), whiteKingPos.getColumn()) : null;
            ChessPosition cloneBKP = blackKingPos != null ? new ChessPosition(blackKingPos.getRow(), blackKingPos.getColumn()) : null;
            // update clone king positions
            clone.whiteKingPos = cloneWKP;
            clone.blackKingPos = cloneBKP;

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
