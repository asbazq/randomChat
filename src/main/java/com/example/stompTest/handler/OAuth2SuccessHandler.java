package com.example.stompTest.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.stompTest.Exception.CustomException;
import com.example.stompTest.Exception.ErrorCode;
import com.example.stompTest.model.Member;
import com.example.stompTest.repository.MemberRepository;
import com.example.stompTest.security.jwt.JwtProperties;
import com.example.stompTest.security.jwt.JwtTokenProvider;

import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("Authentication Success: Redirecting to chat/room");
        
        // SecurityContextHolder에 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String email = oauthToken.getPrincipal().getAttribute("email");
        Member member = memberRepository.findByEmail(email).orElseThrow(
            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        Long userId = member.getId();
        log.info("OAuth2SuccessHandler email : {} userId : {}", email, userId);

        String jwtToken = jwtTokenProvider.createToken(email, userId, Member.MemberRole.USER);
        log.info("Generated JWT Token: {}", jwtToken);
        
        response.setHeader(JwtProperties.HEADER_ACCESS, JwtProperties.TOKEN_PREFIX + jwtToken);
        response.sendRedirect("/admin/room");
    }
}
