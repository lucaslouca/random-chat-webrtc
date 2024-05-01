package com.fizzbuzzer.webrtc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;

@SpringBootApplication
public class WebRTCDemoApplication {

    public static void main(String[] args) {
        Security.setProperty("crypto.policy", "unlimited");
        SpringApplication.run(WebRTCDemoApplication.class, args);
    }
}
