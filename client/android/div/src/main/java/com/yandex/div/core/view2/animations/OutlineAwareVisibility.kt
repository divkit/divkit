package com.yandex.div.core.view2.animations

import android.animation.Animator
import android.view.ViewGroup
import androidx.transition.TransitionValues
import androidx.transition.Visibility
import com.yandex.div.internal.widget.TransientView

internal open class OutlineAwareVisibility : Visibility() {

    override fun onAppear(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        startVisibility: Int,
        endValues: TransitionValues?,
        endVisibility: Int
    ): Animator? {
        val transientView = endValues?.view as? TransientView
        transientView?.transitionStarted(endValues.view)
        doOnEnd { transientView?.transitionFinished(endValues.view) }
        return super.onAppear(sceneRoot, startValues, startVisibility, endValues, endVisibility)
    }

    override fun onDisappear(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        startVisibility: Int,
        endValues: TransitionValues?,
        endVisibility: Int
    ): Animator? {
        val transientView = startValues?.view as? TransientView
        transientView?.transitionStarted(startValues.view)
        doOnEnd { transientView?.transitionFinished(startValues.view) }
        return super.onDisappear(sceneRoot, startValues, startVisibility, endValues, endVisibility)
    }
}
