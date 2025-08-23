package org.godigit.trackwise.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration class for setting up WebSocket communication using STOMP over WebSocket.
 * This enables real-time, two-way communication between the server and connected clients,
 * which is essential for features like the live asset tracking dashboard.
 */
@Configuration
@EnableWebSocketMessageBroker // Enables the WebSocket message handling, backed by a message broker.
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configures the message broker that will be used to route messages
     * from one client to another.
     * @param config The registry for configuring the message broker.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Sets up a simple, in-memory message broker.
        // It defines that messages whose destination starts with "/topic" should be
        // routed to this broker. The broker broadcasts messages to all subscribed clients.
        config.enableSimpleBroker("/topic");

        // Defines the prefix for messages that are bound for @MessageMapping-annotated
        // methods in your controllers. For example, a client would send a message to
        // "/app/hello", which would be routed to a controller method mapped to "/hello".
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registers the STOMP endpoint that clients will use to connect to the
     * WebSocket server.
     * @param registry The registry for STOMP endpoints.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // This is the initial HTTP endpoint that clients will connect to.
        // Upon connection, the protocol is upgraded from HTTP to WebSocket.
        // The endpoint is available at "/ws".
        registry.addEndpoint("/ws")
                // withSockJS() provides a fallback option for browsers that do not
                // support WebSockets, allowing them to use alternative transports
                // like long-polling.
                .withSockJS();
    }
}