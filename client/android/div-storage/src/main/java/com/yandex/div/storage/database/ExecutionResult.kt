package com.yandex.div.storage.database

class ExecutionResult(
    val errors: List<StorageException> = emptyList()
) {
    val isSuccessful: Boolean
        get() = errors.isEmpty()
}
