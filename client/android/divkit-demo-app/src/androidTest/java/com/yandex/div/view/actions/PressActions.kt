package com.yandex.div.view.actions

import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed

private var downTimeMs: Long? = null

fun pressDown(x: Float) = object : ViewAction {

    override fun getConstraints() = isDisplayed()

    override fun getDescription(): String = "Perform ACTION_DOWN on $x px from left view's edge"

    override fun perform(uiController: UiController, view: View) {
        val coordinates = GeneralLocation.CENTER_LEFT.calculateCoordinates(view)
        val downTime = SystemClock.uptimeMillis()
        downTimeMs = downTime
        val downEvent = MotionEvent.obtain(
            downTime,
            downTime,
            MotionEvent.ACTION_DOWN,
            coordinates[0] + x,
            coordinates[1],
            0,
        )
        uiController.injectMotionEvent(downEvent)
        downEvent.recycle()
    }
}

fun pressUp(x: Float) = object : ViewAction {

    override fun getConstraints() = isDisplayed()

    override fun getDescription(): String = "Perform ACTION_UP on $x px from left view's edge"

    override fun perform(uiController: UiController, view: View) {
        val coordinates = GeneralLocation.CENTER_LEFT.calculateCoordinates(view)
        val downTime = downTimeMs ?: SystemClock.uptimeMillis()
        val eventTime = SystemClock.uptimeMillis()
        val upEvent = MotionEvent.obtain(
            downTime,
            eventTime,
            MotionEvent.ACTION_UP,
            coordinates[0] + x,
            coordinates[1],
            0,
        )
        uiController.injectMotionEvent(upEvent)
        upEvent.recycle()
        downTimeMs = null
    }
}
