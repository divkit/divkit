package com.yandex.div.core.view2.divs

import android.graphics.Typeface
import android.os.Build
import android.text.Layout
import com.yandex.div.core.view2.DivTypefaceResolver
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivText
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
    private val binder = createBinder()

    @Before
    fun setUp() {
        whenever(typefaceResolver.getTypeface(any(), any())).thenReturn(Typeface.DEFAULT)
        whenever(divView.resources).thenReturn(context.resources)
    }

    @Test
    fun `url action applied`() {
        val (divText, view) = createTestData("with_action.json")

        binder.bindView(view, divText, divView)

        assertActionApplied(divView, view, Expected.ACTION_URI)
    }

    @Test
    fun `state action applied`() {
        val (divText, view) = createTestData("with_set_state_action.json")

        binder.bindView(view, divText, divView)

        assertActionApplied(divView, view, Expected.STATE_ACTION_URI)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.O])
    fun `apply hyphenation for text with soft hyphens`() {
        val (divText, view) = createTestData("with_hyphenation.json")

        binder.bindView(view, divText, divView)

        Assert.assertEquals(Layout.HYPHENATION_FREQUENCY_NORMAL, view.hyphenationFrequency)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.O])
    fun `set hyphenation frequency to none if word break is to long`() {
        val (divText, view) = createTestData("with_hyphenation_long_word_break.json")

        binder.bindView(view, divText, divView)

        Assert.assertEquals(Layout.HYPHENATION_FREQUENCY_NONE, view.hyphenationFrequency)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.O])
    fun `set hyphenation frequency to none if hyphenation is not supported`() {
        val binder = createBinder(isHyphenationEnabled = false)
        val (divText, view) = createTestData("with_hyphenation.json")

        binder.bindView(view, divText, divView)

        Assert.assertEquals(Layout.HYPHENATION_FREQUENCY_NONE, view.hyphenationFrequency)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.O])
    fun `apply hyphenation if text has ellipsis`() {
        val (divText, view) = createTestData("with_hyphenation_ellipsis.json")

        binder.bindView(view, divText, divView)

        Assert.assertEquals(Layout.HYPHENATION_FREQUENCY_NORMAL, view.hyphenationFrequency)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.O])
    fun `reset hyphenation after text has no soft hyphens`() {
        val (divText, view) = createTestData("with_hyphenation.json")

        binder.bindView(view, divText, divView)

        val (newDivText, newView) = createTestData("with_action.json")

        binder.bindView(newView, newDivText, divView)

        Assert.assertEquals(Layout.HYPHENATION_FREQUENCY_NONE, newView.hyphenationFrequency)
    }


    private fun createTestData(filename: String): Pair<DivText, DivLineHeightTextView> {
        val div = UnitTestData(TEXT_DIR, filename).div
        val divText = div.value() as DivText
        val view = viewCreator.create(div, ExpressionResolver.EMPTY) as DivLineHeightTextView
        view.layoutParams = defaultLayoutParams()
        return divText to view
    }

    private fun createBinder(isHyphenationEnabled: Boolean = true) = DivTextBinder(
        baseBinder, typefaceResolver, imageLoader, isHyphenationEnabled = isHyphenationEnabled
    )

    companion object {
        private const val TEXT_DIR = "div-text"
    }
}
