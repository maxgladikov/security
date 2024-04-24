package dev.gladikov.gateway.filter;


import dev.gladikov.gateway.rest.client.AuthServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final RouteSpecification specification;
    private final AuthServiceClient client;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest ();
        if (specification.isSecured.test (request)) {
            if (specification.isAuthBearer.test (request)) {
                final String token = this.getAuthHeader (request);
                client.validateToken (token).doOnError (e -> onError (exchange, "Token authorization failed", HttpStatus.UNAUTHORIZED));
                return null;
            } else {
                return this.onError (exchange, "Authorization header is missing", HttpStatus.UNAUTHORIZED);
            }


//            if (jwtUtil.isInvalid(token))
//                return this.onError(exchange, "Authorization header is invalid", HttpStatus.UNAUTHORIZED);

//            this.populateRequestWithHeaders(exchange, token);
//            return chain.filter (exchange);
        }
        return null;
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private String getAuthHeader(ServerHttpRequest request) {
        return request.getHeaders().getOrEmpty("Authorization").get(0);
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }

//    private void populateRequestWithHeaders(ServerWebExchange exchange, String token) {
//        Claims claims = jwtUtil.getAllClaimsFromToken(token);
//        exchange.getRequest().mutate()
//            .header("id", String.valueOf(claims.get("id")))
//            .header("role", String.valueOf(claims.get("role")))
//            .build();
//    }
}