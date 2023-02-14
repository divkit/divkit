package com.yandex.div.storage

import com.yandex.div.json.ParsingException

/**
 * Exception that can be gotten from [DivDataRepository].
 * Combines json parsing exception and exceptions from [DivStorage].
 */
sealed class DivDataRepositoryException(
        message: String? = null,
        cause: Throwable? = null,
        val cardId: String? = null,
): Exception(message, cause) {

    class JsonParsingException(message: String? = null,
                               cause: ParsingException? = null,
                               cardId: String,
    ): DivDataRepositoryException(message, cause, cardId)

    class StorageException(storageException: com.yandex.div.storage.database.StorageException)
        : DivDataRepositoryException(
            message = storageException.message,
            cause = storageException,
            cardId = storageException.cardId,
        )
}
