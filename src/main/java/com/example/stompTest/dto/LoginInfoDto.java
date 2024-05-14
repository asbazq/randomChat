package com.example.stompTest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginInfoDto {
    private String username;
    private String token;
 
    @Builder
    public LoginInfoDto(String username, String token) {
        this.username = username;
        this.token = token;
    }
}
