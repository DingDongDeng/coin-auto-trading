package com.dingdongdeng.autotrading.infra.client.upbit

import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime


// 업비트API 초당 호출 제한 : https://docs.upbit.com/docs/user-request-guide
// [주문 요청]       초당 8회
// [주문 요청 외 API] 초당 30회
@Component
class UpbitApiRateLimiter {

    private var countPerSeconds = 0
    private var lastRequestedAt = LocalDateTime.now()

    fun waitForReady() {
        synchronized(this) {
            countPerSeconds++
            val diff = Duration.between(LocalDateTime.now(), lastRequestedAt)
            if (diff.seconds > 1) {
                resetState()
                return
            }

            if (countPerSeconds > 28) {
                resetState()
                Thread.sleep(500)
            }
        }
    }

    private fun resetState() {
        countPerSeconds = 0
        lastRequestedAt = null
    }
}
