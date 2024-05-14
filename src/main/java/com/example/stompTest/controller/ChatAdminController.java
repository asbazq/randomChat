package com.example.stompTest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.stompTest.Exception.CustomException;
import com.example.stompTest.Exception.ErrorCode;
import com.example.stompTest.dto.ChatRoomDto;
import com.example.stompTest.dto.LoginInfoDto;
import com.example.stompTest.model.Member;
import com.example.stompTest.repository.MemberRepository;
import com.example.stompTest.security.jwt.JwtTokenProvider;
import com.example.stompTest.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class ChatAdminController {

    private final ChatRoomService chatRoomService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

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
    public ResponseEntity<ChatRoomDto> createRoom() {
        return ResponseEntity.ok(chatRoomService.createChatRoom());
    }
         
    // 채팅방 입장 화면
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


    // 유저 정보
    @GetMapping("/user")
    @ResponseBody
    public LoginInfoDto getMemberInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        Member member = memberRepository.findByName(name)
            .orElseThrow(() ->new CustomException(ErrorCode.USER_NOT_FOUND));
        Long memberId = member.getId();
        return LoginInfoDto.builder().username(name).token(jwtTokenProvider.createToken(name, memberId, member.getRole())).build();
    }
}

