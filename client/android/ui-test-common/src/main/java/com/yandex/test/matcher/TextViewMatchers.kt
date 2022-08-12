package com.yandex.test.matcher

import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.yandex.test.util.Drawable.checkBitmap
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

object TextViewMatchers {

    fun textViewWithIdAndTextAndIcon(@IdRes id: Int, @StringRes text: Int, @DrawableRes icon: Int): Matcher<View> =
        allOf(withId(id), withText(text), withIcon(icon))

    /**
     * Checks current number of text lines in TextView
     * */
    fun withTextInLines(lines: Int): BoundedMatcher<View, TextView> {
        return object : BoundedMatcher<View, TextView>(TextView::class.java) {

            override fun describeTo(description: Description) {
                description.appendText(" has text in $lines lines")
            }

            override fun matchesSafely(item: TextView): Boolean {
                return item.lineCount == lines
            }
        }
    }

    fun withTextNoCase(text: String): BoundedMatcher<View, TextView> {
        return object : BoundedMatcher<View, TextView>(TextView::class.java) {

            override fun describeTo(description: Description) {
                description.appendText(" with text (ignoring case): ");
            }

            override fun matchesSafely(item: TextView): Boolean {
                return item.text.toString().equals(text, ignoreCase = true)
            }
        }
    }

    fun withTextNoCase(textId: Int): BoundedMatcher<View, TextView> {
        return object : BoundedMatcher<View, TextView>(TextView::class.java) {

            override fun describeTo(description: Description) {
                description.appendText(" with text (ignoring case): ");
            }

            override fun matchesSafely(item: TextView): Boolean {
                return item.text.toString().equals(item.context.getString(textId), ignoreCase = true)
            }
        }
    }

    fun withTextInside(text: String): BoundedMatcher<View, TextView> =
        object : BoundedMatcher<View, TextView>(TextView::class.java) {

            override fun describeTo(description: Description) {
                description.appendText(" with text (ignoring case): ");
            }

            override fun matchesSafely(item: TextView): Boolean {
                return item.text.toString().contains(text)
            }
        }

    fun withTextTrimmed(text: String): BoundedMatcher<View, TextView> =
        object : BoundedMatcher<View, TextView>(TextView::class.java) {

            override fun describeTo(description: Description) {
                description.appendText(" with text (ignoring case): ");
            }

            override fun matchesSafely(item: TextView): Boolean {
                return item.text.trim().toString().contains(text)
            }
        }

    /**
     *  Check that TextView has equal drawable icon
     * */
    fun withIcon(@DrawableRes iconId: Int): BoundedMatcher<View, TextView> {
        return object : BoundedMatcher<View, TextView>(TextView::class.java) {

            override fun describeTo(description: Description) {
                description.appendText(" with icon id: $iconId")
            }

            override fun matchesSafely(view: TextView): Boolean {
                return checkBitmap(view.context, view.compoundDrawables[0], iconId)
            }
        }
    }
}
