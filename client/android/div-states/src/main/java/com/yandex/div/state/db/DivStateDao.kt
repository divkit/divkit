package com.yandex.div.state.db

import androidx.annotation.WorkerThread

internal interface DivStateDao {
    @WorkerThread
    fun getStates(cardId: String): List<PathToState>

    @WorkerThread
    fun updateState(state: DivStateEntity)

    @WorkerThread
    fun deleteAll()

    @WorkerThread
    fun deleteByCardId(cardId: String)

    @WorkerThread
    fun deleteAllExcept(cardIds: List<String>)

    @WorkerThread
    fun deleteModifiedBefore(timestamp: Long)

    @WorkerThread
    fun getRootStateId(cardId: String): String?

    @WorkerThread
    fun deleteCardRootState(cardId: String)
}
