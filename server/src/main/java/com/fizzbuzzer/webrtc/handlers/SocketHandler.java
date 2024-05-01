package com.fizzbuzzer.webrtc.handlers;

import com.fizzbuzzer.webrtc.Utils;
import com.fizzbuzzer.webrtc.rtc.Peer;
import com.fizzbuzzer.webrtc.rtc.SessionData;
import com.fizzbuzzer.webrtc.rtc.messages.Ready;
import com.fizzbuzzer.webrtc.rtc.messages.SignalMessage;
import com.fizzbuzzer.webrtc.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Map.entry;

@Component
public class SocketHandler extends TextWebSocketHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SocketHandler.class);

    StatisticsService statisticsService;

    Map<String, SessionData> sessions = new ConcurrentHashMap<>();

    Map<String, Set<SessionData>> rooms = new ConcurrentHashMap<>();

    Set<SessionData> idle = new HashSet<>();

    public SocketHandler(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    private Optional<WebSocketSession> getConnection(String clientId) {
        for (SessionData sessionData : this.sessions.values()) {
            Peer client = sessionData.getPeer();
            if (client.getId().equals(clientId)) {
                return Optional.ofNullable(sessionData.getWebsocketSession());
            }
        }
        return null;
    }

    private void send(WebSocketSession session, SignalMessage message) throws Exception {
        session.sendMessage(new TextMessage(Utils.getString(message)));
    }

    private void notifyClient(WebSocketSession session, Peer client, Peer other, boolean remove) throws Exception {
        LOG.info("Notifying " + client + " that " + other + (remove ? " disconnected" : " became available"));

        final SignalMessage message = new SignalMessage();
        message.setType("peer");
        message.setData(Map.ofEntries(
                entry("remove", remove)
                , entry("id", other.getId())
                , entry("name", other.getName())
        ));
        send(session, message);
    }

    private void removeUserAndSendLogout(WebSocketSession session) {
        if (this.sessions.containsKey(session.getId())) {
            Peer client = this.sessions.get(session.getId()).getPeer();
            LOG.info(client + " disconnected");

            this.sessions.remove(session.getId());

            // Notify all connected clients that a client has disconnected
            this.sessions.values().forEach(peerSessionData -> {
                Peer peer = peerSessionData.getPeer();
                try {
                    notifyClient(peerSessionData.getWebsocketSession(), peer, client, true);
                } catch (Exception e) {
                    LOG.warn("Error while message sending.", e);
                }
            });

            statisticsService.removeCapture(client.getId());
        }
    }

    private SessionData nextIdleClient(SessionData clientSession) {
        SessionData result = null;
        int size = this.idle.size();
        if (size > 0) {
            int item = new Random().nextInt(size);
            int i = 0;
            for (SessionData sessionData : this.idle) {
                if (i == item) {
                    result = sessionData;
                }
                i++;
            }
        }
        if (result != null) {
            this.idle.remove(result);
        }

        return result;
    }

    private void leaveCurrentRoom(WebSocketSession session) throws Exception {
        if (this.sessions.containsKey(session.getId())) {
            SessionData sessionData = this.sessions.get(session.getId());
            Peer client = sessionData.getPeer();
            String room = client.getCurrentRoom();
            if (room != null) {
                if (this.rooms.containsKey(room) && rooms.get(room).contains(sessionData)) {
                    LOG.info("Client " + client.getName() + " leaves current room " + room);
                    if (this.rooms.get(room).size() == 1) {
                        this.rooms.remove(room);
                    } else {
                        this.rooms.get(room).remove(sessionData);

                        // Inform remaining node(s) in the room that counterpart has left
                        for (SessionData otherSessionData : this.rooms.get(room)) {
                            Peer peer = otherSessionData.getPeer();
                            LOG.info("Notify " + peer.getName() + " that " + client.getName() + " left the room " + room);
                            this.notifyClient(otherSessionData.getWebsocketSession(), peer, client, true);
                            peer.setCurrentRoom(null);
                        }
                    }
                } else {
                    LOG.info("Unknown room " + room);
                }

                client.setCurrentRoom(null);
            }
        }
    }

    private String toString(Set<SessionData> s) {
        StringBuilder sb = new StringBuilder("[");
        String sep = "";
        for (SessionData d : s) {
            sb.append(sep);
            sb.append(d.getPeer().getName());
            sep = ", ";
        }
        sb.append("]");
        return sb.toString();
    }


    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws InterruptedException, IOException, Exception {
        SignalMessage signalMessage = Utils.getObject(message.getPayload());
        Map<String, String> data = (Map<String, String>) signalMessage.getData();
        String type = signalMessage.getType();
        if ("identify".equals(type)) {
            Peer client = new Peer();
            client.setId(data.get("id"));
            client.setName(data.get("name"));

            if (!this.sessions.containsKey(session.getId())) {
                // This is a new client -- tell it about any existing peers
                SignalMessage responseHello = new SignalMessage();
                responseHello.setType("hello");
                send(session, responseHello);

                LOG.info("New client " + client);
            }

            this.sessions.put(session.getId(), new SessionData(new ConcurrentWebSocketSessionDecorator(session, 2000, 4096), client));
        } else if ("room".equals(type)) {
            Peer client = this.sessions.get(session.getId()).getPeer();
            LOG.info(client.getName() + " seeks next");
            // Leave current room
            this.leaveCurrentRoom(session);

            SessionData clientSessionData = this.sessions.get(session.getId());
            SessionData roommateSessionData = null;
            if (idle.size() >= 1) {
                roommateSessionData = nextIdleClient(this.sessions.get(session.getId()));

                // Remove any ghost peers that left without saying good bye
                while (roommateSessionData != null && (!roommateSessionData.getWebsocketSession().isOpen() || roommateSessionData.equals(clientSessionData))) {
                    if (!roommateSessionData.equals(clientSessionData)) {
                        this.leaveCurrentRoom(roommateSessionData.getWebsocketSession());
                        this.sessions.remove(roommateSessionData.getWebsocketSession().getId());
                    }
                    roommateSessionData = nextIdleClient(this.sessions.get(session.getId()));
                }
            }


            if (roommateSessionData != null) {
                LOG.info("Random picked " + roommateSessionData.getPeer().getName() + " for " + client.getName() + ". Idles now are: " + toString(this.idle));

                // Get a new room
                String roomName = UUID.randomUUID().toString();
                Set<SessionData> roommates = new HashSet<>();

                this.idle.remove(this.sessions.get(session.getId()));
                this.idle.remove(this.sessions.get(roommateSessionData.getWebsocketSession().getId()));

                this.rooms.put(roomName, roommates);

                this.sessions.get(session.getId()).getPeer().setCurrentRoom(roomName);
                this.sessions.get(roommateSessionData.getWebsocketSession().getId()).getPeer().setCurrentRoom(roomName);

                SignalMessage responseRoom = new SignalMessage();
                responseRoom.setType("room");
                responseRoom.setData(Map.ofEntries(entry("name", roomName)));

                send(session, responseRoom);
                send(roommateSessionData.getWebsocketSession(), responseRoom);

                LOG.info("Generated new room " + roomName + " for " + roommateSessionData.getPeer().getName() + " and " + client.getName());
            } else {
                LOG.info("No roommates available for " + client.getName());
                this.idle.add(this.sessions.get(session.getId()));
            }

        } else if ("join".equals(type)) {
            Peer client = this.sessions.get(session.getId()).getPeer();

            String roomName = data.get("name");
            Set<SessionData> roommates = this.rooms.get(roomName);

            roommates.add(this.sessions.get(session.getId()));
            LOG.info("Client " + this.sessions.get(session.getId()).getPeer() + " entered room " + roomName + " of size " + roommates.size());

            for (SessionData otherSessionData : roommates) {
                Peer peer = otherSessionData.getPeer();
                if (!peer.getId().equals(client.getId())) {
                    // This is a new client -- tell it about any existing peers
                    this.notifyClient(session, client, peer, false);

                    // Announce the new peer to other peers already in room
                    this.notifyClient(otherSessionData.getWebsocketSession(), peer, client, false);

                    SignalMessage inviteSignal = new SignalMessage();
                    inviteSignal.setType("invite");
                    inviteSignal.setData(Map.ofEntries(
                            entry("id", client.getId())
                            , entry("name", client.getName())));
                    send(otherSessionData.getWebsocketSession(), inviteSignal);
                }
            }
        } else {
            if (data.containsKey("target")) {
                Optional<WebSocketSession> con = getConnection(data.get("target"));
                if (con.isPresent()) {
                    send(con.get(), signalMessage);
                }
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Ready ready = new Ready();
        ready.setType("ready");
        ready.setVersion("1");
        ready.setId(UUID.randomUUID().toString());
        send(session, ready);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        removeUserAndSendLogout(session);
    }


}