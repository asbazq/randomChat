package com.example.stompTest.controller;

import java.util.List;

<<<<<<< HEAD
import org.springframework.http.ResponseEntity;
=======
>>>>>>> a9d529bb42d525b8b869238cddc213d7e57b123f
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
<<<<<<< HEAD
    public ResponseEntity<String> rooms(Model model) {
        return ResponseEntity.ok("/chat/room");
=======
    public String rooms(Model model) {
        return "/chat/room";
>>>>>>> a9d529bb42d525b8b869238cddc213d7e57b123f
    }

    // 모든 채팅방
    @GetMapping("/rooms")
    @ResponseBody
<<<<<<< HEAD
    public ResponseEntity<List<ChatRoomDto>> room() {
        List<ChatRoomDto> chatRooms = chatRoomService.findAllRoom();
        chatRooms.stream().forEach(room -> room.setUserCount(chatRoomService.getUserCount(room.getId())));
        return ResponseEntity.ok(chatRooms);
=======
    public List<ChatRoomDto> room() {
        return chatRoomService.findAllRoom();
>>>>>>> a9d529bb42d525b8b869238cddc213d7e57b123f
    }

    // 채팅방 입장
    @PostMapping("/room")
    @ResponseBody
<<<<<<< HEAD
    public ResponseEntity<ChatRoomDto> createRoom(@RequestParam String name) {
        return ResponseEntity.ok(chatRoomService.createChatRoom(name));
    }
         
    // 채팅방 입장 화면s
    @GetMapping("/room/enter/{roomId}")
    public ResponseEntity<String> roomDetail(Model model, @PathVariable Long roomId) {
        model.addAttribute("roomId", roomId);
        return ResponseEntity.ok("/chat/roomdetail");
=======
    public ChatRoomDto createRoom(@RequestParam String name) {
        return chatRoomService.createChatRoomDto(name);
    }
         
    // 채팅방 입장 화면
    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "/chat/roomdetail";
>>>>>>> a9d529bb42d525b8b869238cddc213d7e57b123f
    }

    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    @ResponseBody
<<<<<<< HEAD
    public ResponseEntity<ChatRoomDto> roomInfo(@PathVariable String roomId) {
        return ResponseEntity.ok(chatRoomService.findRoomById(roomId));
=======
    public ChatRoomDto roomInfo(@PathVariable String roomId) {
        return chatRoomService.findRoomId(roomId);
>>>>>>> a9d529bb42d525b8b869238cddc213d7e57b123f
    }
}
