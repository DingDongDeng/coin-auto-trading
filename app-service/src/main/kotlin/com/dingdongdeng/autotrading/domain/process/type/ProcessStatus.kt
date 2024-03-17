package com.dingdongdeng.autotrading.domain.process.type

enum class ProcessStatus {
    INIT,
    RUNNING,
    STOPPED,
    TERMINATED,
    FAIL,
    ;

    fun isRunning(): Boolean = this == RUNNING
    fun isFail(): Boolean = this == FAIL
    fun isStop(): Boolean = this == STOPPED
}
