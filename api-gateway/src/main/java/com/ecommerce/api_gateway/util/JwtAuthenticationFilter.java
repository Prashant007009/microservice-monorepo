package com.ecommerce.api_gateway.util;

import com.ecommerce.api_gateway.exception.CustomAuthExceptionHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/verify-otp"
    );

    private final JwtUtil jwtUtil;
    private final CustomAuthExceptionHandler exceptionHandler;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomAuthExceptionHandler exceptionHandler) {
        this.jwtUtil = jwtUtil;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        if (EXCLUDED_PATHS.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return exceptionHandler.handleUnauthorized(exchange);
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return exceptionHandler.handleUnauthorized(exchange);
        }

        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(jwtUtil.getAuthentication(token)));
    }
}
