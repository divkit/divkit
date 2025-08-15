// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class WithoutDefault internal constructor(
) : EnumWithDefaultType {

    @JsonProperty("type") override val type = "non_default"
}

fun <T> TemplateContext<T>.withoutDefault(): LiteralProperty<WithoutDefault> {
    return value(WithoutDefault(
    ))
}

fun CardContext.withoutDefault(): WithoutDefault {
    return WithoutDefault(
    )
}
