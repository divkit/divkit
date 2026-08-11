package com.yandex.div.compose

/**
 * Exception thrown when an unrecoverable error occurs in DivKit pipeline.
 */
class DivException internal constructor(message: String) : Exception(message)
