package com.example.stompTest.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Random;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import com.example.stompTest.Exception.CustomException;
import com.example.stompTest.Exception.ErrorCode;
import com.example.stompTest.dto.ChatMessageDto;
import com.example.stompTest.dto.ChatRoomDto;
import com.example.stompTest.model.Member;
import com.example.stompTest.repository.MemberRepository;
import com.example.stompTest.security.UserDetailsImpl;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final MemberRepository memberRepository;
    private final static Random random = new Random();
    
    // Redis CacheKeys
    private static final String CHAT_ROOMS = "CHAT_ROOM"; // 채팅룸 저장
    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장
    public static final String USER_COUNT = "USER_COUNT"; // 채팅룸에 입장한 클라이언트수 저장

    
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, ChatRoomDto> hashOpsChatRoom; // 채팅룸 저장
    @Resource(name = "redisTemplate")
    private HashOperations<String, Long, String> hashOpsEnterInfo; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장
    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOps; // 채팅룸에 입장한 클라이언트수 저장

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

    // membership 회원이라면 우선권 부여
    private static class MemberComparator implements Comparator<Member> {
        public int compare(Member m1, Member m2) {
            if (m1.getRole() == Member.MemberRole.MEMBERSHIP && m2.getRole() != Member.MemberRole.MEMBERSHIP) {
                return -1;
            } else if (m1.getRole() != Member.MemberRole.MEMBERSHIP && m2.getRole() == Member.MemberRole.MEMBERSHIP) {
                return 1;
            }
            // role이 같을 시 랜덤
            return random.nextBoolean() ? 1 : -1;
        }
    }

    private final PriorityQueue<Member> userQueue = new PriorityQueue<>(new MemberComparator());

    public synchronized ChatRoomDto addUserToQueue(UserDetailsImpl userDetails) {
        Member member = memberRepository.findById(userDetails.getmember().getId()).orElseThrow(
            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        Long userId = member.getId();
        Member.MemberRole role = member.getRole();
        if (userQueue.contains(member)) {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
        }
        userQueue.offer(new Member(userId, role));
        log.info("대기 수1 : " + userQueue.size());
        return matchUsers();
    }

    private ChatRoomDto matchUsers() {
        while (userQueue.size() > 1) {
            Member m1 = userQueue.poll();
            Member m2 = userQueue.poll();
            ChatRoomDto chatRoom = createChatRoom();
            log.info("대기 수2 : " + userQueue.size());

            if (m1 != null && m2 != null) {
                String roomId = chatRoom.getId();
                setUserEnterInfo(m1.getId(), roomId);
                setUserEnterInfo(m2.getId(), roomId);
                sendJoinMessage(m1, m2, roomId);
                return chatRoom;
            }
        }
        return null;
    }

    private void sendJoinMessage(Member m1, Member m2, String roomId) {
        ChatMessageDto message1 = ChatMessageDto.builder().type(ChatMessageDto.MessageType.ENTER).roomId(roomId).sender(m1.getNickname()).build();
        ChatMessageDto message2 = ChatMessageDto.builder().type(ChatMessageDto.MessageType.ENTER).roomId(roomId).sender(m2.getNickname()).build();
        sendChatMessage(message1);
        sendChatMessage(message2);
    }

    public synchronized void minusUserToQueue(UserDetailsImpl userDetails) {
        Member member = memberRepository.findById(userDetails.getmember().getId()).orElseThrow(
            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        if (userQueue.contains(member)) {
            userQueue.remove(member);
        }
        log.info("대기 수1 : " + userQueue.size());
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
        ChatRoomDto chatRoom = ChatRoomDto.create();
        hashOpsChatRoom.put(CHAT_ROOMS, chatRoom.getId(), chatRoom);
        return chatRoom;
    }

    // 유저가 입장한 채팅방ID와 유저 세션ID 맵핑 정보 저장
    public void setUserEnterInfo(Long memberId, String roomId) {
        hashOpsEnterInfo.put(ENTER_INFO, memberId, roomId);
    }

    // 유저 세션으로 입장해 있는 채팅방 ID 조회
    public String getUserEnterRoomId(Long memberId) {
        return hashOpsEnterInfo.get(ENTER_INFO, memberId);
    }

    // 유저 세션정보와 맵핑된 채팅방ID 삭제
    public void removeUserEnterInfo(Long memberId) {
        hashOpsEnterInfo.delete(ENTER_INFO, memberId);
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
