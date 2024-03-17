package com.dingdongdeng.autotrading.test

object TestUtils {
}

fun waitByCondition(condition: () -> Boolean) {
    val timeoutMillis = 90000L // 대기 타임아웃 시간 (60초)
    val intervalMillis = 1000L // 확인 간격 (1초)

    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime < timeoutMillis) {
        if (condition()) {
            break // 조건이 충족되면 루프를 탈출
        }
        Thread.sleep(intervalMillis) // 일정 간격으로 대기
    }
}