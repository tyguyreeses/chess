package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

import static websocket.commands.UserGameCommand.CommandType.*;

public class WebSocketHandler extends Endpoint {

    WebSocketSessions sessions = new WebSocketSessions();

    @OnWebSocketConnect
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
    @OnWebSocketClose
    public void onClose(Session session) {

    }
    @OnWebSocketError
    public void onError(Throwable throwable) {

    }
    @OnWebSocketMessage
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
    public void broadcastMessage(Integer gameID, ServerMessage message, Session excludedSession) {

    }

}
