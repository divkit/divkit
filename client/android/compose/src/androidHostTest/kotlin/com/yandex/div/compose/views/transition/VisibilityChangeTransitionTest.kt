package com.yandex.div.compose.views.transition

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.animation.AnimationConfiguration
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.mockLocalComponent
import com.yandex.div.compose.visibility.VisibilityTransitionStorage
import com.yandex.div.compose.visibility.observeRenderedVisibility
import com.yandex.div.core.DivAnimationsEnabledProvider
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.render.common.animation.SystemAnimationConfiguration
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.doubleExpression
import com.yandex.div.test.data.text
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div2.DivAnimationInterpolator
import com.yandex.div2.DivAppearanceSetTransition
import com.yandex.div2.DivAppearanceTransition
import com.yandex.div2.DivBase
import com.yandex.div2.DivDimension
import com.yandex.div2.DivFadeTransition
import com.yandex.div2.DivScaleTransition
import com.yandex.div2.DivSlideTransition
import com.yandex.div2.DivVisibility
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import org.robolectric.RuntimeEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class VisibilityChangeTransitionTest {
    @get:Rule
    val rule = createComposeRule()

    private val visibilityVariable = Variable.StringVariable("visibility", "visible")
    private val variableController = DivVariableController().apply { declare(visibilityVariable) }
    private val animationsEnabled = MutableStateFlow(true)
    private val component = mockLocalComponent(variableController = variableController).apply {
        whenever(visibilityTransitionStorage) doReturn VisibilityTransitionStorage()
        whenever(animationConfiguration) doReturn AnimationConfiguration(
            SystemAnimationConfiguration(RuntimeEnvironment.getApplication()),
            object : DivAnimationsEnabledProvider {
                override val animationsEnabled = this@VisibilityChangeTransitionTest.animationsEnabled
            },
        )
    }
    private val mounted = mutableStateOf(true)
    private val visibility = mutableStateOf(DivVisibility.VISIBLE)
    private var appearance: StateChangeTransitionModifier? = null
    private val appearanceAlpha: Float? get() = appearance?.appearanceAlpha
    private var renderedVisibility: DivVisibility? = null
    private var containerVisibility: DivVisibility? = null

    @Test
    fun `remounting gone content does not restart a disposed exit`() {
        setContent(text(text = "Content", transitionOut = fade()).value())
        setVisibility(DivVisibility.GONE)
        rule.mainClock.advanceTimeBy(320)
        setMounted(false)
        rule.onNodeWithTag("content").assertDoesNotExist()

        setMounted(true)

        assertEquals(DivVisibility.GONE, renderedVisibility)
    }

    @Test
    fun `content hidden while unmounted does not animate on remount`() {
        setContent(text(text = "Content", transitionOut = fade()).value())
        setMounted(false)
        rule.onNodeWithTag("content").assertDoesNotExist()
        setVisibility(DivVisibility.GONE)

        setMounted(true)

        assertEquals(DivVisibility.GONE, renderedVisibility)
    }

    @Test
    fun `content shown while unmounted does not animate on remount`() {
        visibility.value = DivVisibility.GONE
        setContent(text(text = "Content", transitionIn = fade()).value())
        setMounted(false)
        setVisibility(DivVisibility.VISIBLE)

        setMounted(true)

        assertNull(appearance)
    }

    @Test
    fun `content shown while its container is unmounted does not animate on remount`() {
        visibilityVariable.set("gone")
        setContainerContent(containerChild(transitionIn = fade()))
        setMounted(false)
        setVisibilityVariable("visible")

        setMounted(true)

        assertNull(appearance)
    }

    @Test
    fun `content shown after its container remounts with it hidden animates`() {
        setContainerContent(containerChild(transitionIn = fade()))
        setMounted(false)
        setVisibilityVariable("gone")
        setMounted(true)

        setVisibilityVariable("visible")

        assertEquals(0f, appearanceAlpha)
    }

    @Test
    fun `container retains hidden content while its exit is running`() {
        setContainerContent(containerChild(transitionOut = fade()))

        setVisibilityVariable("gone")

        assertEquals(DivVisibility.VISIBLE, containerVisibility)
    }

    @Test
    fun `container releases hidden content that has no exit transition`() {
        setContainerContent(containerChild(transitionIn = fade()))

        setVisibilityVariable("gone")

        assertEquals(DivVisibility.GONE, containerVisibility)
    }

    @Test
    fun `container releases hidden content when animations are disabled`() {
        animationsEnabled.value = false
        setContainerContent(containerChild(transitionOut = fade()))

        setVisibilityVariable("gone")

        assertEquals(DivVisibility.GONE, containerVisibility)
    }

    @Test
    fun `visibility alpha is not applied on top of a state snapshot`() {
        val stateTransition = StateChangeTransitionModifier(appearanceAlpha = 1f, snapshotId = "content")

        val combined = stateTransition.withVisibilityTransition(runningTransition(), blocksPointerInput = false)

        assertEquals(1f, combined.appearanceAlpha)
    }

    @Test
    fun `visibility alpha replaces the element alpha outside a state snapshot`() {
        val combined = StateChangeTransitionModifier()
            .withVisibilityTransition(runningTransition(), blocksPointerInput = false)

        assertEquals(0.8f, combined.appearanceAlpha)
    }

    @Test
    fun `initially visible content has no enter animation`() {
        setContent(text(text = "Content", transitionIn = fade()).value())

        rule.mainClock.advanceTimeBy(100)

        assertNull(appearanceAlpha)
    }

    @Test
    fun `enter fade starts at absolute transition alpha`() {
        visibility.value = DivVisibility.GONE
        setContent(text(
            text = "Content",
            alpha = constant(0.5),
            transitionIn = fade(alpha = 0.8),
        ).value())

        setVisibility(DivVisibility.VISIBLE)

        assertEquals(0.8f, requireNotNull(appearanceAlpha), 0.01f)
    }

    @Test
    fun `finished enter releases appearance override`() {
        visibility.value = DivVisibility.GONE
        setContent(text(
            text = "Content",
            alpha = constant(0.5),
            transitionIn = fade(alpha = 0.8),
        ).value())

        setVisibility(DivVisibility.VISIBLE)
        rule.mainClock.advanceTimeBy(1_100)

        assertNull(appearance)
    }

    @Test
    fun `alpha update after completed enter leaves appearance override released`() {
        val alpha = Variable.DoubleVariable("alpha", 0.5)
        variableController.declare(alpha)
        visibility.value = DivVisibility.GONE
        setContent(text(
            text = "Content",
            alpha = doubleExpression("@{alpha}"),
            transitionIn = fade(),
        ).value())
        setVisibility(DivVisibility.VISIBLE)
        rule.mainClock.advanceTimeBy(1_100)

        rule.runOnIdle { alpha.set(0.8) }
        rule.waitForIdle()

        assertNull(appearance)
    }

    @Test
    fun `enter fade preserves starting alpha during delay`() {
        visibility.value = DivVisibility.GONE
        setContent(text(text = "Content", transitionIn = fade(startDelay = 320)).value())

        setVisibility(DivVisibility.VISIBLE)
        rule.mainClock.advanceTimeBy(160)

        assertEquals(0f, appearanceAlpha)
    }

    @Test
    fun `linear enter reaches midpoint after its delay`() {
        visibility.value = DivVisibility.GONE
        setContent(text(
            text = "Content",
            transitionIn = fade(duration = 640, startDelay = 320),
        ).value())

        setVisibility(DivVisibility.VISIBLE)
        rule.mainClock.advanceTimeBy(640)

        assertEquals(0.5f, requireNotNull(appearanceAlpha), 0.03f)
    }

    @Test
    fun `finished exit fade stays transparent while longer scale runs`() {
        setContent(text(text = "Content", transitionOut = exitSet()).value())

        setVisibility(DivVisibility.GONE)
        rule.mainClock.advanceTimeBy(600)

        assertEquals(0f, appearanceAlpha)
    }

    @Test
    fun `finished exit scale keeps its factor and pivot while longer fade runs`() {
        setContent(text(text = "Content", transitionOut = DivAppearanceTransition.Set(
            DivAppearanceSetTransition(items = listOf(
                DivAppearanceTransition.Scale(DivScaleTransition(
                    duration = constant(400L),
                    pivotX = constant(0.2),
                )),
                fade(),
            ))
        )).value())

        setVisibility(DivVisibility.GONE)
        rule.mainClock.advanceTimeBy(600)

        val left = rule.onNodeWithTag("content").getUnclippedBoundsInRoot().left
        assertTrue(left in 19.dp..21.dp, "Completed scale pivot position: $left")
    }

    @Test
    fun `finished exit slide keeps its offset while longer fade runs`() {
        setContent(text(text = "Content", transitionOut = DivAppearanceTransition.Set(
            DivAppearanceSetTransition(items = listOf(
                DivAppearanceTransition.Slide(DivSlideTransition(
                    duration = constant(400L),
                    distance = DivDimension(value = constant(100.0)),
                    edge = constant(DivSlideTransition.Edge.RIGHT),
                )),
                fade(),
            ))
        )).value())

        setVisibility(DivVisibility.GONE)
        rule.mainClock.advanceTimeBy(600)

        val left = rule.onNodeWithTag("content").getUnclippedBoundsInRoot().left
        assertTrue(left in 99.dp..101.dp, "Completed slide position: $left")
    }

    @Test
    fun `completed scale keeps an earlier pivot reset while longer fade runs`() {
        setContent(text(text = "Content", transitionOut = DivAppearanceTransition.Set(
            DivAppearanceSetTransition(items = listOf(
                DivAppearanceTransition.Scale(DivScaleTransition(
                    scale = constant(0.5),
                    duration = constant(400L),
                    pivotX = constant(0.0),
                )),
                DivAppearanceTransition.Scale(DivScaleTransition(
                    scale = constant(0.25),
                    duration = constant(200L),
                    pivotX = constant(1.0),
                )),
                fade(),
            ))
        )).value())

        setVisibility(DivVisibility.GONE)
        rule.mainClock.advanceTimeBy(600)

        val left = rule.onNodeWithTag("content").getUnclippedBoundsInRoot().left
        assertTrue(left in 24.dp..26.dp, "Completed scale position after earlier pivot reset: $left")
    }

    @Test
    fun `scales finishing together keep the last active pivot while longer fade runs`() {
        setContent(text(text = "Content", transitionOut = DivAppearanceTransition.Set(
            DivAppearanceSetTransition(items = listOf(
                DivAppearanceTransition.Scale(DivScaleTransition(
                    scale = constant(0.5),
                    duration = constant(400L),
                    pivotX = constant(0.0),
                )),
                DivAppearanceTransition.Scale(DivScaleTransition(
                    scale = constant(0.25),
                    duration = constant(400L),
                    pivotX = constant(1.0),
                )),
                fade(),
            ))
        )).value())

        setVisibility(DivVisibility.GONE)
        rule.mainClock.advanceTimeBy(600)

        val left = rule.onNodeWithTag("content").getUnclippedBoundsInRoot().left
        assertTrue(left in 74.dp..76.dp, "Completed simultaneous scale position: $left")
    }

    @Test
    fun `exit set retains content while longer scale is running`() {
        setContent(text(text = "Content", transitionOut = exitSet()).value())

        setVisibility(DivVisibility.GONE)
        rule.mainClock.advanceTimeBy(600)

        assertEquals(DivVisibility.VISIBLE, renderedVisibility)
    }

    @Test
    fun `exit set releases content after longest transition finishes`() {
        setContent(text(text = "Content", transitionOut = exitSet()).value())

        setVisibility(DivVisibility.GONE)
        rule.mainClock.advanceTimeBy(1_100)

        assertEquals(DivVisibility.GONE, renderedVisibility)
    }

    @Test
    fun `exit continues from partially entered alpha`() {
        visibility.value = DivVisibility.GONE
        setContent(text(text = "Content", transitionIn = fade(), transitionOut = fade()).value())
        setVisibility(DivVisibility.VISIBLE)
        rule.mainClock.advanceTimeBy(320)
        val alphaBeforeExit = requireNotNull(appearanceAlpha)

        setVisibility(DivVisibility.GONE)

        assertEquals(alphaBeforeExit, requireNotNull(appearanceAlpha), 0.03f)
    }

    @Test
    fun `completed reversal releases appearance override`() {
        setContent(text(
            text = "Content",
            alpha = constant(0.5),
            transitionIn = fade(),
            transitionOut = fade(),
        ).value())
        setVisibility(DivVisibility.GONE)
        rule.mainClock.advanceTimeBy(320)

        setVisibility(DivVisibility.VISIBLE)
        rule.mainClock.advanceTimeBy(1_100)

        assertNull(appearance)
    }

    private fun fade(
        alpha: Double = 0.0,
        duration: Long = 1_000,
        startDelay: Long = 0,
    ) = DivAppearanceTransition.Fade(DivFadeTransition(
        alpha = constant(alpha),
        duration = constant(duration),
        startDelay = constant(startDelay),
        interpolator = constant(DivAnimationInterpolator.LINEAR),
    ))

    private fun exitSet() = DivAppearanceTransition.Set(DivAppearanceSetTransition(
        items = listOf(
            fade(duration = 400),
            DivAppearanceTransition.Scale(DivScaleTransition(duration = constant(1_000L))),
        )
    ))

    private fun setContent(data: DivBase) {
        rule.mainClock.autoAdvance = false
        rule.setContent {
            CompositionLocalProvider(LocalComponent provides component) {
                if (!mounted.value) return@CompositionLocalProvider
                val transition = rememberVisibilityChangeTransition(data, visibility.value)
                SideEffect {
                    appearance = transition.appearance
                    renderedVisibility = transition.visibility
                }
                Box((transition.appearance?.modifier ?: Modifier).size(100.dp).testTag("content"))
            }
        }
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()
    }

    private fun runningTransition() = VisibilityChangeTransition(
        visibility = DivVisibility.VISIBLE,
        appearance = StateChangeTransitionModifier(appearanceAlpha = 0.8f),
    )

    private fun containerChild(
        transitionIn: DivAppearanceTransition? = null,
        transitionOut: DivAppearanceTransition? = null,
    ) = text(
        text = "Content",
        visibility = visibilityExpression("@{visibility}"),
        transitionIn = transitionIn,
        transitionOut = transitionOut,
    ).value()

    private fun setContainerContent(data: DivBase) {
        rule.mainClock.autoAdvance = false
        rule.setContent {
            CompositionLocalProvider(LocalComponent provides component) {
                if (!mounted.value) return@CompositionLocalProvider
                val retainedVisibility = data.observeRenderedVisibility()
                SideEffect { containerVisibility = retainedVisibility }
                if (retainedVisibility == DivVisibility.GONE) return@CompositionLocalProvider
                val transition = rememberVisibilityChangeTransition(data, data.visibility.observedValue())
                SideEffect { appearance = transition.appearance }
            }
        }
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()
    }

    private fun setVisibilityVariable(value: String) {
        rule.runOnIdle { visibilityVariable.set(value) }
        rule.waitForIdle()
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()
    }

    private fun setMounted(value: Boolean) {
        rule.runOnIdle { mounted.value = value }
        rule.waitForIdle()
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()
    }

    private fun setVisibility(value: DivVisibility) {
        rule.runOnIdle { visibility.value = value }
        rule.waitForIdle()
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()
    }
}
