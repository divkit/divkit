package com.yandex.div.compose

import android.graphics.Matrix
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.utils.gradient.LinearGradientBrush
import com.yandex.div.core.DivAnimationsEnabledProvider
import com.yandex.div.core.util.AnimatedTextGradientMath
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.animatedTextGradient
import com.yandex.div.test.data.data
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivText
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull

@RunWith(AndroidJUnit4::class)
class AnimatedTextGradientTest {

    @get:Rule
    val rule = createComposeRule()

    @Before
    fun setUp() {
        rule.mainClock.autoAdvance = false
    }

    @Test
    fun `animated gradient renders through real DivText`() {
        render(animatedText())

        val gradientStyle = renderedText().spanStyles.single().item

        assertIs<LinearGradientBrush>(gradientStyle.brush)
    }

    @Test
    fun `zero duration keeps static gradient with identity transform`() {
        render(animatedText(duration = 0L))

        rule.mainClock.advanceTimeBy(HALF_CYCLE_MILLIS)

        val brush = assertIs<LinearGradientBrush>(renderedText().spanStyles.single().item.brush)
        assertEquals(0f, brush.createShader(GRADIENT_SIZE).translationX(), TOLERANCE)
    }

    @Test
    fun `controlled frame time produces exact gradient translation`() {
        render(animatedText())

        rule.mainClock.advanceTimeBy(HALF_CYCLE_MILLIS)
        rule.mainClock.advanceTimeByFrame()

        val brush = assertIs<LinearGradientBrush>(renderedText().spanStyles.single().item.brush)
        val phase = AnimatedTextGradientMath.phase(rule.mainClock.currentTime, DURATION_MILLIS)
        val expectedTranslation = AnimatedTextGradientMath.linearTranslation(
            angleDegrees = 0f,
            width = GRADIENT_SIZE.width,
            height = GRADIENT_SIZE.height,
            phase = phase,
        ).x
        assertEquals(expectedTranslation, brush.createShader(GRADIENT_SIZE).translationX(), TOLERANCE)
    }

    @Test
    fun `disabled animations keep static gradient with identity transform`() {
        val animationsEnabledState = MutableStateFlow(false)
        val configuration = DivConfiguration(
            animationsEnabledProvider = object : DivAnimationsEnabledProvider {
                override val animationsEnabled = animationsEnabledState
            },
        )
        render(animatedText(), configuration)

        rule.mainClock.advanceTimeBy(HALF_CYCLE_MILLIS)

        val brush = assertIs<LinearGradientBrush>(renderedText().spanStyles.single().item.brush)
        assertEquals(0f, brush.createShader(GRADIENT_SIZE).translationX(), TOLERANCE)
    }

    @Test
    fun `range text color replaces animated gradient`() {
        val range = DivText.Range(
            start = Expression.constant(0L),
            end = Expression.constant(4L),
            textColor = Expression.constant(RANGE_COLOR),
        )
        render(animatedText(ranges = listOf(range)))

        val rangeStyle = renderedText().spanStyles.single { span -> span.start == 0 && span.end == 4 }.item

        assertNotNull(rangeStyle.brush)
        assertFalse(rangeStyle.brush is LinearGradientBrush)
    }

    private fun render(
        text: Div,
        configuration: DivConfiguration = DivConfiguration(),
    ) {
        rule.setContent(configuration = configuration, data = data(text))
    }

    private fun renderedText() = rule.onNodeWithTag(TEXT_ID)
        .fetchSemanticsNode()
        .config[SemanticsProperties.Text]
        .single()

    private fun animatedText(
        ranges: List<DivText.Range>? = null,
        duration: Long = DURATION_MILLIS,
    ) = text(
        id = TEXT_ID,
        text = Expression.constant(TEXT),
        ranges = ranges,
        textGradient = animatedTextGradient(duration = Expression.constant(duration)),
    )

    private fun android.graphics.Shader.translationX(): Float {
        val matrix = Matrix()
        @Suppress("DEPRECATION")
        getLocalMatrix(matrix)
        return FloatArray(9).also(matrix::getValues)[Matrix.MTRANS_X]
    }

    private companion object {
        const val TEXT_ID = "text"
        const val TEXT = "animated gradient"
        const val DURATION_MILLIS = 1600L
        const val HALF_CYCLE_MILLIS = DURATION_MILLIS / 2
        const val RANGE_COLOR = 0xFF00FF00.toInt()
        const val TOLERANCE = 0.001f
        val GRADIENT_SIZE = Size(width = 200f, height = 40f)
    }
}
