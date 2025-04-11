package ui.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.*;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

public class WebsocketFacade extends Endpoint {

    public Session session;
    GameHandler gameHandler;

    public WebsocketFacade(int port, GameHandler gameHandler) throws ResponseException {
        try {
            URI uri = new URI("ws://localhost:" + port + "/ws");
            this.gameHandler = gameHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            // set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage severMessage = new Gson().fromJson(message, ServerMessage.class);
                    gameHandler.printMessage(severMessage);
                }
            });
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

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
