package com.fizzbuzzer.webrtc.controller;

import com.fizzbuzzer.webrtc.dao.request.*;
import com.fizzbuzzer.webrtc.service.AuthenticationService;
import com.fizzbuzzer.webrtc.service.PasswordResetTokenService;
import com.fizzbuzzer.webrtc.service.impl.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;


@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UserDetailsServiceImpl userService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;


    @PostMapping(value = "/resetpassword")
    public GenericResponseDTO resetPassword(@RequestBody UserCredentialsDTO userDto) {
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequestUri();
        URI newUri = builder.build().toUri();

        String url = newUri.getHost();

        return authenticationService.resetPassword(userDto, url);
    }

    @PostMapping(value = "/changelostpassword")
    public ChangePasswordResponseDTO changeLostPassword(@RequestBody ChangeLostPasswordDTO changePasswordDto) throws IOException {
        return authenticationService.changeLostPassword(changePasswordDto.getUserId(), changePasswordDto.getToken(), changePasswordDto.getPassword());
    }

    @PostMapping(value = "/password")
    public ChangePasswordResponseDTO changePassword(@RequestBody ChangePasswordDTO changePasswordDto) {
        return authenticationService.changePasswordProcessingUser(changePasswordDto);
    }


    @PostMapping(value = "/email")
    public GenericResponseDTO changeEmail(@RequestBody ChangeEmailDTO changeEmailDto, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.contains("Bearer")) {
            String tokenId = authorization.substring("Bearer".length() + 1);
            return userService.changeEmail(changeEmailDto.getEmail(), tokenId);
        }
        return null;
    }

    @GetMapping(value = "/username")
    public String getUsername() {
        return userService.getProccesingUserUsername();
    }

    @GetMapping(value = "/email")
    public String getEmail() {
        return userService.getProccesingUserEmail();
    }

    @PostMapping(value = "/picture")
    public GenericResponseDTO uploadFile(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.contains("Bearer")) {
            String tokenId = authorization.substring("Bearer".length() + 1);
            return userService.changePicture(file, tokenId);
        }
        return null;
    }

    @RequestMapping(value = "/picture")
    public String getPicture() {
        return userService.getProccesingUserPictureURL();
    }

    @RequestMapping(value = "/picture/{fileName}")
    public String getPictureFromURL(@PathVariable String fileName) {
        return userService.getPictureForProccesingUserPicture(fileName);
    }

    @PostMapping(value = "/logout")
    @PreAuthorize("hasAuthority('ADMIN_USER') or hasAuthority('STANDARD_USER')")
    public GenericResponseDTO logout(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.contains("Bearer")) {
            String tokenId = authorization.substring("Bearer".length() + 1);
            return authenticationService.logoutProcessingUser(tokenId);
        }
        return null;
    }
}
