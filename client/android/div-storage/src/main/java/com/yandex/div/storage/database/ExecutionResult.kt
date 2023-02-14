package com.yandex.div.storage.database

import com.yandex.div.core.annotations.Mockable

@Mockable
class ExecutionResult(
        val errors: List<StorageException> = emptyList()
) {
    val isSuccessful: Boolean
        get() = errors.isEmpty()
}
