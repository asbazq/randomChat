package com.example.stompTest.dto;

import java.util.UUID;

import lombok.Getter;

@Getter
public class ChatRoomDto {
<<<<<<< HEAD
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
=======
    private String roomId;
    private String name;

    public static ChatRoomDto create(String name) {
        ChatRoomDto chatRoomDto = new ChatRoomDto();
        chatRoomDto.roomId = UUID.randomUUID().toString();
        chatRoomDto.name = name;
        return chatRoomDto;
    }
    
>>>>>>> a9d529bb42d525b8b869238cddc213d7e57b123f
}
