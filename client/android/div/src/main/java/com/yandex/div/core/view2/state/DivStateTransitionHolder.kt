package com.yandex.div.core.view2.state

import androidx.core.view.doOnPreDraw
import androidx.transition.Transition
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.animations.enumerateTargetIds
import javax.inject.Inject

@Mockable
@DivViewScope
internal class DivStateTransitionHolder @Inject constructor(
    private val div2View: Div2View
) {

    private val transitions = mutableListOf<Transition>()
    private var cleanScheduled = false

    fun append(transition: Transition) {
        transitions += transition
        scheduleClean()
    }

    fun clear() {
        transitions.clear()
    }

    private fun scheduleClean() {
        if (!cleanScheduled) {
            div2View.doOnPreDraw {
                clear()
            }
            cleanScheduled = true
        }
    }

    fun enumerateTargetIds(): List<Int> {
        return transitions.flatMap { transition ->
            transition.enumerateTargetIds()
        }
    }
}
