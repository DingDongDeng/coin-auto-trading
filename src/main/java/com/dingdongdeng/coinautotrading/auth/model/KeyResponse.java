package com.dingdongdeng.coinautotrading.auth.model;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeyResponse {

    private String pairId;
    private CoinExchangeType coinExchangeType;
    private String name;
}
