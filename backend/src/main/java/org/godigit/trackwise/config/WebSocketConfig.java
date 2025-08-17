package org.godigit.trackwise.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // This sets up a simple in-memory message broker.
        // It defines that messages whose destination starts with "/topic" should be routed to the broker.
        config.enableSimpleBroker("/topic");
        // This defines that messages sent from clients should be prefixed with "/app".
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // This is the HTTP endpoint that clients will connect to to upgrade to a WebSocket connection.
        // withSockJS() is a fallback for browsers that don't support WebSockets.
        registry.addEndpoint("/ws").withSockJS();
    }
}