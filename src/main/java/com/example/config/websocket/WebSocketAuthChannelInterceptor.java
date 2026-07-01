package com.example.config.websocket;

import com.example.entities.User;
import com.example.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Optional;

@Component
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand()) ||
            StompCommand.SEND.equals(accessor.getCommand())) {

            Authentication auth = authenticate(accessor);
            if (auth == null) {
                return null;
            }
            accessor.setUser(auth);
        }

        return message;
    }

    private Authentication authenticate(StompHeaderAccessor accessor) {
        String token = accessor.getFirstNativeHeader("Authorization");
        if (token == null) {
            token = accessor.getFirstNativeHeader("token");
        }
        if (token == null) {
            return null;
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty() || !userOpt.get().isActive()) {
                return null;
            }

            User user = userOpt.get();
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
