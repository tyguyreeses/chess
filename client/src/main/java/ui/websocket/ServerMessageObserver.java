package ui.websocket;

import websocket.messages.ServerMessage;

/*

the GameRepl object will implement this class, since it is the only one that needs to utilize websocket

 */
public interface ServerMessageObserver {
    void notify(ServerMessage serverMessage);
}
