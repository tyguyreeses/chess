package model;

import java.util.Collection;

/**
 * @param games
 */
public record ListGamesResponse(Collection<GameData> games) {}