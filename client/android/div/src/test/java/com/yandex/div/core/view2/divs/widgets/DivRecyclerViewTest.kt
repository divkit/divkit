package com.yandex.div.core.view2.divs.widgets

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.context
import com.yandex.div.core.view2.divs.rootPath
import com.yandex.div.core.widget.makeAtMostSpec
import com.yandex.div.core.widget.makeExactSpec
import com.yandex.div.core.widget.makeUnspecifiedSpec
import com.yandex.div.internal.core.toBlock
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.insets
import com.yandex.div.test.data.matchParent
import com.yandex.div.test.data.wrapContent
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.Div
import com.yandex.div2.DivGallery
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DivRecyclerViewTest {

    private val recyclerView = DivRecyclerView(context())
    private val divView = Div2View(
        Div2Context(testContextThemeWrapper(), DivConfiguration.Builder(mock()).build())
    )
    private val gallery = DivGallery(
        id = "gallery",
        height = wrapContent(),
        itemSpacing = constant(0L),
    )

    @Test
    fun `restores default animator when animations are enabled again`() {
        val animator = requireNotNull(recyclerView.itemAnimator)
        recyclerView.setItemAnimationsEnabled(false)

        recyclerView.setItemAnimationsEnabled(true)

        assertSame(animator, recyclerView.itemAnimator)
    }

    @Test
    fun `keeps animations disabled when client sets an animator`() {
        recyclerView.setItemAnimationsEnabled(false)

        recyclerView.itemAnimator = mock()

        assertNull(recyclerView.itemAnimator)
    }

    @Test
    fun `uses latest client animator when animations are enabled again`() {
        recyclerView.itemAnimator = mock()
        recyclerView.setItemAnimationsEnabled(false)
        val animator = mock<RecyclerView.ItemAnimator>()
        recyclerView.itemAnimator = animator

        recyclerView.setItemAnimationsEnabled(true)

        assertSame(animator, recyclerView.itemAnimator)
    }

    @Test
    fun `keeps animator cleared by client when animations are enabled again`() {
        recyclerView.setItemAnimationsEnabled(false)
        recyclerView.itemAnimator = null

        recyclerView.setItemAnimationsEnabled(true)

        assertNull(recyclerView.itemAnimator)
    }

    @Test
    fun `keeps animations suppressed when client sets an animator before layout`() {
        recyclerView.suppressItemAnimationsUntilLayout()

        recyclerView.itemAnimator = mock()

        assertNull(recyclerView.itemAnimator)
    }

    @Test
    fun `uses latest client animator after layout`() {
        recyclerView.suppressItemAnimationsUntilLayout()
        val animator = mock<RecyclerView.ItemAnimator>()
        recyclerView.itemAnimator = animator

        recyclerView.layout(0, 0, 100, 100)

        assertSame(animator, recyclerView.itemAnimator)
    }

    @Test
    fun `keeps animator cleared by client after layout`() {
        recyclerView.suppressItemAnimationsUntilLayout()
        recyclerView.itemAnimator = null

        recyclerView.layout(0, 0, 100, 100)

        assertNull(recyclerView.itemAnimator)
    }

    @Test
    fun `keeps animations suppressed when enabled before layout`() {
        recyclerView.setItemAnimationsEnabled(false)
        recyclerView.suppressItemAnimationsUntilLayout()

        recyclerView.setItemAnimationsEnabled(true)

        assertNull(recyclerView.itemAnimator)
    }

    @Test
    fun `keeps animations disabled after layout`() {
        recyclerView.suppressItemAnimationsUntilLayout()
        recyclerView.setItemAnimationsEnabled(false)

        recyclerView.layout(0, 0, 100, 100)

        assertNull(recyclerView.itemAnimator)
    }

    @Test
    fun `does not end animations when enablement stays unchanged`() {
        val animator = mock<RecyclerView.ItemAnimator>()
        recyclerView.itemAnimator = animator

        recyclerView.setItemAnimationsEnabled(true)

        verify(animator, never()).endAnimations()
    }

    @Test
    fun `ends animations when client explicitly sets the same animator`() {
        val animator = mock<RecyclerView.ItemAnimator>()
        recyclerView.itemAnimator = animator

        recyclerView.itemAnimator = animator

        verify(animator).endAnimations()
    }

    @Test
    fun `layout is not requested when grid already occupies parent limit rounded to spans`() {
        bind(gallery.copy(columnCount = constant(3L), items = List(6) {
            container(width = fixed(200), height = fixed(50))
        }))
        measureAndLayout(heightSpec = makeAtMostSpec(100))

        recyclerView.scrollBy(200, 0)

        assertFalse(recyclerView.isLayoutRequested)
    }

    @Test
    fun `layout is not requested when gallery height is fixed`() {
        bind(gallery.copy(items = listOf(
            container(width = fixed(200), height = fixed(50)),
            container(width = fixed(200), height = fixed(80)),
        )))
        measureAndLayout(heightSpec = makeExactSpec(60))

        recyclerView.scrollBy(200, 0)

        assertFalse(recyclerView.isLayoutRequested)
    }

    @Test
    fun `scrolling to smaller item does not request gallery shrink`() {
        bind(gallery.copy(items = listOf(
            container(width = fixed(200), height = fixed(80)),
            container(width = fixed(200), height = fixed(50)),
        )))
        measureAndLayout()

        recyclerView.scrollBy(200, 0)

        assertFalse(recyclerView.isLayoutRequested)
    }

    @Test
    fun `layout is requested when wider items appear in vertical grid`() {
        bind(gallery.copy(
            orientation = constant(DivGallery.Orientation.VERTICAL),
            width = wrapContent(),
            height = fixed(100),
            columnCount = constant(3L),
            crossSpacing = constant(5L),
            paddings = insets(start = 5, end = 7),
            items = List(3) { container(width = fixed(50), height = fixed(200)) } +
                List(3) { container(width = fixed(80), height = fixed(200)) },
        ))
        measureAndLayout(makeUnspecifiedSpec(), makeExactSpec(100))

        recyclerView.scrollBy(0, 200)

        assertTrue(recyclerView.isLayoutRequested)
    }

    @Test
    fun `match parent item fills gallery minimum height`() {
        bind(gallery.copy(items = listOf(container(
            id = "match_parent", width = fixed(200), height = matchParent(),
            items = listOf(container(height = fixed(20))),
        ))))
        recyclerView.minimumHeight = 80

        measureAndLayout()

        assertEquals(80, recyclerView.findViewWithTag<View>("match_parent").height)
    }

    @Test
    fun `layout is requested after match parent measurement makes effective height spec exact`() {
        bind(gallery.copy(items = listOf(
            container(width = fixed(200), height = fixed(50)),
            container(width = fixed(200), height = fixed(80)),
            container(width = fixed(200), height = matchParent()),
        )))
        measureAndLayout()

        recyclerView.scrollBy(200, 0)

        assertTrue(recyclerView.isLayoutRequested)
    }

    private fun bind(model: DivGallery) {
        divView.addView(recyclerView)
        val divBlock = Div.Gallery(model).toBlock(divView.expressionResolver, rootPath())
        divView.div2Component.divBinder.bind(recyclerView, divBlock, divView)
    }

    private fun measureAndLayout(
        widthSpec: Int = makeExactSpec(100),
        heightSpec: Int = makeUnspecifiedSpec(),
    ) {
        recyclerView.measure(widthSpec, heightSpec)
        recyclerView.layout(0, 0, recyclerView.measuredWidth, recyclerView.measuredHeight)
    }
}
