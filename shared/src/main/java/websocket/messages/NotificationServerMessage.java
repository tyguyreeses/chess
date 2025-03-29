package websocket.messages;

public class NotificationServerMessage extends ServerMessage {

    private final String message;

    public NotificationServerMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }
}
