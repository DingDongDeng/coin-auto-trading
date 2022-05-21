package com.dingdongdeng.coinautotrading.trading.exchange.client;

import com.dingdongdeng.coinautotrading.common.client.util.QueryParamsConverter;
import com.dingdongdeng.coinautotrading.domain.entity.ExchangeKey;
import com.dingdongdeng.coinautotrading.domain.service.ExchangeKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class BinanceFutureTokenGenerator {
    // https://github.com/binance/binance-signature-examples/tree/master/java

    private final String HMAC_SHA256 = "HmacSHA256";

    private final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private final String SECRET_KEY_NAME = "SECRET_KEY";

    private final ExchangeKeyService exchangeKeyService;
    private final QueryParamsConverter queryParamsConverter;


    public String getSignature(Object request, String keyPairId) {
        byte[] hmacSha256 = null;
        try {
            String params = queryParamsConverter.convertStr(request).substring(1); //?name=aaa&age=12 형태에서 ? 제거
            String paramUrl = params.replaceAll("&signature=null","");
            log.info("binance make for signature url : {}",paramUrl);
            List<ExchangeKey> exchangeKeyList = exchangeKeyService.findByPairId(keyPairId);
            String key = getKey(exchangeKeyList, SECRET_KEY_NAME);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), HMAC_SHA256);
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(secretKeySpec);
            hmacSha256 = mac.doFinal(paramUrl.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate hmac-sha256", e);
        }
        return bytesToHex(hmacSha256);
    }

    public String getAccessKey(String keyPairId) {
        List<ExchangeKey> exchangeKeyList = exchangeKeyService.findByPairId(keyPairId);
        return getKey(exchangeKeyList, ACCESS_KEY_NAME);
    }

    //convert byte array to hex string
    private String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0, v; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private String getKey(List<ExchangeKey> exchangeKeyList, String keyName) {
        if (exchangeKeyList.size() != 2) {
            throw new RuntimeException("바이낸스 선물에서 사용할 키가 2개가 아닙니다. \n키 정보를 확인해주세요.");
        }

        List<String> keyNameList = exchangeKeyList.stream()
                .map(ExchangeKey::getName)
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        if (!keyNameList.contains(ACCESS_KEY_NAME)) {
            throw new RuntimeException("ACCESS_KEY가 존재하지 않습니다.");
        }

        if (!keyNameList.contains(SECRET_KEY_NAME)) {
            throw new RuntimeException("SECRET_KEY가 존재하지 않습니다.");
        }

        return exchangeKeyList.stream()
                .filter(key -> key.getName().equalsIgnoreCase(keyName))
                .map(ExchangeKey::getValue)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

}
