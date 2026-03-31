package com.yandex.div.compose

import com.yandex.div.core.annotations.PublicApi

/**
 * Exception thrown when an unrecoverable error occurs in DivKit pipeline.
 */
@PublicApi
class DivException(message: String) : Exception(message)
