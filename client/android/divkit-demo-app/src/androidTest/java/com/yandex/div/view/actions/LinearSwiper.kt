package com.yandex.div.view.actions

import android.view.MotionEvent
import androidx.test.espresso.InjectEventSecurityException
import androidx.test.espresso.UiController
import androidx.test.espresso.action.MotionEvents
import androidx.test.espresso.action.Swiper

internal class LinearSwiper(
    private val holdDurationMs: Long,
    private val moveDurationMs: Long = MOVE_DURATION_MS,
) : Swiper {

    override fun sendSwipe(
        uiController: UiController,
        startCoordinates: FloatArray,
        endCoordinates: FloatArray,
        precision: FloatArray,
    ): Swiper.Status {
        val down = MotionEvents.obtainDownEvent(startCoordinates, precision)
        val events = mutableListOf<MotionEvent>(down)
        val moveSteps = (moveDurationMs / EVENT_PERIOD_MS).toInt()
        for (step in 1..moveSteps) {
            val fraction = step.toFloat() / moveSteps
            val point = FloatArray(2) { i ->
                startCoordinates[i] + (endCoordinates[i] - startCoordinates[i]) * fraction
            }
            val eventTime = down.downTime + moveDurationMs * step / moveSteps
            events += MotionEvents.obtainMovement(down, eventTime, point)
        }
        val moveEndTime = down.downTime + moveDurationMs
        var held = 0L
        while (held < holdDurationMs) {
            held += EVENT_PERIOD_MS
            events += MotionEvents.obtainMovement(down, moveEndTime + held, endCoordinates)
        }
        events += MotionEvents.obtainUpEvent(down, moveEndTime + held, endCoordinates)
        return try {
            if (uiController.injectMotionEventSequence(events)) {
                Swiper.Status.SUCCESS
            } else {
                Swiper.Status.FAILURE
            }
        } catch (e: InjectEventSecurityException) {
            Swiper.Status.FAILURE
        } finally {
            events.forEach { it.recycle() }
        }
    }
}

private const val MOVE_DURATION_MS = 100L
private const val EVENT_PERIOD_MS = 10L
