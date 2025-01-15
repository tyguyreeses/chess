package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
       return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        return PieceMovesCalculator.calculateMoves(this, board, position);
    }
}

// ChessPiece subclass for move calculation
class PieceMovesCalculator {
    static Collection<ChessMove> calculateMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                return BishopMovesCalculator.calculateMoves(board, position);
        }
        return null;
    }
}

// PieceMovesCalculator subclass for Bishop move calculation
class BishopMovesCalculator extends PieceMovesCalculator {
    static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {

        // initialize the collection of moves to return
        Collection<ChessMove> validMoves = new ArrayList<>();

        // tracks the direction
        int[] rowDirection = {1, -1, -1, 1};
        int[] colDirection = {1, 1, -1, -1};

        // loops over each diagonal direction
        for (int i = 0; i < 4; i++) {
            // retrieves the row and column indices
            int row = position.getRow();
            int col = position.getColumn();

            // goes in a diagonal line, storing each valid move
            while (row < 8 && row > 1 && col < 8 && col > 1) {

                row += rowDirection[i];
                col += colDirection[i];

                ChessPosition endPosition = new ChessPosition(row, col);

                // if the square is occupied by a piece
                if (board.getPiece(endPosition) != null) {
                    // if it can attack that piece
                    if (CalculatePotentialAttack.canAttack(board, position, endPosition)) {
                        // add it to valid moves
                        validMoves.add(new ChessMove(position, endPosition, null));
                    }
                    // then ignore the rest of the diagonal
                    break;
                }
                validMoves.add(new ChessMove(position, endPosition, null));
            }
        }

        return validMoves;
    }
}

// class to calculate whether a piece can be taken
class CalculatePotentialAttack {
    static boolean canAttack(ChessBoard board, ChessPosition position1, ChessPosition position2) {
        ChessPiece piece = board.getPiece(position1);
        ChessPiece opponent = board.getPiece(position2);
        return piece.getTeamColor() != opponent.getTeamColor();
    }
}
