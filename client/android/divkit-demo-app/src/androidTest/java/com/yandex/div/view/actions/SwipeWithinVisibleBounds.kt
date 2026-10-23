package com.yandex.div.view.actions

import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.yandex.div.test.crossplatform.clipSwipeToBounds
import org.hamcrest.Matcher

class SwipeWithinVisibleBounds(
    private val normalizedFrom: PointF,
    private val normalizedTo: PointF
) : ViewAction {

    override fun getConstraints(): Matcher<View> = isDisplayed()

    override fun getDescription() = "Swipe within the view's visible bounds"

    override fun perform(uiController: UiController, view: View) {
        val visibleBounds = Rect()
        check(view.getLocalVisibleRect(visibleBounds)) { "Swipe target has no visible bounds" }

        val from = PointF(view.width * normalizedFrom.x, view.height * normalizedFrom.y)
        val to = PointF(view.width * normalizedTo.x, view.height * normalizedTo.y)
        clipSwipeToBounds(from, to, RectF(visibleBounds))

        val location = IntArray(2)
        view.getLocationOnScreen(location)
        GeneralSwipeAction(
            Swipe.FAST,
            { floatArrayOf(location[0] + from.x, location[1] + from.y) },
            { floatArrayOf(location[0] + to.x, location[1] + to.y) },
            Press.FINGER
        ).perform(uiController, view)
    }
}
