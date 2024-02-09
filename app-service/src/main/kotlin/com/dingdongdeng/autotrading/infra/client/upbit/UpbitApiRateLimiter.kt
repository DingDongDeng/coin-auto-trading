package com.dingdongdeng.autotrading.infra.client.upbit

import com.dingdongdeng.autotrading.infra.common.log.Slf4j.Companion.log
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime


// 업비트API 초당 호출 제한 : https://docs.upbit.com/docs/user-request-guide
// [주문 요청]       초당 8회
// [주문 요청 외 API] 초당 30회
// 초당 10회 (종목, 캔들, 체결, 티커, 호가별 각각 적용)
@Component
class UpbitApiRateLimiter {

    //FIXME keyPairId 단위로 해야함..

    private var countPerSeconds = 0
    private var lastRequestedAt = LocalDateTime.now()

    fun waitForReady() {
        synchronized(this) {
            // API가 1초안에 여러번 호출 되었는지 확인
            val diff = Duration.between(lastRequestedAt, LocalDateTime.now())
            if (diff.seconds > 1) {
                resetState()
                countRequest()
                return
            }

            // API가 초당 N번 호출되었다면 sleep
            if (countPerSeconds > 4) {
                Thread.sleep(1000)
                log.warn("업비트API 호출량 조절을 위해 sleep 동작")
                resetState()
                countRequest()
                return
            }

            // API 호출 카운트
            countRequest()
        }
    }

    private fun countRequest() {
        countPerSeconds++
        lastRequestedAt = LocalDateTime.now()
    }
    private fun resetState() {
        countPerSeconds = 0
        lastRequestedAt = LocalDateTime.now()
    }
}
