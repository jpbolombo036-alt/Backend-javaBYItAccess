package com.example.config.websocket;

import com.example.entities.User;
import com.example.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import javax.crypto.SecretKey;
import java.util.List;

@Component
public class JwtHandshakeHandler implements WebSocketHandler {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = extractToken(session);
        if (token == null) {
            session.close(new CloseStatus(4401, "Missing authentication token"));
            return;
        }

        Authentication auth = authenticateToken(token);
        if (auth == null) {
            session.close(new CloseStatus(4401, "Invalid authentication token"));
            return;
        }

        session.getAttributes().put("user", auth.getPrincipal());
        session.getAttributes().put("auth", auth);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {}

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {}

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {}

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private String extractToken(WebSocketSession session) {
        String uri = session.getUri().toString();
        String token = session.getHandshakeHeaders().getFirst("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        String[] query = uri.split("\\?");
        if (query.length > 1) {
            for (String param : query[1].split("&")) {
                String[] pair = param.split("=");
                if (pair.length == 2 && pair[0].equals("token")) {
                    return pair[1];
                }
            }
        }
        return null;
    }

    private Authentication authenticateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null || !user.isActive()) {
                return null;
            }

            String role = claims.get("role", String.class);
            List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + role)
            );

            return new UsernamePasswordAuthenticationToken(user, null, authorities);
        } catch (Exception e) {
            return null;
        }
    }
}
