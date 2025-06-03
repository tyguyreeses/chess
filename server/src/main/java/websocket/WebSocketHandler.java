package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessGame.TeamColor;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.ResponseException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import services.Service;
import websocket.commands.*;
import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationServerMessage;
import websocket.messages.ServerMessage;

import javax.websocket.MessageHandler;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@WebSocket
public class WebSocketHandler {

    WebSocketSessions wss = new WebSocketSessions();
    Gson gson = new Gson();
    Service service;

    public WebSocketHandler(Service service) {
        this.service = service;
    }

    @OnWebSocketError
    public void onError(Throwable throwable) {
        System.out.println("Error in websocket: " + throwable.getMessage());
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws ResponseException {
        // determine message type
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
        UserGameCommand.CommandType commandType = UserGameCommand.CommandType.valueOf(jsonObject.get("commandType").getAsString());
        try {
            // call the appropriate message handler
            switch (commandType) {
                case CONNECT -> {
                    ConnectGameCommand cgc = gson.fromJson(message, ConnectGameCommand.class);
                    connect(cgc, session);
                }
                case MAKE_MOVE -> {
                    MakeMoveGameCommand mmgc = gson.fromJson(message, MakeMoveGameCommand.class);
                    makeMove(mmgc, session);
                }
                case LEAVE -> {
                    LeaveGameCommand lgc = gson.fromJson(message, LeaveGameCommand.class);
                    leaveGame(lgc, session);
                }
                case RESIGN -> {
                    ResignGameCommand rgc = gson.fromJson(message, ResignGameCommand.class);
                    resignGame(rgc, session);
                }
            }
        } catch (Exception e) {
            ErrorServerMessage esm = new ErrorServerMessage("Error: " + e.getMessage());
            sendMessage(esm, session);
        }
    }

    private void connect(ConnectGameCommand command, Session session) throws ResponseException {
        // tell client to load game
//        LoadGameMessage lgm = new LoadGameMessage(retrieveGameData(command));
//        sendMessage(lgm, session);
        // message everyone else
        String user = getUsername(command);
        TeamColor color = getPlayerColor(command, retrieveGameData(command));
        String message;
        if (color == null) {
            message = user + " is observing the game";
        } else {
            message = user + " joined the game as " + color.toString().toUpperCase();
        }
        // add connection to connection manager
        wss.addSessionToGame(command.getGameID(), session);
        // broadcast notification
        NotificationServerMessage broadcast = new NotificationServerMessage(message);
        broadcastMessage(command.getGameID(), broadcast, session);
    }

    private void makeMove(MakeMoveGameCommand command, Session session) throws ResponseException {
        try {
            // retrieve move and game
            ChessMove move = command.getMove();
            GameData gameData = retrieveGameData(command);
            ChessGame game = gameData.game();
            String userName = getUsername(command);

            TeamColor color;
            TeamColor opponentColor;
            if (Objects.equals(getUsername(command), gameData.whiteUsername())) {
                color = TeamColor.WHITE;
                opponentColor = TeamColor.BLACK;
            } else {
                color = TeamColor.BLACK;
                opponentColor = TeamColor.WHITE;
            }

            String opponentUsername = color == TeamColor.WHITE ? gameData.blackUsername() : gameData.whiteUsername();

            if (game.gameOver) {
                throw new ResponseException(500, "Invalid move, game is over");
            }

            // retrieve player data and confirm the right player is making the move
            checkCorrectPlayer(command, gameData, move);
            ChessPiece movedPiece = gameData.game().getBoard().getPiece(move.getStartPosition());

            // if a valid move
            if (game.validMoves(move.getStartPosition()).contains(move)) {
                // make the move
                game.makeMove(move);

                // check if checkmate or stalemate
                if (game.isInCheckmate(opponentColor) || game.isInStalemate(opponentColor)) {
                    game.gameOver = true;
                }

                // update database
                updateGame(gameData.withUpdatedGame(game));
                // tell everyone to update their game
                LoadGameMessage lgm = new LoadGameMessage(gameData);
                broadcastMessage(command.getGameID(), lgm, null);
                // tell everyone but root client what move was made
                NotificationServerMessage nsm = new NotificationServerMessage(getUsername(command) + " made move: " + move);
                broadcastMessage(command.getGameID(), nsm, session);


                // check if checkmate or stalemate
                if (game.isInCheckmate(opponentColor)) {
                    NotificationServerMessage checkMate = new NotificationServerMessage(opponentUsername + " is in checkmate");
                    broadcastMessage(command.getGameID(), checkMate, null);
                } else if (game.isInCheck(opponentColor)) {
                        NotificationServerMessage check = new NotificationServerMessage(opponentUsername + " is in check");
                        broadcastMessage(command.getGameID(), check, null);
                    }
                if (game.isInStalemate(opponentColor)) {
                    NotificationServerMessage staleMate = new NotificationServerMessage(opponentUsername + " is in stalemate");
                    broadcastMessage(command.getGameID(), staleMate, null);
                }

            // if not a valid move
            } else {
                throw new ResponseException(500, "Invalid move");
            }
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void leaveGame(LeaveGameCommand command, Session session) throws ResponseException {
        // if player, set corresponding user to null
        GameData gameData = retrieveGameData(command);
        TeamColor color = getPlayerColor(command, gameData);
        if (color != null) {
            updateGame(switch (color) {
                case WHITE -> gameData.withWhiteUser(null);
                case BLACK -> gameData.withBlackUser(null);
            });
        }
        // broadcast to all other participants
        String user = getUsername(command);
        NotificationServerMessage message = new NotificationServerMessage(user + " has left the game");
        broadcastMessage(command.getGameID(), message, session);
        // remove connection
        wss.removeSessionFromGame(command.getGameID(), session);
    }

    private void resignGame(ResignGameCommand command, Session session) throws ResponseException {
        GameData gameData = retrieveGameData(command);
        // check if player
        if (getPlayerColor(command, gameData) == null) {
            throw new ResponseException(500, "Only players can resign");
        }
        // check if game is already over
        if (gameData.game().gameOver) {
            throw new ResponseException(500, "Cannot resign, game is already over");
        }
        // end the game
        gameData.game().gameOver = true;
        updateGame(gameData);
        // broadcast who forfeited
        String user = getUsername(command);
        NotificationServerMessage message = new NotificationServerMessage(user + " resigned\nGame over");
        broadcastMessage(command.getGameID(), message,null);
    }

    public void sendMessage(ServerMessage message, Session session) throws ResponseException {
        try {
            session.getRemote().sendString(new Gson().toJson(message));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void broadcastMessage(Integer gameID, ServerMessage message, Session excludedSession) throws ResponseException {
        Set<Session> sessions = wss.getSessions(gameID);
        for (Session session : sessions) {
            if (!session.equals(excludedSession)) {
                try {
                    session.getRemote().sendString(new Gson().toJson(message));
                } catch (IOException e) {
                    throw new ResponseException(500, e.getMessage());
                }
            }
        }
    }

    private String getUsername(UserGameCommand command) throws ResponseException {
        return service.dataAccess.getAuth(command.getAuthToken()).username();
    }

    private void checkCorrectPlayer(UserGameCommand command, GameData gameData, ChessMove move) throws ResponseException {
        String username = getUsername(command);
        ChessPiece movedPiece = gameData.game().getBoard().getPiece(move.getStartPosition());

        // validate user is a player
        if (getPlayerColor(command, gameData) == null) {
            throw new ResponseException(500, "Only players can make moves");
        }

        TeamColor color = Objects.equals(username, gameData.blackUsername()) ? TeamColor.BLACK : TeamColor.WHITE;

        // validate moved piece matches user team
        if (color != movedPiece.getTeamColor()) {
            throw new ResponseException(500, "Only your own pieces can be moved");
        }
    }

    private TeamColor getPlayerColor(UserGameCommand command, GameData gameData) throws ResponseException {
        String username = getUsername(command);
        if (Objects.equals(username, gameData.blackUsername())) {
            return TeamColor.BLACK;
        } else if (Objects.equals(username, gameData.whiteUsername())) {
            return TeamColor.WHITE;
        } else {
            return null;
        }
    }

    private GameData retrieveGameData(UserGameCommand command) throws ResponseException {
        try {
            Collection<GameData> gameCollection = service.listGames(command.getAuthToken());
            for (GameData gameData : gameCollection) {
                if (gameData.gameID() == command.getGameID()) {
                    return gameData;
                }
            }
            throw new ResponseException(500, "Unable to retrieve game data");
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void updateGame(GameData gameData) throws ResponseException {
        try {
            service.dataAccess.updateGame(gameData);
        } catch (Exception e) {
            throw new ResponseException(500, "Unable to update gameData");
        }
    }
}
