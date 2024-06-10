package com.example.stompTest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.stompTest.dto.ChatRoomDto;
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
    public ResponseEntity<ChatRoomDto> randomRoom(@AuthenticationPrincipal OAuth2User oAuth2User) {
        return chatRoomService.randomRoom(oAuth2User);
    }
}
