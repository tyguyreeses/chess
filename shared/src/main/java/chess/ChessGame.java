package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor turn = TeamColor.WHITE;
    private final ChessBoard chessBoard = new ChessBoard();

    public ChessGame() {

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
        return chessBoard.getPiece(startPosition).pieceMoves(chessBoard, startPosition);
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
        // if it's the right team's turn
        if (piece.getTeamColor() == getTeamTurn()) {

            // If the piece is a king, do stuff with check

            // Otherwise just return the regular list of valid moves
            Collection<ChessMove> moves = validMoves(move.getStartPosition());
            // check if it's a valid move
            if (moves.contains(move)) {
                // add the piece to the end position
                chessBoard.addPiece(move.getEndPosition(), chessBoard.getPiece(move.getStartPosition()));
                // remove the piece from starting position
                chessBoard.addPiece(move.getStartPosition(), null);
            }
            // otherwise throw an error
            else {
                throw new InvalidMoveException("Invalid move: move= " + move + ", validMoves: " + moves);
            }
            // update team turn
            setTeamTurn(getTeamTurn() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE );
            // check for checkmate and stalemate after the move
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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
                ChessPosition pos = new ChessPosition(row, col);
                // if there is a piece
                if (board.getPiece(pos) != null) {
                    ChessPiece piece = board.getPiece(pos);
                    chessBoard.addPiece(pos, piece);
                } else {
                    chessBoard.addPiece(pos, null);
                }
            }
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
}
