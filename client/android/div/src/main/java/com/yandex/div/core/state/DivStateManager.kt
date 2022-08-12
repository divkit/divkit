package com.yandex.div.core.state

import androidx.annotation.AnyThread
import androidx.collection.ArrayMap
import com.yandex.div.DivDataTag
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.state.DivStateCache
import javax.inject.Inject

/**
 * Manipulates application's div data state change and retrieval.
 */
@DivScope
@AnyThread
class DivStateManager @Inject internal constructor(
    private val cache: DivStateCache,
    private val temporaryCache: TemporaryDivStateCache
) {
    private val states = ArrayMap<DivDataTag, DivViewState>()

    fun getState(tag: DivDataTag): DivViewState? = synchronized(states) {
        var state = states[tag]
        if (state == null) {
            state = cache.getRootState(tag.id)?.toInt()?.let { DivViewState(it) }
            states[tag] = state
        }
        return state
    }

    fun updateState(tag: DivDataTag, stateId: Int, temporary: Boolean) {
        if (DivDataTag.INVALID != tag) {
            synchronized(states) {
                val state = getState(tag)
                states[tag] = if (state == null) DivViewState(stateId) else DivViewState(
                    stateId,
                    state.blockStates
                )
                temporaryCache.putRootState(tag.id, stateId.toString())
                if (!temporary) {
                    cache.putRootState(tag.id, stateId.toString())
                }
            }
        }
    }

    fun updateStates(cardId: String, divStatePath: DivStatePath, temporary: Boolean) {
        val path = divStatePath.pathToLastState
        val stateId = divStatePath.lastStateId
        if (path != null && stateId != null) {
            synchronized(states) {
                temporaryCache.putState(cardId, path, stateId)
                if (!temporary) {
                    cache.putState(cardId, path, stateId)
                }
            }
        }
    }
}
