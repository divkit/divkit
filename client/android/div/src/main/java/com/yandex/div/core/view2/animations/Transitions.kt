package com.yandex.div.core.view2.animations

import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionSet

internal operator fun TransitionSet.plusAssign(transition: Transition) {
    addTransition(transition)
}

internal operator fun TransitionSet.plusAssign(transitions: Iterable<Transition>) {
    transitions.forEach { transition ->
        addTransition(transition)
    }
}

internal operator fun TransitionSet.minusAssign(transition: Transition) {
    removeTransition(transition)
}

internal operator fun TransitionSet.minusAssign(transitions: Iterable<Transition>) {
    transitions.forEach { transition ->
        removeTransition(transition)
    }
}

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
