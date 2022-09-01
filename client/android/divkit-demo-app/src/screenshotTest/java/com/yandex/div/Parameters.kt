package com.yandex.div

internal fun List<String>.withEscapedParameter(): List<Array<String>> {
    return map { case ->
        arrayOf(case, case.replace('/', '_').replace(' ', '_'))
    }
}
