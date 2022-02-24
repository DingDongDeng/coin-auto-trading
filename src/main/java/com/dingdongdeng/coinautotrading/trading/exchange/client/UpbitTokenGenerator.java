package com.dingdongdeng.coinautotrading.trading.exchange.client;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.dingdongdeng.coinautotrading.common.client.util.QueryParamsConverter;
import com.dingdongdeng.coinautotrading.domain.entity.ExchangeKey;
import com.dingdongdeng.coinautotrading.domain.service.ExchangeKeyService;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UpbitTokenGenerator {

    private final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private final String SECRET_KEY_NAME = "SECRET_KEY";

    private final ExchangeKeyService exchangeKeyService;
    private final QueryParamsConverter queryParamsConverter;

    public String makeToken(Object request, String keyPairId) {
        List<ExchangeKey> exchangeKeyList = exchangeKeyService.findByPairId(keyPairId);
        String accessKey = getKey(exchangeKeyList, ACCESS_KEY_NAME);
        String secretKey = getKey(exchangeKeyList, SECRET_KEY_NAME);

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTCreator.Builder jwtBuilder = JWT.create()
            .withClaim("access_key", accessKey)
            .withClaim("nonce", UUID.randomUUID().toString());

        if (Objects.nonNull(request)) {
            jwtBuilder
                .withClaim("query_hash", makeQueryHash(request))
                .withClaim("query_hash_alg", "SHA512");
        }

        return "Bearer " + jwtBuilder.sign(algorithm);
    }

    private String makeQueryHash(Object request) {
        try {
            String params = queryParamsConverter.convertStr(request).substring(1); //?name=aaa&age=12 형태에서 ? 제거
            log.info("upbit query hash by params : {}", params);
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(params.getBytes("UTF-8"));

            return String.format("%0128x", new BigInteger(1, md.digest()));
        } catch (Exception e) {
            throw new RuntimeException("fail make query hash", e);
        }
    }

    private String getKey(List<ExchangeKey> exchangeKeyList, String keyName) {
        return exchangeKeyList.stream()
            .map(ExchangeKey::getName)
            .filter(key -> key.equalsIgnoreCase(keyName))
            .findFirst()
            .orElseThrow(NoSuchElementException::new);
    }
}
