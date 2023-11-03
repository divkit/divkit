package com.yandex.div.core.view2

import android.content.Context
import android.graphics.Path
import android.util.DisplayMetrics
import android.view.Gravity
import androidx.core.graphics.PathParser
import androidx.transition.ChangeBounds
import androidx.transition.Transition
import androidx.transition.TransitionSet
import androidx.transition.Visibility
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.dagger.Names
import com.yandex.div.core.util.androidInterpolator
import com.yandex.div.core.util.walk
import com.yandex.div.core.view2.animations.Fade
import com.yandex.div.core.view2.animations.Scale
import com.yandex.div.core.view2.animations.Slide
import com.yandex.div.core.view2.animations.plusAssign
import com.yandex.div.core.view2.divs.toPx
import com.yandex.div.internal.KLog
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivAppearanceTransition
import com.yandex.div2.DivChangeTransition
import com.yandex.div2.DivSlideTransition
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.max

@DivViewScope
@Mockable
internal class DivTransitionBuilder @Inject constructor(
    @Named(Names.CONTEXT) private val context: Context,
    private val viewIdProvider: DivViewIdProvider
) {

    private val displayMetrics: DisplayMetrics
        get() = context.resources.displayMetrics

    fun buildTransitions(fromDiv: Div?, toDiv: Div?, resolver: ExpressionResolver): TransitionSet {
        return buildTransitions(from = fromDiv?.walk(resolver), to = toDiv?.walk(resolver), resolver)
    }

    fun buildTransitions(from: Sequence<Div>?, to: Sequence<Div>?,
                         resolver: ExpressionResolver): TransitionSet {
        val transitionSet = TransitionSet().apply {
            ordering = TransitionSet.ORDERING_TOGETHER
        }

        if (from != null) {
            transitionSet += buildOutgoingTransitions(from, resolver)
        }

        if (from != null && to != null) {
            transitionSet += buildChangeTransitions(from, resolver)
        }

        if (to != null) {
            transitionSet += buildIncomingTransitions(to, resolver)
        }

        return transitionSet
    }

    private fun buildOutgoingTransitions(
        divSequence: Sequence<Div>,
        resolver: ExpressionResolver
    ): List<Transition> {
        val transitions = mutableListOf<Transition>()

        divSequence.forEach { div ->
            val id = div.value().id
            val outgoingTransition = div.value().transitionOut
            if (id != null && outgoingTransition != null) {
                val transition = outgoingTransition.toAndroidTransition(Visibility.MODE_OUT, resolver).apply {
                    addTarget(viewIdProvider.getViewId(id))
                }
                transitions += transition
            }
        }

        return transitions
    }

    private fun buildChangeTransitions(
        divSequence: Sequence<Div>,
        resolver: ExpressionResolver
    ): List<Transition> {
        val transitions = mutableListOf<Transition>()

        divSequence.forEach { div ->
            val id = div.value().id
            val changeTransition = div.value().transitionChange
            if (id != null && changeTransition != null) {
                val transition = changeTransition.toAndroidTransition(resolver).apply {
                    addTarget(viewIdProvider.getViewId(id))
                }
                transitions += transition
            }
        }

        return transitions
    }

    private fun buildIncomingTransitions(
        divSequence: Sequence<Div>,
        resolver: ExpressionResolver
    ): List<Transition> {
        val transitions = mutableListOf<Transition>()

        divSequence.forEach { div ->
            val id = div.value().id
            val incomingTransition = div.value().transitionIn
            if (id != null && incomingTransition != null) {
                val transition = incomingTransition.toAndroidTransition(Visibility.MODE_IN, resolver).apply {
                    addTarget(viewIdProvider.getViewId(id))
                }
                transitions += transition
            }
        }

        return transitions
    }

   fun createAndroidTransition(
       divAppearanceTransition: DivAppearanceTransition?,
       @Visibility.Mode transitionMode: Int,
       resolver: ExpressionResolver
   ): Transition? {
       if (divAppearanceTransition == null) return null

       return divAppearanceTransition.toAndroidTransition(transitionMode, resolver)
   }

    private fun DivAppearanceTransition.toAndroidTransition(@Visibility.Mode transitionMode: Int,
                                                            resolver: ExpressionResolver): Transition {
        return when (this) {
            is DivAppearanceTransition.Set -> {
                TransitionSet().apply {
                    value.items.forEach { transition ->
                        val androidTransaction = transition.toAndroidTransition(transitionMode, resolver)

                        duration = max(duration, androidTransaction.startDelay + androidTransaction.duration)

                        addTransition(androidTransaction)
                    }
                }
            }

            is DivAppearanceTransition.Fade -> {
                Fade(alpha = value.alpha.evaluate(resolver).toFloat()).apply {
                    mode = transitionMode
                    duration = value.duration.evaluate(resolver).toLong()
                    startDelay = value.startDelay.evaluate(resolver).toLong()
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
                    duration = value.duration.evaluate(resolver).toLong()
                    startDelay = value.startDelay.evaluate(resolver).toLong()
                    interpolator = value.interpolator.evaluate(resolver).androidInterpolator
                }
            }

            is DivAppearanceTransition.Slide -> {
                Slide(
                    distance = value.distance?.toPx(displayMetrics, resolver) ?: Slide.DISTANCE_TO_EDGE,
                    slideEdge = value.edge.evaluate(resolver).toGravity()
                ).apply {
                    mode = transitionMode
                    duration = value.duration.evaluate(resolver).toLong()
                    startDelay = value.startDelay.evaluate(resolver).toLong()
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
                    duration = value.duration.evaluate(resolver).toLong()
                    startDelay = value.startDelay.evaluate(resolver).toLong()
                    interpolator = value.interpolator.evaluate(resolver).androidInterpolator
                }
            }
        }
    }

    private fun String.toPathOrNull(): Path? {
        return try {
            PathParser.createPathFromPathData(this)
        } catch (e: RuntimeException) {
            KLog.e(TAG, e) { "Unable to parse path data: $this" }
            null
        }
    }

    private companion object {
        private const val TAG = "DivTransitionController"
    }
}
