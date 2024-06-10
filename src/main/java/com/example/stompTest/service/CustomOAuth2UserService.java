package com.example.stompTest.service;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Service;

import com.example.stompTest.model.Member;
import com.example.stompTest.model.Member.MemberRole;
import com.example.stompTest.repository.MemberRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomOAuth2UserService extends OidcUserService {

    private final MemberRepository memberRepository;
    private final HttpServletRequest request;

    public CustomOAuth2UserService(MemberRepository memberRepository, HttpServletRequest request) {
        this.memberRepository = memberRepository;
        this.request = request;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService.loadUser() called");
        OidcUser oidcUser;
        try {
            oidcUser = super.loadUser(userRequest);
        } catch (OAuth2AuthenticationException e) {
            log.error("Error loading user: ", e);
            throw e;
        }

        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        String registrationId = clientRegistration.getRegistrationId();
        log.info("registrationId : " + registrationId);

        String accessToken = userRequest.getAccessToken().getTokenValue();
        log.info("OAuth2 Access Token: " + accessToken);

        Map<String, Object> claims = new HashMap<>(oidcUser.getClaims());

        log.info("User claims: {}", claims);
        
        if ("kakao".equals(registrationId)) {
            String email = (String) claims.get("email");
            log.info("User email : {}", email);
            String name = UUID.randomUUID().toString();
            Member member = memberRepository.findByEmail(email).orElseGet( 
                () -> {
                    Member newMember = Member.builder()
                                            .email(email)
                                            .name(name)
                                            .role(MemberRole.USER)
                                            .build();
                    
                    return  memberRepository.save(newMember);
                });
                log.info("Member details: {}", member);
                claims.put("name", member.getName());

        }
        // Set<SimpleGrantedAuthority> mappedAuthorities = oidcUser.getAuthorities().stream()
        //     .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
        //     .collect(Collectors.toSet());


        OidcUserInfo oidcUserInfo = new OidcUserInfo(claims);

        DefaultOidcUser defaultOidcUser = new DefaultOidcUser(
            oidcUser.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toSet()),
            oidcUser.getIdToken(),
            oidcUserInfo
        );

        log.info("Returning OidcUser: {}", defaultOidcUser);

        request.getSession().setAttribute("defaultOidcUser", defaultOidcUser);

        return defaultOidcUser;

        // return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("USER")),
        //                              oAuth2User.getAttributes(),"id");
        
    }
}
