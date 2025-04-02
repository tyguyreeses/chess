package websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import javax.websocket.*;
import java.io.IOException;
import java.util.Set;

public class WebSocketHandler extends Endpoint {

    WebSocketSessions WSS = new WebSocketSessions();

    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("New WebSocket connection established: " + session.getId());
    }
    public void onClose(Session session) {
        System.out.println("New WebSocket connection closed: " + session.getId());
    }
    public void onError(Throwable throwable) {
        System.out.println("Websocket encountered error: " + throwable.getMessage());
    }
    public void onMessage(Session session, String message) {
        // determine message type
        // call the appropriate message handler
        UserGameCommand ugc = new Gson().fromJson(message, UserGameCommand.class);
        switch (ugc.getCommandType()) {
            case CONNECT -> connect(ugc);
            case MAKE_MOVE -> makeMove(ugc);
            case LEAVE -> leaveGame(ugc);
            case RESIGN -> resignGame(ugc);
        }
    }
    private void connect(UserGameCommand command) {

    }
    private void makeMove(UserGameCommand command) {

    }
    private void leaveGame(UserGameCommand command) {

    }
    private void resignGame(UserGameCommand command) {

    }

    public void sendMessage(ServerMessage message, Session session) {

    }
    public void broadcastMessage(Integer gameID, ServerMessage message, Session excludedSession) throws ResponseException {
        Set<Session> sessions = WSS.getSessions(gameID);
        for (Session session : sessions) {
            if (!session.equals(excludedSession)) {
                try {
                    session.getBasicRemote().sendText(new Gson().toJson(message));
                } catch (IOException e) {
                    throw new ResponseException(500, e.getMessage());
                }
            }
        }
    }
}
