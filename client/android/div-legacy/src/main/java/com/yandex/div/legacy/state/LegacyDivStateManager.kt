package com.yandex.div.legacy.state

import androidx.annotation.MainThread
import androidx.collection.ArrayMap
import com.yandex.div.DivDataTag
import com.yandex.div.legacy.dagger.DivLegacyScope
import javax.inject.Inject

/**
 * Manipulates application's div
 * data state change and retrieval.
 */
@DivLegacyScope
@MainThread
class LegacyDivStateManager @Inject internal constructor(
    private val cache: LegacyDivStateCache,
) {
    private val states = ArrayMap<DivDataTag, LegacyDivViewState>()

    fun getState(tag: DivDataTag): LegacyDivViewState? {
        var state = states[tag]
        if (state == null) {
            state = cache.getRootState(tag.id)?.toInt()?.let { LegacyDivViewState(it) }
            states[tag] = state
        }
        return state
    }

    fun updateState(tag: DivDataTag, stateId: Int) {
        if (DivDataTag.INVALID != tag) {
            val state = getState(tag)
            states[tag] = if (state == null) {
                LegacyDivViewState(stateId)
            } else {
                LegacyDivViewState(stateId, state.blockStates)
            }
            cache.putRootState(tag.id, stateId.toString())
        }
    }
}
