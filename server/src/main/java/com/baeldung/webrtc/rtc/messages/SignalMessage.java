package com.baeldung.webrtc.rtc.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignalMessage {
    private String type;
    private Object data;
}
