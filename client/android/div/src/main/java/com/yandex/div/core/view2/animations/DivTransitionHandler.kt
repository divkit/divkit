package com.yandex.div.core.view2.animations

import android.view.View
import androidx.transition.Transition
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.yandex.div.core.view2.Div2View

internal class DivTransitionHandler(
    private val divView: Div2View
) {
    private var pendingTransitions = mutableListOf<TransitionData>()
    private var activeTransitions = mutableListOf<TransitionData>()

    private var posted = false

    private fun postTransitions() {
        if (!posted) {
            posted = true

            divView.post {
                beginDelayedTransitions()

                posted = false
            }
        }
    }

    fun putTransition(transition: Transition, view: View, changeType: ChangeType.Visibility) {
        pendingTransitions
            .add(TransitionData(transition, view, mutableListOf(changeType), mutableListOf()))

        postTransitions()
    }

    fun getLastChange(target: View): ChangeType.Visibility? {
        val pendingTransition = pendingTransitions.getChange(target).lastOrNull()

        if (pendingTransition != null) return pendingTransition

        val activeTransition = activeTransitions.getChange(target).lastOrNull()

        if (activeTransition != null) return activeTransition

        return null
    }

    private fun List<TransitionData>.getChange(target: View): List<ChangeType.Visibility> {
        return mapNotNull { transitionData ->
            if (transitionData.target == target) transitionData.savedChanges.lastOrNull() else null
        }
    }

    private fun beginDelayedTransitions() {
        val transitionSet = TransitionSet()

        pendingTransitions.forEach { transitionData ->
            transitionSet.addTransition(transitionData.transition)
        }

        transitionSet.doOnEnd {
            activeTransitions.clear()
        }

        TransitionManager.beginDelayedTransition(divView, transitionSet)

        pendingTransitions.forEach { transitionData ->
            transitionData.changes.forEach { change ->
                change.apply(transitionData.target)

                transitionData.savedChanges.add(change)
            }
        }

        activeTransitions.clear()
        activeTransitions.addAll(pendingTransitions)
        pendingTransitions.clear()
    }

    private class TransitionData(
        val transition: Transition,
        val target: View,
        val changes: MutableList<ChangeType.Visibility>,
        val savedChanges: MutableList<ChangeType.Visibility>
    )

    sealed class ChangeType {
        abstract fun apply(view: View)

        class Visibility(val new: Int): ChangeType() {
            override fun apply(view: View) {
                view.visibility = new
            }
        }
    }
}
