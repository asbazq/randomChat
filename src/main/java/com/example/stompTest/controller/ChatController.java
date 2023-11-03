package com.example.stompTest.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import com.example.stompTest.model.ChatMessage;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
// publisher 구현
// 기존의 WebSockChatHandler가 했던 역할을 대체하므로 WebSockChatHandler는 삭제
public class ChatController {
    private final SimpMessageSendingOperations simpMessageSendingOperations;

    // Websocket으로 들어오는 메시지 발행을 처리
    // 클라이언트에서는 prefix를 붙여서 /pub/chat/message로 발행 요청을 하면 Controller가 해당 메시지를 받아 처리
    @MessageMapping("/chat/message")
    public void message(ChatMessage chatMessage) {
        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 입장하셨습니다.");
            // /sub/chat/room/{roomId}은 채팅룸을 구분하는 값이므로 pub/sub에서 Topic의 역할
            simpMessageSendingOperations.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
        }
    }
}
