package com.yandex.div.core.view2

import android.app.Activity
import android.os.Looper
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.test.platform.app.InstrumentationRegistry
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivKit
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.view2.divs.widgets.DivInputView
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController

@RunWith(RobolectricTestRunner::class)
class Div2ViewInputFocusTouchTest {
    private val imageLoader = mock<DivImageLoader>()
    private var activity: ActivityController<Activity>? = null
    private val divViews = mutableListOf<Div2View>()
    private var clicks = 0

    @After
    fun tearDown() {
        divViews.forEach { it.cleanup() }
        activity?.pause()?.stop()?.destroy()
        shadowOf(Looper.getMainLooper()).idle()
        DivKit.resetSingletonForTesting()
    }

    @Test
    fun `configuration defaults to consuming touches and keeps instances independent`() {
        val builder = DivConfiguration.Builder(imageLoader)
        val original = builder.build()
        val disabled = builder.consumeTouchesToClearInputFocus(false).build()

        assertTrue(original.consumeTouchesToClearInputFocus)
        assertFalse(disabled.consumeTouchesToClearInputFocus)
        assertTrue(DivConfiguration.Builder(imageLoader).build().consumeTouchesToClearInputFocus)
    }

    @Test
    fun `default background tap clears focus without selecting sibling suggestion`() {
        val screen = createScreen()

        screen.tap()

        assertFalse(screen.input.isFocused)
        assertEquals(0, clicks)
        screen.tap()
        assertEquals(1, clicks)
    }

    @Test
    fun `disabled capture selects sibling suggestion on first tap`() {
        val screen = createScreen(consumeTouches = false)

        screen.tap()

        assertEquals(1, clicks)
        assertFalse(screen.input.isFocused)
    }

    @Test
    fun `disabled capture allows repeated sibling swipes while input stays focused`() {
        val screen = createScreen(consumeTouches = false)

        screen.swipe()
        val firstOffset = screen.scroll.scrollY
        screen.swipe()

        assertTrue(firstOffset > 0)
        assertTrue(screen.scroll.scrollY > firstOffset)
        assertTrue(screen.input.isFocused)
        assertEquals(0, clicks)
    }

    @Test
    fun `default capture blocks sibling swipe while input stays focused`() {
        val screen = createScreen()

        screen.swipe()

        assertEquals(0, screen.scroll.scrollY)
        assertTrue(screen.input.isFocused)
        assertEquals(0, clicks)
    }

    @Test
    fun `cancelled sibling tap does not select and next tap works`() {
        val screen = createScreen(consumeTouches = false)
        val downTime = SystemClock.uptimeMillis()
        screen.dispatch(downTime, downTime, MotionEvent.ACTION_DOWN, 600f)
        screen.dispatch(downTime, downTime + 20, MotionEvent.ACTION_CANCEL, 600f)
        shadowOf(Looper.getMainLooper()).idle()

        assertEquals(0, clicks)
        assertTrue(screen.input.isFocused)
        screen.tap()
        assertEquals(1, clicks)
    }

    @Test
    fun `disabled capture lets input regain focus on tap`() {
        val screen = createScreen(consumeTouches = false)
        assertTrue(screen.root.requestFocus())
        assertFalse(screen.input.isFocused)
        val downTime = SystemClock.uptimeMillis()

        screen.dispatch(downTime, downTime, MotionEvent.ACTION_DOWN, 40f)
        screen.dispatch(downTime, downTime + 20, MotionEvent.ACTION_UP, 40f)

        assertTrue(screen.input.isFocused)
        assertEquals(0, clicks)
    }

    private fun createScreen(consumeTouches: Boolean? = null): Screen {
        val builder = DivConfiguration.Builder(imageLoader).actionHandler(object : DivActionHandler() {
            override fun handleAction(
                action: DivAction,
                view: DivViewFacade,
                resolver: ExpressionResolver,
            ): Boolean {
                clicks++
                return true
            }
        })
        consumeTouches?.let { builder.consumeTouchesToClearInputFocus(it) }
        val context = Div2Context(testContextThemeWrapper(), builder.build())
        val root = FrameLayout(context).apply {
            isFocusableInTouchMode = true
        }
        val suggestion = Div2View(context).also { divViews += it }
        suggestion.setData(DivData(
            DivParsingEnvironment(ParsingErrorLogger.ASSERT),
            JSONObject("""
                {"log_id":"suggestions","states":[{"state_id":0,"div":{
                  "type":"container","height":{"type":"fixed","value":3000},
                  "items":[{"type":"text","text":"Suggestion","font_size":16,
                    "height":{"type":"fixed","value":3000},
                    "actions":[{"log_id":"select","url":"test://select"}]}]
                }}]}
            """.trimIndent()),
        ), DivDataTag("suggestions"))
        val scroll = ScrollView(context).apply {
            addView(suggestion)
        }
        root.addView(scroll, FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
        val overlay = Div2View(context).also { divViews += it }
        val input = DivInputView(context).apply {
            tag = "input"
            isFocusableInTouchMode = true
            focusTracker = overlay.inputFocusTracker
        }
        overlay.addView(input)
        input.layoutParams.width = 200
        input.layoutParams.height = 100
        root.addView(overlay, FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
        InstrumentationRegistry.getInstrumentation().setInTouchMode(true)
        activity = Robolectric.buildActivity(Activity::class.java).setup().also {
            it.get().setContentView(root)
            it.visible()
        }
        root.measure(
            View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY),
        )
        root.layout(0, 0, 1000, 1000)
        assertTrue(input.requestFocus())
        assertTrue(overlay.inputFocusTracker.isFocusedOnInput())
        assertTrue(overlay.isInTouchMode)
        return Screen(root, input, scroll)
    }

    private class Screen(val root: FrameLayout, val input: DivInputView, val scroll: ScrollView) {
        fun tap() {
            val downTime = SystemClock.uptimeMillis()
            dispatch(downTime, downTime, MotionEvent.ACTION_DOWN, 600f)
            dispatch(downTime, downTime + 20, MotionEvent.ACTION_UP, 600f)
            shadowOf(Looper.getMainLooper()).idle()
        }

        fun swipe() {
            val downTime = SystemClock.uptimeMillis()
            dispatch(downTime, downTime, MotionEvent.ACTION_DOWN, 800f)
            dispatch(downTime, downTime + 100, MotionEvent.ACTION_MOVE, 700f)
            dispatch(downTime, downTime + 200, MotionEvent.ACTION_MOVE, 500f)
            dispatch(downTime, downTime + 300, MotionEvent.ACTION_MOVE, 300f)
            dispatch(downTime, downTime + 400, MotionEvent.ACTION_UP, 300f)
        }

        fun dispatch(downTime: Long, eventTime: Long, action: Int, y: Float) {
            val event = MotionEvent.obtain(downTime, eventTime, action, 50f, y, 0)
            try {
                root.dispatchTouchEvent(event)
            } finally {
                event.recycle()
            }
        }
    }
}
