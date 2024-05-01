package com.baeldung.webrtc.service;

import com.baeldung.webrtc.dao.request.*;
import com.baeldung.webrtc.dao.response.JwtAuthenticationResponse;

public interface AuthenticationService {
    UserSignupDTO signup(SignUpRequest request);

    JwtAuthenticationResponse signin(SigninRequest request);

    GenericResponseDTO resetPassword(UserCredentialsDTO userDto, String url);

    ChangePasswordResponseDTO changeLostPassword(Long userId, String token, String password);

    ChangePasswordResponseDTO changePasswordProcessingUser(ChangePasswordDTO changePasswordDto);

    GenericResponseDTO logoutProcessingUser(String token);
}
