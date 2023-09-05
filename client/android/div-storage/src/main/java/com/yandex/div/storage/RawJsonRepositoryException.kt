package com.yandex.div.storage

/**
 * Exception that can be gotten from [RawJsonRepository].
 * Combines json parsing exception and exceptions from [DivStorage].
 */
class RawJsonRepositoryException(
    storageException: com.yandex.div.storage.database.StorageException,
) : Exception(storageException.message, storageException) {
    val jsonId: String? = storageException.cardId
}
