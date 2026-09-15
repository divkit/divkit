package com.yandex.div.core.view2.divs.widgets

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowChoreographer
import org.robolectric.shadows.ShadowLooper
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
internal class ParentRecyclerViewAwaiterTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun pauseChoreographer() {
        ShadowChoreographer.setPaused(true)
        ShadowChoreographer.setFrameDelay(FRAME_DELAY)
    }

    @Test
    fun `waits for attachment and completes immediately without recycler parent`() = runTest {
        val view = View(context)
        var completed = false
        launch(start = CoroutineStart.UNDISPATCHED) {
            view.awaitParentRecyclersStableIdle()
            completed = true
        }

        assertFalse(completed)

        attach(view)
        advanceFrame()

        assertTrue(completed)
    }

    @Test
    fun `waits for two idle frames from every recycler parent`() = runTest {
        val view = View(context)
        val innerRecycler = TestRecyclerView(context).apply { addTestChild(view) }
        val outerRecycler = TestRecyclerView(context, RecyclerView.SCROLL_STATE_SETTLING).apply {
            addTestChild(innerRecycler)
        }
        attach(outerRecycler)
        var completed = false
        launch(start = CoroutineStart.UNDISPATCHED) {
            view.awaitParentRecyclersStableIdle()
            completed = true
        }

        advanceFrame()
        outerRecycler.state = RecyclerView.SCROLL_STATE_IDLE
        advanceFrame()

        assertFalse(completed)

        advanceFrame()

        assertTrue(completed)
    }

    @Test
    fun `computing layout breaks consecutive idle frames`() = runTest {
        val view = View(context)
        val recycler = TestRecyclerView(context).apply { addTestChild(view) }
        attach(recycler)
        var completed = false
        launch(start = CoroutineStart.UNDISPATCHED) {
            view.awaitParentRecyclersStableIdle()
            completed = true
        }

        advanceFrame()
        recycler.computingLayout = true
        advanceFrame()
        recycler.computingLayout = false
        advanceFrame()

        assertFalse(completed)

        advanceFrame()

        assertTrue(completed)
    }

    @Test
    fun `requested layout breaks consecutive idle frames before traversal`() = runTest {
        val recycler = attachRecyclerWithAdapter()
        val view = recycler.getChildAt(0)
        var completed = false
        launch(start = CoroutineStart.UNDISPATCHED) {
            view.awaitParentRecyclersStableIdle()
            completed = true
        }

        advanceFrame()
        recycler.requestLayout()
        assertTrue(recycler.isLayoutRequested)
        assertFalse(recycler.isComputingLayout)

        advanceFrame()

        assertFalse(completed)
        assertFalse(recycler.isLayoutRequested)

        advanceFrame()
        assertFalse(completed)
        advanceFrame()
        assertTrue(completed)
    }

    @Ignore("Flaky test")
    @Test
    fun `pending adapter update breaks consecutive idle frames before traversal`() = runTest {
        val recycler = attachRecyclerWithAdapter()
        val view = recycler.getChildAt(0)
        var completed = false
        launch(start = CoroutineStart.UNDISPATCHED) {
            view.awaitParentRecyclersStableIdle()
            completed = true
        }

        advanceFrame()
        recycler.adapter!!.notifyItemChanged(0)
        assertTrue(recycler.hasPendingAdapterUpdates())
        assertFalse(recycler.isLayoutRequested)
        assertFalse(recycler.isComputingLayout)

        advanceFrame()

        assertFalse(completed)
        assertFalse(recycler.hasPendingAdapterUpdates())

        advanceFrame()
        assertFalse(completed)
        advanceFrame()
        assertTrue(completed)
    }

    @Test
    fun `nonzero scrolling from every parent keeps resetting idle frames`() = runTest {
        val view = View(context)
        val innerRecycler = TestRecyclerView(context).apply { addTestChild(view) }
        val outerRecycler = TestRecyclerView(context).apply { addTestChild(innerRecycler) }
        attach(outerRecycler)
        var completed = false
        launch(start = CoroutineStart.UNDISPATCHED) {
            view.awaitParentRecyclersStableIdle()
            completed = true
        }

        advanceFrame()
        repeat(6) { index ->
            if (index % 2 == 0) {
                innerRecycler.dispatchScrolled(1, 0)
            } else {
                outerRecycler.dispatchScrolled(0, -1)
            }
            advanceFrame()
            assertFalse(completed)
        }

        advanceFrame()

        assertTrue(completed)
        assertEquals(0, innerRecycler.scrollListenerCount)
        assertEquals(0, outerRecycler.scrollListenerCount)
    }

    @Test
    fun `layout only scroll callbacks do not restart idle frames`() = runTest {
        val view = View(context)
        val recycler = TestRecyclerView(context).apply { addTestChild(view) }
        attach(recycler)
        var completed = false
        launch(start = CoroutineStart.UNDISPATCHED) {
            view.awaitParentRecyclersStableIdle()
            completed = true
        }

        repeat(2) {
            recycler.dispatchScrolled(0, 0)
            advanceFrame()
        }

        assertTrue(completed)
    }

    @Test
    fun `detach and reparent reset consecutive idle frames`() = runTest {
        val view = View(context)
        val firstRecycler = TestRecyclerView(context).apply { addTestChild(view) }
        val secondRecycler = TestRecyclerView(context)
        attach(FrameLayout(context).apply {
            addView(firstRecycler)
            addView(secondRecycler)
        })
        var completed = false
        launch(start = CoroutineStart.UNDISPATCHED) {
            view.awaitParentRecyclersStableIdle()
            completed = true
        }

        advanceFrame()
        firstRecycler.removeView(view)
        assertEquals(0, firstRecycler.scrollListenerCount)
        advanceFrame()
        secondRecycler.addTestChild(view)
        assertEquals(1, secondRecycler.scrollListenerCount)
        assertTrue(secondRecycler.isLayoutRequested)
        advanceFrame()

        assertFalse(completed)
        assertFalse(secondRecycler.isLayoutRequested)

        advanceFrame()

        assertFalse(completed)

        advanceFrame()

        assertTrue(completed)
        assertEquals(0, secondRecycler.scrollListenerCount)
    }

    @Test
    fun `cancellation removes frame callback and attach listener`() = runTest {
        val view = TrackingView(context)
        val recycler = TestRecyclerView(context).apply { addTestChild(view) }
        attach(recycler)
        val job = launch(start = CoroutineStart.UNDISPATCHED) {
            view.awaitParentRecyclersStableIdle()
        }

        assertEquals(1, view.attachListenerCount)
        assertEquals(1, recycler.scrollListenerCount)

        job.cancel()

        assertEquals(0, view.attachListenerCount)
        assertEquals(0, recycler.scrollListenerCount)
        assertTrue(view.removeCallbacksCount > 0)
    }

    private fun attach(content: View): Activity {
        return Robolectric.buildActivity(Activity::class.java).setup().get().also {
            it.setContentView(content)
            ShadowLooper.idleMainLooper(FRAME_DELAY.toMillis(), TimeUnit.MILLISECONDS)
        }
    }

    private fun attachRecyclerWithAdapter(): RecyclerView {
        return RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = null
            setHasFixedSize(true)
            adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                    return object : RecyclerView.ViewHolder(View(context).apply {
                        layoutParams = RecyclerView.LayoutParams(100, 100)
                    }) {}
                }

                override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = Unit

                override fun getItemCount(): Int = 1
            }
            attach(this)
            assertEquals(1, childCount)
            assertFalse(isLayoutRequested)
            assertFalse(hasPendingAdapterUpdates())
        }
    }

    private fun TestScope.advanceFrame() {
        ShadowLooper.idleMainLooper(FRAME_DELAY.toMillis(), TimeUnit.MILLISECONDS)
        testScheduler.runCurrent()
    }

    private fun TestRecyclerView.addTestChild(view: View) {
        addView(
            view,
            RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.MATCH_PARENT,
            ),
        )
    }

    private open class TestRecyclerView(
        context: Context,
        var state: Int = RecyclerView.SCROLL_STATE_IDLE,
    ) : RecyclerView(context) {

        init {
            layoutManager = LinearLayoutManager(context)
        }

        var computingLayout = false
        private val scrollListeners = mutableSetOf<OnScrollListener>()
        val scrollListenerCount: Int get() = scrollListeners.size

        override fun addOnScrollListener(listener: OnScrollListener) {
            super.addOnScrollListener(listener)
            scrollListeners += listener
        }

        override fun removeOnScrollListener(listener: OnScrollListener) {
            super.removeOnScrollListener(listener)
            scrollListeners -= listener
        }

        fun dispatchScrolled(dx: Int, dy: Int) {
            scrollListeners.toList().forEach { it.onScrolled(this, dx, dy) }
        }

        override fun getScrollState(): Int = state

        override fun isComputingLayout(): Boolean = computingLayout

        override fun hasPendingAdapterUpdates(): Boolean = false

        override fun onMeasure(widthSpec: Int, heightSpec: Int) {
            setMeasuredDimension(MeasureSpec.getSize(widthSpec), MeasureSpec.getSize(heightSpec))
            for (index in 0 until childCount) {
                getChildAt(index).measure(widthSpec, heightSpec)
            }
        }

        override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
            for (index in 0 until childCount) {
                getChildAt(index).layout(0, 0, right - left, bottom - top)
            }
        }
    }

    private class TrackingView(context: Context) : View(context) {

        var attachListenerCount = 0
            private set
        var removeCallbacksCount = 0
            private set

        override fun addOnAttachStateChangeListener(listener: OnAttachStateChangeListener) {
            super.addOnAttachStateChangeListener(listener)
            attachListenerCount++
        }

        override fun removeOnAttachStateChangeListener(listener: OnAttachStateChangeListener) {
            super.removeOnAttachStateChangeListener(listener)
            attachListenerCount--
        }

        override fun removeCallbacks(action: Runnable): Boolean {
            removeCallbacksCount++
            return super.removeCallbacks(action)
        }
    }

    private companion object {
        val FRAME_DELAY: Duration = Duration.ofMillis(10)
    }
}
