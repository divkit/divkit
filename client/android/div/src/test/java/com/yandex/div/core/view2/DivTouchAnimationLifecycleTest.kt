package com.yandex.div.core.view2

import android.app.Activity
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.transition.TransitionManager
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivKit
import com.yandex.div.test.data.action
import com.yandex.div.test.data.booleanExpression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.variable
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivAppearanceSetTransition
import com.yandex.div2.DivAppearanceTransition
import com.yandex.div2.DivContainer
import com.yandex.div2.DivFadeTransition
import com.yandex.div2.DivScaleTransition
import com.yandex.div2.DivText
import com.yandex.div2.DivVisibility
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowChoreographer
import java.time.Duration
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class DivTouchAnimationLifecycleTest {
    private val activity = Robolectric.buildActivity(Activity::class.java).setup()
    private val root = FrameLayout(activity.get())
    private val content = View(activity.get())
    private val context = Div2Context(
        testContextThemeWrapper(),
        DivConfiguration.Builder(mock()).enableBindOnAttach(false).build(),
    )
    private val divView = Div2View(context)
    private lateinit var target: View
    private lateinit var hidingView: View
    private var contentClicks = 0
    private var eventTime = 0L

    @BeforeTest
    fun setUp() {
        content.setOnClickListener { contentClicks++ }
        root.addView(content, FrameLayout.LayoutParams(200, 200))
        root.addView(divView, FrameLayout.LayoutParams(200, 200))
        activity.get().setContentView(root)
        activity.visible()
    }

    @AfterTest
    fun tearDown() {
        TransitionManager.endTransitions(divView)
        divView.cleanup()
        activity.pause().stop().destroy()
        DivKit.resetSingletonForTesting()
    }

    @Test
    fun `action expression refresh during down preserves press feedback and completes gesture`() {
        load(hideAncestor = false, clickUrl = "test://tap")
        dispatch(MotionEvent.ACTION_DOWN)
        val direct = assertNotNull(target.animation)

        divView.setVariable("tick", "1")

        assertSame(direct, target.animation)
        dispatch(MotionEvent.ACTION_UP)
        assertNotNull(target.animation)
        assertNotSame(direct, target.animation)
    }

    @Test
    fun `focus expression refresh preserves a running reverse animation`() {
        load(hideAncestor = false, clickUrl = "test://tap")
        dispatch(MotionEvent.ACTION_DOWN)
        dispatch(MotionEvent.ACTION_UP)
        val reverse = assertNotNull(target.animation)

        divView.setVariable("capture", "1")

        assertSame(reverse, target.animation)
    }

    @Test
    fun `action disabling itself does not interrupt its release animation`() {
        load(hideAncestor = false, clickUrl = "div-action://set_variable?name=tick&value=-1")
        dispatch(MotionEvent.ACTION_DOWN)
        dispatch(MotionEvent.ACTION_UP)
        val reverse = assertNotNull(target.animation)

        shadowOf(Looper.getMainLooper()).idle()

        assertSame(reverse, target.animation)
    }

    @Test
    fun `hiding after expression refresh clears the previously started animation`() {
        load(hideAncestor = false, clickUrl = "test://tap")
        dispatch(MotionEvent.ACTION_DOWN)
        dispatch(MotionEvent.ACTION_UP)
        val reverse = assertNotNull(target.animation)
        divView.setVariable("tick", "1")
        assertSame(reverse, target.animation)

        startDisappearance("gone")
        TransitionManager.endTransitions(divView)

        assertNull(target.animation)
        tap()
        assertEquals(1, contentClicks)
    }

    @Test
    fun `full data rebind invalidates the old press gesture`() {
        load(hideAncestor = false, clickUrl = "test://tap")
        dispatch(MotionEvent.ACTION_DOWN)
        val oldTarget = target
        assertNotNull(target.animation)

        load(hideAncestor = false, clickUrl = "test://tap")
        dispatch(MotionEvent.ACTION_UP)

        assertSame(oldTarget, target)
        assertNull(target.animation)
    }

    @Test
    fun `tap during outgoing fade does not block content after gone`() {
        checkDisappearance("gone", hideAncestor = false)
    }

    @Test
    fun `tap during outgoing fade does not block content after invisible`() {
        checkDisappearance("invisible", hideAncestor = false)
    }

    @Test
    fun `ancestor outgoing fade clears descendant press animation`() {
        checkDisappearance("gone", hideAncestor = true)
    }

    @Test
    fun `release after outgoing fade cannot restart press animation`() {
        load(hideAncestor = false, clickUrl = "test://tap")
        startDisappearance("gone")
        dispatch(MotionEvent.ACTION_DOWN)
        assertNotNull(target.animation)

        TransitionManager.endTransitions(divView)
        dispatch(MotionEvent.ACTION_UP)

        assertNull(target.animation)
        tap()
        assertEquals(1, contentClicks)
    }

    @Test
    fun `showing after outgoing fade allows a fresh press gesture`() {
        load(hideAncestor = false, clickUrl = "test://tap")
        startDisappearance("gone")
        TransitionManager.endTransitions(divView)
        divView.setVariable("visibility", "visible")
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(View.VISIBLE, hidingView.visibility)

        dispatch(MotionEvent.ACTION_DOWN)
        val direct = assertNotNull(target.animation)
        TransitionManager.endTransitions(divView)

        assertSame(direct, target.animation)
        dispatch(MotionEvent.ACTION_UP)
        assertNotNull(target.animation)
        assertNotSame(direct, target.animation)
    }

    @Test
    fun `short outgoing transition clears press animation before the set ends for gone`() {
        checkCompositeDisappearance("gone", hideAncestor = false, nested = false)
    }

    @Test
    fun `short outgoing transition clears press animation before the set ends for invisible`() {
        checkCompositeDisappearance("invisible", hideAncestor = false, nested = false)
    }

    @Test
    fun `short outgoing transition clears descendant press animation before the set ends`() {
        checkCompositeDisappearance("gone", hideAncestor = true, nested = false)
    }

    @Test
    fun `short nested outgoing transition clears press animation before the outer set ends`() {
        checkCompositeDisappearance("gone", hideAncestor = false, nested = true)
    }

    private fun checkCompositeDisappearance(visibility: String, hideAncestor: Boolean, nested: Boolean) {
        ShadowChoreographer.setPaused(true)
        ShadowChoreographer.setFrameDelay(Duration.ofMillis(16))
        val fade = DivAppearanceTransition.Fade(DivFadeTransition(duration = constant(100L)))
        val shortTransition = if (nested) DivAppearanceTransition.Set(DivAppearanceSetTransition(
            items = listOf(fade),
        )) else fade
        val transition = DivAppearanceTransition.Set(DivAppearanceSetTransition(items = listOf(
            shortTransition,
            DivAppearanceTransition.Scale(DivScaleTransition(
                duration = constant(1000L), scale = constant(0.8),
            )),
        )))
        load(hideAncestor, "test://tap", transition)
        shadowOf(Looper.getMainLooper()).idle()
        root.suppressLayout(true)
        startDisappearance(visibility)
        dispatch(MotionEvent.ACTION_DOWN)
        dispatch(MotionEvent.ACTION_UP)
        assertNotNull(target.animation)

        repeat(20) {
            shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(16))
        }

        assertNotNull(divView.viewComponent.transitionHandler.getLastChange(hidingView))
        assertEquals(if (visibility == "gone") View.GONE else View.INVISIBLE, hidingView.visibility)
        val retainedAnimation = target.animation
        tap()
        assertEquals(1, contentClicks)
        assertNull(retainedAnimation)
    }

    private fun checkDisappearance(visibility: String, hideAncestor: Boolean) {
        load(hideAncestor = hideAncestor, clickUrl = "test://tap")
        startDisappearance(visibility)
        dispatch(MotionEvent.ACTION_DOWN)
        dispatch(MotionEvent.ACTION_UP)
        assertNotNull(target.animation)

        TransitionManager.endTransitions(divView)

        assertEquals(if (visibility == "gone") View.GONE else View.INVISIBLE, hidingView.visibility)
        val retainedAnimation = target.animation
        tap()
        assertEquals(1, contentClicks)
        assertNull(retainedAnimation)
    }

    private fun startDisappearance(visibility: String) {
        assertTrue(divView.isAttachedToWindow)
        assertTrue(divView.isLaidOut)
        assertTrue(divView.div2Component.animationsEnabledController.isEnabled())
        divView.setVariable("visibility", visibility)
        divView.viewComponent.transitionHandler.runTransitions(divView, endTransitions = true)
        assertNotNull(divView.viewComponent.transitionHandler.getLastChange(hidingView))
        divView.viewTreeObserver.dispatchOnPreDraw()
        assertEquals(View.VISIBLE, hidingView.visibility)
    }

    private fun load(hideAncestor: Boolean, clickUrl: String) {
        load(hideAncestor, clickUrl, DivAppearanceTransition.Fade(DivFadeTransition(duration = constant(200L))))
    }

    private fun load(hideAncestor: Boolean, clickUrl: String, transition: DivAppearanceTransition) {
        val text = Div.Text(DivText(
            id = "touch-target", text = constant("Tap"), width = fixed(200), height = fixed(200),
            actions = listOf(DivAction(
                logId = constant("tap"), url = action(url = clickUrl).url,
                isEnabled = booleanExpression("@{tick >= 0}"),
            )),
            captureFocusOnAction = booleanExpression("@{capture > 0}"),
            visibility = if (hideAncestor) constant(DivVisibility.VISIBLE) else visibilityExpression("@{visibility}"),
            transitionOut = if (hideAncestor) null else transition,
        ))
        val element = if (hideAncestor) Div.Container(DivContainer(
            id = "touch-parent", width = fixed(200), height = fixed(200), items = listOf(text),
            visibility = visibilityExpression("@{visibility}"), transitionOut = transition,
        )) else text
        divView.setData(data(element, variables = listOf(
            variable("visibility", "visible"), variable("tick", 0), variable("capture", 0),
        )), DivDataTag("touch-lifecycle"))
        root.measure(
            View.MeasureSpec.makeMeasureSpec(200, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(200, View.MeasureSpec.EXACTLY),
        )
        root.layout(0, 0, 200, 200)
        target = divView.findViewWithTag("touch-target")
        hidingView = if (hideAncestor) divView.findViewWithTag("touch-parent") else target
    }

    private fun tap() {
        dispatch(MotionEvent.ACTION_DOWN)
        dispatch(MotionEvent.ACTION_UP)
        shadowOf(Looper.getMainLooper()).idle()
    }

    private fun dispatch(action: Int) {
        eventTime += 10
        val event = MotionEvent.obtain(0L, eventTime, action, 50f, 50f, 0)
        try {
            root.dispatchTouchEvent(event)
        } finally {
            event.recycle()
        }
    }
}
