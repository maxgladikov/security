package dev.gladikov.gateway.rest.client;

import dev.gladikov.gateway.rest.dto.JwtVerificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthServiceClient {
    private final DiscoveryService discoveryService;

    WebClient client = WebClient.create (discoveryService.getServiceAddress ("auth-web"));


    public Mono<ResponseEntity<Void>> validateToken(String token) {
        return client.post ().uri ("/auth/verify")
            .header (HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body (Mono.just (new JwtVerificationRequest (token)), JwtVerificationRequest.class)
            .exchangeToMono (response -> {
                if (response.statusCode ().equals (HttpStatus.OK)) {
                    return response.toBodilessEntity ();
                } else {
                    return response.createError ();
                }
            });
    }
}


