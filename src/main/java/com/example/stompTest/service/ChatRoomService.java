package com.example.stompTest.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.example.stompTest.dto.ChatRoomDto;

import jakarta.annotation.PostConstruct;

public class ChatRoomService {
    // 예제로 db에 저장하지 않고 map에 저장
    private Map<String, ChatRoomDto> chatRoomMap;

    @PostConstruct
    private void init() {
        chatRoomMap = new LinkedHashMap<>();
    }

    public List<ChatRoomDto> findAllRoom() {
        List<ChatRoomDto> chatRooms = new ArrayList<>(chatRoomMap.values());
        // 최근 생성 순
        Collections.reverse(chatRooms);
        return chatRooms;
    }

    public ChatRoomDto findRoomId(String id) {
        return chatRoomMap.get(id);
    }

    public ChatRoomDto createChatRoomDto(String name) {
        ChatRoomDto chatRoomDto = ChatRoomDto.create(name);
        chatRoomMap.put(chatRoomDto.getRoomId(), chatRoomDto);
        return chatRoomDto;
    }
}
