package com.example.stompTest.security;

<<<<<<< HEAD
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
=======
import org.springframework.context.annotation.Configuration;
>>>>>>> a9d529bb42d525b8b869238cddc213d7e57b123f
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

<<<<<<< HEAD
    private final StompHandler stompHandler;

    @Autowired
    public WebSocketConfig(StompHandler stompHandler) {
        this.stompHandler = stompHandler;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        WebSocketMessageBrokerConfigurer.super.configureMessageBroker(registry);
        registry.enableSimpleBroker("/sub"); // 클라이언트에서 보낸 메세지를 받을 prefix
=======
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        WebSocketMessageBrokerConfigurer.super.configureMessageBroker(registry);
        registry.enableSimpleBroker("/sub");
>>>>>>> a9d529bb42d525b8b869238cddc213d7e57b123f
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        WebSocketMessageBrokerConfigurer.super.registerStompEndpoints(registry);
        registry.addEndpoint("/ws-stomp").setAllowedOrigins("*").withSockJS();
    }
    
<<<<<<< HEAD
     @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }
=======
>>>>>>> a9d529bb42d525b8b869238cddc213d7e57b123f
}
