package com.yandex.div.core.view2.divs

import androidx.recyclerview.widget.DivLinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.state.DivViewState
import com.yandex.div.core.state.GalleryState
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.divs.gallery.DivGalleryBinder
import com.yandex.div.core.view2.divs.gallery.ScrollPosition
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.Div
import com.yandex.div2.DivGallery
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
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
        divPatchCache = mock(),
        recyclerScrollInterceptionAngle = DivRecyclerView.NOT_INTERCEPT
    )

    private val div = div()
    private val recyclerView = divRecyclerView(div).apply {
        layoutParams = defaultLayoutParams()
    }

    @Before
    fun `init current state`() {
        whenever(divView.currentState).thenReturn(divViewState)
    }

    @Test
    fun `scroll to default item`() {
        underTest.bindView(bindingContext, recyclerView, div, rootPath())

        Assert.assertEquals(DEFAULT_ITEM, recyclerView.layoutManager.shadow().position)
    }

    @Test
    fun `keep scroll position on rebind`() {
        underTest.bindView(bindingContext, recyclerView, div, rootPath())

        (recyclerView.layoutManager as? DivLinearLayoutManager)!!.instantScrollToPosition(
            DEFAULT_ITEM + 1,
            ScrollPosition.DEFAULT
        )
        underTest.bindView(bindingContext, recyclerView, div, rootPath())

        Assert.assertEquals(DEFAULT_ITEM + 1, recyclerView.layoutManager.shadow().position)
    }

    @Test
    fun `set default item when has current state without visible item index`() {
        underTest.bindView(bindingContext, recyclerView, div, rootPath())

        Assert.assertEquals(DEFAULT_ITEM, recyclerView.layoutManager.shadow().position)
    }

    @Test
    fun `restore previous position`() {
        whenever(divViewState.getBlockState<GalleryState>(any())).thenReturn(GalleryState(DEFAULT_ITEM + 1, 0))

        underTest.bindView(bindingContext, recyclerView, div, rootPath())

        Assert.assertEquals(DEFAULT_ITEM + 1, recyclerView.layoutManager.shadow().position)
    }

    @Test
    fun `do not snap on first position`() {
        val galleryJson = div.writeToJSON()
        galleryJson.remove("default_item")
        val divGallery = Div.Gallery(DivGallery(DivParsingEnvironment(ParsingErrorLogger.ASSERT), galleryJson))

        underTest.bindView(bindingContext, recyclerView, divGallery, rootPath())

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
        fun instantScrollToPosition(position: Int, scrollPosition: ScrollPosition) {
            this.position = position
        }

        @Implementation
        fun instantScrollToPositionWithOffset(position: Int, offset: Int, scrollPosition: ScrollPosition) {
            this.position = position
        }
    }

    private companion object {
        private const val GALLERY_DIR = "div-gallery"
        private const val DEFAULT_ITEM = 2
    }
}
