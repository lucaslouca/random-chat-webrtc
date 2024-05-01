package com.fizzbuzzer.webrtc.dao.request;


import lombok.Builder;

@Builder
public class UserSignupDTO {
    private String username;

    private String message;

    private String token;

    boolean success;

    public UserSignupDTO() {
    }

    public UserSignupDTO(String username, String message, String token, boolean success) {
        this.username = username;
        this.message = message;
        this.token = token;
        this.success = success;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

