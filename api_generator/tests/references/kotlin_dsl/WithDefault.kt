// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class WithDefault internal constructor(
) : EnumWithDefaultType {

    @JsonProperty("type") override val type = "default"
}

fun <T> TemplateContext<T>.withDefault(): LiteralProperty<WithDefault> {
    return value(WithDefault(
    ))
}

fun CardContext.withDefault(): WithDefault {
    return WithDefault(
    )
}
