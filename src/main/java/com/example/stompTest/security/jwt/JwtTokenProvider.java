package com.example.stompTest.security.jwt;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.security.Key;
import java.security.SignatureException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.example.stompTest.Exception.CustomException;
import com.example.stompTest.Exception.ErrorCode;
import com.example.stompTest.model.Member;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.example.stompTest.security.jwt.JwtProperties.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    @Value("${jwt.secretKey}")
    private String secretKey;

    public final HttpServletResponse response;
    private final UserDetailsService userDetailsService;
    private Key key;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 생성
    public String createToken(String userPk, Long userId, Member.MemberRole role) {
        Claims claims = Jwts.claims().setSubject(userPk);
        claims.put("role", role);
        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)// 정보저장
                .claim("userId", userId)
                .setIssuedAt(now)// 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + ACCESS_EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)// 사용할 암호화 알고리즘
                // signature에 들어갈 secret값 세팅
                .compact();

        response.addHeader(HEADER_ACCESS, TOKEN_PREFIX + token);
        return token;
    }

    public String createRefreshToken(String userPk) {
        Claims claims = Jwts.claims().setSubject(userPk);
        Date now = new Date();
        String refreshToken = Jwts.builder()
                .setClaims(claims)// 정보저장
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // response.addHeader("RefreshToken","Bearer " + refreshToken);
        return refreshToken;
    }

    // 토큰에서 회원 정보 추출
    public String getUserPk(String jwtToken) {
        return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(setTokenName(jwtToken))
                    .getBody()
                    .getSubject();
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String jwtToken) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(setTokenName(jwtToken)));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // Request의 Header에서 token 값을 가져옴
    // "X-AUTH-TOKEN":"TOKEN 값"
    public String resolveToken(HttpServletRequest request) {
        // return request.getHeader("Authorization");
        return request.getHeader("AccessToken");
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        return request.getHeader("RefreshToken");
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                                    .setSigningKey(key)
                                    .build()
                                    .parseClaimsJws(setTokenName(jwtToken));
            return !claims.getBody().getExpiration().before(new Date());
        } catch (SecurityException | MalformedJwtException | IllegalArgumentException e) {
            log.error(jwtToken, new CustomException(ErrorCode.TOKEN_INVALID));
        } catch (ExpiredJwtException e) {
            log.error(jwtToken, new CustomException(ErrorCode.TOKEN_EXPIRED));
        } catch (UnsupportedJwtException e) {
            log.error(jwtToken, new CustomException(ErrorCode.TOKEN_UNSUPPORTED));
        }
        return false;
    }
    

    // 만료 기간 확인
    public Date ExpireTime(String token) {
        Jws<Claims> claims = Jwts.parserBuilder()
                                .setSigningKey(key)
                                .build()
                                .parseClaimsJws(setTokenName(token));
        return claims.getBody().getExpiration();
    }

    // Bearer 삭제
    public String setTokenName(String bearerToken) {
        return bearerToken.replace(TOKEN_PREFIX, "");
    }

    // 만료 accessToken 정보 추출
    public String getExpiredAccessTokenPk(String token) throws JsonProcessingException {
        String[] splitJwt = token.split("\\.");
        Base64.Decoder decoder = Base64.getDecoder();
        String payload = new String(decoder.decode(splitJwt[1].getBytes()));
        HashMap<String, String> payloadMap = new ObjectMapper().readValue(payload,
                new TypeReference<HashMap<String, String>>() {
                });
        return payloadMap.get("sub");
    }
}