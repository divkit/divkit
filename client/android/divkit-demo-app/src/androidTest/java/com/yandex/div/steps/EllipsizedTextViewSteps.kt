package com.yandex.div.steps

import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.LayoutAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import com.yandex.div.internal.widget.EllipsizedTextView
import com.yandex.div.internal.widget.textHeight
import com.yandex.div.utils.CharSequences
import com.yandex.div.utils.contentView
import com.yandex.div.utils.runOnView
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.startsWith
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert

internal fun ellipsizedTextView(f: EllipsizedTextViewSteps.() -> Unit) = f(EllipsizedTextViewSteps())

@StepsDsl
internal class EllipsizedTextViewSteps : DivTestAssetSteps() {

    /**
     * Text after being ellipsized.
     */
    val ellipsizedText: CharSequence?
        get() = step("Get ellipsized text") {
            performOnEllipsizedTextView { it.ellipsizedText }
        }

    /**
     * Actual display text.
     */
    val displayText: CharSequence?
        get() = step("Get display text") {
            performOnEllipsizedTextView { it.displayText }
        }

    /**
     * Set new text to the text view
     */
    fun setText(text: CharSequence) {
        step("set text to $text") {
            performOnEllipsizedTextView { it.text = text }
        }
    }

    fun ActivityTestRule<*>.buildContainer(height: Int? = null): Unit = step("Build container") {
        buildContainer(height ?: activity.contentView!!.width / 2, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    fun assert(f: EllipsizedTextViewAssertions.() -> Unit) = f(EllipsizedTextViewAssertions())
}

private const val CUSTOM_ELLIPSIS = "… Развернуть"

@StepsDsl
internal class EllipsizedTextViewAssertions {

    fun hasDefaultEllipsis(): Unit = step("Check text has default ellipsis") {
        longText().check(matches(isEllipsized()))
    }

    fun hasNullEllipsize(): Unit = step("Check text has no ellipsize") {
        val ellipsize =
            longText().runOnView<EllipsizedTextView, TextUtils.TruncateAt> { it.ellipsize }
        Assert.assertNull(ellipsize)
    }

    fun hasTruncateAtEndEllipsize(): Unit = step("Check text has ellipsize TruncateAt.END") {
        val ellipsize =
            longText().runOnView<EllipsizedTextView, TextUtils.TruncateAt> { it.ellipsize }
        Assert.assertEquals(TextUtils.TruncateAt.END, ellipsize)
    }

    fun hasCustomEllipsis(ellipsis: String = CUSTOM_ELLIPSIS): Unit = step("Check text has custom ellipsis") {
        firstText().check(matches(allOf(not(isEllipsized()), withCustomEllipsis(ellipsis))))
    }

    fun hasHeight(lines: Int): Unit = step("Check text has proper height for $lines lines") {
        longText().check(matches(withHeight(lines)))
    }

    fun noCutOff(): Unit = step("Check text has no cut off") {
        firstText().check(LayoutAssertions.noEllipsizedText())
    }

    fun checkEllipsizedTextChanged(before: CharSequence, after: CharSequence): Unit =
        step("Check ellipsized text changed") {
            Assert.assertFalse(CharSequences.equals(before, after))
        }

    fun checkEllipsizedTextIsDisplayed(displayText: CharSequence, ellipsizedText: CharSequence): Unit =
        step("Check ellipsized text is displayed") {
            Assert.assertTrue(CharSequences.equals(displayText, ellipsizedText))
        }
}

private fun longText() = onView(withText(startsWith("This is a very long text")))
private fun firstText() = onView(isAssignableFrom(EllipsizedTextView::class.java))

private fun withHeight(lines: Int) = object : TypeSafeMatcher<View>() {
    override fun describeTo(description: Description?) {
        description?.appendText("with height for $lines lines")
    }

    override fun describeMismatchSafely(item: View?, mismatchDescription: Description?) {
        val textView = item as? TextView
        val description = mismatchDescription?.appendText(
            "viewHeight=${textView?.height}, " +
                "textHeight=${textView?.textHeight(lines)}, " +
                "paddingTop = ${textView?.paddingTop}, " +
                "paddingBottom = ${textView?.paddingBottom}"
        )
        super.describeMismatchSafely(item, description)
    }

    override fun matchesSafely(item: View?): Boolean {
        if (item !is TextView) {
            return false
        }
        return with(item) {
            height == textHeight(lines) + compoundPaddingBottom + compoundPaddingBottom
        }
    }
}

private fun <T> performOnEllipsizedTextView(block: (EllipsizedTextView) -> T?): T? {
    return longText().runOnView<EllipsizedTextView, T>(block)
}

private fun isEllipsized() = object : TypeSafeMatcher<View>() {
    override fun describeTo(description: Description?) {}

    override fun matchesSafely(item: View?): Boolean {
        if (item !is TextView) {
            return false
        }
        return item.ellipsize == TextUtils.TruncateAt.END
    }
}

private fun withCustomEllipsis(ellipsis: String) = object : TypeSafeMatcher<View>() {
    override fun describeTo(description: Description?) {
        description?.appendText("with custom ellipsis $ellipsis")
    }

    override fun matchesSafely(item: View?): Boolean {
        if (item !is EllipsizedTextView) {
            return false
        }
        val ellipsizedText = item.ellipsizedText ?: return false
        return item.ellipsize == null && ellipsizedText.endsWith(ellipsis)
    }
}
