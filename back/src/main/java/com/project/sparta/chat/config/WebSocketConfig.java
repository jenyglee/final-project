package com.project.sparta.chat.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    //웹 소켓 연결을 위한 엔드포인트 설정 및 stomp sub/pub 엔트 포인트 설정
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //stomp 접속 주소 url => /ws-stomp
        registry.addEndpoint("/ws-stomp").setAllowedOriginPatterns("*")//연결될 엔드 포인트
                .withSockJS();  //SocketJs를 연결한다는 설정

    }

    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //메세지 구독 요청 url => 즉 메세지 받을때
        registry.enableSimpleBroker("/sub");

        //메세지를 발행하는 요청 url => 즉 메세지를 보낼 때
        registry.setApplicationDestinationPrefixes("/pub");
    }

}