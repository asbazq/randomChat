package com.example.stompTest.Exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
    COMPLETED_OK(HttpStatus.OK, "수행 완료."),
    FILE_CREATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 생성 중 오류 발생."),
    CHMOD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 권한 설정 오류."),
    PROCESS_EXECUTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "프로세스 실행 오류."),
    FILE_DELETION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제 오류."),
    USERNAME_LENGTH(HttpStatus.BAD_REQUEST, "ID는 4자 이상으로 설정해주세요."),
    USERNAME_EMAIL(HttpStatus.BAD_REQUEST, "ID는 이메일 형식으로 입력해주세요."),
    PASSWORD_CONTAIN_USERNAME(HttpStatus.BAD_REQUEST, "비밀번호에 ID를 포함할 수 없습니다."),
    PASSWORD_LENGTH(HttpStatus.BAD_REQUEST, "비밀번호는 8자 이상으로 설정해주세요."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    MEMBER_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "최대 인원 수 초과."),
    EMPTY_CONTENT(HttpStatus.BAD_REQUEST, "필수 입력값이 누락되었습니다."),
    
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    TOKEN_UNSUPPORTED(HttpStatus.BAD_REQUEST, "지원하지 않는 토큰 형식입니다."),
    TOKEN_MALFORMED(HttpStatus.BAD_REQUEST, "토큰이 올바르지 않습니다."),
    TOKEN_SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, "토큰 서명 오류입니다."),
    
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."),
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방 정보를 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리 정보를 찾을 수 없습니다."),
    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    LANGUAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "지원하는 언어 정보를 찾을 수 없습니다."),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "리소스가 중복되었습니다."),
    
    SELF_REGISTRATION_DENIED(HttpStatus.BAD_REQUEST, "자기 자신을 등록할 수 없습니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    REQUEST_ACCEPTED(HttpStatus.ACCEPTED, "요청이 수락되었습니다."),
    
    CHATROOM_UNSUPPORED(HttpStatus.BAD_REQUEST, "지원하지 않는 값입니다."),
    CHATROOM_CREATION_FAILED(HttpStatus.BAD_REQUEST, "채팅방 생성 실패."),
    CHATROOM_DUPLICATE(HttpStatus.BAD_REQUEST, "채팅방이 이미 존재합니다."),
    MESSAGE_SEND_FAILED(HttpStatus.BAD_REQUEST, "메시지 전송 실패.");

    private final HttpStatus httpStatus;
    private final String errorMessage;

    ErrorCode(HttpStatus httpStatus, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }
}
