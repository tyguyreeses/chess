package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import services.Service;
import websocket.commands.*;
import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationServerMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@WebSocket
public class WebSocketHandler {

    WebSocketSessions WSS = new WebSocketSessions();
    Gson gson = new Gson();
    Service service;

    public WebSocketHandler(Service service) {
        this.service = service;
    }

    @OnWebSocketError
    public void onError(Throwable throwable) {
        System.out.println("Websocket encountered error: " + throwable.getMessage());
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
            ErrorServerMessage esm = new ErrorServerMessage(e.getMessage());
            sendMessage(esm, session);
        }
    }
    private void connect(ConnectGameCommand command, Session session) throws ResponseException {
        // add connection to connection manager
        WSS.addSessionToGame(command.getGameID(), session);
        GameData gameData = retrieveGameData(command, session);
        // tell player to load game
        LoadGameMessage message = new LoadGameMessage(gameData.game());
        sendMessage(message, session);

        // message everyone else
        NotificationServerMessage broadcast = new NotificationServerMessage("A player joined the game");
        broadcastMessage(command.getGameID(), broadcast, session);
    }

    private void makeMove(MakeMoveGameCommand command, Session session) throws ResponseException {
        try {
            // retrieve move and game
            ChessMove move = command.getMove();
            GameData gameData = retrieveGameData(command, session);
            ChessGame game = gameData.game();
            if (game.gameOver) {
                throw new ResponseException(500, "Invalid move, game is over");
            }

            // retrieve player data and confirm the right player is making the move
            checkCorrectPlayer(command, gameData, move);

            // if a valid move
            if (game.validMoves(move.getStartPosition()).contains(move)) {
                // make the move
                game.makeMove(move);
                // update database
                service.dataAccess.updateGame(gameData);
                // tell everyone to update their game
                LoadGameMessage lgm = new LoadGameMessage(game);
                broadcastMessage(command.getGameID(), lgm, null);
//                System.out.println("Chess board: \n" + game.getBoard());
                // tell everyone but root client what move was made
                NotificationServerMessage nsm = new NotificationServerMessage("Move was made: " + move);
                broadcastMessage(command.getGameID(), nsm, session);
            // if not a valid move
            } else {
                throw new ResponseException(500, "Invalid move");
            }
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void checkCorrectPlayer(UserGameCommand command, GameData gameData, ChessMove move) throws ResponseException {
        String username = service.dataAccess.getAuth(command.getAuthToken()).username();
        ChessPiece movedPiece = gameData.game().getBoard().getPiece(move.getStartPosition());
        ChessGame.TeamColor color;

        // validate user is a player
        if (Objects.equals(username, gameData.whiteUsername())) {
            color = ChessGame.TeamColor.WHITE;
        } else if (Objects.equals(username, gameData.blackUsername())) {
            color = ChessGame.TeamColor.BLACK;
        } else {
            throw new ResponseException(500, "Only players can make moves");
        }

        // validate moved piece matches user team
        if (color != movedPiece.getTeamColor()) {
            throw new ResponseException(500, "Only your own pieces can be moved");
        }
    }

    private void leaveGame(LeaveGameCommand command, Session session) {

    }
    private void resignGame(ResignGameCommand command, Session session) {

    }

    private GameData retrieveGameData(UserGameCommand command, Session session) throws ResponseException {
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

    public void sendMessage(ServerMessage message, Session session) throws ResponseException {
        try {
            session.getRemote().sendString(new Gson().toJson(message));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
    public void broadcastMessage(Integer gameID, ServerMessage message, Session excludedSession) throws ResponseException {
        Set<Session> sessions = WSS.getSessions(gameID);
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
}
