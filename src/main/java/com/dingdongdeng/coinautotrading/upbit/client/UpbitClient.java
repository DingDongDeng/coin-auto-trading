package com.dingdongdeng.coinautotrading.upbit.client;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.dingdongdeng.coinautotrading.common.client.Client;
import com.dingdongdeng.coinautotrading.upbit.client.config.UpbitClientResourceProperties;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class UpbitClient extends Client {

    private final UpbitClientResourceProperties properties;

    public UpbitClient(WebClient upbitWebClient, UpbitClientResourceProperties properties) {
        super(upbitWebClient);
        this.properties = properties;
    }

    public String getAccounts() {
        return get("/v1/accounts", String.class, makeToken());
    }

    public String getAvailOrder(String marketId) {
        String queryParam = "market=" + marketId;
        return get("/v1/orders/chance", queryParam, String.class, makeToken(queryParam));
    }

    public String getMarketList(boolean isDetail) {
        String queryParam = "isDetails=" + isDetail;
        return get("/v1/market/all", queryParam, String.class, makeToken(queryParam));
    }

    private HttpHeaders makeToken() {
        HttpHeaders headers = new HttpHeaders();
        String accessKey = properties.getAccessKey();
        String secretKey = properties.getSecretKey();

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String jwtToken = JWT.create()
            .withClaim("access_key", accessKey)
            .withClaim("nonce", UUID.randomUUID().toString())
            .sign(algorithm);
        headers.add("Authorization", "Bearer " + jwtToken);
        return headers;
    }

    private HttpHeaders makeToken(String queryParam) {
        try {
            HttpHeaders headers = new HttpHeaders();
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

            headers.add("Authorization", "Bearer " + jwtToken);
            return headers;
        } catch (Exception e) {
            throw new RuntimeException("fail make upbit token");
        }
    }
}
