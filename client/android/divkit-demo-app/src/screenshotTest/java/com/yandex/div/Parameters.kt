package com.yandex.div

internal fun List<String>.withEscapedParameter(prefix: String = ""): List<Array<String>> {
    return map { case ->
        arrayOf(
            case,
            case.removePrefix(prefix).replace('/', '_').replace(' ', '_')
        )
    }
}
