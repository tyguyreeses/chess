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
        ChessPiece piece = (ChessPiece) o;
        return pieceColor == piece.pieceColor && type == piece.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return (pieceColor + " " + type);
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
        return CalcPieceMoves.calcMoves(board, position);
    }
}

class CalcPieceMoves {
    static Collection<ChessMove> calcMoves(ChessBoard board, ChessPosition position) {
        return (switch (board.getPiece(position).getPieceType()) {
            case BISHOP -> BishopMovesCalc.calcMoves(board, position);
            case ROOK -> RookMovesCalc.calcMoves(board, position);
            case KING -> KingMovesCalc.calcMoves(board, position);
            case QUEEN -> QueenMovesCalc.calcMoves(board, position);
            case KNIGHT -> KnightMovesCalc.calcMoves(board, position);
            case PAWN -> PawnMovesCalc.calcMoves(board, position);
        });
    }
}

class PawnMovesCalc {
    static Collection<ChessMove> calcMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        int row = position.getRow();
        int col = position.getColumn();
        // determines which direction the pawn is moving
        int step = 1;
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();
        // if black, change the direction
        if (color == ChessGame.TeamColor.BLACK) {
            step = -1;
        }

        ChessPosition endPos = new ChessPosition(row+step, col);
        // if the space in front is on the board and isn't blocked
        if (DetermineInBounds.inBounds(row+step, col) && board.getPiece(endPos) == null) {

            // check if it's the initial move
            if ((row == 2 && color == ChessGame.TeamColor.WHITE) || (row == 7 && color == ChessGame.TeamColor.BLACK)) {
                // add en passant move if two spaces in front is empty
                ChessPosition enPassant = new ChessPosition(row + (2 * step), col);
                if (board.getPiece(enPassant) == null) {
                    validMoves.add(new ChessMove(position, enPassant, null));
                }
            }
            // handle if it needs to be promoted, otherwise just add the space in front;
            validMoves.addAll(HandlePawnPromotion.promotionMoves(row,color,position,endPos));
        }

        // calculate pawn attacks

        // calculate left attack
        if(DetermineInBounds.inBounds(row+step, col-1)) {
            ChessPosition leftAttack = new ChessPosition(row+step, col-1);
            // if the position is occupied by an enemy piece
            if(board.getPiece(leftAttack) != null && DetermineCanAttack.canAttack(board, position, leftAttack)) {
                // check if it can be promoted
                validMoves.addAll(HandlePawnPromotion.promotionMoves(row, color, position, leftAttack));
            }
        }
        // calculate the right attack
        if(DetermineInBounds.inBounds(row+step, col+1)) {
            ChessPosition rightAttack = new ChessPosition(row+step, col+1);
            // if the position is occupied by an enemy piece, add the move
            if(board.getPiece(rightAttack) != null && DetermineCanAttack.canAttack(board, position, rightAttack)) {
                // check if it can be promoted
                validMoves.addAll(HandlePawnPromotion.promotionMoves(row, color, position, rightAttack));
            }
        }
        return validMoves;
    }
}

class KingMovesCalc {
    static Collection<ChessMove> calcMoves(ChessBoard board, ChessPosition position) {

        int[] rowDir = {1,1,1,0,0,-1,-1,-1};
        int[] colDir = {-1,0,1,-1,1,-1,0,1};

        return KKnMovesCalc.calcMoves(rowDir, colDir, board, position);
    }
}

class KnightMovesCalc {
    static Collection<ChessMove> calcMoves(ChessBoard board, ChessPosition position) {
        int[] rowDir = {1,1,-1,-1,2,2,-2,-2};
        int[] colDir = {2,-2,2,-2,1,-1,1,-1};

        return KKnMovesCalc.calcMoves(rowDir, colDir, board, position);
    }
}

class QueenMovesCalc {
    static Collection<ChessMove> calcMoves(ChessBoard board, ChessPosition position) {
        int[] rowDir = {1,-1,0,0};
        int[] colDir = {0,0,-1,1};

        Collection<ChessMove> validMoves = DirectionMovesCalc.calcMoves(rowDir, colDir, board, position);

        int[] diagRowDir = {1,1,-1,-1};
        int[] diagColDir = {-1,1,-1,1};

        validMoves.addAll(DirectionMovesCalc.calcMoves(diagRowDir, diagColDir, board, position));

        return validMoves;
    }
}

class RookMovesCalc {
    static Collection<ChessMove> calcMoves(ChessBoard board, ChessPosition position) {
        int[] rowDir = {1,-1,0,0};
        int[] colDir = {0,0,-1,1};

        return DirectionMovesCalc.calcMoves(rowDir, colDir, board, position);
    }
}

class BishopMovesCalc {
    static Collection<ChessMove> calcMoves(ChessBoard board, ChessPosition position) {
        int[] rowDir = {1,1,-1,-1};
        int[] colDir = {-1,1,-1,1};

        return DirectionMovesCalc.calcMoves(rowDir, colDir, board, position);
    }
}

class DirectionMovesCalc {
    static Collection<ChessMove> calcMoves(int[] rowDir, int[] colDir, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (int i=0;i<4;i++) {

            int row = position.getRow();
            int col = position.getColumn();
            // while it's on the board
            while (DetermineInBounds.inBounds(row,col)) {
                // increment in given direction
                row += rowDir[i];
                col += colDir[i];

                // catch if it is now off of the board
                if(!DetermineInBounds.inBounds(row,col)) {
                    break;
                }

                ChessPosition endPos = new ChessPosition(row, col);

                // if the space isn't empty
                if(board.getPiece(endPos) != null) {
                    // if it can attack it
                    if(DetermineCanAttack.canAttack(board, position, endPos)) {
                        validMoves.add(new ChessMove(position, endPos, null));
                    }
                    // ignore rest of direction
                    break;
                }
                // otherwise, add the move
                validMoves.add(new ChessMove(position, endPos, null));
            }
        }
        return validMoves;
    }
}

class KKnMovesCalc {
    static Collection<ChessMove> calcMoves(int[] rowDir, int[] colDir, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (int i=0; i<8; i++) {
            int row = position.getRow();
            int col = position.getColumn();

            row += rowDir[i];
            col += colDir[i];


            // catch if it is now off of the board
            if(!DetermineInBounds.inBounds(row,col)) {
                continue;
            }

            ChessPosition endPos = new ChessPosition(row, col);

            // if the space isn't empty
            if(board.getPiece(endPos) != null) {
                // if it can attack it
                if(DetermineCanAttack.canAttack(board, position, endPos)) {
                    validMoves.add(new ChessMove(position, endPos, null));
                }
            }
            // otherwise, add the move
            else {
                validMoves.add(new ChessMove(position, endPos, null));
            }
        }
        return validMoves;
    }
}

class DetermineCanAttack {
    static boolean canAttack(ChessBoard board, ChessPosition position1, ChessPosition position2) {
        return (board.getPiece(position1).getTeamColor() != board.getPiece(position2).getTeamColor());
    }
}

class DetermineInBounds {
    static boolean inBounds(int row, int col) {
        return(row>=1 && row<=8 && col>=1 && col<=8);
    }
}

class HandlePawnPromotion {
    static Collection<ChessMove> promotionMoves(int row, ChessGame.TeamColor color, ChessPosition position1, ChessPosition position2) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        // if it needs to be promoted
        if ((row == 7 && color == ChessGame.TeamColor.WHITE) || (row == 2 && color == ChessGame.TeamColor.BLACK)) {
            validMoves.add(new ChessMove(position1, position2, ChessPiece.PieceType.QUEEN));
            validMoves.add(new ChessMove(position1, position2, ChessPiece.PieceType.BISHOP));
            validMoves.add(new ChessMove(position1, position2, ChessPiece.PieceType.ROOK));
            validMoves.add(new ChessMove(position1, position2, ChessPiece.PieceType.KNIGHT));
        }
        // otherwise just add the standard move
        else {
            validMoves.add(new ChessMove(position1, position2, null));
        }

        return validMoves;
    }
}