package ui.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.*;
import javax.websocket.*;
import java.net.URI;

public class WebsocketFacade extends Endpoint implements MessageHandler.Whole<String> {

    public Session session;
    GameHandler gameHandler;

    public WebsocketFacade(int port) throws Exception {
        URI uri = new URI("ws://localhost:"+port+"/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
        this.session.addMessageHandler(this);
    }

    @Override
    public void onMessage(String message) {
        gameHandler.printMessage(message);
    }

    public void connect(String auth, int gameID, Session session) throws ResponseException {
        ConnectGameCommand command = new ConnectGameCommand(auth, gameID);
        sendMessage(command, session);
    }

    public void makeMove(String auth, int gameID, ChessMove move, Session session) throws ResponseException {
        MakeMoveGameCommand command = new MakeMoveGameCommand(auth, gameID, move);
        sendMessage(command, session);
    }

    public void leaveGame(String auth, int gameID, Session session) throws ResponseException {
        LeaveGameCommand command = new LeaveGameCommand(auth, gameID);
        sendMessage(command, session);
    }

    public void resignGame(String auth, int gameID, Session session) throws ResponseException {
        ResignGameCommand command = new ResignGameCommand(auth, gameID);
        sendMessage(command, session);
    }

    private void sendMessage(UserGameCommand command, Session session) throws ResponseException {
        try {
            session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}
