package com.example.stompTest.security;

import com.example.stompTest.Exception.CustomException;
import com.example.stompTest.dto.ChatMessageDto;
import com.example.stompTest.handler.StompHandler;
import com.example.stompTest.service.ChatRoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StompHandlerTest {

    @Mock
    private ChatRoomService chatRoomService;

    @Mock
    private Authentication authentication;

    @Mock
    private OAuth2User oAuth2User;

    @Mock
    private MessageChannel messageChannel;

    @InjectMocks
    private StompHandler stompHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPreSendConnectCommand() {
        // Setup
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("name")).thenReturn("testUser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader("token", "validToken");

        Message<byte[]> message = mock(Message.class);
        when(message.getHeaders()).thenReturn(accessor.toMessageHeaders());

        // Act
        Message<?> result = stompHandler.preSend(message, messageChannel);

        // Assert
        assertNotNull(result);
        verify(oAuth2User).getAttribute("name");
    }

    @Test
    void testPreSendSubscribeCommand() {
        // Setup
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("name")).thenReturn("testUser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination("/topic/roomId");
        accessor.setSessionId("123");

        Message<byte[]> message = mock(Message.class);
        when(message.getHeaders()).thenReturn(accessor.toMessageHeaders());

        when(chatRoomService.getRoomId("/topic/roomId")).thenReturn("roomId");

        // Act
        Message<?> result = stompHandler.preSend(message, messageChannel);

        // Assert
        assertNotNull(result);
        verify(chatRoomService).setUserEnterInfo("123L", "roomId");
        verify(chatRoomService).sendChatMessage(any(ChatMessageDto.class));
    }

    @Test
    void testPreSendDisconnectCommand() {
        // Setup
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("name")).thenReturn("testUser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.DISCONNECT);
        accessor.setSessionId("123");

        Message<byte[]> message = mock(Message.class);
        when(message.getHeaders()).thenReturn(accessor.toMessageHeaders());

        when(chatRoomService.getUserEnterRoomId("123L")).thenReturn("roomId");

        // Act
        Message<?> result = stompHandler.preSend(message, messageChannel);

        // Assert
        assertNotNull(result);
        verify(chatRoomService).minusUserCount("roomId");
        verify(chatRoomService).removeUserEnterInfo("123L");
        verify(chatRoomService).sendChatMessage(any(ChatMessageDto.class));
    }

    @Test
    void testPreSendInvalidToken() {
        // Setup
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader("token", "invalidToken");

        Message<byte[]> message = mock(Message.class);
        when(message.getHeaders()).thenReturn(accessor.toMessageHeaders());

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // Act & Assert
        assertThrows(CustomException.class, () -> stompHandler.preSend(message, messageChannel));
    }
}
