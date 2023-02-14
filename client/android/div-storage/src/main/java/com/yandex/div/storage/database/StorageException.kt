package com.yandex.div.storage.database

abstract class StorageException(
        message: String? = null,
        cause: Throwable? = null,
        val cardId: String? = null
) : Exception(message, cause)
