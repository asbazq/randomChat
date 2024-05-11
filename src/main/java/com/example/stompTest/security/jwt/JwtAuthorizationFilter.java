package com.example.stompTest.security.jwt;

import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

// 인가
// Security Filter 의 BasicAuthenticationFilter 는 상시 실행되나 권한이나 인증이 필요한 주소를 요청했을 때 token 의 유무를 검사
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private JwtTokenProvider jwtTokenProvider;

    public JwtAuthorizationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 인증이나 권한이 필요한 요청이 있을 때 해당 filter 를 실행
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        log.info("JwtAuthorizationFilter : 진입");
        String header = request.getHeader(JwtProperties.HEADER_ACCESS);
        if (header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        String token = jwtTokenProvider.setTokenName(header);

        // 유효한 토큰인지 확인
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰이 유효하면 토큰으로부터 유저 정보를 받아와서 저장
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // 로그인, 리프레시 요청이라면 토큰 검사하지 않음
        // 리프레시 요청은 만료된 토큰을 파싱하기 위해 검사하지 않음
        return request.getServletPath().equals("/login") || request.getServletPath().equals("/api/refresh");
    }

}