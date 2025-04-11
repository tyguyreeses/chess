package ui.clients;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import exception.ResponseException;
import model.*;
import ui.BoardPrinter;
import ui.websocket.WebsocketFacade;

import java.util.Arrays;

public class GameClient {
    private GameData gameData;
    private final AuthData authData;
    private final WebsocketFacade websocket;
    private final ChessGame.TeamColor color;
    private final Gson gson = new Gson();

    public GameClient(AuthData authData, GameData gameData, ChessGame.TeamColor color, WebsocketFacade websocket) {
        this.gameData = gameData;
        this.color = color;
        this.authData = authData;
        this.websocket = websocket;
    }

    public Object eval(String input) {
        try {
            String[] tokens = input.split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd.toLowerCase()) {
                case "hl", "highlight" -> highlight(params);
                case "m", "move" -> makeMove(params);
                case "r", "redraw" -> drawBoard(color);
                case "res", "resign" -> resign();
                case "leave" -> leaveGame();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String highlight(String... params) throws ResponseException {
        try {
            if (params.length == 1) {
                ChessPosition selectedPos = processChessPosition(params[0]);
                new BoardPrinter(gameData, color).printBoardWithHighlights(selectedPos);
                return "Highlighted " + params[0] + "'s possible moves";
            }
            throw new ResponseException(500, "Expected format: <POSITION> (e.g. f5)");
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private String makeMove(String... params) throws ResponseException {
        try {
            if (params.length == 2 || params.length == 3) {
                ChessMove move = processChessMove(params); // process chess move
                websocket.makeMove(authData.authToken(), gameData.gameID(), move, websocket.session);
                return "";
            }
            throw new ResponseException(500, "Expected format: <SOURCE> <DESTINATION> <OPTIONAL PROMOTION> (e.g. f5 e4 q)");
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private ChessMove processChessMove(String... string) throws ResponseException {
        try {
            ChessPosition source = processChessPosition(string[0]);
            ChessPosition destination = processChessPosition(string[1]);
            ChessPiece.PieceType promotion = null;
            if (string.length == 3) {
                promotion = processChessPromotion(string[2]);
            }
            return new ChessMove(source, destination, promotion);
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }

    }

    private ChessPosition processChessPosition(String string) throws ResponseException {
        try {
            if (string.length() != 2) {
                throw new ResponseException(500, "Invalid chess position");
            }
            // get chars
            char columnChar = string.charAt(0);
            char rowChar = string.charAt(1);
            // convert to int
            int col = columnChar - 'a' + 1;
            int row = rowChar - '0';
            // check for errors
            if (row > 8 || row < 1 || col > 8 || col < 1) {
                throw new ResponseException(500, "Invalid chess position");
            }
            // create ChessPosition
            return new ChessPosition(row, col);
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private ChessPiece.PieceType processChessPromotion(String string) throws ResponseException {
        return switch (string.toLowerCase()) {
            case "q" -> ChessPiece.PieceType.QUEEN;
            case "b" -> ChessPiece.PieceType.BISHOP;
            case "r" -> ChessPiece.PieceType.ROOK;
            case "n" -> ChessPiece.PieceType.KNIGHT;
            default -> throw new ResponseException(500, "Invalid promotion");
        };
    }

    private String resign() throws ResponseException {
        websocket.resignGame(authData.authToken(), gameData.gameID(), websocket.session);
        return "";
    }

    private String leaveGame() throws ResponseException {
        websocket.leaveGame(authData.authToken(), gameData.gameID(), websocket.session);
        return "Successfully left game";
    }

    public String drawBoard(ChessGame.TeamColor color) throws ResponseException {
        new BoardPrinter(gameData, color).printBoard();
        return "";
    }

    public String help() {
        return """
            Options:
            Highlight legal moves: "hl", "highlight" <POSITION> (e.g. f5)
            Make a move: "m", "move" <SOURCE> <DESTINATION> <OPTIONAL PROMOTION> (e.g. f5 e4 q)
            Redraw board: "r", "redraw"
            Resign from game: "res", "resign"
            Leave game: "leave"
            """;
    }
}
