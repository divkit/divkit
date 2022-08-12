package com.yandex.div.view

import android.graphics.Color
import android.view.View
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.util.HumanReadables
import com.yandex.test.util.ColorUtils
import com.yandex.test.util.Drawable.getAverageBackgroundColorForViewDrawable
import org.hamcrest.MatcherAssert.assertThat

/**
 * Checks for view exists and visible or not does not exist or not visible
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

}

class ViewDoesNotExistsException(message: String) : RuntimeException(message)
