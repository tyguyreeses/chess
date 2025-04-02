package websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.*;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Set;

@WebSocket
public class WebSocketHandler {

    WebSocketSessions WSS = new WebSocketSessions();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("New WebSocket connection established");
    }
    @OnWebSocketClose
    public void onClose(Session session) {
        System.out.println("New WebSocket connection closed");
    }
    @OnWebSocketError
    public void onError(Throwable throwable) {
        System.out.println("Websocket encountered error: " + throwable.getMessage());
    }
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws ResponseException {
        // determine message type
        // call the appropriate message handler
        UserGameCommand ugc = new Gson().fromJson(message, UserGameCommand.class);
        switch (ugc.getCommandType()) {
            case CONNECT -> connect((ConnectGameCommand) ugc, session);
            case MAKE_MOVE -> makeMove((MakeMoveGameCommand) ugc, session);
            case LEAVE -> leaveGame((LeaveGameCommand) ugc, session);
            case RESIGN -> resignGame((ResignGameCommand) ugc, session);
        }
    }
    private void connect(ConnectGameCommand command, Session session) throws ResponseException {
        // add connection to connection manager
        WSS.add(command.getGameID(), session);
        // message the sender
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        sendMessage(message, session);
        // message everyone else
        ServerMessage broadcast = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
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
