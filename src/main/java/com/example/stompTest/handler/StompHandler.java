package com.example.stompTest.handler;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.stompTest.Exception.CustomException;
import com.example.stompTest.Exception.ErrorCode;
import com.example.stompTest.dto.ChatMessageDto;
import com.example.stompTest.model.Member;
import com.example.stompTest.repository.MemberRepository;
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
    private final MemberRepository memberRepository;

    // websocket을 통해 들어온 요청이 처리되기 전 실행
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // accessor 을 이용하면 내용 패킷에 접근 가능
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // websocket 연결시 헤더의 jwt token 검증
        if (StompCommand.CONNECT == accessor.getCommand()) { // websocket 연결요청
            Map<String, Object> headers = accessor.getMessageHeaders();
            String accessorJwtToken = accessor.getFirstNativeHeader("Authorization");
            log.info("accessorJwtToken check : {}", accessorJwtToken);
            String jwtToken = null;
            if (headers.containsKey("nativeHeaders")) {
                Map<String, List<String>> nativeHeaders = (Map<String, List<String>>) headers.get("nativeHeaders");
                List<String> authorizationHeaders = nativeHeaders.get("Authorization");
                if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
                    jwtToken = authorizationHeaders.get(0);
                }
            }
            if (jwtToken == null || !jwtToken.startsWith("Bearer ")) {
                throw new CustomException(ErrorCode.TOKEN_INVALID);
            }
            log.info("Attempting to connect with token: {}", jwtToken);
            if (!jwtTokenProvider.validateToken(jwtToken)) {
                log.error("Invalid token attempt: {}", jwtToken);
                throw new CustomException(ErrorCode.TOKEN_INVALID);
            }
            // 유효한 토큰인 경우 SecurityContextHolder에 인증 정보 설정
            Authentication auth = jwtTokenProvider.getAuthentication(jwtToken);
            SecurityContextHolder.getContext().setAuthentication(auth);

        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // 채팅룸 구독요청
            log.info("SUBSCRIBE command received");
            accessor.getMessageHeaders().forEach((key, value) -> {
                log.info("Header: {} = {}", key, value);
            });

            // header정보에서 구독 destination정보를 얻고, roomId를 추출(roomId를 URL로 전송해주고 있어 추출 필요)
            String destination = Optional.ofNullable((String) message.getHeaders().get("simpDestination"))
                    .orElseThrow(() -> new IllegalArgumentException("No destination found"));
            String roomId = chatRoomService.getRoomId(destination);
            // 채팅방에 들어온 클라이언트 sessionId를 roomId와 맵핑(세션이 들어가 있는 채팅방 확인용)
            String sessionId = Optional.ofNullable((String) message.getHeaders().get("simpSessionId"))
                    .orElseThrow(() -> new IllegalArgumentException("No session ID found"));
            // sessionId와 roomId를 맵핑
            chatRoomService.setUserEnterInfo(sessionId, roomId);
            log.info("Subscribe request for sessionId : {} to roomId : {}", sessionId, roomId);
            chatRoomService.plusUserCount(roomId);

            Map<String, Object> headers = accessor.getMessageHeaders();
            String jwtToken = null;
            if (headers.containsKey("nativeHeaders")) {
                Map<String, List<String>> nativeHeaders = (Map<String, List<String>>) headers.get("nativeHeaders");
                List<String> authorizationHeaders = nativeHeaders.get("Authorization");
                if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
                    jwtToken = authorizationHeaders.get(0);
                }
            }
            if (jwtToken == null || !jwtToken.startsWith("Bearer ")) {
                throw new CustomException(ErrorCode.TOKEN_INVALID);
            }
            log.info("Attempting to connect with token: {}", jwtToken);
            if (!jwtTokenProvider.validateToken(jwtToken)) {
                log.error("Invalid token attempt: {}", jwtToken);
                throw new CustomException(ErrorCode.TOKEN_INVALID);
            }

            String email = jwtTokenProvider.getUserPk(jwtToken);
            Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
            );
            String name = member.getName();
            log.info("Subscribe email : {}", email);
            log.info("Subscribe name : {}", name);
            chatRoomService.sendChatMessage(ChatMessageDto.builder().type(ChatMessageDto.MessageType.ENTER).roomId(roomId).sender(name).build());
            log.info("SUBSCRIBED {}, {}", name, roomId);

        } else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket 연결 종료
            String sessionId = Optional.ofNullable((String) message.getHeaders().get("simpSessionId"))
                    .orElseThrow(() -> new IllegalArgumentException("No session ID found"));
            String roomId = chatRoomService.getUserEnterRoomId(sessionId);
            chatRoomService.minusUserCount(roomId);

           
            Map<String, Object> headers = accessor.getMessageHeaders();
            String jwtToken = null;
            if (headers.containsKey("nativeHeaders")) {
                Map<String, List<String>> nativeHeaders = (Map<String, List<String>>) headers.get("nativeHeaders");
                List<String> authorizationHeaders = nativeHeaders.get("Authorization");
                if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
                    jwtToken = authorizationHeaders.get(0);
                }
            }
            if (jwtToken == null || !jwtToken.startsWith("Bearer ")) {
                throw new CustomException(ErrorCode.TOKEN_INVALID);
            }
            log.info("Attempting to connect with token: {}", jwtToken);
            if (!jwtTokenProvider.validateToken(jwtToken)) {
                log.error("Invalid token attempt: {}", jwtToken);
                throw new CustomException(ErrorCode.TOKEN_INVALID);
            }

            String email = jwtTokenProvider.getUserPk(jwtToken);
            Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
            );
            String name = member.getName();
            chatRoomService.sendChatMessage(ChatMessageDto.builder().type(ChatMessageDto.MessageType.QUIT).roomId(roomId).sender(name).build());
            chatRoomService.removeUserEnterInfo(sessionId);
            log.info("DISCONNECT {}, {}", sessionId, roomId);
        }
        return message;
    }
}
