package dev.BatimenTIGER.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // C'est cette annotation qui crée le bean SimpMessagingTemplate
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Active un broker simple en mémoire pour envoyer des messages aux clients
        config.enableSimpleBroker("/topic");
        // Préfixe pour les messages envoyés du client vers le serveur
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // L'URL que ton frontend React/Next.js utilisera pour se connecter
        registry.addEndpoint("/ws-batimentiger")
                .setAllowedOrigins("http://localhost:3000") // L'URL de ton frontend
                .withSockJS();
    }
}