package com.yandex.div.core.view2.animations

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivAnimationsEnabledProvider
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.view2.Div2View
import com.yandex.div.test.data.action
import com.yandex.div.test.data.animatorStartAction
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.infinityCount
import com.yandex.div.test.data.numberAnimator
import com.yandex.div.test.data.typedValue
import com.yandex.div.test.data.variable
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.DivAction
import com.yandex.div2.DivAnimationDirection
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import kotlin.test.Test
import kotlin.test.assertEquals

private const val ALPHA = "alpha"
private const val ROOT = "root"
private const val DURATION = 300L

@RunWith(AndroidJUnit4::class)
class DivAnimatorControllerTest {

    private val divView = createDivView(animationsEnabled = false)

    @Test
    fun `animator_start applies end value immediately when animations are disabled`() {
        divView.handleAction(startAnimator("fade", endValue = 0.0))

        assertEquals(0.0, divView.alpha())
    }

    @Test
    fun `animator_start with reverse direction ends at start value when animations are disabled`() {
        divView.handleAction(
            action(
                scopeId = ROOT,
                typed = animatorStartAction(
                    "fade",
                    direction = DivAnimationDirection.REVERSE,
                    startValue = typedValue(0.5),
                ),
            )
        )

        assertEquals(0.5, divView.alpha())
    }

    @Test
    fun `end_actions continue the chain when animations are disabled`() {
        divView.handleAction(startAnimator("fade_half"))

        assertEquals(0.25, divView.alpha())
    }

    @Test
    fun `end_actions restarting the same animator do not loop when animations are disabled`() {
        divView.handleAction(startAnimator("fade_loop"))

        assertEquals(0.0, divView.alpha())
    }

    @Test
    fun `infinite animator keeps current value when animations are disabled`() {
        divView.handleAction(startAnimator("pulse"))

        assertEquals(1.0, divView.alpha())
    }

    @Test
    fun `animator_start animates from current value when animations are enabled`() {
        val enabledDivView = createDivView(animationsEnabled = true)

        enabledDivView.handleAction(startAnimator("fade", endValue = 0.0))

        assertEquals(1.0, enabledDivView.alpha())
    }

    private fun createDivView(animationsEnabled: Boolean): Div2View {
        val provider = object : DivAnimationsEnabledProvider {
            override val animationsEnabled = MutableStateFlow(animationsEnabled)
        }
        val configuration = DivConfiguration.Builder(mock())
            .animationsEnabledProvider(provider)
            .build()
        val divView = Div2View(
            Div2Context(
                baseContext = testContextThemeWrapper(),
                configuration = configuration,
                lifecycleOwner = null,
            )
        )
        val divData = data(
            container(
                id = ROOT,
                animators = listOf(
                    numberAnimator(id = "fade", variableName = ALPHA, endValue = 0.25, duration = DURATION),
                    numberAnimator(
                        id = "fade_half",
                        variableName = ALPHA,
                        endValue = 0.5,
                        duration = DURATION,
                        endActions = listOf(startAnimator("fade")),
                    ),
                    numberAnimator(
                        id = "fade_loop",
                        variableName = ALPHA,
                        endValue = 0.0,
                        duration = DURATION,
                        endActions = listOf(startAnimator("fade_loop")),
                    ),
                    numberAnimator(
                        id = "pulse",
                        variableName = ALPHA,
                        endValue = 0.0,
                        duration = DURATION,
                        repeatCount = infinityCount(),
                    ),
                ),
            ),
            variables = listOf(variable(ALPHA, 1.0)),
        )
        divView.setData(divData, DivDataTag("tag"))
        return divView
    }

    private fun startAnimator(animatorId: String, endValue: Double? = null): DivAction = action(
        scopeId = ROOT,
        typed = animatorStartAction(animatorId, endValue = endValue?.let { typedValue(it) }),
    )

    private fun Div2View.alpha(): Double {
        val resolver = expressionResolver as ExpressionResolverImpl
        return resolver.variableController.getMutableVariable(ALPHA)!!.getValue() as Double
    }
}
