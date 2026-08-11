package com.yandex.div.core.view2.divs

import androidx.recyclerview.widget.DivLinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.Disposable
import com.yandex.div.core.state.DivViewState
import com.yandex.div.core.state.GalleryState
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.divs.gallery.DivGalleryAdapter
import com.yandex.div.core.view2.divs.gallery.DivGalleryBinder
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.toBlock
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivCollectionItemBuilder
import com.yandex.div2.DivGallery
import org.json.JSONArray
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadow.api.Shadow

@RunWith(RobolectricTestRunner::class)
@Config(shadows = [DivGalleryBinderTest.ShadowDivLinearLayoutManager::class])
class DivGalleryBinderTest : DivBinderTest() {

    private val divViewState = mock<DivViewState>()
    private val divBinder = mock<DivBinder>()

    private val underTest = DivGalleryBinder(
        baseBinder = baseBinder,
        viewCreator = viewCreator,
        divBinder = { divBinder },
        recyclerScrollInterceptionAngle = DivRecyclerView.NOT_INTERCEPT
    )

    private val div = div()
    private val divBlock = div.toBlock(resolver, rootPath()) as DivBlock.Gallery
    private val recyclerView = divRecyclerView(div).apply {
        layoutParams = defaultLayoutParams()
    }

    @Before
    fun `init current state`() {
        whenever(divView.currentState).thenReturn(divViewState)
    }

    @Test
    fun `scroll to default item`() {
        underTest.bindView(recyclerView, divBlock, divView)

        Assert.assertEquals(DEFAULT_ITEM, recyclerView.layoutManager.shadow().position)
    }

    @Test
    fun `keep scroll position on rebind`() {
        underTest.bindView(recyclerView, divBlock, divView)

        (recyclerView.layoutManager as? DivLinearLayoutManager)!!.instantScrollToPosition(DEFAULT_ITEM + 1, 0)
        underTest.bindView(recyclerView, divBlock, divView)

        Assert.assertEquals(DEFAULT_ITEM + 1, recyclerView.layoutManager.shadow().position)
    }

    @Test
    fun `set default item when has current state without visible item index`() {
        underTest.bindView(recyclerView, divBlock, divView)

        Assert.assertEquals(DEFAULT_ITEM, recyclerView.layoutManager.shadow().position)
    }

    @Test
    fun `restore previous position`() {
        whenever(divViewState.getBlockState<GalleryState>(any())).thenReturn(GalleryState(DEFAULT_ITEM + 1, 0))

        underTest.bindView(recyclerView, divBlock, divView)

        Assert.assertEquals(DEFAULT_ITEM + 1, recyclerView.layoutManager.shadow().position)
    }

    @Test
    fun `use updated default item only when gallery state is missing`() {
        val data = mock<Expression<JSONArray>>()
        val defaultItem = mock<Expression<Long>>()
        val observer = argumentCaptor<(JSONArray) -> Unit>()
        whenever(data.evaluate(resolver)).thenReturn(JSONArray())
        whenever(data.observe(any(), observer.capture())).thenReturn(Disposable.NULL)
        whenever(defaultItem.evaluate(resolver)).thenReturn(DEFAULT_ITEM.toLong())
        val itemBuilder = DivCollectionItemBuilder(
            data = data,
            prototypes = listOf(DivCollectionItemBuilder.Prototype(div.value.items!!.first())),
        )
        val itemBuilderDiv = Div.Gallery(div.value.copy(defaultItem = defaultItem, itemBuilder = itemBuilder))
        val itemBuilderBlock = itemBuilderDiv.toBlock(resolver, rootPath()) as DivBlock.Gallery
        val itemBuilderView = divRecyclerView(itemBuilderDiv).apply { layoutParams = defaultLayoutParams() }
        underTest.bindView(itemBuilderView, itemBuilderBlock, divView)
        val adapter = mock<DivGalleryAdapter>()
        whenever(adapter.itemCount).thenReturn(ITEM_COUNT)
        itemBuilderView.adapter = adapter
        itemBuilderView.itemAnimator = mock()
        whenever(defaultItem.evaluate(resolver)).thenReturn(UPDATED_DEFAULT_ITEM.toLong())

        observer.firstValue(JSONArray())

        Assert.assertNull(itemBuilderView.itemAnimator)
        verify(itemBuilderView).scrollToPosition(UPDATED_DEFAULT_ITEM)

        whenever(divViewState.getBlockState<GalleryState>(any())).thenReturn(GalleryState(SAVED_ITEM, 0))
        (itemBuilderView.layoutManager as DivLinearLayoutManager).instantScrollToPosition(SAVED_ITEM, 0)
        val itemAnimator = mock<RecyclerView.ItemAnimator>()
        itemBuilderView.itemAnimator = itemAnimator
        whenever(defaultItem.evaluate(resolver)).thenReturn((UPDATED_DEFAULT_ITEM + 1).toLong())

        observer.firstValue(JSONArray())

        Assert.assertSame(itemAnimator, itemBuilderView.itemAnimator)
        Assert.assertEquals(SAVED_ITEM, itemBuilderView.layoutManager.shadow().position)
    }

    @Test
    fun `do not snap on first position`() {
        val galleryJson = div.writeToJSON()
        galleryJson.remove("default_item")
        val divGallery = Div.Gallery(DivGallery(DivParsingEnvironment(ParsingErrorLogger.ASSERT), galleryJson))
            .toBlock(resolver, rootPath()) as DivBlock.Gallery

        underTest.bindView(recyclerView, divGallery, divView)

        Assert.assertEquals(0, recyclerView.layoutManager.shadow().position)
        verify(recyclerView, never()).scrollToPosition(any())
    }

    private fun div() = UnitTestData(GALLERY_DIR, "gallery_default_item.json").div as Div.Gallery

    private fun divRecyclerView(div: Div) = spy(viewCreator.create(div, mock()) as DivRecyclerView)

    private fun RecyclerView.LayoutManager?.shadow(): ShadowDivLinearLayoutManager {
        return Shadow.extract(this) as ShadowDivLinearLayoutManager
    }

    @Suppress("unused", "UNUSED_PARAMETER")
    @Implements(DivLinearLayoutManager::class)
    class ShadowDivLinearLayoutManager {

        var position: Int = RecyclerView.NO_POSITION

        @Implementation
        fun instantScrollToPosition(position: Int, offset: Int) {
            this.position = position
        }
    }

    private companion object {
        private const val GALLERY_DIR = "div-gallery"
        private const val DEFAULT_ITEM = 2
        private const val ITEM_COUNT = 5
        private const val SAVED_ITEM = 1
        private const val UPDATED_DEFAULT_ITEM = 3
    }
}
