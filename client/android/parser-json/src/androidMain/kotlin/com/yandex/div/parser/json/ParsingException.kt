package com.yandex.div.parser.json

import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.core.json.JsonElement

@ExperimentalApi
public class ParsingException internal constructor(
    public val reason: ParsingExceptionReason,
    message: String,
    cause: Throwable? = null,
    public val source: JsonElement? = null,
) : RuntimeException(message, cause)
