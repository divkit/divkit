package com.yandex.div.compose.views.gallery

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ScrollWheel
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeWithVelocity
import androidx.compose.ui.unit.LayoutDirection
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.LocalDivContext
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.observer.DivComponentObserver
import com.yandex.div.compose.observer.events.DivGalleryScrollEvent
import com.yandex.div.compose.observer.events.DivScrollDirection
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.gallery
import com.yandex.div.test.data.scrollByAction
import com.yandex.div.test.data.text
import com.yandex.div.test.data.wrapContent
import com.yandex.div2.Div
import com.yandex.div2.DivData
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivGallery
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions

@RunWith(AndroidJUnit4::class)
class GalleryScrollObservationTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val observer = mock<DivComponentObserver>()
    private lateinit var divContext: DivContext
    private lateinit var gallery: Div.Gallery
    private lateinit var card: DivData
    private val content = mutableStateOf<DivData?>(null)

    @Test
    fun `instant list scroll logs visible items on completion`() {
        setContent()

        scrollBy(100, animated = false)

        verifyScroll(first = 1, last = 3)
    }

    @Test
    fun `animated grid scroll logs visible items on completion`() {
        setContent(columns = 2)

        scrollBy(100, animated = true)

        verifyScroll(first = 2, last = 7)
    }

    @Test
    fun `measured lanes scroll logs visible items on completion`() {
        setContent(columns = 2, wrapHeight = true)

        scrollBy(100, animated = false)

        verifyScroll(first = 2, last = 7)
    }

    @Test
    fun `grid scroll that stops between rows logs completion without visible items`() {
        gallery = gallery(
            id = "gallery",
            columnCount = constant(2L),
            orientation = constant(DivGallery.Orientation.VERTICAL),
            width = fixed(200),
            height = fixed(100),
            itemSpacing = constant(200L),
            items = List(12) { index -> text(text = constant(index.toString()), height = fixed(40)) },
        ) as Div.Gallery
        showGallery(LayoutDirection.Ltr)
        scrollBy(240, animated = false)
        clearInvocations(observer)

        scrollBy(-130, animated = false)

        verifyScroll(first = -1, last = -1, direction = DivScrollDirection.BACKWARD)
    }

    @Test
    fun `movement below the significant threshold only reports completion`() {
        setContent()

        scrollBy(5, animated = false)

        verify(observer).onGalleryScrollCompleted(
            DivGalleryScrollEvent(card.logId, gallery.value.id, 0, 3, DivScrollDirection.FORWARD),
        )
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `significant event describes items at threshold crossing before the action finishes`() {
        setContent()

        scrollBy(600, animated = true, duration = 1_000)

        inOrder(observer) {
            verify(observer).onGallerySignificantScroll(
                DivGalleryScrollEvent(card.logId, gallery.value.id, 0, 3, DivScrollDirection.FORWARD),
            )
            verify(observer).onGalleryScrollCompleted(
                DivGalleryScrollEvent(card.logId, gallery.value.id, 6, 8, DivScrollDirection.FORWARD),
            )
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `scroll clamped at start does not log movement`() {
        setContent()

        scrollBy(-100, animated = false)

        verifyNoInteractions(observer)
    }

    @Test
    fun `backward scroll logs backward direction`() {
        setContent(defaultItem = 3)

        scrollBy(-100, animated = false)

        verifyScroll(first = 2, last = 4, direction = DivScrollDirection.BACKWARD)
    }

    @Test
    fun `horizontal RTL scroll reports logical direction`() {
        setContent(layoutDirection = LayoutDirection.Rtl)

        scrollBy(100, animated = false)

        verifyScroll(first = 1, last = 3, direction = DivScrollDirection.FORWARD)
    }

    @Test
    fun `visible range includes item partly shown through start padding`() {
        setContent(padding = 20)

        scrollBy(100, animated = false)

        composeRule.onNodeWithText("0").assertIsDisplayed()
        verifyScroll(first = 0, last = 3)
    }

    @Test
    fun `grid default item alignment does not log a scroll`() {
        setContent(columns = 2, defaultItem = 4)

        verifyNoInteractions(observer)
    }

    @Test
    fun `measured lanes default alignment does not log a scroll`() {
        setContent(columns = 2, wrapHeight = true, defaultItem = 4)

        verifyNoInteractions(observer)
    }

    @Test
    fun `swipe logs gallery without an id`() {
        setContent(galleryId = null)

        composeRule.onNode(hasScrollToIndexAction()).performTouchInput {
            swipeWithVelocity(centerRight, centerLeft, endVelocity = 0f)
        }
        composeRule.waitForIdle()

        val events = argumentCaptor<DivGalleryScrollEvent>()
        inOrder(observer) {
            verify(observer).onGallerySignificantScroll(events.capture())
            verify(observer).onGalleryScrollCompleted(events.capture())
            verifyNoMoreInteractions()
        }
        for (event in events.allValues) {
            assertEquals(card.logId, event.cardId)
            assertEquals(null, event.galleryId)
            assertEquals(DivScrollDirection.FORWARD, event.scrollDirection)
            assertTrue(event.lastVisibleItem > event.firstVisibleItem)
        }
        assertTrue(events.lastValue.firstVisibleItem > 0)
    }

    @Test
    fun `mouse wheel moving gallery reports final visible items`() {
        setContent(vertical = true)

        composeRule.onNode(hasScrollToIndexAction()).performMouseInput {
            moveTo(center)
            scroll(20f)
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("11").assertIsDisplayed()
        verifyScroll(first = 6, last = 11)
    }

    @Test
    fun `mouse wheel clamped at start does not report movement`() {
        setContent(vertical = true)

        composeRule.onNode(hasScrollToIndexAction()).performMouseInput {
            moveTo(center)
            scroll(-20f)
        }
        composeRule.waitForIdle()

        verifyNoInteractions(observer)
    }

    @Test
    fun `nested swipe reports both galleries when the outer gallery consumes the remainder`() {
        setNestedContent(innerCount = 4)

        swipeInnerGallery()

        val events = argumentCaptor<DivGalleryScrollEvent>()
        verify(observer, times(2)).onGalleryScrollCompleted(events.capture())
        assertEquals(setOf("inner", "outer"), events.allValues.map { it.galleryId }.toSet())
        assertTrue(events.allValues.all { it.scrollDirection == DivScrollDirection.FORWARD })
    }

    @Test
    fun `nested wheel scroll completes once for each gallery that moved`() {
        setNestedContent(innerCount = 4)

        val inner = hasScrollToIndexAction() and hasAnyAncestor(hasScrollToIndexAction())
        composeRule.onNode(inner).performMouseInput {
            moveTo(center)
            scroll(20f, ScrollWheel.Horizontal)
        }
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(200)
        composeRule.waitForIdle()

        val events = argumentCaptor<DivGalleryScrollEvent>()
        verify(observer, times(2)).onGalleryScrollCompleted(events.capture())
        assertEquals(setOf("inner", "outer"), events.allValues.map { it.galleryId }.toSet())
    }

    @Test
    fun `nested wheel completion waits for the last input`() {
        setNestedContent(innerCount = 3)
        composeRule.mainClock.autoAdvance = false
        val inner = hasScrollToIndexAction() and hasAnyAncestor(hasScrollToIndexAction())

        composeRule.onNode(inner).performMouseInput {
            moveTo(center)
            scroll(1f, ScrollWheel.Horizontal)
        }
        composeRule.mainClock.advanceTimeBy(48)
        composeRule.onNode(inner).performMouseInput { scroll(1f, ScrollWheel.Horizontal) }
        composeRule.mainClock.advanceTimeBy(48)
        composeRule.waitForIdle()

        verify(observer, never()).onGalleryScrollCompleted(any())
        composeRule.mainClock.advanceTimeBy(200)
        composeRule.waitForIdle()

        verifyScroll(first = 0, last = 1)
    }

    @Test
    fun `outer gallery stays silent while inner gallery consumes the swipe`() {
        setNestedContent(innerCount = 12)

        swipeInnerGallery()

        val events = argumentCaptor<DivGalleryScrollEvent>()
        verify(observer).onGalleryScrollCompleted(events.capture())
        assertEquals("inner", events.firstValue.galleryId)
    }

    @Test
    fun `fling logs one completion with the final visible items`() {
        setContent()

        composeRule.onNode(hasScrollToIndexAction()).performTouchInput {
            swipeWithVelocity(centerRight, centerLeft, endVelocity = 8_000f)
        }
        composeRule.waitForIdle()

        verifyScroll(first = 9, last = 11)
    }

    @Test
    fun `mouse wheel completes after an action cancels a fling`() {
        setContent()
        composeRule.mainClock.autoAdvance = false
        val scrollable = composeRule.onNode(hasScrollToIndexAction())
        scrollable.performTouchInput {
            swipeWithVelocity(centerRight, centerLeft, endVelocity = 8_000f)
        }
        composeRule.mainClock.advanceTimeByFrame()
        val scrollRange = scrollable.fetchSemanticsNode()
            .config[SemanticsProperties.HorizontalScrollAxisRange]
        val flingPosition = scrollRange.value()
        composeRule.mainClock.advanceTimeByFrame()
        assertTrue(scrollRange.value() > flingPosition)
        scrollBy(-10_000, animated = false)
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()
        clearInvocations(observer)

        scrollable.performMouseInput {
            moveTo(center)
            scroll(20f, ScrollWheel.Horizontal)
        }
        composeRule.mainClock.autoAdvance = true
        composeRule.waitForIdle()

        composeRule.onNodeWithText("11").assertIsDisplayed()
        verifyScroll(first = 9, last = 11)
    }

    @Test
    fun `URL action jumping beyond visible items logs one completion`() {
        setContent()

        composeRule.runOnIdle {
            divContext.debugFeatures.performAction(
                card,
                action(url = "div-action://set_current_item?id=gallery&item=8&animated=false"),
            )
        }
        composeRule.waitForIdle()

        verifyScroll(first = 8, last = 10)
    }

    @Test
    fun `replacement card logs its own card and gallery ids`() {
        setContent()
        gallery = gallery(
            id = "replacement_gallery",
            width = fixed(300),
            height = fixed(100),
            itemSpacing = constant(0L),
            items = List(12) { index ->
                text(text = constant(index.toString()), width = fixed(100), height = fixed(50))
            },
        ) as Div.Gallery
        card = data(gallery, logId = "replacement_card")
        composeRule.runOnIdle { content.value = card }
        composeRule.waitForIdle()

        scrollBy(100, animated = false)

        verifyScroll(first = 1, last = 3)
    }

    private fun setContent(
        columns: Int = 1,
        wrapHeight: Boolean = false,
        galleryId: String? = "gallery",
        layoutDirection: LayoutDirection = LayoutDirection.Ltr,
        vertical: Boolean = false,
        defaultItem: Int = 0,
        padding: Int = 0,
    ) {
        gallery = gallery(
            id = galleryId,
            columnCount = constant(columns.toLong()),
            defaultItem = constant(defaultItem.toLong()),
            orientation = constant(if (vertical) DivGallery.Orientation.VERTICAL else DivGallery.Orientation.HORIZONTAL),
            width = fixed(if (vertical) 100 else 300),
            height = if (wrapHeight) wrapContent() else fixed(if (vertical) 300 else 100),
            paddings = DivEdgeInsets(left = constant(padding.toLong()), right = constant(padding.toLong())),
            itemSpacing = constant(0L),
            items = List(12) { index ->
                text(text = constant(index.toString()), width = fixed(100), height = fixed(50))
            },
        ) as Div.Gallery
        showGallery(layoutDirection)
    }

    private fun setNestedContent(innerCount: Int) {
        val child = gallery(
            id = "inner", width = fixed(300), height = fixed(100), itemSpacing = constant(0L),
            items = List(innerCount) { text(text = constant("inner $it"), width = fixed(100)) },
        )
        gallery = gallery(
            id = "outer", width = fixed(300), height = fixed(100), itemSpacing = constant(0L),
            items = listOf(child, text(text = constant("outer"), width = fixed(300))),
        ) as Div.Gallery
        showGallery(LayoutDirection.Ltr)
    }

    private fun swipeInnerGallery() {
        val inner = hasScrollToIndexAction() and hasAnyAncestor(hasScrollToIndexAction())
        composeRule.onNode(inner).performTouchInput {
            swipeWithVelocity(centerRight, centerLeft, endVelocity = 0f)
        }
        composeRule.waitForIdle()
    }

    private fun showGallery(layoutDirection: LayoutDirection) {
        card = data(gallery)
        content.value = card
        val configuration = divConfiguration {
            reporter = TestReporter()
            componentObserver = observer
        }
        composeRule.setContentWithDivContext(configuration) {
            divContext = LocalDivContext.current
            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                content.value?.let { DivView(data = it) }
            }
        }
        composeRule.waitForIdle()
    }

    private fun scrollBy(offset: Int, animated: Boolean, duration: Long? = null) {
        composeRule.runOnIdle {
            divContext.debugFeatures.performAction(
                card,
                action(typed = scrollByAction(
                    checkNotNull(gallery.value.id),
                    offset = offset.toLong(),
                    animated = animated,
                    duration = duration?.let { constant(it) },
                )),
            )
        }
        composeRule.waitForIdle()
    }

    private fun verifyScroll(first: Int, last: Int, direction: DivScrollDirection = DivScrollDirection.FORWARD) {
        val events = argumentCaptor<DivGalleryScrollEvent>()
        inOrder(observer) {
            verify(observer).onGallerySignificantScroll(events.capture())
            verify(observer).onGalleryScrollCompleted(
                DivGalleryScrollEvent(card.logId, gallery.value.id, first, last, direction),
            )
            verifyNoMoreInteractions()
        }
        assertEquals(card.logId, events.firstValue.cardId)
        assertEquals(gallery.value.id, events.firstValue.galleryId)
        assertEquals(direction, events.firstValue.scrollDirection)
    }
}
