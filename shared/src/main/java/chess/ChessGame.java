package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor turn = TeamColor.WHITE;
    private final ChessBoard chessBoard = new ChessBoard();
    public boolean gameOver = false;

    public ChessGame() {
        chessBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(chessBoard, chessGame.chessBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, chessBoard);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = chessBoard.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        Collection<ChessMove> movesToTest = piece.pieceMoves(chessBoard, startPosition);
        // check each move if it moves
        movesToTest.removeIf(move -> testIntoCheck(move, piece.getTeamColor()));
        return movesToTest;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = chessBoard.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("No piece to be moved: move= " + move);
        }
        if (gameOver) {
            throw new InvalidMoveException("Game is over");
        }
        // if it's the right team's turn
        if (piece.getTeamColor() == getTeamTurn()) {

            // Otherwise just return the regular list of valid moves
            Collection<ChessMove> moves = validMoves(move.getStartPosition());
            // check if it's a valid move
            if (moves.contains(move)) {
                // make the move
                chessBoard.movePiece(move);
            }
            // otherwise throw an error
            else {
                throw new InvalidMoveException("Invalid move: move= " + move + ", validMoves: " + moves);
            }
            // update team turn
            setTeamTurn(getTeamTurn() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE );
        }
        // otherwise throw an error
        else {
            throw new InvalidMoveException("Wrong turn: turn= " + getTeamTurn() + ", move= " + move);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return chessBoard.isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // first check if king in check
        if (isInCheck(teamColor)) {
            // check if any piece can move so the king isn't in check
            for (int row=0; row<9; row++) {
                for (int col = 0; col < 9; col++) {
                    // if that piece can move out of check, not in checkmate
                    if (canMoveOutOfCheck(teamColor, row, col)) {
                        return false;
                    }
                }
            }
            // if no piece can move so that the king isn't in check, you're in checkmate
            gameOver = true;
            return true;
        }
        return false;
    }

    // Determines if a piece can move so that the king is not in check
    private boolean canMoveOutOfCheck(TeamColor teamColor, int row, int col) {
        // checks if a piece can move so that the king isn't in check
        ChessPosition pos = new ChessPosition(row, col);
        ChessPiece piece = chessBoard.getPiece(pos);
        if (piece != null && piece.getTeamColor() == teamColor) {
            for (ChessMove move : validMoves(pos)) {
                // if it can move so that the king isn't in check, return true
                if (!testIntoCheck(move, teamColor)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPosition kingPos = teamColor == TeamColor.WHITE ? chessBoard.whiteKingPos : chessBoard.blackKingPos;
        // check if current turn, if not in checkmate, and if the king can't move
        if (getTeamTurn() == teamColor && !isInCheck(teamColor) && validMoves(kingPos).isEmpty()) {
            // if any pieces of that team have moves, you're not in stalemate
            if (!doPiecesHaveMoves(teamColor)) {
                gameOver = true;
                return true;
            }
        }
        return false;
    }

    // Determines if any piece has moves
    public boolean doPiecesHaveMoves(TeamColor teamColor) {
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = chessBoard.getPiece(pos);
                // if piece exists, is same color, and isn't the king
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() != ChessPiece.PieceType.KING) {
                    // if it has possible moves return true
                    if (!validMoves(pos).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        // if no pieces have moves return false
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        // iterate over the whole board
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                // create a ChessPosition object
                setBoardHelper(board, row, col);
            }
        }
    }

    private void setBoardHelper(ChessBoard board, int row, int col) {
        ChessPosition pos = new ChessPosition(row, col);
        // if there is a piece
        if (board.getPiece(pos) != null) {
            ChessPiece piece = board.getPiece(pos);
            chessBoard.addPiece(pos, piece);
            // update king location if it's a king
            if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                if (piece.getTeamColor() == TeamColor.WHITE) {
                    chessBoard.whiteKingPos = pos;
                } else {
                    chessBoard.blackKingPos = pos;
                }
            }
        } else {
            chessBoard.addPiece(pos, null);
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return chessBoard;
    }

    public boolean testIntoCheck(ChessMove move, ChessGame.TeamColor teamColor) {
        // copy the chessboard and move the piece
        ChessBoard cloneBoard = chessBoard.clone();

        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        ChessPiece piece = cloneBoard.getPiece(startPos);

        cloneBoard.movePiece(move);

        // update king position if king was moved
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                cloneBoard.whiteKingPos = endPos;
            } else {
                cloneBoard.blackKingPos = endPos;
            }
        }
        return cloneBoard.isInCheck(teamColor);
    }
}


