package dev.gladikov.gateway.filter;


import dev.gladikov.gateway.rest.client.AuthServiceClient;
import dev.gladikov.gateway.rest.dto.JwtVerificationResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.AuthenticationException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
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
                final Result result = new Result (chain, exchange);
                return client.validateToken (getAuthHeader (request))
                    .flatMap (result::populateRequestWithHeaders)
                    .onErrorResume (result::onError);
            } else
                return onError (exchange);
        }
        return chain.filter (exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private Mono<Void> onError(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse ();
        response.setStatusCode (HttpStatus.UNAUTHORIZED);
        return response.setComplete ();
    }

    private String getAuthHeader(ServerHttpRequest request) {
        return request.getHeaders ().getOrEmpty ("Authorization").get (0).replace ("Bearer ", "");
    }

    private static class Result {

        private final GatewayFilterChain chain;
        private final ServerWebExchange exchange;
        private final ServerHttpResponse response;

        public Result(GatewayFilterChain chain, ServerWebExchange exchange) {
            this.exchange = exchange;
            this.chain = chain;
            this.response = exchange.getResponse ();
        }

        public Mono<Void> populateRequestWithHeaders(JwtVerificationResponse claims) {
            log.info ("populating");
            ServerHttpRequest request =exchange.getRequest ().mutate ()
                .header ("user", claims.email ())
                .header ("roles", claims.roles ())
                .build ();
            ServerWebExchange newExchange = exchange.mutate().request(request).build();
            return chain.filter(newExchange);
        }

        public Mono<Void> onError(Throwable e) {
            log.info ("set error");
            response.setStatusCode (HttpStatus.UNAUTHORIZED);
            return response.setComplete ();
        }

    }
}