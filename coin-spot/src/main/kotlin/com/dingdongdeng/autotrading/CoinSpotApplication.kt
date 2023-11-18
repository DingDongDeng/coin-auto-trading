package com.dingdongdeng.autotrading

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CoinSpotApplication

fun main(args: Array<String>) {
    runApplication<CoinSpotApplication>(*args)
}