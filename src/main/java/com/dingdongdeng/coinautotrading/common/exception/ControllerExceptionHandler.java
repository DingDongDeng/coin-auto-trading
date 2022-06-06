package com.dingdongdeng.coinautotrading.common.exception;

import com.dingdongdeng.coinautotrading.common.slack.SlackSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ControllerExceptionHandler {

  private final SlackSender slackSender;

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // fixme status 처리 고도화 필요
  @ExceptionHandler(Exception.class)
  public String exceptionHandler(Exception e) {
    log.error(e.getMessage(), e);
    slackSender.send(e);
    return "occured exception : " + e.getMessage();
  }
}
