package com.yandex.div.storage

import com.yandex.div.storage.rawjson.RawJson

data class RawJsonRepositoryResult(
    val resultData: List<RawJson>,
    val errors: List<RawJsonRepositoryException>
) {
    fun addData(data: Collection<RawJson>) = copy(resultData = resultData + data)

    companion object {
        val EMPTY = RawJsonRepositoryResult(emptyList(), emptyList())
    }
}

data class RawJsonRepositoryRemoveResult(
    val ids: Set<String>,
    val errors: List<RawJsonRepositoryException>
)
