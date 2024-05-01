package com.baeldung.webrtc.repository;

import com.baeldung.webrtc.entities.PasswordResetToken;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Long> {
    public List<PasswordResetToken> findAll();

    public Optional<PasswordResetToken> findById(Long id);

    public PasswordResetToken save(PasswordResetToken token);

    public List<PasswordResetToken> findByToken(String token);
}
