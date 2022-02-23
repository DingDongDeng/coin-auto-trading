package com.dingdongdeng.coinautotrading.auth.model;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import java.util.List;
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
public class KeyRegisterRequest {

    @NotNull
    private CoinExchangeType coinExchangeType;
    @NotNull
    private List<KeyPair> keyPairList;

    @ToString
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class KeyPair {

        private String keyName;
        private String value;
    }
}
