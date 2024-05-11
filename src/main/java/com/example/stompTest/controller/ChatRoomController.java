package com.example.stompTest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.stompTest.dto.ChatRoomDto;
import com.example.stompTest.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    // 채팅 리스트
    @GetMapping("/room")
    public ResponseEntity<String> rooms(Model model) {
        return ResponseEntity.ok("/chat/room");
    }

    // 모든 채팅방
    @GetMapping("/rooms")
    @ResponseBody
    public ResponseEntity<List<ChatRoomDto>> room() {
        List<ChatRoomDto> chatRooms = chatRoomService.findAllRoom();
        chatRooms.stream().forEach(room -> room.setUserCount(chatRoomService.getUserCount(room.getId())));
        return ResponseEntity.ok(chatRooms);
    }

    // 채팅방 입장
    @PostMapping("/room")
    @ResponseBody
    public ResponseEntity<ChatRoomDto> createRoom(@RequestParam String name) {
        return ResponseEntity.ok(chatRoomService.createChatRoom(name));
    }
         
    // 채팅방 입장 화면s
    @GetMapping("/room/enter/{roomId}")
    public ResponseEntity<String> roomDetail(Model model, @PathVariable Long roomId) {
        model.addAttribute("roomId", roomId);
        return ResponseEntity.ok("/chat/roomdetail");
    }

    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ResponseEntity<ChatRoomDto> roomInfo(@PathVariable String roomId) {
        return ResponseEntity.ok(chatRoomService.findRoomById(roomId));
    }
}
