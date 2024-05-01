package com.baeldung.webrtc.dao.request;


import lombok.Builder;

@Builder
public class GenericResponseDTO {
    private String message;

    boolean success;

    public GenericResponseDTO() {
    }

    public GenericResponseDTO(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }


}
