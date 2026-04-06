package com.yandex.div.state

import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread

/**
 * Storage of div states
 */
interface DivStateStorage {

    val cache: DivStateCache

    @AnyThread
    fun preloadState(cardId: String)

    @WorkerThread
    fun deleteAllStates()

    @WorkerThread
    fun deleteStatesExceptGiven(cardIds: List<String>)
}
