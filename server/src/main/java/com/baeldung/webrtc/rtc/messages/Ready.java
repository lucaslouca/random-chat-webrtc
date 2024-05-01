package com.baeldung.webrtc.rtc.messages;

public class Ready extends SignalMessage {
    private String version;

    private String id;

    public Ready() {
    }

    public Ready(String version, String id) {
        this.version = version;
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
