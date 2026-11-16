package com.yandex.div.storage.database


public class ExecutionResult(
    public val errors: List<StorageException> = emptyList()
) {
    public val isSuccessful: Boolean
        get() = errors.isEmpty()
}
