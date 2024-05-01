package com.fizzbuzzer.webrtc.service.impl;

import com.fizzbuzzer.webrtc.entities.PasswordResetToken;
import com.fizzbuzzer.webrtc.repository.PasswordResetTokenRepository;
import com.fizzbuzzer.webrtc.service.PasswordResetTokenService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

// https://www.baeldung.com/spring-security-registration-i-forgot-my-password
@Service
@Transactional
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;


    @Override
    public List<PasswordResetToken> save(PasswordResetToken token) {
        return Collections.singletonList(passwordResetTokenRepository.save(token));
    }

    @Override
    public List<PasswordResetToken> getAll() {
        return passwordResetTokenRepository.findAll();
    }

    @Override
    public List<PasswordResetToken> getById(Long id) {
        return Collections.singletonList(passwordResetTokenRepository.findById(id).get());
    }

    @Override
    public boolean validatePasswordResetToken(Long userId, String token) {
        PasswordResetToken passToken = null;
        List<PasswordResetToken> passTokenResults = passwordResetTokenRepository.findByToken(token);

        if (passTokenResults.isEmpty()) {
            throw new RuntimeException("No tokens found with token=" + token);
        } else {
            passToken = passTokenResults.get(0);
        }

        if ((passToken == null) || (passToken.getUser().getId() != userId)) {
            return false;
        }

        Calendar cal = Calendar.getInstance();
        if ((passToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            return false;
        }


        return true;
    }

}
