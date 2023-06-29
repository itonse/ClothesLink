package com.itonse.clotheslink.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    ALREADY_REGISTERED_SELLER(HttpStatus.BAD_REQUEST, "이미 가입된 이메일 입니다."),
    LOGIN_FAIL(HttpStatus.BAD_REQUEST, "일치하는 회원정보가 없습니다."),
    NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "회원 정보를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}