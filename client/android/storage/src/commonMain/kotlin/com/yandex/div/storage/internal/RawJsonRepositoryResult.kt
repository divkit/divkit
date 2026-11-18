package com.yandex.div.storage.internal

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.storage.RawJsonRepositoryException
import com.yandex.div.storage.internal.rawjson.RawJson

@InternalApi
public data class RawJsonRepositoryResult<T : Any>(
    public val resultData: List<RawJson<T>>,
    public val errors: List<RawJsonRepositoryException>
) {
    public fun addData(data: Collection<RawJson<T>>): RawJsonRepositoryResult<T> =
        copy(resultData = resultData + data)

    public companion object {
        public fun <T : Any> empty(): RawJsonRepositoryResult<T> =
            RawJsonRepositoryResult(emptyList(), emptyList())
    }
}

@InternalApi
public data class RawJsonRepositoryRemoveResult(
    public val ids: Set<String>,
    public val errors: List<RawJsonRepositoryException>
)
