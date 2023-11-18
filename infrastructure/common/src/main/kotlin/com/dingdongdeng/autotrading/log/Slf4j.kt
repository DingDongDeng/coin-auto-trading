package com.dingdongdeng.autotrading.log

import mu.KLogger
import mu.KotlinLogging

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Slf4j {
    companion object {
        inline val <reified T> T.log: KLogger
            get() = KotlinLogging.logger(T::class.java.name)
    }
}
