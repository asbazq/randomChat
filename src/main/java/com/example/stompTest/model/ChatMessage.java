package com.example.stompTest.model;

<<<<<<< HEAD
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
=======
>>>>>>> a9d529bb42d525b8b869238cddc213d7e57b123f
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
<<<<<<< HEAD
@Entity
public class ChatMessage {
    // 메시지 타입 : 입장, 채팅
    public enum MessageType {
        ENTER, QUIT, TALK
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room")
    private ChatRoom room;

    private MessageType type; 

    @ManyToOne
    @JoinColumn(name = "member")
    private Member member; 

    @Column
    private String message; 

    @Column
    private Long userCount;

    @Builder
    public ChatMessage(MessageType type, ChatRoom room, Member member, String message, Long userCount) {
        this.type = type;
        this.room = room;
        this.member = member;
        this.message = message;
        this.userCount = userCount;
    }
=======
public class ChatMessage {
    // 메시지 타입 : 입장, 채팅
    public enum MessageType {
        ENTER, TALK
    }
    private MessageType type; // 메시지 타입
    private String roomId; // 방번호
    private String sender; // 메시지 보낸사람
    private String message; // 메시지
>>>>>>> a9d529bb42d525b8b869238cddc213d7e57b123f
}

