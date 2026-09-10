package com.yandex.div.core.tooltip

import android.animation.ObjectAnimator
import android.content.Context
import android.transition.TransitionValues
import android.transition.Visibility
import android.view.View
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.asExpression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivAnimation
import com.yandex.div2.DivText
import com.yandex.div2.DivTooltip
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@RunWith(AndroidJUnit4::class)
class DivTooltipAnimationTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun `scale appears from zero when start value is absent`() {
        val transition = createScaleTransition(incoming = true)

        val range = transition.scaleRange(incoming = true)

        assertEquals(ScaleRange(0f, 1f), range)
    }

    @Test
    fun `scale disappears to zero when end value is absent`() {
        val transition = createScaleTransition(incoming = false)

        val range = transition.scaleRange(incoming = false)

        assertEquals(ScaleRange(1f, 0f), range)
    }

    @Test
    fun `explicit start value is used for appearing scale`() {
        val transition = createScaleTransition(incoming = true, startValue = 0.25)

        val range = transition.scaleRange(incoming = true)

        assertEquals(ScaleRange(0.25f, 1f), range)
    }

    @Test
    fun `explicit end value is used for disappearing scale`() {
        val transition = createScaleTransition(incoming = false, endValue = 0.75)

        val range = transition.scaleRange(incoming = false)

        assertEquals(ScaleRange(1f, 0.75f), range)
    }

    private fun createScaleTransition(
        incoming: Boolean,
        startValue: Double? = null,
        endValue: Double? = null,
    ): Visibility {
        val animation = DivAnimation(
            name = DivAnimation.Name.SCALE.asExpression(),
            startValue = startValue?.asExpression(),
            endValue = endValue?.asExpression(),
        )
        val tooltip = DivTooltip(
            animationIn = animation.takeIf { incoming },
            animationOut = animation.takeUnless { incoming },
            div = Div.Text(DivText(text = "tooltip".asExpression())),
            id = "tooltip",
            position = DivTooltip.Position.BOTTOM.asExpression(),
        )
        val transition = PopupWindow(context).run {
            setupAnimation(tooltip, ExpressionResolver.EMPTY)
            if (incoming) requireNotNull(enterTransition) else requireNotNull(exitTransition)
        }
        return assertIs<Visibility>(transition)
    }

    private fun Visibility.scaleRange(incoming: Boolean): ScaleRange {
        val view = View(context)
        val values = TransitionValues(view)
        val animator = if (incoming) {
            onAppear(FrameLayout(context), view, values, values)
        } else {
            onDisappear(FrameLayout(context), view, values, values)
        }
        val objectAnimator = assertIs<ObjectAnimator>(animator)

        objectAnimator.setCurrentFraction(0f)
        val startScale = view.scaleX
        objectAnimator.setCurrentFraction(1f)
        return ScaleRange(startScale, view.scaleX)
    }

    private data class ScaleRange(val start: Float, val end: Float)
}
