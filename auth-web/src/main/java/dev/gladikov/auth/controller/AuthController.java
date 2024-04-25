package dev.gladikov.auth.controller;

import dev.gladikov.auth.dto.JwtAuthenticationResponse;
import dev.gladikov.auth.dto.JwtVerificationRequest;
import dev.gladikov.auth.dto.JwtVerificationResponse;
import dev.gladikov.auth.dto.SignInRequest;
import dev.gladikov.auth.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthenticationService service;

    @PostMapping("/login")
    public JwtAuthenticationResponse login(@RequestBody SignInRequest req) {
        return service.login (req);
    }

    @PostMapping("/verify")
    public ResponseEntity<JwtVerificationResponse> verify(@RequestBody JwtVerificationRequest request) {
        log.info ("*** verify called ***");
        try {
            return  ResponseEntity.ok(service.validate (request.getToken ()));
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

}
