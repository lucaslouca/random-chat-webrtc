package com.fizzbuzzer.webrtc.rtc;

public class Peer {

    String id;

    String name;

    String currentRoom;

    boolean remove;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRemove() {
        return remove;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }

    public String getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(String currentRoom) {
        this.currentRoom = currentRoom;
    }

    @Override
    public String toString() {
        return "Peer{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", currentRoom='" + currentRoom + '\'' +
                '}';
    }
}
