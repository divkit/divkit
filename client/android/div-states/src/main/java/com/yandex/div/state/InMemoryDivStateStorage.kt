package com.yandex.div.state

import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread

/**
 * In memory storage of div states.
 */
class InMemoryDivStateStorage : DivStateStorage {

    override val cache = InMemoryDivStateCache()

    @AnyThread
    override fun preloadState(cardId: String) = Unit

    @WorkerThread
    override fun deleteAllStates() {
        cache.clear()
    }

    @WorkerThread
    override fun deleteStatesExceptGiven(cardIds: List<String>) = Unit
}
