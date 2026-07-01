package com.example.config.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

@Component
public class WebSocketAuthInterceptor extends HttpSessionHandshakeInterceptor {

    private final JwtHandshakeHandler jwtHandshakeHandler;

    public WebSocketAuthInterceptor(JwtHandshakeHandler jwtHandshakeHandler) {
        this.jwtHandshakeHandler = jwtHandshakeHandler;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        try {
            jwtHandshakeHandler.afterConnectionEstablished(null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {}
}
