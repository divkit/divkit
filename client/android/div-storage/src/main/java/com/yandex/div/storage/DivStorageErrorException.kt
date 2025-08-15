package com.yandex.div.storage

import com.yandex.div.storage.database.StorageException

/**
 * Exception to be thrown in case of errors during div storage transactions.
 */
class DivStorageErrorException(
        errorMessage: String = "",
        cause: Throwable? = null,
        cardId: String? = null
) : StorageException(getMessage(errorMessage, cardId), cause, cardId) {
    companion object {
        private fun getMessage(errorMessage: String, cardId: String?) =
                errorMessage + (cardId?.let { " Card id: $it" } ?: "")
    }
}
