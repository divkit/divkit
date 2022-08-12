// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivInfinityCount internal constructor(
) : DivCount() {

    @JsonProperty("type") override val type = "infinity"
}

fun <T> TemplateContext<T>.divInfinityCount(): LiteralProperty<DivInfinityCount> {
    return value(DivInfinityCount(
    ))
}

fun CardContext.divInfinityCount(): DivInfinityCount {
    return DivInfinityCount(
    )
}
