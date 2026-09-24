package com.yandex.div.internal.widget

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils.TruncateAt
import android.text.style.RelativeSizeSpan
import android.util.TypedValue
import android.view.View.MeasureSpec
import android.view.ViewGroup.LayoutParams
import android.widget.TextView.BufferType
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.Locale
import kotlin.math.ceil
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class NativeTextEllipsisTest {

    private val view = EllipsizedTextView(getApplicationContext()).apply {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        setPadding(0, 0, 0, 0)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, 20f)
        typeface = Typeface.MONOSPACE
        textLocale = Locale.US
        maxLines = 1
    }

    @Test
    fun `plain ascii keeps native end ellipsis`() {
        view.text = "abcdefghij"

        measureAtWidth("abcd…")

        assertEquals("abcd…", renderedText())
        assertEquals(TruncateAt.END, view.ellipsize)
    }

    @Test
    fun `unchanged cyrillic text keeps native end ellipsis after remeasure`() {
        view.text = "абвгдежзий"
        measureAtWidth("абвг…")

        view.requestLayout()
        measureAtWidth("абвг…")

        assertEquals("абвг…", renderedText())
    }

    @Test
    fun `changed text replaces previously checked native text`() {
        view.text = "абвгдежзий"
        measureAtWidth("абвг…")

        view.text = "клмнопрсту"
        measureAtWidth("клмн…")

        assertEquals("клмн…", renderedText())
    }

    @Test
    fun `narrower width changes previously checked native boundary`() {
        view.text = "абвгдежзий"
        measureAtWidth("абвг…")

        measureAtWidth("аб…")

        assertEquals("аб…", renderedText())
    }

    @Test
    fun `larger font changes native boundary at unchanged width`() {
        view.text = "абвгдежзий"
        val width = ceil(view.paint.measureText("абвгд…")).toInt()
        measureAtWidth(width)

        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, 40f)
        measureAtWidth(width)

        assertEquals("аб…", renderedText())
    }

    @Test
    fun `changed locale preserves native cyrillic ellipsis`() {
        view.text = "абвгдежзий"
        measureAtWidth("абвг…")

        view.textLocale = Locale.forLanguageTag("ru")
        measureAtWidth("абвг…")

        assertEquals("абвг…", renderedText())
    }

    @Test
    fun `changed metric span changes native boundary at unchanged width`() {
        view.setSingleLine()
        view.setText(SpannableString("абвгдежзий"), BufferType.SPANNABLE)
        val width = ceil(view.paint.measureText("абвгд…")).toInt()
        measureAtWidth(width)

        (view.text as Spannable).setSpan(
            RelativeSizeSpan(2f), 0, view.text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
        )
        view.requestLayout()
        measureAtWidth(width)

        assertEquals("аб…", renderedText())
    }

    private fun measureAtWidth(visibleText: String) {
        measureAtWidth(ceil(view.paint.measureText(visibleText)).toInt())
    }

    private fun measureAtWidth(width: Int) {
        view.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    private fun renderedText(): String {
        val layout = view.layout
        if (layout.getEllipsisCount(0) == 0) {
            return layout.text.toString()
        }
        return layout.text.subSequence(0, layout.getEllipsisStart(0)).toString() + "…"
    }
}
