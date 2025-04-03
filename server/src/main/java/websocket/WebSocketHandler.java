package websocket;

import chess.ChessGame;
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

import java.io.IOException;
import java.util.Collection;
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
        };
    }
    private void connect(ConnectGameCommand command, Session session) throws ResponseException {
        Collection<GameData> gameCollection;
        try {
            // retrieve game
            gameCollection = service.listGames(command.getAuthToken());
        } catch (ResponseException e) {
            ErrorServerMessage errorMessage = new ErrorServerMessage(e.getMessage());
            sendMessage(errorMessage, session);
            return;
        }
        // add connection to connection manager
        WSS.add(command.getGameID(), session);
        System.out.printf("GameID: %d%n", command.getGameID());
        ChessGame game = null;
        for (GameData data : gameCollection) {
            if (data.gameID() == command.getGameID()) {
                game = data.game();
                LoadGameMessage message = new LoadGameMessage(game);
                sendMessage(message, session);
            }
        }
        if (game == null) {
            ErrorServerMessage errorMessage = new ErrorServerMessage("Unable to connect to game");
            sendMessage(errorMessage, session);
            return;
        }
        // message everyone else
        NotificationServerMessage broadcast = new NotificationServerMessage("A player joined the game");
        broadcastMessage(command.getGameID(), broadcast, session);
    }
    private void makeMove(MakeMoveGameCommand command, Session session) throws ResponseException {
        // validate move
        // make the move
        // tell everyone to update their game
//        ServerMessage everyone = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, );
//        broadcastMessage(command.getGameID(), everyone, null);
        // tell everyone but root client what move was made
        ServerMessage whatMove = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
    }
    private void leaveGame(LeaveGameCommand command, Session session) {

    }
    private void resignGame(ResignGameCommand command, Session session) {

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
