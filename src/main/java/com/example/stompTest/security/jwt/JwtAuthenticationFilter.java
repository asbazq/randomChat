package com.example.stompTest.security.jwt;

import java.io.UnsupportedEncodingException;



import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.stompTest.RedisUtil;
import com.example.stompTest.Exception.CustomException;
import com.example.stompTest.Exception.ErrorCode;
import com.example.stompTest.dto.LoginRequestDto;
import com.example.stompTest.security.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor // final 생성자 자동 생성
// @AllArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;

    // Authentication 객체 만들어서 리턴 => 의존 : AuthenticationManager
    // 인증 요청시에 실행되는 함수 => /login
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        log.info("JwtAuthenticationFilter : 진입");
        // 1. request에 있는 username과 password를 파싱해서 자바 Object로 받기
        ObjectMapper om = new ObjectMapper();
        LoginRequestDto loginRequestDto = null;
        try {
            // x-www.form-urlencoded 방식
            // BufferedReader br = request.getReader();
            //
            // String input = null;
            // while ((input = br.readLine()) != null) {
            // log.info(input);
            // }
            // json 방식
            loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("JwtAuthenticationFilter : " + loginRequestDto);

        if (loginRequestDto == null)
            throw new CustomException(ErrorCode.EMPTY_CONTENT);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequestDto.getUsername(),
                loginRequestDto.getPassword());

        log.info("JwtAuthenticationFilter : 토큰생성완료");
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        log.info("Authentication : " + userDetailsImpl.getmember().getName());
        return authentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws UnsupportedEncodingException {

        // 해당 principalDetails 정보를 통해 Jwt token 생성
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authResult.getPrincipal();

        // Hash 방식
        String accessToken = jwtTokenProvider.createToken(userDetailsImpl.getUsername(),
                userDetailsImpl.getmember().getId());

        String refreshToken = jwtTokenProvider.createRefreshToken(userDetailsImpl.getUsername());

        redisUtil.setDataExpire(userDetailsImpl.getUsername() + JwtProperties.HEADER_ACCESS,
                JwtProperties.TOKEN_PREFIX + accessToken, JwtProperties.ACCESS_EXPIRATION_TIME);
        redisUtil.setDataExpire(userDetailsImpl.getUsername() + JwtProperties.HEADER_REFRESH,
                JwtProperties.TOKEN_PREFIX + refreshToken, JwtProperties.REFRESH_EXPIRATION_TIME);
        // String nickname
        // =URLEncoder.encode(userDetailsImpl.getUsers().getNickname(),"utf-8");
        response.setHeader(JwtProperties.HEADER_ACCESS, JwtProperties.TOKEN_PREFIX + accessToken);
        response.setHeader(JwtProperties.HEADER_REFRESH, JwtProperties.TOKEN_PREFIX + refreshToken);
        response.setHeader("username", userDetailsImpl.getUsername());

    }

}