package websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/*

Tracks which connections are still active
Needs to function correctly when multiple games are running concurrently
    - ex: notifications from one game don't get sent to the others

 */
public class WebSocketSessions {
    // tracks current connections by gameID and a set of all connections
    private final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void addSessionToGame(Integer gameID, Session session) {
        connections.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void removeSessionFromGame(Integer gameID, Session session) {
        Set<Session> sessions = connections.get(gameID);
        if (sessions != null) {
            sessions.remove(session);
            // remove from map if sessions is empty after removal
            if (sessions.isEmpty()) {
                connections.remove(gameID);
            }
        }
    }

    public Set<Session> getSessions(Integer gameID) {
        return connections.get(gameID);
    }
}
