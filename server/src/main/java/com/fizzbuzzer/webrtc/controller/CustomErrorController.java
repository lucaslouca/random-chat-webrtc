package com.fizzbuzzer.webrtc.controller;

import com.fizzbuzzer.webrtc.dao.response.MyErrorResponse;
import com.fizzbuzzer.webrtc.exceptions.CustomAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorController implements ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(PATH)
    public ResponseEntity<MyErrorResponse> handleError(final HttpServletRequest request, final HttpServletResponse response) throws Throwable {
        Object e = request.getAttribute("jakarta.servlet.error.exception");
        if (e == null) {
            throw new NullPointerException("Not found");
        } else if (e instanceof io.jsonwebtoken.ExpiredJwtException) {
            throw new CustomAuthenticationException("Expired Token");
        } else {
            throw (Throwable) e;
        }
    }


    public String getErrorPath() {
        return PATH;
    }
}