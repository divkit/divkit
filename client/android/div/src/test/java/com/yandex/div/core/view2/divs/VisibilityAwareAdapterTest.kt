package com.yandex.div.core.view2.divs

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.Disposable
import com.yandex.div.internal.core.DivItemBuilderResult
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivVisibility
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.mockito.kotlin.mock

/**
 * Tests for [VisibilityAwareAdapter] visibility filtering semantics:
 * - [DivVisibility.VISIBLE]   -> item is shown in layout
 * - [DivVisibility.INVISIBLE] -> item still reserves a layout slot, child view is set
 *   to [View.INVISIBLE] by `DivBaseBinder`
 * - [DivVisibility.GONE]      -> item is removed from the adapter (no layout slot)
 */
class VisibilityAwareAdapterTest {

    private val resolver = mock<ExpressionResolver>()

    /**
     * Notification kinds recorded by [TestAdapter] so tests can assert the
     * raw `RecyclerView.Adapter.notify*` calls that fire on visibility changes.
     */
    private sealed class Notification {
        data class Inserted(val position: Int) : Notification()
        data class Removed(val position: Int) : Notification()
        data class Changed(val position: Int) : Notification()
    }

    private class TestAdapter(
        items: List<DivItemBuilderResult>,
    ) : VisibilityAwareAdapter<RecyclerView.ViewHolder>(items) {

        val notifications = mutableListOf<Notification>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            object : RecyclerView.ViewHolder(View(parent.context)) {}

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = Unit

        override fun notifyRawItemRemoved(position: Int) {
            notifications.add(Notification.Removed(position))
        }

        override fun notifyRawItemInserted(position: Int) {
            notifications.add(Notification.Inserted(position))
        }

        override fun notifyRawItemChanged(position: Int) {
            notifications.add(Notification.Changed(position))
        }
    }

    @Test
    fun `invisible items reserve a slot in the adapter`() {
        val visibleItem = item(DivVisibility.VISIBLE)
        val invisibleItem = item(DivVisibility.INVISIBLE)

        val adapter = TestAdapter(listOf(visibleItem, invisibleItem))

        assertEquals(2, adapter.itemCount)
        assertEquals(listOf(visibleItem, invisibleItem), adapter.visibleItems)
    }

    @Test
    fun `gone items are excluded from the adapter`() {
        val visibleItem = item(DivVisibility.VISIBLE)
        val goneItem = item(DivVisibility.GONE)

        val adapter = TestAdapter(listOf(visibleItem, goneItem))

        assertEquals(1, adapter.itemCount)
        assertEquals(listOf(visibleItem), adapter.visibleItems)
    }

    @Test
    fun `visible to invisible transition does not change adapter slots`() {
        val visibilityObserver = CapturingVisibilityExpression(DivVisibility.VISIBLE)
        val item = item(visibilityObserver)

        val adapter = TestAdapter(listOf(item))
        assertEquals(1, adapter.itemCount)

        visibilityObserver.emit(DivVisibility.INVISIBLE)

        assertEquals(1, adapter.itemCount)
        assertEquals(emptyList<Notification>(), adapter.notifications)
    }

    @Test
    fun `invisible to visible transition does not change adapter slots`() {
        val visibilityObserver = CapturingVisibilityExpression(DivVisibility.INVISIBLE)
        val item = item(visibilityObserver)

        val adapter = TestAdapter(listOf(item))
        assertEquals(1, adapter.itemCount)

        visibilityObserver.emit(DivVisibility.VISIBLE)

        assertEquals(1, adapter.itemCount)
        assertEquals(emptyList<Notification>(), adapter.notifications)
    }

    @Test
    fun `visible to gone transition removes the slot`() {
        val visibilityObserver = CapturingVisibilityExpression(DivVisibility.VISIBLE)
        val item = item(visibilityObserver)

        val adapter = TestAdapter(listOf(item))

        visibilityObserver.emit(DivVisibility.GONE)

        assertEquals(0, adapter.itemCount)
        assertEquals(listOf(Notification.Removed(0)), adapter.notifications)
    }

    @Test
    fun `invisible to gone transition removes the slot`() {
        val visibilityObserver = CapturingVisibilityExpression(DivVisibility.INVISIBLE)
        val item = item(visibilityObserver)

        val adapter = TestAdapter(listOf(item))

        visibilityObserver.emit(DivVisibility.GONE)

        assertEquals(0, adapter.itemCount)
        assertEquals(listOf(Notification.Removed(0)), adapter.notifications)
    }

    @Test
    fun `gone to invisible transition inserts a slot`() {
        val visibilityObserver = CapturingVisibilityExpression(DivVisibility.GONE)
        val item = item(visibilityObserver)

        val adapter = TestAdapter(listOf(item))
        assertEquals(0, adapter.itemCount)

        visibilityObserver.emit(DivVisibility.INVISIBLE)

        assertEquals(1, adapter.itemCount)
        assertEquals(listOf(Notification.Inserted(0)), adapter.notifications)
    }

    @Test
    fun `gone to visible transition inserts a slot`() {
        val visibilityObserver = CapturingVisibilityExpression(DivVisibility.GONE)
        val item = item(visibilityObserver)

        val adapter = TestAdapter(listOf(item))

        visibilityObserver.emit(DivVisibility.VISIBLE)

        assertEquals(1, adapter.itemCount)
        assertEquals(listOf(Notification.Inserted(0)), adapter.notifications)
    }

    @Test
    fun `visible position is computed relative to non gone items only`() {
        val gone = item(DivVisibility.GONE)
        val visible = item(DivVisibility.VISIBLE)
        val invisible = item(DivVisibility.INVISIBLE)
        val visibleEnd = item(DivVisibility.VISIBLE)

        val adapter = TestAdapter(listOf(gone, visible, invisible, visibleEnd))

        assertEquals(3, adapter.itemCount)
        assertSame(visible, adapter.visibleItems[0])
        assertSame(invisible, adapter.visibleItems[1])
        assertSame(visibleEnd, adapter.visibleItems[2])
    }

    @Test
    fun `transition of an invisible item to gone removes the correct visible position`() {
        val visibilityObserver = CapturingVisibilityExpression(DivVisibility.INVISIBLE)
        val first = item(DivVisibility.VISIBLE)
        val target = item(visibilityObserver)
        val last = item(DivVisibility.VISIBLE)

        val adapter = TestAdapter(listOf(first, target, last))
        assertEquals(3, adapter.itemCount)

        visibilityObserver.emit(DivVisibility.GONE)

        assertEquals(2, adapter.itemCount)
        assertEquals(listOf(Notification.Removed(1)), adapter.notifications)
    }

    private fun item(visibility: DivVisibility): DivItemBuilderResult =
        DivItemBuilderResult(
            div = Div.Container(DivContainer(visibility = Expression.constant(visibility))),
            expressionResolver = resolver,
        )

    private fun item(expression: Expression<DivVisibility>): DivItemBuilderResult =
        DivItemBuilderResult(
            div = Div.Container(DivContainer(visibility = expression)),
            expressionResolver = resolver,
        )

    /**
     * Mutable visibility expression used to drive `subscribeOnElements` callbacks in tests.
     * Captures the observer registered by [VisibilityAwareAdapter] so the test can emit changes.
     */
    private class CapturingVisibilityExpression(
        initial: DivVisibility,
    ) : Expression<DivVisibility>() {

        private var current: DivVisibility = initial
        private var callback: ((DivVisibility) -> Unit)? = null

        override val rawValue: Any get() = current

        override fun evaluate(resolver: ExpressionResolver): DivVisibility = current

        override fun observe(
            resolver: ExpressionResolver,
            callback: (DivVisibility) -> Unit,
        ): Disposable {
            this.callback = callback
            return Disposable { this.callback = null }
        }

        override fun observeAndGet(
            resolver: ExpressionResolver,
            callback: (DivVisibility) -> Unit,
        ): Disposable {
            val disposable = observe(resolver, callback)
            callback(current)
            return disposable
        }

        fun emit(value: DivVisibility) {
            current = value
            callback?.invoke(value)
        }
    }
}
