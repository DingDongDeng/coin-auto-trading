package com.dingdongdeng.coinautotrading.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderState {
  WAIT("체결 대기"),
  WATCH("예약 주문 대기"),
  DONE("체결 완료"),
  CANCEL("주문 취소"),
  ;
  private String desc;
}
