package com.yandex.div.internal.widget

import android.app.Activity
import android.text.TextUtils.TruncateAt
import android.view.View.MeasureSpec
import android.view.ViewGroup.LayoutParams
import androidx.core.widget.doOnTextChanged
import com.yandex.div.internal.view.DrawingPassOverrideStrategy
import com.yandex.div2.DivText
import java.util.Locale
import kotlin.math.ceil
import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class EllipsizedTextViewTest {

    @Test
    fun `custom ellipsis does not split emoji grapheme`() {
        val familyEmoji = "👨‍👩‍👧‍👦"
        val sourceText = "prefix $familyEmoji suffix"
        val ellipsis = "… more"
        val view = EllipsizedTextView(RuntimeEnvironment.getApplication()).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            setPadding(0, 0, 0, 0)
            textSize = 20f
            maxLines = 1
            truncatePolicy = DivText.TruncatePolicy.GRAPHEME
            this.ellipsis = ellipsis
            text = sourceText
        }
        val width = ceil(view.paint.measureText("prefix 👨‍👩$ellipsis")).toInt()

        view.measureAtWidth(width)

        assertEquals("prefix $ellipsis", view.text.toString())
    }

    @Test
    fun `fitted symbols always snap to emoji grapheme boundary`() {
        val familyEmoji = "👨‍👩‍👧‍👦"
        val prefix = "prefix "
        val sourceText = "$prefix$familyEmoji suffix"

        val safeOffset = findSafeFittedSymbols(
            text = sourceText,
            locale = Locale.US,
            fittedSymbols = prefix.length + Character.charCount(familyEmoji.codePointAt(0)),
            isSafeBoundary = { true },
        )

        assertEquals(prefix.length, safeOffset)
    }

    @Test
    fun `default custom ellipsis preserves legacy fitted text`() {
        val ellipsis = "… more"
        val view = EllipsizedTextView(RuntimeEnvironment.getApplication()).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            setPadding(0, 0, 0, 0)
            textSize = 16f
            maxLines = 1
            this.ellipsis = ellipsis
            text = "This is a very long text that will be truncated with ellipsis that has corner radius on range"
        }

        val width = ceil(view.paint.measureText("This is a very long"))

        view.measureAtWidth(width.toInt())

        assertEquals("This is a ver… more", view.text.toString())
    }

    @Test
    fun `standard grapheme ellipsis uses native behavior`() {
        val sourceText = "A long text that does not fit"
        val view = EllipsizedTextView(RuntimeEnvironment.getApplication()).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            setPadding(0, 0, 0, 0)
            textSize = 20f
            maxLines = 1
            truncatePolicy = DivText.TruncatePolicy.GRAPHEME
            text = sourceText
        }

        view.measureAtWidth(10)

        assertEquals(sourceText, view.text.toString())
        assertEquals(TruncateAt.END, view.ellipsize)
    }

    @Test
    fun `standard word ellipsis uses manual behavior`() {
        val sourceText = "A long text that does not fit"
        val view = EllipsizedTextView(RuntimeEnvironment.getApplication()).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            setPadding(0, 0, 0, 0)
            textSize = 20f
            maxLines = 1
            truncatePolicy = DivText.TruncatePolicy.WORD
            text = sourceText
        }

        view.measureAtWidth(60)

        assertEquals(null, view.ellipsize)
    }

    @Test
    fun `word policy keeps native start ellipsis`() {
        val sourceText = "A long text that does not fit"
        val view = EllipsizedTextView(RuntimeEnvironment.getApplication()).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            setPadding(0, 0, 0, 0)
            textSize = 20f
            maxLines = 1
            ellipsisLocation = TruncateAt.START
            truncatePolicy = DivText.TruncatePolicy.WORD
            text = sourceText
        }

        view.measureAtWidth(10)

        assertEquals(sourceText, view.text.toString())
        assertEquals(TruncateAt.START, view.ellipsize)
    }

    @Test
    fun `custom ellipsis wider than line keeps source text`() {
        val sourceText = "text"
        val view = EllipsizedTextView(RuntimeEnvironment.getApplication()).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            setPadding(0, 0, 0, 0)
            maxLines = 1
            truncatePolicy = DivText.TruncatePolicy.WORD
            ellipsis = "… more"
            text = sourceText
        }

        view.measureAtWidth(1)

        assertEquals(sourceText, view.text.toString())
    }

    @Test
    fun `custom ellipsis wider than line keeps source text in layout`() {
        // Arrange
        val sourceText = "text"
        val view = EllipsizedTextView(RuntimeEnvironment.getApplication()).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            setPadding(0, 0, 0, 0)
            maxLines = 1
            truncatePolicy = DivText.TruncatePolicy.WORD
            ellipsis = "… more"
            text = sourceText
        }

        // Act
        view.measureAtWidth(1)

        // Assert
        assertEquals(sourceText, view.layout?.text?.toString())
    }

    @Test
    fun `word ellipsis exposes full source text to accessibility`() {
        val sourceText = "A long text that does not fit"
        val view = EllipsizedTextView(RuntimeEnvironment.getApplication()).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            setPadding(0, 0, 0, 0)
            textSize = 20f
            maxLines = 1
            truncatePolicy = DivText.TruncatePolicy.WORD
            text = sourceText
        }
        view.measureAtWidth(60)

        val accessibilityText = view.createAccessibilityNodeInfo().text

        assertEquals(sourceText, accessibilityText.toString())
    }

    @Test
    fun `custom ellipsis keeps text before hard line break`() {
        val ellipsis = "… more"
        val view = EllipsizedTextView(RuntimeEnvironment.getApplication()).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            setPadding(0, 0, 0, 0)
            textSize = 20f
            maxLines = 2
            truncatePolicy = DivText.TruncatePolicy.GRAPHEME
            this.ellipsis = ellipsis
            text = "first\nsecond\nthird"
        }
        val width = ceil(view.paint.measureText("second$ellipsis")).toInt()

        view.measureAtWidth(width)

        assertEquals("first\nsecond$ellipsis", view.text.toString())
    }

    @Test
    fun `unchanged remeasure does not notify text watcher`() {
        val sourceText = "A long text that does not fit"
        val ellipsis = "… more"
        val expectedText = "A long$ellipsis"
        val view = EllipsizedTextView(RuntimeEnvironment.getApplication()).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            setPadding(0, 0, 0, 0)
            textSize = 20f
            maxLines = 1
            truncatePolicy = DivText.TruncatePolicy.WORD
            this.ellipsis = ellipsis
            text = sourceText
        }
        val width = ceil(view.paint.measureText(expectedText)).toInt()
        view.measureAtWidth(width)
        assertEquals(expectedText, view.text.toString())
        val observedTexts = mutableListOf<String?>()
        view.doOnTextChanged { text, _, _, _ -> observedTexts += text?.toString() }

        view.measureAtWidth(width)

        assertEquals(emptyList(), observedTexts)
    }

    @Test
    fun `layout request with unchanged inputs does not notify text watcher`() {
        val sourceText = "A long text that does not fit"
        val ellipsis = "… more"
        val expectedText = "A long$ellipsis"
        val view = EllipsizedTextView(RuntimeEnvironment.getApplication()).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            setPadding(0, 0, 0, 0)
            textSize = 20f
            maxLines = 1
            truncatePolicy = DivText.TruncatePolicy.WORD
            this.ellipsis = ellipsis
            text = sourceText
        }
        val width = ceil(view.paint.measureText(expectedText)).toInt()
        view.measureAtWidth(width)
        val observedTexts = mutableListOf<String?>()
        view.doOnTextChanged { text, _, _, _ -> observedTexts += text?.toString() }

        view.requestLayout()
        view.measureAtWidth(width)

        assertEquals(emptyList(), observedTexts)
    }

    @Test
    fun `unchanged measure does not restart completed auto ellipsize`() {
        // Arrange
        val view = EllipsizedTextView(RuntimeEnvironment.getApplication()).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            setPadding(0, 0, 0, 0)
            maxLines = 1
            autoEllipsize = true
            text = "Short text"
        }
        var drawingPassCount = 0
        view.drawingPassOverrideStrategy = DrawingPassOverrideStrategy { _, proceed ->
            drawingPassCount++
            proceed
        }
        Robolectric.buildActivity(Activity::class.java)
            .setup()
            .get()
            .setContentView(view)
        drawingPassCount = 0

        // Act
        view.measureAtWidth(200)
        view.viewTreeObserver.dispatchOnPreDraw()
        view.measureAtWidth(200)
        view.viewTreeObserver.dispatchOnPreDraw()

        // Assert
        assertEquals(1, drawingPassCount)
    }

    private fun EllipsizedTextView.measureAtWidth(width: Int) {
        measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
        )
    }
}
