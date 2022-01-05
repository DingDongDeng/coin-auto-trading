package com.dingdongdeng.coinautotrading.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderState {
    WAIT("미체결"),
    DONE("체결 완료"),
    CANCELED("주문 취소");
    private String desc;
}
