package com.yandex.div.steps

import android.view.View
import android.view.ViewGroup
import androidx.annotation.Px
import androidx.appcompat.widget.AppCompatTextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import com.yandex.test.util.StepsDsl
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import ru.tinkoff.allure.step

internal fun superLineHeightTextView(f: SuperLineHeightTextViewSteps.() -> Unit) = f(SuperLineHeightTextViewSteps())

@StepsDsl
class SuperLineHeightTextViewSteps : DivTestAssetSteps() {

    fun ActivityTestRule<*>.buildContainer(height: Int): Unit = step("Build container with height $height") {
        buildContainer(ViewGroup.LayoutParams.MATCH_PARENT, height)
    }

    fun assert(f: SuperLineHeightTextViewAssertions.() -> Unit) = f(SuperLineHeightTextViewAssertions())
}

private fun singleLineText() = onView(withText("найдётся всё"))
private fun multiLineText() = onView(withText("multi\nline"))

@StepsDsl
class SuperLineHeightTextViewAssertions {

    fun hasExtraSpacing(): Unit = step("Check text has extra spacing") {
        singleLineText().check(matches(withExtraSpacing()))
    }

    fun noExtraSpacing(): Unit = step("Check text has no extra spacing") {
        multiLineText().check(matches(withNoExtraSpacing()))
    }

    fun hasHeight(height: Int): Unit = step("Check text has height $height") {
        multiLineText().check(matches(withHeight(height)))
    }
}

private fun withExtraSpacing() = withTextView("with extra spacing") { item ->
    item.compoundPaddingTop + item.compoundPaddingBottom > 0
}

private fun withNoExtraSpacing() = withTextView("with no extra spacing") { item ->
    item.compoundPaddingTop + item.compoundPaddingBottom == 0
}

private fun withTextView(desc: String, block: (item: AppCompatTextView) -> Boolean) =
    object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description?) {
            description?.appendText(desc)
        }

        override fun matchesSafely(item: View?): Boolean {
            if (item !is AppCompatTextView) {
                return false
            }
            return block(item)
        }
    }

private fun withHeight(@Px height: Int): Matcher<View> = object : TypeSafeMatcher<View>() {
    override fun describeTo(description: Description?) {
        description?.appendText("with height $height")
    }

    override fun matchesSafely(item: View?): Boolean {
        return item?.height == height
    }
}
