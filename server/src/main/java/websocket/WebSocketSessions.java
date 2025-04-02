package websocket;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

/*

Tracks which connections are still active
Needs to function correctly when multiple games are running concurrently
    - ex: notifications from one game don't get sent to the others

 */
public class WebSocketSessions {
    // tracks current connections by gameID and a set of all connections
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void add(Integer gameID, Session session) {
        connections.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void removeGame(Integer gameID) {
        connections.remove(gameID);
    }

    public void removeSession(Integer gameID, Session session) {
        Set<Session> sessions = connections.get(gameID);
        if (sessions != null) {
            sessions.remove(session);
            // remove from map if sessions is empty after removal
            if (sessions.isEmpty()) {
                connections.remove(gameID);
            }
        }
    }

    public void removeAllOfSession(Session session) {
        for (ConcurrentHashMap.Entry<Integer, Set<Session>> entry : connections.entrySet()) {
            Set<Session> sessions = entry.getValue();
            sessions.remove(session);
            // remove from map if sessions is empty after removal
            if (sessions.isEmpty()) {
                connections.remove(entry.getKey());
            }
        }
    }

    public Set<Session> getSessions(Integer gameID) {
        return connections.get(gameID);
    }
}
