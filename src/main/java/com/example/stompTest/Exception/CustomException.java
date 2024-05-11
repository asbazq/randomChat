package com.example.stompTest.Exception;

import org.springframework.beans.factory.annotation.Autowired;

public class CustomException extends RuntimeException {
    private ErrorCode errorCode;

    @Autowired
    public CustomException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode, String msg) {
        this.errorCode = errorCode;
    }
}
