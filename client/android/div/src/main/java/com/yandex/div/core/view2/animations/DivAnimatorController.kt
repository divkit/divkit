package com.yandex.div.core.view2.animations

import android.animation.Animator
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import com.yandex.div.core.actions.logWarning
import com.yandex.div.core.animation.DivVariableAnimatorBuilder
import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.DivHolderView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionAnimatorStart
import com.yandex.div2.DivAnimator
import javax.inject.Inject

@DivViewScope
internal class DivAnimatorController @Inject constructor(
    private val divView: Div2View
) {

    private val runningAnimators = mutableMapOf<Pair<String, String>, Animator>()
    private val handler = Handler(Looper.getMainLooper())

    fun startAnimator(
        scopeId: String,
        targetView: View,
        action: DivActionAnimatorStart,
        resolver: ExpressionResolver
    ) {
        val animatorId = action.animatorId
        val animator = findAnimator(targetView, animatorId) ?: return
        val animatorKey = scopeId to animatorId
        if (animatorKey in runningAnimators) {
            runningAnimators.remove(animatorKey)?.cancel()
        }

        val variableAnimator = DivVariableAnimatorBuilder.build(divView, animator, action, resolver) ?: return
        variableAnimator.doOnEnd { runningAnimators.remove(animatorKey) }
        variableAnimator.doOnCancel { runningAnimators.remove(animatorKey) }
        runningAnimators[animatorKey] = variableAnimator
        variableAnimator.start()
    }

    private fun findAnimator(view: View, animatorId: String): DivAnimator? {
        return when (view) {
            is DivHolderView<*> -> {
                findAnimator(view.div?.animators, animatorId) ?: (view.parent as? View)?.let { findAnimator(it, animatorId) }
            }
            is Div2View -> {
                divView.logWarning(RuntimeException("Unable to find animator with id '$animatorId'"))
                return null
            }
            else -> (view.parent as? View)?.let { findAnimator(it, animatorId) }
        }
    }

    private fun findAnimator(animators: List<DivAnimator>?, animatorId: String): DivAnimator? {
        val matchingAnimators = animators?.filter { animator -> animator.value().id == animatorId } ?: return null
        if (matchingAnimators.isEmpty()) {
            return null
        }
        if (matchingAnimators.size > 1) {
            return null
        }
        return matchingAnimators.first()
    }

    fun stopAnimator(scopeId: String, animatorId: String) {
        val animator = runningAnimators.remove(scopeId to animatorId) ?: return
        animator.cancel()
    }

    fun onDetachedFromWindow() {
        val animators = ArrayList(runningAnimators.values)
        animators.forEach { animator ->
            animator.cancel()
        }
        runningAnimators.clear()
    }
}
