package com.dingdongdeng.autotrading

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CoinFuturesApplication

fun main(args: Array<String>) {
    runApplication<CoinFuturesApplication>(*args)
}