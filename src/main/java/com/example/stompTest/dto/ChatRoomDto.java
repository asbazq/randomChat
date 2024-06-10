package com.example.stompTest.dto;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatRoomDto implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;

    private String roomId;
    
    private Long userCount;

    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }

    @Builder
    public ChatRoomDto(String roomId, Long userCount) {
        this.roomId = roomId;
        this.userCount = userCount;
    }
}
