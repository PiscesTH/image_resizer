package com.th.image_resizer.exception;


import org.springframework.http.HttpStatus;

public interface ErrorCode {
    String name();  //enum 의 이름을 가져올 때 사용. enum에 기본적으로 구현되어 있음.
    HttpStatus getHttpStatus();
    String getMessage();
}
