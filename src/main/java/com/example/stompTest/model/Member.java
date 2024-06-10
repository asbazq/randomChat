package com.example.stompTest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    public enum MemberRole {
        USER, ADMIN, MEMBERSHIP
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column(nullable = false)
    private String email;

    @Column
    @Enumerated(EnumType.STRING)
    private MemberRole role; 

    @Builder
    public Member(String name, String email, MemberRole role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }
    
    public Member(Long id, Member.MemberRole role) {
        this.id = id;
        this.role = role;
    }
}
