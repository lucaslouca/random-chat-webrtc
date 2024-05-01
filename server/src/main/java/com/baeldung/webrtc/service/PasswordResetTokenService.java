package com.baeldung.webrtc.service;

import com.baeldung.webrtc.entities.PasswordResetToken;

import java.util.List;


public interface PasswordResetTokenService {
    List<PasswordResetToken> save(PasswordResetToken item);

    List<PasswordResetToken> getAll();

    List<PasswordResetToken> getById(Long id);

    boolean validatePasswordResetToken(Long userId, String token);
}
