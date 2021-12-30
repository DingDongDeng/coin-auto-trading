package com.dingdongdeng.coinautotrading.autotrading.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.dingdongdeng.coinautotrading.autotrading.controller")
public class ControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String aa(Exception e) {
        log.error(e.getMessage(), e);
        return "occured exception : " + e.getMessage();
    }
}
