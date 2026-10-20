package com.yandex.div.compose.views.slider

import android.graphics.Typeface
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.font.FontWeight
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.font.DivFontSource
import com.yandex.div.compose.font.DivFontSourceProvider
import com.yandex.div.compose.setContent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.slider
import com.yandex.div2.DivFontWeight
import com.yandex.div2.DivSlider
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class SliderStylesTest {
    @get:Rule
    val rule = createComposeRule()

    private val fontSourceProvider = RecordingFontSourceProvider()
    private val family = Variable.StringVariable("family", "display")
    private val variableController = DivVariableController().apply {
        declare(
            Variable.IntegerVariable("value", 30),
            Variable.IntegerVariable("secondary_value", 70),
            family,
        )
    }
    private val configuration = DivConfiguration(
        fontSourceProvider = fontSourceProvider,
        reporter = TestReporter(),
        variableController = variableController,
    )

    @Test
    fun `thumb text font is requested from the font source provider with the family and weight`() {
        setContent(
            thumbTextStyle = DivSlider.TextStyle(
                fontFamily = constant("display"),
                fontWeight = constant(DivFontWeight.BOLD),
            ),
        )

        assertEquals(listOf<Pair<String?, FontWeight>>("display" to FontWeight.Bold), fontSourceProvider.requests)
    }

    @Test
    fun `thumb text font weight value takes precedence over font weight`() {
        setContent(
            thumbTextStyle = DivSlider.TextStyle(
                fontWeight = constant(DivFontWeight.BOLD),
                fontWeightValue = constant(600L),
            ),
        )

        assertEquals(listOf<Pair<String?, FontWeight>>(null to FontWeight(600)), fontSourceProvider.requests)
    }

    @Test
    fun `secondary thumb text font is requested separately`() {
        setContent(
            thumbTextStyle = DivSlider.TextStyle(fontFamily = constant("display")),
            thumbSecondaryValueVariable = "secondary_value",
            thumbSecondaryTextStyle = DivSlider.TextStyle(
                fontFamily = constant("caption"),
                fontWeight = constant(DivFontWeight.MEDIUM),
            ),
        )

        assertEquals(
            listOf<Pair<String?, FontWeight>>("display" to FontWeight.Normal, "caption" to FontWeight.Medium),
            fontSourceProvider.requests,
        )
    }

    @Test
    fun `secondary thumb text font is not requested without a secondary thumb`() {
        setContent(
            thumbTextStyle = DivSlider.TextStyle(fontFamily = constant("display")),
            thumbSecondaryTextStyle = DivSlider.TextStyle(fontFamily = constant("caption")),
        )

        assertEquals(listOf<Pair<String?, FontWeight>>("display" to FontWeight.Normal), fontSourceProvider.requests)
    }

    @Test
    fun `thumb text font is requested again when the font family expression changes`() {
        setContent(thumbTextStyle = DivSlider.TextStyle(fontFamily = expression("@{family}")))

        family.set("caption")
        rule.waitForIdle()

        assertEquals(
            listOf<Pair<String?, FontWeight>>("display" to FontWeight.Normal, "caption" to FontWeight.Normal),
            fontSourceProvider.requests,
        )
    }

    private fun setContent(
        thumbTextStyle: DivSlider.TextStyle? = null,
        thumbSecondaryValueVariable: String? = null,
        thumbSecondaryTextStyle: DivSlider.TextStyle? = null,
    ) {
        rule.setContent(
            configuration = configuration,
            data = data(
                slider(
                    thumbValueVariable = "value",
                    thumbTextStyle = thumbTextStyle,
                    thumbSecondaryValueVariable = thumbSecondaryValueVariable,
                    thumbSecondaryTextStyle = thumbSecondaryTextStyle,
                ),
            ),
        )
    }

    private class RecordingFontSourceProvider : DivFontSourceProvider {
        val requests = mutableListOf<Pair<String?, FontWeight>>()

        override fun getFontSource(fontFamilyName: String?, weight: FontWeight): DivFontSource {
            requests += fontFamilyName to weight
            return DivFontSource.Typeface(Typeface.DEFAULT)
        }
    }
}
