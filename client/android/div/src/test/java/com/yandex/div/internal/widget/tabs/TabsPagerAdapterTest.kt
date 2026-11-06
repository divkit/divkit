package com.yandex.div.internal.widget.tabs

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.internal.viewpool.PseudoViewPool
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
class TabsPagerAdapterTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val parent = FrameLayout(context)
    private val pageFactory = TabPageFactory<String, EditText>(
        viewPool = PseudoViewPool().apply {
            register("page", { FrameLayout(context) }, 0)
        },
        tabItemTag = "page",
        bindTabData = { container, data ->
            EditText(context).apply {
                id = android.R.id.edit
                setText(data)
                container.addView(this)
            }
        },
        unbindTabData = { view -> (view.parent as ViewGroup).removeAllViews() },
    )
    private val pagesBinder = TabPagesBinder(pageFactory, currentItem = { 0 })
    private val adapter = TabsPagerAdapter(pagesBinder).apply {
        updateData(listOf("first"))
    }

    @Test
    fun `data replacement binds current fixed height page without scrolling`() {
        val pager = ViewPager2(context).apply {
            adapter = this@TabsPagerAdapterTest.adapter
            setPageTransformer { placeholder, position ->
                if (position > -1f && position < 1f) {
                    pagesBinder.bindBySwipeTransitionRequest(placeholder)
                }
            }
        }
        layoutPager(pager)
        assertEquals("first", pager.findViewById<EditText>(android.R.id.edit)?.text?.toString())

        pagesBinder.clear()
        adapter.updateData(listOf("second"))
        pager.setCurrentItem(0, false)
        layoutPager(pager)

        assertEquals("second", pager.findViewById<EditText>(android.R.id.edit)?.text?.toString())
    }

    @Test
    fun `holder reuses measured page without binding again`() {
        val page = pagesBinder.bindByContainerHeightRequest(0, "first")
        val content = page.container.getChildAt(0)
        val holder = adapter.onCreateViewHolder(parent, 0)

        adapter.onBindViewHolder(holder, 0)
        pagesBinder.bindBySwipeTransitionRequest(holder.itemView)

        assertSame(holder.itemView as ViewGroup, page.container.parent)
        assertSame(content, page.container.getChildAt(0))
    }

    @Test
    fun `recreated page does not restore child state`() {
        val page = pagesBinder.bindByContainerHeightRequest(0, "first")
        (page.container.getChildAt(0) as EditText).setText("edited input")
        pagesBinder.clear()

        val recreatedPage = pagesBinder.bindByContainerHeightRequest(0, "first")

        assertEquals("first", (recreatedPage.container.getChildAt(0) as EditText).text.toString())
    }

    @Test
    fun `clearing pages releases measured content`() {
        val page = pagesBinder.bindByContainerHeightRequest(0, "first")

        pagesBinder.clear()

        assertEquals(0, page.container.childCount)
    }

    @Test
    fun `replacing data releases previous holder content`() {
        val holder = adapter.onCreateViewHolder(parent, 0)
        adapter.onBindViewHolder(holder, 0)
        val page = pagesBinder.bindByContainerHeightRequest(0, "first")
        pagesBinder.clear()
        adapter.updateData(listOf("second"))

        adapter.onBindViewHolder(holder, 0)
        pagesBinder.bindBySwipeTransitionRequest(holder.itemView)

        val replacement = pagesBinder.bindByContainerHeightRequest(0, "second")
        assertEquals("second", (replacement.container.getChildAt(0) as EditText).text.toString())
        assertEquals(0, page.container.childCount)
    }

    @Test
    fun `recycling old holder preserves replacement measured page`() {
        val holder = adapter.onCreateViewHolder(parent, 0)
        adapter.onBindViewHolder(holder, 0)
        pagesBinder.clear()
        adapter.updateData(listOf("second"))
        val replacementPage = pagesBinder.bindByContainerHeightRequest(0, "second")

        adapter.onViewRecycled(holder)

        assertSame(replacementPage, pagesBinder.bindByContainerHeightRequest(0, "second"))
        assertEquals("second", (replacementPage.container.getChildAt(0) as EditText).text.toString())
    }

    @Test
    fun `placeholder content binds when swipe makes it visible`() {
        adapter.updateData(listOf("first", "second"))
        val holder = adapter.onCreateViewHolder(parent, 0)
        adapter.onBindViewHolder(holder, 1)

        pagesBinder.bindBySwipeTransitionRequest(holder.itemView)

        val container = (holder.itemView as ViewGroup).getChildAt(0) as ViewGroup
        assertEquals("second", (container.getChildAt(0) as EditText).text.toString())
    }

    @Test
    fun `recycled holder releases its page`() {
        val holder = adapter.onCreateViewHolder(parent, 0)
        adapter.onBindViewHolder(holder, 0)
        val page = pagesBinder.bindByContainerHeightRequest(0, "first")

        adapter.onViewRecycled(holder)

        assertEquals(0, page.container.childCount)
        assertNotSame(page, pagesBinder.bindByContainerHeightRequest(0, "first"))
    }

    private fun layoutPager(pager: ViewPager2) {
        pager.measure(
            View.MeasureSpec.makeMeasureSpec(400, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(300, View.MeasureSpec.EXACTLY),
        )
        pager.layout(0, 0, 400, 300)
    }
}
