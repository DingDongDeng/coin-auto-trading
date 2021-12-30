package com.dingdongdeng.coinautotrading.common.client.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@ToString
@Getter
public class ApiResponseException extends RuntimeException {

    private HttpStatus httpStatus;
    private String body;

    public ApiResponseException(HttpStatus status, String body, Throwable cause) {
        super(cause);
        this.httpStatus = status;
        this.body = body;
    }

    public ApiResponseException(Throwable cause) {
        super(cause);
    }
}
