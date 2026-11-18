package com.yandex.div.compose.views.slider

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertLeftPositionInRootIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.accessibility
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.slider
import com.yandex.div2.DivAccessibility
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@RunWith(AndroidJUnit4::class)
class SliderSemanticsTest {
    @get:Rule
    val rule = createComposeRule()

    private val value = Variable.IntegerVariable(name = "value", defaultValue = 30)
    private val secondaryValue = Variable.IntegerVariable(
        name = "secondary_value",
        defaultValue = 70,
    )
    private val configuration = DivConfiguration(
        reporter = TestReporter(),
        variableController = DivVariableController().apply { declare(value, secondaryValue) },
    )
    private val thumb = SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)
    private val rangeStart = thumb and hasContentDescription("Range start")
    private val rangeEnd = thumb and hasContentDescription("Range end")

    @Test
    fun `thumb is described by the slider accessibility description`() {
        setContent(accessibility = accessibility(description = "Volume"))

        rule.onNode(thumb).assertContentDescriptionEquals("Volume")
    }

    @Test
    fun `slider element itself has no content description`() {
        setContent(accessibility = accessibility(description = "Volume"))

        rule.onNodeWithTag("slider")
            .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.ContentDescription))
    }

    @Test
    fun `range slider thumbs name the range start and end`() {
        setContent(accessibility = accessibility(description = "Price"), hasSecondaryThumb = true)

        assertEquals(listOf("Price,Range start", "Price,Range end"), thumbDescriptions())
    }

    @Test
    fun `range slider thumbs without description are named by the range ends`() {
        setContent(hasSecondaryThumb = true)

        assertEquals(listOf("Range start", "Range end"), thumbDescriptions())
    }

    @Test
    fun `secondary value accessibility describes the secondary thumb`() {
        setContent(
            accessibility = accessibility(description = "Price"),
            hasSecondaryThumb = true,
            secondaryValueAccessibility = accessibility(description = "Maximum price"),
        )

        assertEquals(listOf("Price,Range start", "Maximum price"), thumbDescriptions())
    }

    @Test
    fun `excluded secondary value accessibility hides the secondary thumb`() {
        setContent(
            hasSecondaryThumb = true,
            secondaryValueAccessibility = accessibility(mode = DivAccessibility.Mode.EXCLUDE),
        )

        assertEquals(listOf("Range start"), thumbDescriptions())
    }

    @Test
    fun `excluded slider accessibility hides the thumbs`() {
        setContent(
            accessibility = accessibility(mode = DivAccessibility.Mode.EXCLUDE),
            hasSecondaryThumb = true,
        )

        rule.onAllNodes(thumb).assertCountEquals(0)
    }

    @Test
    fun `merged slider accessibility keeps the thumbs separate`() {
        setContent(
            accessibility = accessibility(
                description = "Price",
                mode = DivAccessibility.Mode.MERGE,
            ),
            hasSecondaryThumb = true,
        )

        assertEquals(listOf("Price,Range start", "Price,Range end"), thumbDescriptions())
    }

    @Test
    fun `thumb reports its value within the slider range`() {
        setContent()

        val rangeInfo = rule.onNode(thumb).rangeInfo()
        assertEquals(30f, rangeInfo.current)
        assertEquals(0f..100f, rangeInfo.range)
    }

    @Test
    fun `secondary thumb reports the secondary value`() {
        setContent(hasSecondaryThumb = true)

        assertEquals(70f, rule.onNode(rangeEnd).rangeInfo().current)
    }

    @Test
    fun `thumb node covers the thumb at its value`() {
        setContent()

        rule.onNode(thumb).assertLeftPositionInRootIsEqualTo(54.dp)
    }

    @Test
    fun `thumb node follows the variable`() {
        setContent()

        value.set(100L)

        rule.onNode(thumb).assertLeftPositionInRootIsEqualTo(180.dp)
    }

    @Test
    fun `thumb node covers the thumb at its value in rtl`() {
        setContent(layoutDirection = LayoutDirection.Rtl)

        rule.onNode(thumb).assertLeftPositionInRootIsEqualTo(126.dp)
    }

    @Test
    fun `range thumb nodes cover the whole thumbs when apart`() {
        setContent(hasSecondaryThumb = true)

        rule.onNode(rangeStart)
            .assertLeftPositionInRootIsEqualTo(54.dp)
            .assertWidthIsEqualTo(20.dp)
    }

    @Test
    fun `overlapping thumbs split their nodes at the middle between them`() {
        value.set(50L)
        secondaryValue.set(50L)
        setContent(hasSecondaryThumb = true)

        rule.onNode(rangeStart)
            .assertLeftPositionInRootIsEqualTo(90.dp)
            .assertWidthIsEqualTo(10.dp)
        rule.onNode(rangeEnd)
            .assertLeftPositionInRootIsEqualTo(100.dp)
            .assertWidthIsEqualTo(10.dp)
    }

    @Test
    fun `overlapping thumbs in rtl give the start half to the range start`() {
        value.set(50L)
        secondaryValue.set(50L)
        setContent(hasSecondaryThumb = true, layoutDirection = LayoutDirection.Rtl)

        rule.onNode(rangeStart)
            .assertLeftPositionInRootIsEqualTo(100.dp)
            .assertWidthIsEqualTo(10.dp)
        rule.onNode(rangeEnd)
            .assertLeftPositionInRootIsEqualTo(90.dp)
            .assertWidthIsEqualTo(10.dp)
    }

    @Test
    fun `thumb states its value`() {
        setContent()

        rule.onNode(thumb)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "30"))
    }

    @Test
    fun `thumb state follows the variable`() {
        setContent()

        value.set(45L)

        rule.onNode(thumb)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "45"))
    }

    @Test
    fun `set progress writes the rounded value to the variable`() {
        setContent()

        rule.onNode(thumb).performSemanticsAction(SemanticsActions.SetProgress) { it(55.4f) }

        assertEquals(55L, value.getValue())
    }

    @Test
    fun `set progress on the secondary thumb writes the secondary variable`() {
        setContent(hasSecondaryThumb = true)

        rule.onNode(rangeEnd).performSemanticsAction(SemanticsActions.SetProgress) { it(55f) }

        assertEquals(55L, secondaryValue.getValue())
        assertEquals(30L, value.getValue())
    }

    @Test
    fun `set progress beyond the range moves the thumb to the range end`() {
        setContent()

        rule.onNode(thumb).performSemanticsAction(SemanticsActions.SetProgress) { it(150f) }

        assertEquals(100L, value.getValue())
    }

    @Test
    fun `set progress beyond the range of a thumb at the range end reports no change`() {
        value.set(100L)
        setContent()

        var isChanged = true
        rule.onNode(thumb).performSemanticsAction(SemanticsActions.SetProgress) {
            isChanged = it(150f)
        }

        assertFalse(isChanged)
    }

    @Test
    fun `set progress to not a number keeps the value`() {
        setContent()

        rule.onNode(thumb).performSemanticsAction(SemanticsActions.SetProgress) { it(Float.NaN) }

        assertEquals(30L, value.getValue())
    }

    @Test
    fun `scroll backward moves the thumb by five percent of the range rounded half up`() {
        value.set(100L)
        setContent(maxValue = constant(1010L))

        rule.onNode(thumb).scrollBackward()

        assertEquals(49L, value.getValue())
    }

    @Test
    fun `scroll backward on a range of one value moves the thumb by one value`() {
        value.set(1L)
        setContent(maxValue = constant(1L))

        rule.onNode(thumb).scrollBackward()

        assertEquals(0L, value.getValue())
    }

    @Test
    fun `set progress on a disabled slider keeps the value`() {
        setContent(isEnabled = constant(false))

        var isChanged = true
        rule.onNode(thumb).performSemanticsAction(SemanticsActions.SetProgress) {
            isChanged = it(55f)
        }

        assertFalse(isChanged)
        assertEquals(30L, value.getValue())
    }

    @Test
    fun `thumb of a disabled slider is a disabled seek bar`() {
        setContent(isEnabled = constant(false))

        rule.onNode(thumb)
            .assertIsNotEnabled()
            .assert(SemanticsMatcher.keyIsDefined(SemanticsActions.SetProgress))
    }

    private fun SemanticsNodeInteraction.rangeInfo() =
        fetchSemanticsNode().config[SemanticsProperties.ProgressBarRangeInfo]

    private fun SemanticsNodeInteraction.scrollBackward() {
        val rangeInfo = rangeInfo()
        val span = rangeInfo.range.endInclusive - rangeInfo.range.start
        val increment = if (rangeInfo.steps > 0) span / (rangeInfo.steps + 1) else span / 20
        performSemanticsAction(SemanticsActions.SetProgress) { it(rangeInfo.current - increment) }
    }

    private fun thumbDescriptions(): List<String> {
        return rule.onAllNodes(thumb).fetchSemanticsNodes().map {
            it.config[SemanticsProperties.ContentDescription].single()
        }
    }

    private fun setContent(
        accessibility: DivAccessibility? = null,
        hasSecondaryThumb: Boolean = false,
        isEnabled: Expression<Boolean> = constant(true),
        layoutDirection: LayoutDirection = LayoutDirection.Ltr,
        maxValue: Expression<Long> = constant(100L),
        secondaryValueAccessibility: DivAccessibility? = null,
    ) {
        val data = data(
            slider(
                accessibility = accessibility,
                id = "slider",
                isEnabled = isEnabled,
                maxValue = maxValue,
                secondaryValueAccessibility = secondaryValueAccessibility,
                thumbSecondaryValueVariable = secondaryValue.name.takeIf { hasSecondaryThumb },
                thumbValueVariable = value.name,
            )
        )
        rule.setContentWithDivContext(configuration = configuration) {
            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                DivView(data = data)
            }
        }
    }
}
