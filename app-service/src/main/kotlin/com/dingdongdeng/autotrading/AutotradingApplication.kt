package com.dingdongdeng.autotrading

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AutotradingApplication

fun main(args: Array<String>) {
    runApplication<AutotradingApplication>(*args)
    //FIXME 인텔리제이 2023.3.1 버전 이상으로 업데이트
}