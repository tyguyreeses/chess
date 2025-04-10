package websocket.messages;

public class ErrorServerMessage extends ServerMessage {

    public final String errorMessage;

    public ErrorServerMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }
}
