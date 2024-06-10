package com.example.stompTest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.stompTest.dto.ChatRoomDto;
import com.example.stompTest.dto.LoginInfo;
import com.example.stompTest.model.Member;
import com.example.stompTest.security.UserDetailsImpl;
import com.example.stompTest.security.jwt.JwtTokenProvider;
import com.example.stompTest.service.ChatRoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class ChatAdminController {

    private final ChatRoomService chatRoomService;


    // 채팅 리스트
    @GetMapping("/room")
    public String rooms(Model model) {
        // Add any necessary attributes to the model
        model.addAttribute("chatrooms", chatRoomService.findAllRoom());
        return "chat/room"; // This should map to room.ftl
    }


    // 모든 채팅방
    @GetMapping("/rooms")
    @ResponseBody
    public ResponseEntity<List<ChatRoomDto>> room() {
        List<ChatRoomDto> chatRooms = chatRoomService.findAllRoom();
        chatRooms.stream().forEach(room -> room.setUserCount(chatRoomService.getUserCount(room.getRoomId())));
        return ResponseEntity.ok(chatRooms);
    }

    // 채팅방 입장
    @PostMapping("/room")
    @ResponseBody
    public ResponseEntity<ChatRoomDto> createRoom() {
        return ResponseEntity.ok(chatRoomService.createChatRoom());
    }
         
    // 채팅방 입장 화면
    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "chat/roomdetail";
    }

    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ResponseEntity<ChatRoomDto> roomInfo(@PathVariable String roomId) {
        return ResponseEntity.ok(chatRoomService.findRoomById(roomId));
    }
}

