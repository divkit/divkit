package com.yandex.div.json

import com.yandex.div.serialization.ParsingContext

@Deprecated(
    message = "Use ParsingContext instead.",
    replaceWith = ReplaceWith("ParsingContext", "com.yandex.div.serialization.ParsingContext")
)
interface ParsingEnvironment : ParsingContext
