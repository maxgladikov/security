package dev.gladikov.auth.controller;

import dev.gladikov.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    @PostMapping("/login")
    public Mono<String> login(ServerWebExchange exchange, Authentication authentication){
        System.out.println (authentication.getName ());
        System.out.println (authentication.getCredentials ());
        return Mono.just ("token");
    }

    @PostMapping("/verify")
    public Mono<Void> verify(String token){
        jwtService.isTokenValid (token);
        return Mono.empty();
    }



}
