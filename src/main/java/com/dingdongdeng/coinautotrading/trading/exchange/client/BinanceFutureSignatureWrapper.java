package com.dingdongdeng.coinautotrading.trading.exchange.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class BinanceFutureSignatureWrapper {

    private final ObjectMapper objectMapper;
    private final String SIGNATURE_KEY = "signature";

    public Map<String, Object> getSignatureRequest(Object request, String token){
        Map<String, Object> signatureRequest = objectMapper.convertValue(request, new TypeReference<>() {
        });
        signatureRequest.put(SIGNATURE_KEY, token);
        return signatureRequest;
    }
}
