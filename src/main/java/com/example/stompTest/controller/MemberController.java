package com.example.stompTest.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.stompTest.Exception.CustomException;
import com.example.stompTest.Exception.ErrorCode;
import com.example.stompTest.model.Member;
import com.example.stompTest.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Controller
public class MemberController {

    private final MemberRepository memberRepository;

    // 회원가입 test
    @GetMapping("/api/join")
    public void join() {
        System.out.println("test");
        Member member = new Member("test", "test", Member.MemberRole.USER);
        memberRepository.save(member);
        log.info("Member saved with username: {}", member.getName());
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/nameChange")
    public void nameChange(@AuthenticationPrincipal OAuth2User oAuth2User,
                                            @RequestBody String name) {
        String email = oAuth2User.getAttribute("email");
        Member member = memberRepository.findByEmail(email).orElseThrow(
            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        member.setName(name);
        memberRepository.save(member);
    }
    
}
