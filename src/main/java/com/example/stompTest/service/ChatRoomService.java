package com.example.stompTest.service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.stompTest.dto.ChatMessageDto;
import com.example.stompTest.dto.ChatRoomDto;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final static Random random = new Random();
    
    // Redis CacheKeys
    private static final String CHAT_ROOMS = "CHAT_ROOM"; // 채팅룸 저장
    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장
    public static final String USER_COUNT = "USER_COUNT"; // 채팅룸에 입장한 클라이언트수 저장

    
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, ChatRoomDto> hashOpsChatRoom; // CHAT_ROOM, roomId, ChatRoomDto
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo; // ENTER_INFO, sessionId, roomId
    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOps; // USER_COUNT + "_" + roomId, userCount

    // destination정보에서 roomId 추출
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            return "";
    }

    // 채팅방에 메시지 발송
    public void sendChatMessage(ChatMessageDto chatMessage) {
        log.info("Attempt to sendChatMessage");
        chatMessage.setUserCount(getUserCount(chatMessage.getRoomId()));
        if (ChatMessageDto.MessageType.ENTER.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에 입장했습니다.");
            chatMessage.setSender("[알림]");
        } else if (ChatMessageDto.MessageType.QUIT.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에서 나갔습니다.");
            chatMessage.setSender("[알림]");
        }
        // websocket에 발행된 메세지를 redis로 발행(publish)
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }

     /**
     1. 유저 카운트가 1인 방 찾기
     2-1. 해당하는 방이 없을 경우, 새로운 방을 만들고 방의 정보 리턴
     2-2. 해당하는 방들이 있는 경우, 해당 방들 중 랜덤으로 하나를 뽑아서 그 채팅방의 정보를 리턴
     */
    //유저가 1명만 있는 방들을 찾고, 그 중 하나만 랜덤으로 뽑기
    public ResponseEntity<ChatRoomDto> randomRoom(OAuth2User oAuth2User) {
        String name = oAuth2User.getAttribute("name");
          List<ChatRoomDto> rooms = hashOpsChatRoom.values(CHAT_ROOMS).stream()
                .filter(room -> Integer.parseInt(valueOps.get(USER_COUNT + "_" + room.getRoomId())) == 1)
                .collect(Collectors.toList());
        if (rooms.isEmpty()) {
            ChatRoomDto chatRoom = createChatRoom();
            log.info("Create room user: {}", name);
            return ResponseEntity.ok(ChatRoomDto.builder()
                    .roomId(chatRoom.getRoomId())
                    .userCount(chatRoom.getUserCount())
                    .build());
        } else {
            ChatRoomDto chatRoom = rooms.get(random.nextInt(rooms.size()));
            log.info("Enter room user: {}", name);
            return ResponseEntity.ok(ChatRoomDto.builder()
                    .roomId(chatRoom.getRoomId())
                    .userCount(chatRoom.getUserCount())
                    .build());
        }
    }

    // 모든 채팅방 조회
    public List<ChatRoomDto> findAllRoom() {
        return hashOpsChatRoom.values(CHAT_ROOMS);
    }

    // 특정 채팅방 조회
    public ChatRoomDto findRoomById(String roomId) {
        return hashOpsChatRoom.get(CHAT_ROOMS, roomId);
    }

    // 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장한다.
    public ChatRoomDto createChatRoom() {
        ChatRoomDto chatRoom = ChatRoomDto.builder()
                .roomId(UUID.randomUUID().toString())
                .userCount(0L)
                .build();
        hashOpsChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }

    // 유저가 입장한 채팅방ID와 유저 세션ID 맵핑 정보 저장
    public void setUserEnterInfo(String sessionId, String roomId) {
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId);
    }

    // 유저 세션으로 입장해 있는 채팅방 ID 조회
    public String getUserEnterRoomId(String sessionId) {
        return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
    }

    // 유저 세션정보와 맵핑된 채팅방ID 삭제
    public void removeUserEnterInfo(String sessionId) {
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
    }

    // 채팅방 유저수 조회
    public long getUserCount(String roomId) {
        return Long.valueOf(Optional.ofNullable(valueOps.get(USER_COUNT + "_" + roomId)).orElse("0"));
    }

    // 채팅방에 입장한 유저수 +1
    public long plusUserCount(String roomId) {
        return Optional.ofNullable(valueOps.increment(USER_COUNT + "_" + roomId)).orElse(0L);
    }

    // 채팅방에 입장한 유저수 -1
    public long minusUserCount(String roomId) {
        return Optional.ofNullable(valueOps.decrement(USER_COUNT + "_" + roomId)).filter(count -> count > 0).orElse(0L);
    }

 
}
