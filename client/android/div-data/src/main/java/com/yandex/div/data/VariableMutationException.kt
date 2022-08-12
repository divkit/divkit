package com.yandex.div.data

class VariableMutationException(
    message: String? = null,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
