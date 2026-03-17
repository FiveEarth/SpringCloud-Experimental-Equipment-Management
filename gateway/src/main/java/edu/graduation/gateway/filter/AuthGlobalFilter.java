package edu.graduation.gateway.filter;

import edu.graduation.common.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global auth filter for jwt verification.
 */
@Component
@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static final String AUTH_HEADER = "Authorization";

    private static final String TOKEN_PREFIX = "Bearer ";

    private static final List<String> WHITE_LIST = List.of(
            "/api/user/login",
            "/api/user/register"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        if (isWhitePath(path)) {
            return chain.filter(exchange);
        }
        String authHeader = request.getHeaders().getFirst(AUTH_HEADER);
        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            return unauthorized(exchange.getResponse(), "未登录或令牌缺失");
        }
        String token = authHeader.substring(TOKEN_PREFIX.length());
        try {
            Claims claims = JwtUtils.getClaims(token);
            String userId = String.valueOf(claims.get("userId"));
            String username = String.valueOf(claims.get("username"));
            String roles = extractAsCsv(claims.get("roles"));
            String perms = extractAsCsv(claims.get("perms"));
            ServerHttpRequest newRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Name", username)
                    .header("X-Roles", roles)
                    .header("X-Perms", perms)
                    .build();
            return chain.filter(exchange.mutate().request(newRequest).build());
        } catch (Exception ex) {
            log.warn("jwt parse error", ex);
            return unauthorized(exchange.getResponse(), "令牌无效或已过期");
        }
    }

    @Override
    public int getOrder() {
        return -10;
    }

    private boolean isWhitePath(String path) {
        for (String white : WHITE_LIST) {
            if (path.startsWith(white)) {
                return true;
            }
        }
        return false;
    }

    private Mono<Void> unauthorized(ServerHttpResponse response, String msg) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"code\":401,\"msg\":\"" + msg + "\"}";
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }

    @SuppressWarnings("unchecked")
    private String extractAsCsv(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof String s) {
            return s;
        }
        if (value instanceof Collection<?> collection) {
            return collection.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(","));
        }
        return value.toString();
    }
}

