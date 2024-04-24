package dev.gladikov.gateway.filter;

import dev.gladikov.gateway.config.Endpoints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class RouteSpecification {

    @Autowired
    private Endpoints endpoints;


    public Predicate<ServerHttpRequest> isSecured =
        request -> endpoints.getOpened ()
            .stream()
            .noneMatch(uri -> request.getURI().getPath().contains(uri));

    public Predicate<ServerHttpRequest> isAuthPresent =
        request -> !request.getHeaders().containsKey("Authorization");

    public Predicate<ServerHttpRequest> isAuthBearer =
        request -> request.getHeaders().containsKey("Authorization")
                    && request.getHeaders().getOrEmpty("Authorization").get(0).startsWith ("Bearer ");

}