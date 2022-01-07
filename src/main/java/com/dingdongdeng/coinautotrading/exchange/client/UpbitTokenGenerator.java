package com.dingdongdeng.coinautotrading.exchange.client;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.dingdongdeng.coinautotrading.common.client.util.QueryParamsConverter;
import com.dingdongdeng.coinautotrading.exchange.client.config.UpbitClientResourceProperties;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UpbitTokenGenerator {

    private final UpbitClientResourceProperties properties;
    private final QueryParamsConverter queryParamsConverter;

    public String makeToken(Object request) {
        String accessKey = properties.getAccessKey();
        String secretKey = properties.getSecretKey();

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
            log.info("params ### {}", params);
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(params.getBytes("UTF-8"));

            return String.format("%0128x", new BigInteger(1, md.digest()));
        } catch (Exception e) {
            throw new RuntimeException("fail make query hash", e);
        }
    }
}
