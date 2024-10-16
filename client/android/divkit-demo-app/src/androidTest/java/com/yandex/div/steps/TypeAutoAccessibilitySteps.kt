package com.yandex.div.steps

import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isFocusable
import androidx.test.espresso.matcher.ViewMatchers.isNotFocusable
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.rule.ActivityTestRule
import com.yandex.test.util.Report.step
import com.yandex.test.util.StepsDsl
import org.hamcrest.Matchers.`is`

private const val TEXT_ID = "text"
private const val INPUT_ID = "input"
private const val SELECT_ID = "select"
private const val IMAGE_ID = "image"
private const val IMAGE_WITH_DESCRIPTION_ID = "image with description"
private const val SLIDER_ID = "slider"
private const val SEPARATOR_ID = "separator"

private val textView get() = getViewWithId(TEXT_ID)
private val inputView get() = getViewWithId(INPUT_ID)
private val selectView get() = getViewWithId(SELECT_ID)
private val imageView get() = getViewWithId(IMAGE_ID)
private val imageWithDescriptionView get() = getViewWithId(IMAGE_WITH_DESCRIPTION_ID)
private val sliderView get() = getViewWithId(SLIDER_ID)
private val separatorView get() = getViewWithId(SEPARATOR_ID)

internal fun accessibility(f: TypeAutoAccessibilitySteps.() -> Unit) = f(TypeAutoAccessibilitySteps())

@StepsDsl
internal class TypeAutoAccessibilitySteps: DivTestAssetSteps() {

    fun ActivityTestRule<*>.buildContainer(): Unit = step("Build container") {
        buildContainer(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    internal fun assert(f: TypeAutoAccessibilityAssertions.() -> Unit) = f(TypeAutoAccessibilityAssertions())
}

@StepsDsl
internal class TypeAutoAccessibilityAssertions {

    fun checkTextIsFocusable(): Unit = step("Check text view is focusable ") {
        textView.checkFocusable()
    }
    fun checkInputIsFocusable(): Unit = step("Check input view is focusable ") {
        inputView.checkFocusable()
    }
    fun checkSelectIsFocusable(): Unit = step("Check select view is focusable ") {
        selectView.checkFocusable()
    }
    fun checkImageWithDescriptionIsFocusable(): Unit =
        step("Check image with description is focusable ") {
            imageWithDescriptionView.checkFocusable()
        }
    fun checkSeparatorIsNotFocusable(): Unit = step("Check separator is not focusable") {
        separatorView.checkNotFocusable()
    }
    fun checkImageWithoutDescriptionIsNotFocusable(): Unit =
        step("Check image without description is not focusable") {
            imageView.checkNotFocusable()
        }
    fun checkSliderIsNotFocusable(): Unit = step("Check slider is not focusable") {
        sliderView.checkNotFocusable() // only it's virtual view should be focusable
    }

    private fun ViewInteraction.checkFocusable(): Unit {
        check(matches(isFocusable()))
    }

    private fun ViewInteraction.checkNotFocusable(): Unit {
        check(matches(isNotFocusable()))
    }
}

private fun getViewWithId(id: String) = onView(withTagValue(`is`(id)))
