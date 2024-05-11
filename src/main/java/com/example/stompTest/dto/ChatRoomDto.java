package com.example.stompTest.dto;

import java.util.UUID;

import lombok.Getter;

@Getter
public class ChatRoomDto {
    private String id;
    
    private String name;

    private Long userCount;

    public static ChatRoomDto create(String name) {
        ChatRoomDto chatRoom = new ChatRoomDto();
        chatRoom.id = UUID.randomUUID().toString();
        chatRoom.name = name;
        return chatRoom;
    }

    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }
}
