package websocket.commands;


public class ConnectGameCommand extends UserGameCommand {
    public ConnectGameCommand(String authToken, Integer gameID) {
        super(CommandType.CONNECT, authToken, gameID);
    }
}
