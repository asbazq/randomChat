package com.example.stompTest.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.example.stompTest.model.Member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailsImpl implements UserDetails, OAuth2User {

    private Member member; // 컴포지션

    private Map<String,Object> attributes; // OAuth2

    public UserDetailsImpl(Member member) {
        this.member = member;
    }

    public Member getmember() {
        return member;
    }

    public UserDetailsImpl(Member member, Map<String,Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    // OAuth2 오버라이드 메소드
   @Override
   public Map<String, Object> getAttributes() {
       return attributes;
   }

    // 해당 Users 의 권한의 리턴
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // users.getRole()을 Collection 타입으로 받기위해
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return String.valueOf(member.getRole());
            }
        });
        // Collection<GrantedAuthority> collection = new ArrayList<>();
        // member.getRole().forEach(r-> { // r : return
        // collection.add(()-> String.valueOf(r));
        // });
        return collection;
        // 권한이 존재안할 시
        // return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        // return member.getPassword();
        return null;
    }

    @Override
    public String getUsername() {
        return member.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    }
}