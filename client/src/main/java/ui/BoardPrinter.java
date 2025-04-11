package ui;

import chess.*;
import model.GameData;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static ui.EscapeSequences.*;

public class BoardPrinter {

    private final ChessGame game;
    private final ChessGame.TeamColor color;
    private static final int BOARD_SIZE = 8;

    public BoardPrinter(GameData gameData, ChessGame.TeamColor color) {
        this.game = gameData.game();
        this.color = color;
    }

    public void printBoard() {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);

        drawColumnHeaders(out);
        drawBoardRows(out, null);
        drawColumnHeaders(out);
    }

    public void printBoardWithHighlights(ChessPosition selectedPiecePos) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);

        ChessBoard board = game.getBoard();
        ChessPiece piece = board.getPiece(selectedPiecePos);

        Set<ChessPosition> highlightSet = new HashSet<>();
        if (piece != null && piece.getTeamColor() == color) {
            var validMoves = game.validMoves(selectedPiecePos);
            for (ChessMove move : validMoves) {
                highlightSet.add(move.getEndPosition());
            }
        }

        drawColumnHeaders(out);
        drawBoardRows(out, highlightSet);
        drawColumnHeaders(out);
    }

    private void drawColumnHeaders(PrintStream out) {
        out.print("\n   " + RESET_TEXT_COLOR); // spacing for row numbers
        if (color == ChessGame.TeamColor.WHITE) {
            // columns from a to h
            for (char col = 'a'; col < 'a' + BOARD_SIZE; col++) {
                out.print(" " + col + " ");
            }
        } else {
            // for black perspective, reverse the columns (h to a)
            for (char col = (char) ('a' + BOARD_SIZE - 1); col >= 'a'; col--) {
                out.print(" " + col + " ");
            }
        }
        out.println();
    }

    private void drawBoardRows(PrintStream out, Set<ChessPosition> highlightSet) {
        ChessBoard board = game.getBoard();

        if (color == ChessGame.TeamColor.WHITE) {
            // For white: rows 8 to 1, columns 1 to 8.
            for (int row = BOARD_SIZE; row >= 1; row--) {
                // Print row header (row number)
                out.print(" " + row + " ");
                for (int col = 1; col <= BOARD_SIZE; col++) {
                    drawSquare(out, board, row, col, highlightSet);
                }
                out.print(" " + row + " ");
                if (row != 1) { out.println(); }
            }
        } else {
            // For black: rows 1 to 8, columns 8 to 1.
            for (int row = 1; row <= BOARD_SIZE; row++) {
                out.print(" " + row + " ");
                for (int col = BOARD_SIZE; col >= 1; col--) {
                    drawSquare(out, board, row, col, highlightSet);
                }
                out.print(" " + row + " ");
                if (row < BOARD_SIZE) { out.println(); }
            }
        }
    }

    private void drawSquare(PrintStream out, ChessBoard board, int row, int col, Set<ChessPosition> highlightSet) {
        ChessPosition pos = new ChessPosition(row, col);
        ChessPiece piece = board.getPiece(pos);

        boolean darkSquare = (row + col) % 2 == 0;

        if (highlightSet != null && highlightSet.contains(pos)) {
            out.print(SET_BG_COLOR_GREEN);
            out.print(SET_TEXT_COLOR_BLACK);
        } else if (darkSquare) {
            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_WHITE);
        } else {
            out.print(SET_BG_COLOR_WHITE);
            out.print(SET_TEXT_COLOR_BLACK);
        }

        String display = (piece != null) ? piece.toString() : EMPTY;
        out.print(pad(display, 3));

        // Optionally, reset colors for any vertical separator.
        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
    }

    private String pad(String s, int width) {
        if (s.length() >= width) {
            return s.substring(0, width);
        } else {
            int padding = width - s.length();
            int left = padding / 2;
            int right = padding - left;
            return " ".repeat(left) + s + " ".repeat(right);
        }
    }
}