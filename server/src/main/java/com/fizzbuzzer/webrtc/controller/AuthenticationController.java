package com.fizzbuzzer.webrtc.controller;

import com.fizzbuzzer.webrtc.dao.request.SignUpRequest;
import com.fizzbuzzer.webrtc.dao.request.SigninRequest;
import com.fizzbuzzer.webrtc.dao.request.UserSignupDTO;
import com.fizzbuzzer.webrtc.dao.response.JwtAuthenticationResponse;
import com.fizzbuzzer.webrtc.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<UserSignupDTO> signup(@RequestBody SignUpRequest request) {
        return ResponseEntity.ok(authenticationService.signup(request));
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> signin(@RequestBody SigninRequest request) {
        return ResponseEntity.ok(authenticationService.signin(request));
    }
}
