package com.yandex.div.compose.views.transition

import androidx.compose.ui.unit.IntSize
import com.yandex.div2.DivSlideTransition
import kotlin.test.Test
import kotlin.test.assertEquals

class StateChangeAnimatedValuesTest {
    @Test
    fun `completed exit stays invisible until outgoing content is removed`() {
        val fade = AnimatedAppearance(
            alphas = listOf(
                AnimatedFloat(value = 0f, timing = FINISHED, orderingDurationMillis = DURATION_MS)
            ),
        )
        val scale = AnimatedAppearance(
            scales = listOf(
                AnimatedScale(
                    value = 0.2f,
                    pivotX = 0.5f,
                    pivotY = 0.5f,
                    timing = FINISHED,
                    orderingDurationMillis = DURATION_MS,
                )
            ),
        )
        val slide = AnimatedAppearance(
            slides = listOf(
                AnimatedSlide(
                    progress = 1f,
                    edge = DivSlideTransition.Edge.RIGHT,
                    distance = 100f,
                    exitTranslationX = 0f,
                    exitTranslationY = 0f,
                    timing = FINISHED,
                )
            ),
        )
        val set = fade.combineWith(scale).combineWith(slide)

        listOf(fade, scale, slide, set).forEach { appearance ->
            assertEquals(0f, appearance.resolve(isExiting = true).alpha)
            assertEquals(1f, appearance.resolve(isExiting = false).alpha)
        }
    }

    @Test
    fun `finished set item does not hide another running exit animation`() {
        val appearance = AnimatedAppearance(
            alphas = listOf(
                AnimatedFloat(value = 0f, timing = FINISHED, orderingDurationMillis = DURATION_MS)
            ),
            scales = listOf(
                AnimatedScale(
                    value = 0.6f,
                    pivotX = 0.5f,
                    pivotY = 0.5f,
                    timing = timing(phase = 0.5f),
                    orderingDurationMillis = DURATION_MS,
                )
            ),
        )

        val state = appearance.resolve(isExiting = true)

        assertEquals(1f, state.alpha)
        assertEquals(0.6f, state.scale)
    }

    private fun AnimatedAppearance.resolve(isExiting: Boolean): StateChangeVisualState {
        return resolveVisualState(
            sceneSize = IntSize(100, 100),
            bounds = ElementBounds.Zero,
            baseState = StateChangeVisualState(alpha = 1f),
            isExiting = isExiting,
        )
    }

    private companion object {
        const val DURATION_MS = 1_000L
        val FINISHED = timing(phase = 1f)

        fun timing(phase: Float) = AnimatedTiming(
            phase = phase,
            startTimeMillis = 0L,
            endTimeMillis = DURATION_MS,
            hasAnimator = true,
        )
    }
}
