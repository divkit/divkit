package com.yandex.div.storage

data class DivDataRepositoryResult(
        val resultData: List<DivDataRepository.DivDataWithMeta>,
        val errors: List<DivDataRepositoryException>
) {
    fun addData(data: Collection<DivDataRepository.DivDataWithMeta>) = copy(
            resultData = resultData + data
    )

    companion object {
        val EMPTY = DivDataRepositoryResult(emptyList(), emptyList())
    }
}

data class DivDataRepositoryRemoveResult(
        val ids: Set<String>,
        val errors: List<DivDataRepositoryException>
)
