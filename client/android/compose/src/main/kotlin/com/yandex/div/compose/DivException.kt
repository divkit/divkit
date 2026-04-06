package com.yandex.div.compose

import com.yandex.div.core.annotations.ExperimentalApi

/**
 * Exception thrown when an unrecoverable error occurs in DivKit pipeline.
 */
@ExperimentalApi
class DivException internal constructor(message: String) : Exception(message)
