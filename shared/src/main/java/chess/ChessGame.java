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
            // if it is, try moving every piece all their moves until the king is out of check
            for (int row=0; row<9; row++) {
                for (int col=0; col<9; col++) {
                    ChessPosition pos = new ChessPosition(row, col);
                    ChessPiece piece = chessBoard.getPiece(pos);
                    if (piece != null && piece.getTeamColor() == teamColor) {
                        for (ChessMove move : validMoves(pos)) {
                            // if it can move so that the king isn't in check, return true
                            if (!testIntoCheck(move, teamColor)) {
                                return false;
                            }
                        }
                    }
                }
            }
            // otherwise there is no way to escape checkmate
            return true;
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
        if (getTeamTurn() == teamColor) {
            ChessPosition kingPos = teamColor == TeamColor.WHITE ? chessBoard.whiteKingPos : chessBoard.blackKingPos;
            // first check if not in check and the king has no moves
            if (!isInCheck(teamColor) && validMoves(kingPos).isEmpty()) {
                if (validMoves(kingPos).isEmpty()) {
                    // then check if any other pieces have moves
                    for (int row = 1; row < 9; row++) {
                        for (int col = 1; col < 9; col++) {
                            ChessPosition pos = new ChessPosition(row, col);
                            ChessPiece piece = chessBoard.getPiece(pos);
                            // if piece exists, is same color, and isn't the king
                            if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() != ChessPiece.PieceType.KING) {
                                // if it has possible moves you're not in stalemate
                                if (!validMoves(pos).isEmpty()) {
                                    return false;
                                }
                            }
                        }
                    }
                    return true;
                }
            }
        }
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


