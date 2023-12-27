package com.dingdongdeng.autotrading.infra.client.upbit

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.dingdongdeng.autotrading.infra.client.common.QueryParamsConverter
import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.security.MessageDigest
import java.util.UUID

@Component
class UpbitTokenGenerator(
    private val queryParamsConverter: QueryParamsConverter,
) {

    fun makeToken(request: Any? = null, accessKey: String, secretKey: String): String {
        val algorithm = Algorithm.HMAC256(secretKey)
        val jwtBuilder = JWT.create()
            .withClaim("access_key", accessKey)
            .withClaim("nonce", UUID.randomUUID().toString())
        if (request != null) {
            jwtBuilder
                .withClaim("query_hash", makeQueryHash(request))
                .withClaim("query_hash_alg", "SHA512")
        }
        return "Bearer " + jwtBuilder.sign(algorithm)
    }

    private fun makeQueryHash(request: Any): String {
        return try {
            val params = queryParamsConverter.convertStr(request).substring(1) //?name=aaa&age=12 형태에서 ? 제거
            log.info("upbit query hash by params : {}", params)
            val md = MessageDigest.getInstance("SHA-512")
            md.update(params.toByteArray(charset("UTF-8")))
            String.format("%0128x", BigInteger(1, md.digest()))
        } catch (e: Exception) {
            throw RuntimeException("fail make query hash", e)
        }
    }
}
