package ui.websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;

public class WebsocketFacade extends Endpoint implements MessageHandler {

    Session session;
    GameHandler gameHandler;

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                System.out.println("Received message: " + message);
                try {
                    session.getBasicRemote().sendText("Echo: " + message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        super.onClose(session, closeReason);
    }

    @Override
    public void onError(Session session, Throwable thr) {
        super.onError(session, thr);
    }

    public void connect(Session session) {}

    public void makeMove(Session session) {}

    public void leaveGame(Session session) {}

    public void resignGame(Session session) {}

    private void sendMessage(ServerMessage serverMessage) throws ResponseException {
        try {
            session.getBasicRemote().sendText(new Gson().toJson(serverMessage));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}
