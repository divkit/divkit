package com.yandex.div.view

import android.graphics.Color
import android.view.View
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.util.HumanReadables
import com.yandex.div.view.ViewMatchers.positionMatch
import com.yandex.test.util.ColorUtils
import com.yandex.test.util.Drawable.getAverageBackgroundColorForViewDrawable
import com.yandex.test.util.Report.step
import org.hamcrest.MatcherAssert.assertThat

/**
 * Provides assertions for all types of views.
 * */
object ViewAssertions {

    fun doesNotExistOrGone(): ViewAssertion {
        return ViewAssertion { view, _ ->
            view?.let {
                assertThat(
                    "View is visible or exists: ${HumanReadables.describe(view)}",
                    view.visibility == View.GONE || view.visibility == View.INVISIBLE
                )
            }
        }
    }

    fun doesExistAndVisible(): ViewAssertion {
        return ViewAssertion { view, ex ->
            view?.let {
                assertThat(
                    "View is invisible: ${HumanReadables.describe(view)}",
                    view.visibility == View.VISIBLE
                )
            } ?: throw ViewDoesNotExistsException("View does not exist: ${ex.viewMatcherDescription}")
        }
    }

    fun isOnDarkSide() = ViewAssertion { view, ex ->
        view?.let {
            val color = findBgColorUnder(view)
            assertThat(
                "The color ${Integer.toHexString(color)} under the view is not dark: ${HumanReadables.describe(view)}",
                ColorUtils.isDarkColor(color)
            )
        } ?: throw ViewDoesNotExistsException("The color under the view is not dark ${ex.viewMatcherDescription}")
    }

    private fun findBgColorUnder(child: View): Int {
        var color: Int
        var view: View? = child
        do {
            color = view?.background?.let { getAverageBackgroundColorForViewDrawable(it) } ?: 0
            view = view?.parent as? View
        } while (view != null && Color.alpha(color) < 128)
        return ColorUtils.alphaBlend(Color.WHITE, color)
    }

    fun hasChangedPosition(view: ViewInteraction, lastPosition: FloatArray): Unit =
        step("Assert view has changed position") {
            checkPosition(shouldMatch = false, view, lastPosition)
        }

    fun hasNotChangedPosition(view: ViewInteraction, lastPosition: FloatArray): Unit =
        step("Assert view has not changed position") {
            checkPosition(shouldMatch = true, view, lastPosition)
        }

    private fun checkPosition(
        shouldMatch: Boolean,
        view: ViewInteraction,
        lastPosition: FloatArray
    ) = view.check(matches(positionMatch(shouldMatch, lastPosition)))
}

class ViewDoesNotExistsException(message: String) : RuntimeException(message)
