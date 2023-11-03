package com.example.stompTest.dto;

import java.util.UUID;

import lombok.Getter;

@Getter
public class ChatRoomDto {
    private String roomId;
    private String name;

    public static ChatRoomDto create(String name) {
        ChatRoomDto chatRoomDto = new ChatRoomDto();
        chatRoomDto.roomId = UUID.randomUUID().toString();
        chatRoomDto.name = name;
        return chatRoomDto;
    }
    
}
