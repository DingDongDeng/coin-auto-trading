package com.dingdongdeng.coinautotrading.auth.model;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
public class KeyPairRegisterRequest {

    @NotNull
    private CoinExchangeType coinExchangeType;

    @Valid
    @NotNull
    private List<Key> keyList;
}
