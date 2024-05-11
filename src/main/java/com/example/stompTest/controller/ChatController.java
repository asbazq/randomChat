package com.example.stompTest.controller;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.example.stompTest.dto.ChatMessageDto;
import com.example.stompTest.security.jwt.JwtTokenProvider;
import com.example.stompTest.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
// publisher 구현
// 기존의 WebSockChatHandler가 했던 역할을 대체하므로 WebSockChatHandler는 삭제
public class ChatController {
    private final ChatRoomService chatRoomService;
    private final JwtTokenProvider jwtTokenProvider;

    // Websocket으로 들어오는 메시지 발행을 처리
    // 클라이언트에서는 prefix를 붙여서 /pub/chat/message로 발행 요청을 하면 Controller가 해당 메시지를 받아 처리
    @MessageMapping("/chat/message")
    public void message(ChatMessageDto message, @Header("token") String token) {
        String name = jwtTokenProvider.getUserPk(token);
        // 로그인 회원 정보로 대화명 설정
        message.setSender(name);
        // 채팅방 인원수 세팅
        message.setUserCount(chatRoomService.getUserCount(message.getRoomId()));
        // Websocket에 발행된 메시지를 redis로 발행(publish)
        chatRoomService.sendChatMessage(message);
    }
}
