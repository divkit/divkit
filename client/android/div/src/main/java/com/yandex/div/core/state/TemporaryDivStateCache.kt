package com.yandex.div.core.state

import androidx.annotation.AnyThread
import com.yandex.div.core.dagger.DivScope
import java.util.Collections
import javax.inject.Inject

@DivScope
class TemporaryDivStateCache @Inject constructor() {
    private val temporaryCache = mutableMapOf<String, MutableMap<String, String>>()

    @AnyThread
    fun getState(cardId: String, path: String) = synchronized(temporaryCache) {
        temporaryCache[cardId]?.get(path)
    }

    @AnyThread
    fun resetCard(cardId: String) = synchronized(temporaryCache) {
        temporaryCache.remove(cardId)
    }

    @AnyThread
    fun putState(cardId: String, path: String, stateId: String) {
        synchronized(temporaryCache) {
            temporaryCache.getOrPut(cardId) { mutableMapOf() }[path] = stateId
        }
    }

    @AnyThread
    fun putRootState(cardId: String, stateId: String) {
        putState(cardId, "/", stateId)
    }
}
