package dev.gladikov.auth.service;

import dev.gladikov.auth.dto.JwtAuthenticationResponse;
import dev.gladikov.auth.dto.JwtVerificationResponse;
import dev.gladikov.auth.dto.SignInRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final AuthenticationProviderImpl authenticationProvider;


    public JwtVerificationResponse validate(String token) {
        jwtService.validateToken (token);
        return new JwtVerificationResponse (jwtService.extractUserName (token),jwtService.extractRoles (token) );
    }



    public JwtAuthenticationResponse login(SignInRequest request) {
        System.out.println (request);
        Authentication authentication =
            authenticationProvider.authenticate (
                new UsernamePasswordAuthenticationToken (request.getUsername (), request.getPassword ()));
        return jwtService.generateToken (authentication);
    }
}
