package com.yandex.div.utils

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.FloatRange
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.action.Tap
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.Matcher


fun clickOutside(@FloatRange(from = 0.0, to = 1.0) bias: Float = 0.5f): ViewAction {
    return object : GeneralOutsideAction(bias) {

        private val precision = Press.FINGER.describePrecision()

        override fun getConstraints(): Matcher<View> = isDisplayed()

        override fun getDescription(): String = "click outside of view"

        override fun performOutside(uiController: UiController, view: View, location: IntArray) {
            val coordinates = floatArrayOf(location[X].toFloat(), location[Y].toFloat())
            Tap.SINGLE.sendTap(uiController, coordinates, precision, 0, 0)
        }
    }
}

fun swipeLeftOutside(
    distance: Int = 64,
    @FloatRange(from = 0.0, to = 1.0) bias: Float = 0.5f
): ViewAction {
    return object : GeneralOutsideAction(bias) {

        private val precision = Press.FINGER.describePrecision()

        override fun getConstraints(): Matcher<View> = isDisplayed()

        override fun getDescription(): String = "swipe left outside of view"

        override fun performOutside(uiController: UiController, view: View, location: IntArray) {
            val startCoordinates = floatArrayOf(location[X].toFloat() - distance / 2, location[Y].toFloat())
            val endCoordinates = floatArrayOf(location[X].toFloat() + distance / 2, location[Y].toFloat())
            Swipe.FAST.sendSwipe(uiController, startCoordinates, endCoordinates, precision)
        }
    }
}

private const val X = 0
private const val Y = 1

private abstract class GeneralOutsideAction(
    @FloatRange(from = 0.0, to = 1.0) private val bias: Float
) : ViewAction {

    override fun perform(uiController: UiController, view: View) {
        val viewLocation = intArrayOf(0, 0)
        view.getLocationInWindow(viewLocation)
        view.offsetLocationByWindowLayoutParams(viewLocation)

        val viewBounds = Rect(
            /* left = */ viewLocation[X],
            /* top = */ viewLocation[Y],
            /* right = */ viewLocation[X] + view.width,
            /* bottom = */ viewLocation[Y] + view.height
        )

        val windowBounds = Rect()
        view.getWindowVisibleDisplayFrame(windowBounds)

        val leftSpace = viewBounds.left - windowBounds.left
        val topSpace = viewBounds.top - windowBounds.top
        val rightSpace = windowBounds.right - viewBounds.right
        val bottomSpace = windowBounds.bottom - viewBounds.bottom

        val actionLocation = when (intArrayOf(leftSpace, topSpace, rightSpace, bottomSpace).maxOrNull()) {
            leftSpace -> intArrayOf(viewBounds.left - (leftSpace * bias).toInt(), viewBounds.centerY())
            topSpace -> intArrayOf(viewBounds.centerX(), viewBounds.top - (topSpace * bias).toInt())
            rightSpace -> intArrayOf(viewBounds.right + (rightSpace * bias).toInt(), viewBounds.centerY())
            bottomSpace -> intArrayOf(viewBounds.centerX(), viewBounds.bottom + (bottomSpace * bias).toInt())
            else -> return
        }

        performOutside(uiController, view, actionLocation)
    }

    abstract fun performOutside(uiController: UiController, view: View, location: IntArray)
}

private fun View.offsetLocationByWindowLayoutParams(location: IntArray) {
    val parentView = parent as? ViewGroup ?: return
    val layoutParams = parentView.layoutParams
    if (layoutParams is WindowManager.LayoutParams) {
        location[X] = location[X] + layoutParams.x
        location[Y] = location[Y] + layoutParams.y
        return
    } else {
        parentView.offsetLocationByWindowLayoutParams(location)
    }
}
