package model;

import java.util.Collection;

/**
 * @param games
 */
public record ListGamesResponse(Collection<GameData> games) {
    @Override
    public String toString() {
        if (games == null || games.isEmpty()) {
            return "No games available.";
        }

        StringBuilder sb = new StringBuilder("Available Games:\n");
        for (GameData game : games) {
            sb.append(game).append("\n"); // Assumes GameData has a useful toString()
        }
        return sb.toString();
    }
}