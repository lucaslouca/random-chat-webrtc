package com.fizzbuzzer.webrtc.service.impl;

import com.fizzbuzzer.webrtc.dao.request.*;
import com.fizzbuzzer.webrtc.dao.response.JwtAuthenticationResponse;
import com.fizzbuzzer.webrtc.entities.PasswordResetToken;
import com.fizzbuzzer.webrtc.entities.Role;
import com.fizzbuzzer.webrtc.entities.User;
import com.fizzbuzzer.webrtc.repository.RoleRepository;
import com.fizzbuzzer.webrtc.repository.UserRepository;
import com.fizzbuzzer.webrtc.service.AuthenticationService;
import com.fizzbuzzer.webrtc.service.JwtService;
import com.fizzbuzzer.webrtc.service.PasswordResetTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    private RoleRepository roleRepository;


    @Autowired
    public JavaMailSender emailSender;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Override
    public UserSignupDTO signup(SignUpRequest request) {

        Optional<User> dbUser = userRepository.findByUsername(request.getUsername());
        if (dbUser.isPresent()) {
            return new UserSignupDTO(request.getUsername(), "The username already exists!", "", false);
        }

        Optional<User> dbUserEmail = userRepository.findByEmail(request.getEmail());
        if (dbUserEmail.isPresent()) {
            return new UserSignupDTO(request.getUsername(), "The e-mail is already in use!", "", false);
        }

        var user = User.builder().username(request.getUsername())
                .email(request.getEmail()).password(passwordEncoder.encode(request.getPassword())).build();


        Role role = roleRepository.findByRoleName("STANDARD_USER");
        user.setRoles(Arrays.asList(role));

        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        return UserSignupDTO.builder().token(jwt).success(true).build();
    }

    @Override
    public JwtAuthenticationResponse signin(SigninRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }


    private SimpleMailMessage constructResetTokenEmail(String contextPath, String token, User user) {
        String url = contextPath + "/#/updatepassword?id=" + user.getId() + "&token=" + token;
        String message = "Click on the link to reset your password:";
        return constructEmail("Reset Password", message + " \r\n" + url, user);
    }

    private SimpleMailMessage constructEmail(String subject, String body, User user) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom("luclouc@gmail.com");
        return email;
    }

    private void createPasswordResetTokenForUser(User user, String token) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, 24);
        Date expiryDate = cal.getTime();
        PasswordResetToken myToken = new PasswordResetToken(token, user, expiryDate);
        passwordResetTokenService.save(myToken);
    }


    public Long getProcessingUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user != null) {
            return user.getId();
        } else {
            return null;
        }
    }

    public ChangePasswordResponseDTO changePasswordProcessingUser(ChangePasswordDTO changePasswordDto) {
        Long userId = getProcessingUserId();
        User userInDb = userRepository.findById(userId).get();

        if (changePasswordDto.getNewPassword() == null || changePasswordDto.getNewPasswordConfirm() == null || changePasswordDto.getOldPassword() == null) {
            return new ChangePasswordResponseDTO(userInDb.getUsername(), "Password fields cannot be empty!", false);
        }

        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), userInDb.getPassword())) {
            return new ChangePasswordResponseDTO(userInDb.getUsername(), "Old password does not match!", false);
        }

        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getNewPasswordConfirm())) {
            return new ChangePasswordResponseDTO(userInDb.getUsername(), "New password entry does not match with confirmation entry!", false);
        }

        userInDb.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(userInDb);
        return new ChangePasswordResponseDTO(userInDb.getUsername(), "Password change was successful.", true);
    }

    public GenericResponseDTO logoutProcessingUser(String token) {
//        defaultTokenServices.revokeToken(token);
        return new GenericResponseDTO("Logout Successful!", true);
    }

    public ChangePasswordResponseDTO changeLostPassword(Long userId, String token, String password) {
        if (passwordResetTokenService.validatePasswordResetToken(userId, token)) {
            User userInDb = userRepository.findById(userId).get();
            userInDb.setPassword(passwordEncoder.encode(password));
            userRepository.save(userInDb);
            return new ChangePasswordResponseDTO(userInDb.getUsername(), "Password Reset Successful", true);

        } else {
            return new ChangePasswordResponseDTO("", "Invalid Token", false);
        }
    }

    public GenericResponseDTO resetPassword(UserCredentialsDTO userDto, String url) {
        User user = userRepository.findByEmail(userDto.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user != null) {
            String token = UUID.randomUUID().toString();
            createPasswordResetTokenForUser(user, token);

            emailSender.send(constructResetTokenEmail(url, token, user));

            return new GenericResponseDTO("Please check you mailbox for a password reset link", true);
        } else {
            return new GenericResponseDTO("E-mail does not exist!", false);
        }
    }
}
