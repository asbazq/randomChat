package com.example.stompTest.dto;

import java.io.Serializable;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ChatRoomDto implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;

    private String id;
    
    // private String name;

    private Long userCount;

    public static ChatRoomDto create() {
        ChatRoomDto chatRoom = new ChatRoomDto();
        chatRoom.id = UUID.randomUUID().toString();
        // chatRoom.name = name;
        return chatRoom;
    }

    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }
}
