package ru.drobyazko.fooddeliveryservice;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic/order");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws");
        // TODO: we can add interceptor or custom handler to change the principal used or principal name to userId
        //  (we use userIds everywhere but when sending messages through simpMessagingTemplate username is used instead of userId)
    }

// TODO: since spring websocket security is based around sessions and does not really work in our case
//  we can try to secure ws channels using ChannelInterceptor's registered through below methods
//
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//    }
//
//    @Override
//    public void configureClientOutboundChannel(ChannelRegistration registration) {
//    }
}
