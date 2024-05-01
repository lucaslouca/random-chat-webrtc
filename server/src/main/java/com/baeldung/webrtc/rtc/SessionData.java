package com.baeldung.webrtc.rtc;

import org.springframework.web.socket.WebSocketSession;

import java.util.Objects;

public class SessionData {

    private volatile WebSocketSession websocketSession;

    private Peer peer;

    public SessionData(WebSocketSession websocketSession, Peer peer) {
        this.websocketSession = websocketSession;
        this.peer = peer;
    }

    public WebSocketSession getWebsocketSession() {
        return websocketSession;
    }

    public void setWebsocketSession(WebSocketSession websocketSession) {
        this.websocketSession = websocketSession;
    }

    public Peer getPeer() {
        return peer;
    }

    public void setPeer(Peer peer) {
        this.peer = peer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionData that = (SessionData) o;
        return websocketSession.getId().equals(that.websocketSession.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(websocketSession.getId());
    }
}
