package com.dingdongdeng.coinautotrading.exchange.upbit.client;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.dingdongdeng.coinautotrading.common.client.util.QueryParamsConverter;
import com.dingdongdeng.coinautotrading.exchange.upbit.client.config.UpbitClientResourceProperties;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UpbitTokenGenerator {

    private final UpbitClientResourceProperties properties;
    private final QueryParamsConverter queryParamsConverter;

    public String makeToken() {
        String accessKey = properties.getAccessKey();
        String secretKey = properties.getSecretKey();

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String jwtToken = JWT.create()
            .withClaim("access_key", accessKey)
            .withClaim("nonce", UUID.randomUUID().toString())
            .sign(algorithm);
        return "Bearer " + jwtToken;
    }

    public String makeToken(Object request) {
        try {
            String params = queryParamsConverter.convert(request).entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue().get(0))
                .collect(Collectors.joining("&"));

            String accessKey = properties.getAccessKey();
            String secretKey = properties.getSecretKey();

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(params.getBytes("UTF-8"));

            String queryHash = String.format("%0128x", new BigInteger(1, md.digest()));

            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            String jwtToken = JWT.create()
                .withClaim("access_key", accessKey)
                .withClaim("nonce", UUID.randomUUID().toString())
                .withClaim("query_hash", queryHash)
                .withClaim("query_hash_alg", "SHA512")
                .sign(algorithm);

            return "Bearer " + jwtToken;
        } catch (Exception e) {
            throw new RuntimeException("fail make upbit token");
        }
    }
}
