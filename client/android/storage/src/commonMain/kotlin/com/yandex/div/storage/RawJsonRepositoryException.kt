package com.yandex.div.storage

import com.yandex.div.storage.database.StorageException
import com.yandex.div.storage.internal.DivStorage
import com.yandex.div.storage.internal.RawJsonRepository

/**
 * Exception that can be gotten from [RawJsonRepository].
 * Combines json parsing exception and exceptions from [DivStorage].
 */
public class RawJsonRepositoryException(
    storageException: StorageException,
) : Exception(storageException.message, storageException) {
    public val jsonId: String? = storageException.cardId
}
