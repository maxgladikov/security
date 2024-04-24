package dev.gladikov.resource.controller.handler;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class MockHandler {
    public Mono<ServerResponse> greet(ServerRequest request) {
//        Mono<String> name = Mono.just(request.pathVariable("name"));
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue("Hello, WebFlux!"));
    }
}
