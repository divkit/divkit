package com.yandex.div.compose.scroll

import com.yandex.div.compose.dagger.DivViewScope
import javax.inject.Inject

/**
 * Keeps all currently composed targets for each id, including duplicates.
 * Callers decide how to handle a missing or ambiguous target.
 */
@DivViewScope
internal class ScrollTargetStorage @Inject constructor() {

    private val targets = mutableMapOf<String, MutableSet<ScrollTarget>>()

    fun register(id: String, target: ScrollTarget) {
        targets.getOrPut(id) { mutableSetOf() }.add(target)
    }

    fun unregister(id: String, target: ScrollTarget) {
        targets[id]?.let {
            it.remove(target)
            if (it.isEmpty()) targets.remove(id)
        }
    }

    fun get(id: String): Set<ScrollTarget> = targets[id].orEmpty()
}
