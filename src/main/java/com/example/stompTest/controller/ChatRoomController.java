package com.example.stompTest.controller;

import java.util.List;

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
    public String rooms(Model model) {
        return "/chat/room";
    }

    // 모든 채팅방
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoomDto> room() {
        return chatRoomService.findAllRoom();
    }

    // 채팅방 입장
    @PostMapping("/room")
    @ResponseBody
    public ChatRoomDto createRoom(@RequestParam String name) {
        return chatRoomService.createChatRoomDto(name);
    }
         
    // 채팅방 입장 화면
    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "/chat/roomdetail";
    }

    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoomDto roomInfo(@PathVariable String roomId) {
        return chatRoomService.findRoomId(roomId);
    }
}
