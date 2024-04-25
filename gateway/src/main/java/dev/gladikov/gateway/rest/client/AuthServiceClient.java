package dev.gladikov.gateway.rest.client;

import dev.gladikov.gateway.rest.dto.JwtVerificationRequest;
import dev.gladikov.gateway.rest.dto.JwtVerificationResponse;
import lombok.RequiredArgsConstructor;
import org.apache.http.auth.AuthenticationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatusCode;

@Service
@RequiredArgsConstructor
public class AuthServiceClient {
//    private final DiscoveryService discoveryService;
    private String address = "http://localhost:10002";


//    WebClient client = WebClient.create (discoveryService.getServiceAddress ("auth-web"));
    WebClient client = WebClient.create (address);


    public Mono<JwtVerificationResponse> validateToken(String token) {
        return client
            .post ()
            .uri ("/auth/verify")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new JwtVerificationRequest (token))
            .retrieve ()
//            .onStatus (HttpStatusCode::isError, res ->
//                res.bodyToMono(String.class)
//                    .flatMap(body -> Mono.error(new AuthenticationException ("Authentication failed"))))
            .bodyToMono (JwtVerificationResponse.class);
    }
}


