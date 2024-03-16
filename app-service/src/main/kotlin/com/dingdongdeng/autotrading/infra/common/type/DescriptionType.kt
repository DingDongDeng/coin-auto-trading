package com.dingdongdeng.autotrading.infra.common.type

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty

// https://www.baeldung.com/jackson-serialize-enums
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
interface DescriptionType {
    val desc: String

    @JsonProperty
    fun getType(): String = if (this is Enum<*>) this.name else ""
}