package shared.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import shared.config.security.JwtUtil;

import java.net.URI;
import java.util.List;
import java.util.Map;
@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    public JwtHandshakeInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        String token = getToken(request);
        System.out.println("[Handshake] Received token: " + token);  // <--- Log here

        if (token != null && jwtUtil.validateAccessToken(token)) {
            String userId = jwtUtil.extractUserIdFromAccessToken(token);
            String role = jwtUtil.extractRoleFromAccessToken(token);

            if (userId != null && role != null) {
                attributes.put("userId", userId);
                attributes.put("role", role);
                System.out.println("[Handshake] Authenticated userId: " + userId + ", role: " + role);
            } else {
                System.out.println("[Handshake] Failed to extract userId or role from token.");
            }
        } else {
            System.out.println("[Handshake] Invalid or missing token.");
        }

        // You can return false here to reject the connection if needed
        return true;
    }


    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
        // Optional
    }

    private String getToken(ServerHttpRequest request) {
        // 1. Try Authorization header
        List<String> authHeaders = request.getHeaders().get("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String header = authHeaders.get(0);
            if (header.startsWith("Bearer ")) {
                return header.substring(7);
            }
        }

        // 2. Try query param ?token=xyz
        URI uri = request.getURI();
        String query = uri.getQuery();
        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("token=")) {
                    return param.substring("token=".length());
                }
            }
        }

        return null;
    }
}
