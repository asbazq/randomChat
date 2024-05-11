package com.example.stompTest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.stompTest.model.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>{
    
}
