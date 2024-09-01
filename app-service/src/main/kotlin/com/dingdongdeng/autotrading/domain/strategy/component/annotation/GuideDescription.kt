package com.dingdongdeng.autotrading.domain.strategy.component.annotation


@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class GuideDescription(
    val desc: String = ""
)
