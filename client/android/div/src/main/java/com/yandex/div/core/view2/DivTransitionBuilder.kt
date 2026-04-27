package com.yandex.div.core.view2

import android.content.Context
import android.util.DisplayMetrics
import android.view.Gravity
import androidx.transition.ChangeBounds
import androidx.transition.Transition
import androidx.transition.TransitionSet
import androidx.transition.Visibility
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.dagger.Names
import com.yandex.div.core.util.androidInterpolator
import com.yandex.div.core.view2.animations.DivTransition
import com.yandex.div.core.view2.animations.Fade
import com.yandex.div.core.view2.animations.Scale
import com.yandex.div.core.view2.animations.Slide
import com.yandex.div.core.view2.animations.TransitionData
import com.yandex.div.core.view2.divs.toPx
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAppearanceTransition
import com.yandex.div2.DivChangeTransition
import com.yandex.div2.DivSlideTransition
import javax.inject.Inject
import javax.inject.Named

@DivViewScope
@Mockable
internal class DivTransitionBuilder @Inject constructor(
    @param:Named(Names.CONTEXT) private val context: Context,
    private val viewIdProvider: DivViewIdProvider
) {

    private val displayMetrics: DisplayMetrics
        get() = context.resources.displayMetrics

    fun buildTransitions(
        from: Sequence<TransitionData>?,
        to: Sequence<TransitionData>?,
    ): TransitionSet {
        val transitionSet = TransitionSet()
        transitionSet.ordering = TransitionSet.ORDERING_TOGETHER
        from?.forEach { transitionSet.addData(it) }
        to?.forEach { transitionSet.addData(it) }
        return transitionSet
    }

    private fun TransitionSet.addData(data: TransitionData) {
        val id = viewIdProvider.getViewId(data.viewId)
        data.transitions.forEach {
            val transition = it.toAndroidTransition(data.resolver)
            transition.addTarget(id)
            addTransition(transition)
        }
    }

   fun createAndroidTransition(
       divAppearanceTransition: DivAppearanceTransition?,
       @Visibility.Mode transitionMode: Int,
       resolver: ExpressionResolver
   ): Transition? = divAppearanceTransition?.toAndroidTransition(transitionMode, resolver)

    private fun DivTransition.toAndroidTransition(resolver: ExpressionResolver): Transition {
        return when (this) {
            is DivTransition.Appearance -> value.toAndroidTransition(mode, resolver)
            is DivTransition.Change -> value.toAndroidTransition(resolver)
        }
    }

    private fun DivAppearanceTransition.toAndroidTransition(
        @Visibility.Mode transitionMode: Int,
        resolver: ExpressionResolver,
    ): Transition {
        return when (this) {
            is DivAppearanceTransition.Set -> {
                TransitionSet().apply {
                    value.items.map { transition ->
                        transition.toAndroidTransition(transitionMode, resolver)
                    }.sortedByDescending {
                        it.startDelay + it.duration
                    }.forEach {
                        addTransition(it)
                    }
                }
            }

            is DivAppearanceTransition.Fade -> {
                Fade(alpha = value.alpha.evaluate(resolver).toFloat()).apply {
                    mode = transitionMode
                    duration = value.duration.evaluate(resolver)
                    startDelay = value.startDelay.evaluate(resolver)
                    interpolator = value.interpolator.evaluate(resolver).androidInterpolator
                }
            }

            is DivAppearanceTransition.Scale -> {
                Scale(
                    scaleFactor = value.scale.evaluate(resolver).toFloat(),
                    pivotX = value.pivotX.evaluate(resolver).toFloat(),
                    pivotY = value.pivotY.evaluate(resolver).toFloat()
                ).apply {
                    mode = transitionMode
                    duration = value.duration.evaluate(resolver)
                    startDelay = value.startDelay.evaluate(resolver)
                    interpolator = value.interpolator.evaluate(resolver).androidInterpolator
                }
            }

            is DivAppearanceTransition.Slide -> {
                Slide(
                    distance = value.distance?.toPx(displayMetrics, resolver) ?: Slide.DISTANCE_TO_EDGE,
                    slideEdge = value.edge.evaluate(resolver).toGravity()
                ).apply {
                    mode = transitionMode
                    duration = value.duration.evaluate(resolver)
                    startDelay = value.startDelay.evaluate(resolver)
                    interpolator = value.interpolator.evaluate(resolver).androidInterpolator
                }
            }
        }
    }

    private fun DivSlideTransition.Edge.toGravity(): Int {
        return when (this) {
            DivSlideTransition.Edge.LEFT -> Gravity.LEFT
            DivSlideTransition.Edge.TOP -> Gravity.TOP
            DivSlideTransition.Edge.RIGHT -> Gravity.RIGHT
            DivSlideTransition.Edge.BOTTOM -> Gravity.BOTTOM
        }
    }

    private fun DivChangeTransition.toAndroidTransition(resolver: ExpressionResolver): Transition {
        return when (this) {
            is DivChangeTransition.Set -> {
                TransitionSet().apply {
                    value.items.forEach { transition ->
                        addTransition(transition.toAndroidTransition(resolver))
                    }
                }
            }

            is DivChangeTransition.Bounds -> {
                ChangeBounds().apply {
                    duration = value.duration.evaluate(resolver)
                    startDelay = value.startDelay.evaluate(resolver)
                    interpolator = value.interpolator.evaluate(resolver).androidInterpolator
                }
            }
        }
    }
}
