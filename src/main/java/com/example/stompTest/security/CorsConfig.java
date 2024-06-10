package com.example.stompTest.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.example.stompTest.security.jwt.JwtProperties;



@Configuration // ioc 로 등록
public class CorsConfig {
    // Filter 에 등록해야 됨(여기서는 WebSecurityConfig)
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            CorsConfiguration configuration = new CorsConfiguration();
            // 특정 출처들을 명시합니다. 여기서 **는 와일드카드 패턴을 의미합니다.
            configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:8080", "http://**"));
            // 모든 CRUD 요청을 허용합니다.
            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            // 허용할 헤더를 설정합니다.
            configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
            // 노출할 헤더를 설정합니다.
            configuration.setExposedHeaders(Arrays.asList("name", "email"));
            // 없으면 프론트측 Header에 나타나지않고 network에만 나타나게됨
            configuration.addExposedHeader("Authorization"); 
            configuration.addExposedHeader(JwtProperties.HEADER_ACCESS); 
            // 자격 증명을 허용합니다.
            configuration.setAllowCredentials(true);
            // CORS 구성의 유효 시간을 설정합니다.
            configuration.setMaxAge(3600L); // 1 hour
            // CORS 구성 등록
            source.registerCorsConfiguration("/**", configuration);
        return new CorsFilter(source); // source 를 하나 만듦
    }
    
}