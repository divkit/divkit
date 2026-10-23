package com.yandex.div.core.view2.divs

import android.graphics.LinearGradient
import com.yandex.div.core.Disposable
import com.yandex.div.json.expressions.Expression
import com.yandex.div.core.widget.DivViewWrapper
import com.yandex.div.test.data.constant
import com.yandex.div2.DivDimension
import com.yandex.div2.DivPoint
import com.yandex.div2.DivShadow
import android.text.Layout
import com.yandex.div.core.font.DivTypefaceProvider
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.DivTypefaceResolver
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.core.view2.spannable.SpannedTextBuilder
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.toBlock
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.animatedTextGradient
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivTextBinderTest : DivBinderTest() {

    private val typefaceResolver = mock<DivTypefaceResolver>()
    private val spannedTextBuilder = mock<SpannedTextBuilder>()
    private val path = DivStatePath.fromState(0)
    private val binder = createBinder()

    @Before
    fun setUp() {
        whenever(typefaceResolver.getTypefaceProvider(anyOrNull())).thenReturn(DivTypefaceProvider.DEFAULT)
        whenever(divView.resources).thenReturn(context.resources)
    }

    @Test
    fun `url action applied`() {
        val (divText, view) = createTestData("with_action.json")

        binder.bindView(view, divText, divView)

        assertActionApplied(view, Expected.ACTION_URI, resolver, divView)
    }

    @Test
    fun `state action applied`() {
        val (divText, view) = createTestData("with_set_state_action.json")

        binder.bindView(view, divText, divView)

        assertActionApplied(view, Expected.STATE_ACTION_URI, resolver, divView)
    }

    @Test
    fun `apply hyphenation for text with soft hyphens`() {
        val (divText, view) = createTestData("with_hyphenation.json")

        binder.bindView(view, divText, divView)

        Assert.assertEquals(Layout.HYPHENATION_FREQUENCY_NORMAL, view.hyphenationFrequency)
    }

    @Test
    fun `set hyphenation frequency to none if hyphenation is not supported`() {
        val binder = createBinder(isHyphenationEnabled = false)
        val (divText, view) = createTestData("with_hyphenation.json")

        binder.bindView(view, divText, divView)

        Assert.assertEquals(Layout.HYPHENATION_FREQUENCY_NONE, view.hyphenationFrequency)
    }

    @Test
    fun `apply hyphenation if text has ellipsis`() {
        val (divText, view) = createTestData("with_hyphenation_ellipsis.json")

        binder.bindView(view, divText, divView)

        Assert.assertEquals(Layout.HYPHENATION_FREQUENCY_NORMAL, view.hyphenationFrequency)
    }

    @Test
    fun `reset hyphenation after text has no soft hyphens`() {
        val (divText, view) = createTestData("with_hyphenation.json")

        binder.bindView(view, divText, divView)

        val (newDivText, newView) = createTestData("with_action.json")

        binder.bindView(newView, newDivText, divView)

        Assert.assertEquals(Layout.HYPHENATION_FREQUENCY_NONE, newView.hyphenationFrequency)
    }

    @Test
    fun `animated gradient is applied by binder`() {
        val (divText, view) = createTestData(
            text(text = "ANIMATED GRADIENT", textGradient = animatedTextGradient())
        )

        binder.bindView(view, divText, divView)
        view.text = "ANIMATED GRADIENT"
        view.layout(0, 0, 300, 80)

        Assert.assertTrue(view.paint.shader is LinearGradient)
    }

    @Test
    fun `text shadow disables clipping of the view and wrapper`() {
        val (block, view) = createTestData(textWithShadow())
        val wrapper = DivViewWrapper(context).apply { addView(view) }

        binder.bindView(view, block, divView)

        Assert.assertEquals(listOf(false, false, false),
            listOf(view.clipToOutline, wrapper.clipChildren, wrapper.clipToPadding))
        Assert.assertEquals(4f * context.resources.displayMetrics.density, view.shadowRadius, 0f)
    }

    @Test
    fun `removing text shadow restores clipping and clears shadow`() {
        val (block, view) = createTestData(textWithShadow())
        val wrapper = DivViewWrapper(context).apply { addView(view) }
        binder.bindView(view, block, divView)
        val replacement = Div.Text(block.divValue.copy(textShadow = null)).toBlock(resolver, path) as DivBlock.Text

        binder.bindView(view, replacement, divView)

        Assert.assertEquals(0f, view.shadowRadius, 0f)
        Assert.assertEquals(listOf(true, true, true),
            listOf(view.clipToOutline, wrapper.clipChildren, wrapper.clipToPadding))
    }

    @Test
    fun `changing the shadow blur expression updates the shadow`() {
        val blur = MutableShadowBlur()
        val (block, view) = createTestData(textWithShadow(blur))
        binder.bindView(view, block, divView)

        blur.setValue(8)

        Assert.assertEquals(8f * context.resources.displayMetrics.density, view.shadowRadius, 0f)
    }

    @Test
    fun `rebound text no longer observes the previous shadow expression`() {
        val blur = MutableShadowBlur()
        val (block, view) = createTestData(textWithShadow(blur))
        binder.bindView(view, block, divView)
        val replacement = Div.Text(block.divValue.copy(textShadow = null)).toBlock(resolver, path) as DivBlock.Text
        binder.bindView(view, replacement, divView)

        blur.setValue(8)

        Assert.assertEquals(0f, view.shadowRadius, 0f)
    }

    private fun textWithShadow(blur: Expression<Long> = constant(4L)): Div.Text {
        val text = text(text = "shadow") as Div.Text
        val shadow = DivShadow(
            blur = blur,
            offset = DivPoint(DivDimension(value = constant(0.0)), DivDimension(value = constant(0.0))),
        )
        return Div.Text(text.value.copy(textShadow = shadow))
    }

    private fun createTestData(filename: String): Pair<DivBlock.Text, DivLineHeightTextView> {
        return createTestData(UnitTestData(TEXT_DIR, filename).div)
    }

    private fun createTestData(div: Div): Pair<DivBlock.Text, DivLineHeightTextView> {
        val block = div.toBlock(resolver, path) as DivBlock.Text
        val view = viewCreator.create(block.div, ExpressionResolver.EMPTY) as DivLineHeightTextView
        view.layoutParams = defaultLayoutParams()
        return block to view
    }

    private fun createBinder(isHyphenationEnabled: Boolean = true) = DivTextBinder(
        baseBinder, typefaceResolver, spannedTextBuilder, isHyphenationEnabled = isHyphenationEnabled
    )

    companion object {
        private const val TEXT_DIR = "div-text"
    }
    private class MutableShadowBlur : Expression<Long>() {
        override val rawValue: Any = "shadow_blur"
        private var value = 4L
        private val observers = mutableSetOf<(Long) -> Unit>()

        override fun evaluate(resolver: ExpressionResolver) = value

        override fun observe(resolver: ExpressionResolver, callback: (Long) -> Unit): Disposable {
            observers += callback
            return Disposable { observers -= callback }
        }

        fun setValue(value: Long) {
            this.value = value
            observers.toList().forEach { it(value) }
        }
    }

}
