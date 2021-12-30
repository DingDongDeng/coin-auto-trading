package com.dingdongdeng.coinautotrading.upbit.client;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.dingdongdeng.coinautotrading.upbit.client.config.UpbitClientResourceProperties;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UpbitClientTokenGenerator {

    private final UpbitClientResourceProperties properties;

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

    public String makeToken(String queryParam) {
        try {
            String accessKey = properties.getAccessKey();
            String secretKey = properties.getSecretKey();

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(queryParam.getBytes("UTF-8"));

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
