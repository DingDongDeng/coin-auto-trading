package com.dingdongdeng.autotrading.domain.process.type

import com.dingdongdeng.autotrading.infra.common.type.DescriptionType

enum class ProcessStatus(
    override val desc: String,
) : DescriptionType {
    INIT("초기화"),
    RUNNING("진행 중"),
    STOPPED("정지"),
    TERMINATED("제거"),
    FAIL("실패"),
    ;


    fun isRunning(): Boolean = this == RUNNING
    fun isFail(): Boolean = this == FAIL
    fun isStop(): Boolean = this == STOPPED
}
