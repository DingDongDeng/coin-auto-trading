package com.dingdongdeng.coinautotrading.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        return "occured exception : " + e.getMessage();
    }
}
