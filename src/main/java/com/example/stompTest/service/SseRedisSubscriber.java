package com.example.stompTest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SseRedisSubscriber {
    private final ObjectMapper objectMapper;

    //redis에서 publish(topic = sse)로 전송된 데이터 받음
    public void sendMessage(String message) {

    }
}