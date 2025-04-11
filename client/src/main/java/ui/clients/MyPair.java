package ui.clients;

import chess.ChessGame;

public record MyPair(ChessGame.TeamColor color, Integer gameID) {
}
