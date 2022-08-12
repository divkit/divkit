package com.yandex.div.steps

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.PointF
import android.net.Uri
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.InjectEventSecurityException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import com.yandex.test.util.StepsDsl
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.Matcher
import ru.tinkoff.allure.Step.Companion.step

internal fun pinchToZoom(f: PinchToZoomSteps.() -> Unit) = f(PinchToZoomSteps())

@StepsDsl
internal class PinchToZoomSteps : DivTestAssetSteps() {

    init {
        testAsset = "scenarios/pinch-to-zoom-longtap-actions.json"
    }

    fun withIntending(block: () -> Unit) {
        Intents.init()
        Intents.intending(anyIntent())
            .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))
        try {
            block()
        } finally {
            Intents.release()
        }
    }

    fun longClickOnImage(): Unit = step("Long click on image") {
        imageView().perform(longClick())
    }

    fun zoomAndHold(): Unit = step("Zoom and hold") {
        imageView().perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> = isDisplayed()

            override fun getDescription(): String = "zoom and hold"

            override fun perform(uiController: UiController?, view: View?) {
                val location = GeneralLocation.CENTER
                val (x, y) = location.calculateCoordinates(view)

                val startSpan = 100f
                val endSpan = view!!.width - 100f

                val startPoint1 = PointF(x - startSpan * .5f, y)
                val startPoint2 = PointF(x + startSpan * .5f, y)
                val endPoint1 = PointF(x - endSpan * .5f, y)
                val endPoint2 = PointF(x + endSpan * .5f, y)

                performPinch(uiController!!, startPoint1, startPoint2, endPoint1, endPoint2)
            }
        })
    }

    fun assert(f: PinchToZoomAssertions.() -> Unit) = f(PinchToZoomAssertions())
}

@StepsDsl
internal class PinchToZoomAssertions {

    fun checkNoLongAction(): Unit = step("Check no long actions") {
        Intents.assertNoUnverifiedIntents()
    }

    fun checkLongAction(): Unit = step("Check long action") {
        Intents.intended(
            allOf(
                hasAction(Intent.ACTION_VIEW), hasData(Uri.parse("tel:0987654321"))
            )
        )
    }
}

private fun imageView() = onView(withClassName(containsString(ImageView::class.java.simpleName)))

private const val DEFAULT_GESTURE_DURATION = 500

// See https://github.com/mapbox/mapbox-gestures-android/blob/6fc34d91fc8d8758c51830127c49223624d446aa/app/src/androidTest/java/com/mapbox/android/gestures/GesturesUiTestUtils.kt#L112
private fun performPinch(
    uiController: UiController,
    startPoint1: PointF,
    startPoint2: PointF,
    endPoint1: PointF,
    endPoint2: PointF
) {
    val eventMinInterval: Long = 10
    val startTime = SystemClock.uptimeMillis()
    var eventTime = startTime
    var event: MotionEvent
    var eventX1: Float = startPoint1.x
    var eventY1: Float = startPoint1.y
    var eventX2: Float = startPoint2.x
    var eventY2: Float = startPoint2.y

    // Specify the property for the two touch points
    val properties = arrayOfNulls<MotionEvent.PointerProperties>(2)
    val pp1 = MotionEvent.PointerProperties()
    pp1.id = 0
    pp1.toolType = MotionEvent.TOOL_TYPE_FINGER
    val pp2 = MotionEvent.PointerProperties()
    pp2.id = 1
    pp2.toolType = MotionEvent.TOOL_TYPE_FINGER

    properties[0] = pp1
    properties[1] = pp2

    // Specify the coordinations of the two touch points
    // NOTE: you MUST set the pressure and size value, or it doesn't work
    val pointerCoords = arrayOfNulls<MotionEvent.PointerCoords>(2)
    val pc1 = MotionEvent.PointerCoords()
    pc1.x = eventX1
    pc1.y = eventY1
    pc1.pressure = 1f
    pc1.size = 1f
    val pc2 = MotionEvent.PointerCoords()
    pc2.x = eventX2
    pc2.y = eventY2
    pc2.pressure = 1f
    pc2.size = 1f
    pointerCoords[0] = pc1
    pointerCoords[1] = pc2

    /*
     * Events sequence of zoom gesture:
     *
     * 1. Send ACTION_DOWN event of one start point
     * 2. Send ACTION_POINTER_DOWN of two start points
     * 3. Send ACTION_MOVE of two middle points
     * 4. Repeat step 3 with updated middle points (x,y), until reach the end points
     * 5. Send ACTION_POINTER_UP of two end points
     * 6. Send ACTION_UP of one end point
     */

    try {
        // Step 1
        event = MotionEvent.obtain(
            startTime, eventTime,
            MotionEvent.ACTION_DOWN, 1, properties,
            pointerCoords, 0, 0, 1f, 1f, 0, 0, 0, 0
        )
        injectMotionEventToUiController(uiController, event)

        // Step 2
        event = MotionEvent.obtain(
            startTime,
            eventTime,
            MotionEvent.ACTION_POINTER_DOWN + (pp2.id shl MotionEvent.ACTION_POINTER_INDEX_SHIFT),
            2,
            properties,
            pointerCoords,
            0,
            0,
            1f,
            1f,
            0,
            0,
            0,
            0
        )
        injectMotionEventToUiController(uiController, event)

        // Step 3, 4
        val moveEventNumber = DEFAULT_GESTURE_DURATION / eventMinInterval

        val stepX1: Float = (endPoint1.x - startPoint1.x) / moveEventNumber
        val stepY1: Float = (endPoint1.y - startPoint1.y) / moveEventNumber
        val stepX2: Float = (endPoint2.x - startPoint2.x) / moveEventNumber
        val stepY2: Float = (endPoint2.y - startPoint2.y) / moveEventNumber

        for (i in 0 until moveEventNumber) {
            // Update the move events
            eventTime += eventMinInterval
            eventX1 += stepX1
            eventY1 += stepY1
            eventX2 += stepX2
            eventY2 += stepY2

            pc1.x = eventX1
            pc1.y = eventY1
            pc2.x = eventX2
            pc2.y = eventY2

            pointerCoords[0] = pc1
            pointerCoords[1] = pc2

            event = MotionEvent.obtain(
                startTime, eventTime,
                MotionEvent.ACTION_MOVE, 2, properties,
                pointerCoords, 0, 0, 1f, 1f, 0, 0, 0, 0
            )
            injectMotionEventToUiController(uiController, event)
        }

        // Step 5
        pc1.x = endPoint1.x
        pc1.y = endPoint1.y
        pc2.x = endPoint2.x
        pc2.y = endPoint2.y
        pointerCoords[0] = pc1
        pointerCoords[1] = pc2

        eventTime += eventMinInterval
        event = MotionEvent.obtain(
            startTime,
            eventTime,
            MotionEvent.ACTION_POINTER_UP + (pp2.id shl MotionEvent.ACTION_POINTER_INDEX_SHIFT),
            2,
            properties,
            pointerCoords,
            0,
            0,
            1f,
            1f,
            0,
            0,
            0,
            0
        )
        injectMotionEventToUiController(uiController, event)

        // Step 6
        eventTime += eventMinInterval
        event = MotionEvent.obtain(
            startTime, eventTime,
            MotionEvent.ACTION_UP, 1, properties,
            pointerCoords, 0, 0, 1f, 1f, 0, 0, 0, 0
        )
        injectMotionEventToUiController(uiController, event)
    } catch (ex: InjectEventSecurityException) {
        throw RuntimeException("Could not perform pinch", ex)
    }
}

@Throws(InjectEventSecurityException::class)
private fun injectMotionEventToUiController(uiController: UiController, event: MotionEvent) {
    val injectEventSucceeded = uiController.injectMotionEvent(event)
    if (!injectEventSucceeded) {
        throw IllegalStateException("Error performing event $event")
    }
}
