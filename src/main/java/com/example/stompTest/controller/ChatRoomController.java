package com.example.stompTest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.stompTest.dto.ChatRoomDto;
import com.example.stompTest.security.UserDetailsImpl;
import com.example.stompTest.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
// @Controller
@RequestMapping("/chat")
@RestController
public class ChatRoomController {
    private final ChatRoomService chatRoomService;


    // 랜덤채팅방 입장
    @PostMapping("/random")
    @ResponseBody
    public ResponseEntity<ChatRoomDto> randomRoom(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(chatRoomService.addUserToQueue(userDetails));
    }

    // 대기 취소
    @PostMapping("/cancleWait")
    @ResponseBody
    public void cancleWait(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        chatRoomService.minusUserToQueue(userDetails);
    }
}
