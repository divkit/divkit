package com.yandex.div.core.utils

/**
 * Allows to smartcast var's on the fly.
 */
inline fun <reified T> Any?.ifIs(block: (T) -> Unit) {
    if (this is T) {
        block(this)
    }
}

/**
 * Inverted version of [ifIs].
 */
inline fun <reified T> Any?.ifIsNot(block: (Any?) -> Unit) {
    if (this !is T) {
        block(this)
    }
}
