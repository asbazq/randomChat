package com.example.stompTest.security;

import java.security.Principal;
import java.util.Optional;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.example.stompTest.Exception.CustomException;
import com.example.stompTest.Exception.ErrorCode;
import com.example.stompTest.dto.ChatMessageDto;
import com.example.stompTest.security.jwt.JwtTokenProvider;
import com.example.stompTest.service.ChatRoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 컨트롤러에 가기전 인터셉터를 통해 먼저 경유
@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomService chatRoomService;

    // websocket을 통해 들어온 요청이 처리 되기전 실행
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // accessor 을 이용하면 내용 패킷에 접근 가능
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // websocket 연결시 헤더의 jwt token 검증
        if (StompCommand.CONNECT == accessor.getCommand()) { // websocket 연결요청
            // 토큰의 값만 확인 (로그인 여부를 확인하기 위함)
            String jwtToken = accessor.getFirstNativeHeader("token");
            log.info("Attempting to connect with token: {}", jwtToken);
        if (!jwtTokenProvider.validateToken(jwtToken)) {
            log.error("Invalid token attempt: {}", jwtToken);
            throw new CustomException(ErrorCode.TOKEN_INVALID);
        }
        
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // 채팅룸 구독요청
            // header정보에서 구독 destination정보를 얻고, roomId를 추출(roomId를sRL로 전송해주고 있어 추출 필요)
            String destination = Optional.ofNullable((String) message.getHeaders().get("simpDestination"))
                .orElseThrow(() -> new IllegalArgumentException("No destination found"));
            String roomId = chatRoomService.getRoomId(destination);
            // 채팅방에 들어온 클라이언트 sessionId를 roomId와 맵핑(세션이 들어가 있는 채팅방 확인용)
            // sessionId는 현재 들어와있는 유저를 확인
            String sessionIdStr = Optional.ofNullable((String) message.getHeaders().get("simpSessionId"))
                .orElseThrow(() -> new IllegalArgumentException("No session ID found"));
            Long sessionId = Long.parseLong(sessionIdStr);
            //sessionId와 roomId를 맵핑
            chatRoomService.setUserEnterInfo(sessionId, roomId);
            String token = Optional.ofNullable((Principal) message.getHeaders().get("token")).map(Principal::getName).orElse("UnknownUser");
            String name = jwtTokenProvider.getUserPk(token);
            chatRoomService.sendChatMessage(ChatMessageDto.builder().type(ChatMessageDto.MessageType.ENTER).roomId(roomId).sender(name).build());
            log.info("SUBSCRIBED {}, {}", name, roomId);
        
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket 연결 종료
                String sessionIdStr = Optional.ofNullable((String) message.getHeaders().get("simpSessionId"))
                    .orElseThrow(() -> new IllegalArgumentException("No session ID found"));
                Long sessionId = Long.parseLong(sessionIdStr);
                String roomId = chatRoomService.getUserEnterRoomId(sessionId);
                chatRoomService.minusUserCount(roomId);
                // 클라이언트 퇴장 메시지를 채팅방에 발송(redis publish)
                String token = Optional.ofNullable(accessor.getFirstNativeHeader("token")).orElse("UnknownUser");
                String name = jwtTokenProvider.getUserPk(token);
                chatRoomService.sendChatMessage(ChatMessageDto.builder().type(ChatMessageDto.MessageType.QUIT).roomId(roomId).sender(name).build());
                chatRoomService.removeUserEnterInfo(sessionId);
                log.info("DISCONNECT {}, {}", sessionId, roomId);
            }
        return message;
    }
}

