package com.fizzbuzzer.webrtc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fizzbuzzer.webrtc.rtc.messages.SignalMessage;

public class Utils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Utils() {
    }

    public static SignalMessage getObject(final String message) throws Exception {
        return objectMapper.readValue(message, SignalMessage.class);
    }

    public static String getString(final SignalMessage message) throws Exception {
        return objectMapper.writeValueAsString(message);
    }
}
