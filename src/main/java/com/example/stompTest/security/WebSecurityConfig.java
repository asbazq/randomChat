package com.example.stompTest.security;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.example.stompTest.RedisUtil;
import com.example.stompTest.security.jwt.JwtAuthenticationFilter;
import com.example.stompTest.security.jwt.JwtAuthorizationFilter;
import com.example.stompTest.security.jwt.JwtProperties;
import com.example.stompTest.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Configuration // 빈 등록
@EnableWebSecurity // 스프링 Security 지원을 가능하게 함
@RequiredArgsConstructor // final이 붙거나 @NotNull 이 붙은 필드의 생성자 생성
// @AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자 생성
@EnableMethodSecurity(securedEnabled = true) // @Secured 어노테이션 활성화
public class WebSecurityConfig {
// WebSecurityConfigurerAdapter을 상속받아 configure 재정의 -> SecurityFilterChain과 WebSecurityCustomizer을 Bean으로 등록

        private final CorsFilter corsFilter;
        private final JwtTokenProvider jwtTokenProvider;
        private final RedisUtil redisUtil;

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
                        throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }

        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
                // WebSecurity은 Spring Security Filter Chain을 거치지 않기 때문에 '인증' , '인가' 서비스가 모두 적용X
                return (web) -> web.ignoring()
                                // .antMatchers("/h2-console/**")
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                                .requestMatchers(PathRequest.toH2Console())
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()); // 정적 자원에 대해서 Security를 적용X

        }


        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
            http
                .csrf(csrf -> csrf.disable()) 
                .cors(cors -> cors.disable())
                .formLogin(form -> form.disable()) // 기본적인 formLogin방식을 쓰지않음 -> JWT를 쓰려면 필수 (세션허용,cors등록,formLogin방식을 꺼야함)
                .httpBasic(basic -> basic.disable()) // httpbasic방식(기본인증방식) : authorization에 id,pw를 담아서 보내는 방식(id,pw가 노출 위험)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 토큰 인증이므로 세션 사용x
                .authorizeHttpRequests(auth -> {
                    auth
                        // .requestMatchers(new AntPathRequestMatcher("/chat/**")).hasAnyRole("USER", "MEMBERSHIP")
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        // .requestMatchers(HttpMethod.GET, "/api/user/**").hasRole("USER")
                        // .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasRole("ADMIN")
                        // .anyRequest().authenticated();
                        .anyRequest().permitAll();
                })
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .addFilter(corsFilter)
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager, jwtTokenProvider, redisUtil), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthorizationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
            return http.build();
        }       

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:8080", "http://**"));
            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
            configuration.setExposedHeaders(Arrays.asList("Authorization", JwtProperties.HEADER_ACCESS, "username", "nickname", "profile", "loginto"));
            configuration.setAllowCredentials(true);
            configuration.setMaxAge(3600L); // 1 hour
            source.registerCorsConfiguration("/**", configuration);
            return source;
        }
}
