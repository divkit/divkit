package com.yandex.div.utils

import android.graphics.Point
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import org.hamcrest.Matcher

/**
 * Helper functions for working with view coordinates in UI tests.
 */
object ViewCoordinatesHelper {
    
    /**
     * Gets the center coordinates of a view.
     * @param matcher The matcher to find the view
     * @return Point with x, y coordinates of the center of the view
     */
    fun getViewCenterCoordinates(matcher: Matcher<View>): Point {
        val coordinates = IntArray(2)
        
        onView(matcher).perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isDisplayed()
            }

            override fun getDescription(): String {
                return "Get view center coordinates"
            }

            override fun perform(uiController: UiController, view: View) {
                view.getLocationOnScreen(coordinates)
                coordinates[0] += view.width / 2
                coordinates[1] += view.height / 2
            }
        })
        
        return Point(coordinates[0], coordinates[1])
    }
    
    /**
     * Creates a ViewAction that clicks at specific coordinates on the screen.
     * @param x The x coordinate to click at
     * @param y The y coordinate to click at
     * @return A ViewAction that performs the click
     */
    fun clickAtPosition(x: Int, y: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isRoot()
            }

            override fun getDescription(): String {
                return "Click at position ($x, $y)"
            }

            override fun perform(uiController: UiController, view: View) {
                val downTime = SystemClock.uptimeMillis()
                val eventTime = SystemClock.uptimeMillis()

                val downEvent = MotionEvent.obtain(
                    downTime, eventTime, MotionEvent.ACTION_DOWN, 
                    x.toFloat(), y.toFloat(), 0
                )
                
                val upEvent = MotionEvent.obtain(
                    downTime, eventTime + 100, MotionEvent.ACTION_UP, 
                    x.toFloat(), y.toFloat(), 0
                )

                uiController.injectMotionEvent(downEvent)
                uiController.loopMainThreadForAtLeast(100) // Wait a bit between events
                uiController.injectMotionEvent(upEvent)
                
                uiController.loopMainThreadForAtLeast(300)

                downEvent.recycle()
                upEvent.recycle()
            }
        }
    }
}
