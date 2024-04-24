package dev.gladikov.auth.controller;

import dev.gladikov.auth.dto.JwtAuthenticationResponse;
import dev.gladikov.auth.dto.JwtVerificationRequest;
import dev.gladikov.auth.dto.SignInRequest;
import dev.gladikov.auth.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService service;

    @PostMapping("/login")
    public JwtAuthenticationResponse login(@RequestBody SignInRequest req) {
        return service.login (req);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody JwtVerificationRequest request) {
        try {
            service.validate (request.getToken ());
            return ResponseEntity.ok().build ();
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

}
