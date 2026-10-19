package com.yandex.div.parser.json.util

internal fun String.reference(): String {
    return "$$this"
}
