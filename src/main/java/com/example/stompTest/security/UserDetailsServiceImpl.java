package com.example.stompTest.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.stompTest.model.Member;
import com.example.stompTest.repository.MemberRepository;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository; // 컴포지션

    public UserDetailsServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Member member = memberRepository.findByName(name)
        .orElseThrow(() -> new UsernameNotFoundException("Can't find " + name));
        
        return new UserDetailsImpl(member);
    }
}