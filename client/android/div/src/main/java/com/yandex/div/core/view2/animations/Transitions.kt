package com.yandex.div.core.view2.animations

import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionSet
import androidx.transition.Visibility
import com.yandex.div.internal.core.DivItemBuilderResult
import com.yandex.div2.DivBase

internal inline fun TransitionSet.forEach(crossinline block: (Transition) -> Unit) {
    val count = transitionCount
    for (index in 0 until count) {
        val transition = getTransitionAt(index)
        if (transition != null) {
            block(transition)
        }
    }
}

internal inline fun Transition.doOnEnd(crossinline action: () -> Unit) {
    addListener(object : TransitionListenerAdapter() {
        override fun onTransitionEnd(transition: Transition) {
            action()
            removeListener(this)
        }
    })
}

internal fun Transition.enumerateTargetIds(): List<Int> {
    val targetIds = mutableSetOf<Int>()
    val queue = ArrayDeque<Transition>()
    queue.addLast(this)
    while (queue.isNotEmpty()) {
        val transition = queue.removeFirst()
        if (transition is TransitionSet) {
            transition.forEach { childTransition ->
                queue.addLast(childTransition)
            }
        }
        targetIds.addAll(transition.targetIds)
    }
    return targetIds.toList()
}

internal inline fun DivItemBuilderResult.toTransitionData(
    isIncoming: Boolean,
    allowsTransitions: (DivBase) -> Boolean,
): TransitionData? {
    val div = div.value()

    if (!allowsTransitions(div)) return null

    val id = div.id ?: return null

    if (isIncoming) {
        return div.transitionIn?.let {
            val transition = DivTransition.Appearance(it, Visibility.MODE_IN)
            TransitionData(id, listOf(transition), expressionResolver)
        }
    }

    val transitionChange = div.transitionChange?.let { DivTransition.Change(it) }
    val transitionOut = div.transitionOut?.let { DivTransition.Appearance(it, Visibility.MODE_OUT) }

    if (transitionChange == null && transitionOut == null) return null
    return TransitionData(id, listOfNotNull(transitionChange, transitionOut), expressionResolver)
}
