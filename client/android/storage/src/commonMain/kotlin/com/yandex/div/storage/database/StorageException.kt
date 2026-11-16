package com.yandex.div.storage.database


public abstract class StorageException(
        message: String? = null,
        cause: Throwable? = null,
        public val cardId: String? = null
) : Exception(message, cause)
