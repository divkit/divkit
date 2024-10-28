package com.yandex.div.core.view2.animations

import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.ViewGroup
import androidx.transition.Scene
import androidx.transition.Transition
import androidx.transition.TransitionManager

internal object SceneRootWatcher {

    fun watchFor(scene: Scene, transition: Transition) {
        watchFor(scene.sceneRoot, transition)
    }

    fun watchFor(sceneRoot: ViewGroup, transition: Transition) {
        val detachListener = OnDetachListener(sceneRoot)
        sceneRoot.addOnAttachStateChangeListener(detachListener)
        transition.doOnEnd { sceneRoot.removeOnAttachStateChangeListener(detachListener) }
    }

    private class OnDetachListener(
        private val sceneRoot: ViewGroup
    ) : OnAttachStateChangeListener {

        override fun onViewAttachedToWindow(view: View) = Unit

        override fun onViewDetachedFromWindow(view: View) {
            sceneRoot.removeOnAttachStateChangeListener(this)
            TransitionManager.endTransitions(sceneRoot)
        }
    }
}
