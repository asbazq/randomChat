package com.example.stompTest.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatMessageDto {
    // 메시지 타입 : 입장, 채팅
    public enum MessageType {
        ENTER, QUIT, TALK
    }

    private Long id;

    private String roomId;

    private MessageType type; 

    private String sender; 

    private String message; 

    private Long userCount;

    @Builder
    public ChatMessageDto(MessageType type, String roomId, String sender, String message, Long userCount) {
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
        this.userCount = userCount;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


