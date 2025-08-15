package com.yandex.div.state

import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import com.yandex.div.core.annotations.PublicApi

/**
 * Storage of div states
 */
@PublicApi
interface DivStateStorage {

    val cache: DivStateCache

    @AnyThread
    fun preloadState(cardId: String)

    @WorkerThread
    fun deleteAllStates()

    @WorkerThread
    fun deleteStatesExceptGiven(cardIds: List<String>)
}
