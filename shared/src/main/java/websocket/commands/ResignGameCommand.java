package websocket.commands;

public class ResignGameCommand extends UserGameCommand {
    public ResignGameCommand(String authToken, Integer gameID) {
        super(CommandType.RESIGN, authToken, gameID);
    }
}
