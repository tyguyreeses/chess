package chess;

import java.util.ArrayList;
import java.util.Collection;
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
        return switch (piece.getPieceType()) {
            case KING -> KingMovesCalculator.calculateMoves(board, position);
            case QUEEN -> QueenMovesCalculator.calculateMoves(board, position);
            case BISHOP -> BishopMovesCalculator.calculateMoves(board, position);
            case KNIGHT -> KnightMovesCalculator.calculateMoves(board, position);
            case ROOK -> RookMovesCalculator.calculateMoves(board, position);
            case PAWN -> PawnMovesCalculator.calculateMoves(board, position);
        };
    }
}

// PieceMovesCalculator subclass for King move calculation
class KingMovesCalculator extends PieceMovesCalculator {
    static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        // initialize the collection of moves to return
        Collection<ChessMove> validMoves = new ArrayList<>();

        // tracks the diagonal and orthogonal directions
        int[][] rowDirection = {{1, -1, -1, 1}, {1, -1, 0, 0}};
        int[][] colDirection = {{1, -1, 1, -1}, {0, 0, 1, -1}};

        for (int i = 0; i < 2; i++) {
            // loops over each direction
            for (int j = 0; j < 4; j++) {
                // retrieves the row and column indices
                int row = position.getRow();
                int col = position.getColumn();

                row += rowDirection[i][j];
                col += colDirection[i][j];

                // catch if it starts on an edge
                if (DetermineInBounds.inBounds(row, col)) {
                    ChessPosition endPosition = new ChessPosition(row, col);

                    // if the square is occupied by a piece
                    if (board.getPiece(endPosition) != null) {
                        // if it can attack that piece
                        if (CalculatePotentialAttack.canAttack(board, position, endPosition)) {
                            // add it to valid moves
                            validMoves.add(new ChessMove(position, endPosition, null));
                        }
                    }
                    else validMoves.add(new ChessMove(position, endPosition, null));
                }
            }
        }
        return validMoves;
    }
}

// PieceMovesCalculator subclass for Queen move calculation
class QueenMovesCalculator extends PieceMovesCalculator {
    static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {

        // tracks the diagonal directions
        int[] rowDiagonal = {1, -1, -1, 1};
        int[] colDiagonal = {1, 1, -1, -1};
        // calculates diagonal moves
        Collection<ChessMove> totalMoveSet = DirectionalMovesCalculator.calculateMoves(rowDiagonal, colDiagonal, board, position);

        // tracks the orthogonal directions
        int[] rowOrthogonal = {0, 0, -1, 1};
        int[] colOrthogonal = {1, -1, 0, 0};
        // calculates orthogonal moves
        Collection<ChessMove> orthogonalMoves = DirectionalMovesCalculator.calculateMoves(rowOrthogonal, colOrthogonal, board, position);

        // adds orthogonal moves to diagonal moves
        totalMoveSet.addAll(orthogonalMoves);
        return totalMoveSet;
    }
}

// PieceMovesCalculator subclass for Bishop move calculation
class BishopMovesCalculator extends PieceMovesCalculator {
    static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {

        // tracks the direction
        int[] rowDirection = {1, -1, -1, 1};
        int[] colDirection = {1, 1, -1, -1};

        return DirectionalMovesCalculator.calculateMoves(rowDirection, colDirection, board, position);
    }
}

// PieceMovesCalculator subclass for Knight move calculation
class KnightMovesCalculator extends PieceMovesCalculator {
    static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        // initialize the collection of moves to return
        Collection<ChessMove> validMoves = new ArrayList<>();

        // tracks the diagonal and orthogonal directions
        int[] rowDirection = {2, 2, -2, -2, 1, 1, -1, -1};
        int[] colDirection = {-1, 1, -1, 1, -2, 2, -2, 2};

        // loops over each direction
        for (int j = 0; j < 8; j++) {
            // retrieves the row and column indices
            int row = position.getRow();
            int col = position.getColumn();

            row += rowDirection[j];
            col += colDirection[j];

            // catch if it starts on an edge
            if (DetermineInBounds.inBounds(row, col)) {
                ChessPosition endPosition = new ChessPosition(row, col);

                // if the square is occupied by a piece
                if (board.getPiece(endPosition) != null) {
                    // if it can attack that piece
                    if (CalculatePotentialAttack.canAttack(board, position, endPosition)) {
                        // add it to valid moves
                        validMoves.add(new ChessMove(position, endPosition, null));
                    }
                }
                else validMoves.add(new ChessMove(position, endPosition, null));
            }
        }
        return validMoves;
    }
}

// PieceMovesCalculator subclass for Rook move calculation
class RookMovesCalculator extends PieceMovesCalculator {
    static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {

        // tracks the direction
        int[] rowDirection = {0, 0, -1, 1};
        int[] colDirection = {1, -1, 0, 0};

        return DirectionalMovesCalculator.calculateMoves(rowDirection, colDirection, board, position);
    }
}

// PieceMovesCalculator subclass for Knight move calculation
class PawnMovesCalculator extends PieceMovesCalculator {
    static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        // initialize the collection of moves to return
        Collection<ChessMove> validMoves = new ArrayList<>();

        ChessPiece piece = board.getPiece(position);

        int row = position.getRow();
        int col = position.getColumn();
        int step = 1;
        // if black, change step to -1
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            step = -1;
        }
        // create end position
        ChessPosition normalEnd = new ChessPosition(row+step, col);

        // if space in front empty
        if (board.getPiece(normalEnd) == null) {
            // if final move
            if (row + step == 8 || row + step ==1) {
                validMoves.add(new ChessMove(position, normalEnd, ChessPiece.PieceType.QUEEN));
                validMoves.add(new ChessMove(position, normalEnd, ChessPiece.PieceType.BISHOP));
                validMoves.add(new ChessMove(position, normalEnd, ChessPiece.PieceType.KNIGHT));
                validMoves.add(new ChessMove(position, normalEnd, ChessPiece.PieceType.ROOK));
            }
            // if the square in front is in bounds
            else if (row + step < 8 && row + step > 1) {
                // if initial move
                if ((row == 2 && step > 0) || (row == 7 && step < 0)) {
                    // check space two steps in front
                    ChessPosition initialEnd = new ChessPosition(row + (2*step), col);
                    if (board.getPiece(initialEnd) == null) {
                        validMoves.add(new ChessMove(position, initialEnd, null));
                    }
                }
                // add space in front by default
                validMoves.add(new ChessMove(position, normalEnd, null));
            }

        }
        // check diagonals for attacking
        ChessPosition leftAttack = new ChessPosition(row + step, col - 1);
        ChessPosition rightAttack = new ChessPosition(row+step,col + 1);
        Collection<ChessMove> attackMoves;

        if (DetermineInBounds.inBounds(leftAttack.getRow(), leftAttack.getColumn())) {
            attackMoves = CalculatePawnAttack.calculateAttack(board, position, leftAttack, piece.getTeamColor());
            validMoves.addAll(attackMoves);
        }
        if (DetermineInBounds.inBounds(rightAttack.getRow(), rightAttack.getColumn())) {
            attackMoves = CalculatePawnAttack.calculateAttack(board, position, rightAttack, piece.getTeamColor());
            validMoves.addAll(attackMoves);
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

// class to calculate moves in straight lines
class DirectionalMovesCalculator {
    static Collection<ChessMove> calculateMoves (int[] rowDirection, int[] colDirection, ChessBoard board, ChessPosition position) {

        // initialize the collection of moves to return
        Collection<ChessMove> validMoves = new ArrayList<>();

        // loops over each direction
        for (int i = 0; i < 4; i++) {
            // retrieves the row and column indices
            int row = position.getRow();
            int col = position.getColumn();

            // goes in a line, storing each valid move
            while (row <= 8 && row >= 1 && col <= 8 && col >= 1) {

                row += rowDirection[i];
                col += colDirection[i];

                // catch if it starts on an edge
                if (row>8 || row<1 || col>8 || col<1) {
                    break;
                }

                ChessPosition endPosition = new ChessPosition(row, col);

                // if the square is occupied by a piece
                if (board.getPiece(endPosition) != null) {
                    // if it can attack that piece
                    if (CalculatePotentialAttack.canAttack(board, position, endPosition)) {
                        // add it to valid moves
                        validMoves.add(new ChessMove(position, endPosition, null));
                    }
                    // then ignore the rest of the line
                    break;
                }
                validMoves.add(new ChessMove(position, endPosition, null));
            }
        }
        return validMoves;
    }
}

// class to determine if move is in bounds
class DetermineInBounds {
    static boolean inBounds(int row, int col) {
        // return true if in bounds
        return row <= 8 && row >= 1 && col <= 8 && col >= 1;
    }
}

// class to check diagonal attack spaces for pawns
class CalculatePawnAttack {
    static Collection<ChessMove> calculateAttack(ChessBoard board, ChessPosition position1, ChessPosition position2, ChessGame.TeamColor color) {

        Collection<ChessMove> validMoves = new ArrayList<>();


        int row = position1.getRow();
        int step = 1;
        // if black, change step to -1
        if (color == ChessGame.TeamColor.BLACK) {
            step = -1;
        }

        // check if in bounds
        if (DetermineInBounds.inBounds(position2.getRow(), position2.getColumn())) {
            // if there's a piece in the attacking location
            if (board.getPiece(position2) != null) {
                if(CalculatePotentialAttack.canAttack(board, position1, position2)) {
                    if (row+step == 8 || row+step == 1) {
                        validMoves.add(new ChessMove(position1, position2, ChessPiece.PieceType.QUEEN));
                        validMoves.add(new ChessMove(position1, position2, ChessPiece.PieceType.BISHOP));
                        validMoves.add(new ChessMove(position1, position2, ChessPiece.PieceType.KNIGHT));
                        validMoves.add(new ChessMove(position1, position2, ChessPiece.PieceType.ROOK));
                    } else validMoves.add(new ChessMove(position1, position2, null));
                }
            }
        }
        return validMoves;
    }
}