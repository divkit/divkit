package com.yandex.div.core.view2.divs

import android.graphics.Typeface
import android.os.Build
import android.text.Layout
import com.yandex.div.core.view2.DivTypefaceResolver
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.core.view2.spannable.SpannedTextBuilder
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class DivTextBinderTest : DivBinderTest() {

    private val typefaceResolver = mock<DivTypefaceResolver>()
    private val spannedTextBuilder = mock<SpannedTextBuilder>()
    private val binder = createBinder()

    @Before
    fun setUp() {
        whenever(typefaceResolver.getTypeface(any(), any(), any())).thenReturn(Typeface.DEFAULT)
        whenever(divView.resources).thenReturn(context.resources)
    }

    @Test
    fun `url action applied`() {
        val (divText, view) = createTestData("with_action.json")

        binder.bindView(bindingContext, view, divText)

        assertActionApplied(bindingContext, view, Expected.ACTION_URI)
    }

    @Test
    fun `state action applied`() {
        val (divText, view) = createTestData("with_set_state_action.json")

        binder.bindView(bindingContext, view, divText)

        assertActionApplied(bindingContext, view, Expected.STATE_ACTION_URI)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.O])
    fun `apply hyphenation for text with soft hyphens`() {
        val (divText, view) = createTestData("with_hyphenation.json")

        binder.bindView(bindingContext, view, divText)

        Assert.assertEquals(Layout.HYPHENATION_FREQUENCY_NORMAL, view.hyphenationFrequency)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.O])
    fun `set hyphenation frequency to none if hyphenation is not supported`() {
        val binder = createBinder(isHyphenationEnabled = false)
        val (divText, view) = createTestData("with_hyphenation.json")

        binder.bindView(bindingContext, view, divText)

        Assert.assertEquals(Layout.HYPHENATION_FREQUENCY_NONE, view.hyphenationFrequency)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.O])
    fun `apply hyphenation if text has ellipsis`() {
        val (divText, view) = createTestData("with_hyphenation_ellipsis.json")

        binder.bindView(bindingContext, view, divText)

        Assert.assertEquals(Layout.HYPHENATION_FREQUENCY_NORMAL, view.hyphenationFrequency)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.O])
    fun `reset hyphenation after text has no soft hyphens`() {
        val (divText, view) = createTestData("with_hyphenation.json")

        binder.bindView(bindingContext, view, divText)

        val (newDivText, newView) = createTestData("with_action.json")

        binder.bindView(bindingContext, newView, newDivText)

        Assert.assertEquals(Layout.HYPHENATION_FREQUENCY_NONE, newView.hyphenationFrequency)
    }


    private fun createTestData(filename: String): Pair<Div.Text, DivLineHeightTextView> {
        val div = UnitTestData(TEXT_DIR, filename).div as Div.Text
        val view = viewCreator.create(div, ExpressionResolver.EMPTY) as DivLineHeightTextView
        view.layoutParams = defaultLayoutParams()
        return div to view
    }

    private fun createBinder(isHyphenationEnabled: Boolean = true) = DivTextBinder(
        baseBinder, typefaceResolver, spannedTextBuilder, isHyphenationEnabled = isHyphenationEnabled
    )

    companion object {
        private const val TEXT_DIR = "div-text"
    }
}
