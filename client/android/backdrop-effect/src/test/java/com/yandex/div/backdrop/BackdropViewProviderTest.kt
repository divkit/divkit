package com.yandex.div.backdrop

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.view2.Div2View
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BackdropViewProviderTest {

    private val activity = Robolectric.buildActivity(Activity::class.java).get()
    private val context: Context = activity
    private val divContext = Div2Context(
        baseContext = activity,
        configuration = DivConfiguration.Builder(mock<DivImageLoader>()).build()
    )

    @Test
    fun `parent provider returns nearest ViewGroup ancestor`() {
        val child = View(context)
        val parent = FrameLayout(context).apply { addView(child) }
        FrameLayout(context).apply { addView(parent) }

        val provider = ParentBackdropViewProvider(child)

        assertSame(parent, provider.backdropView)
    }

    @Test
    fun `parent provider returns null when view has no parent`() {
        val child = View(context)

        val provider = ParentBackdropViewProvider(child)

        assertNull(provider.backdropView)
    }

    @Test
    fun `parent provider does not return the view itself`() {
        val child = FrameLayout(context)
        val parent = FrameLayout(context).apply { addView(child) }

        val provider = ParentBackdropViewProvider(child)

        assertSame(parent, provider.backdropView)
    }

    @Test
    fun `DivView provider returns nearest Div2View ancestor`() {
        val child = View(context)
        val parent = FrameLayout(context).apply { addView(child) }
        val divView = Div2View(divContext).apply { addView(parent) }

        val provider = DivViewBackdropViewProvider(child)

        assertSame(divView, provider.backdropView)
    }

    @Test
    fun `DivView provider returns null when there is no Div2View ancestor`() {
        val child = View(context)
        FrameLayout(context).apply { addView(child) }

        val provider = DivViewBackdropViewProvider(child)

        assertNull(provider.backdropView)
    }

    @Test
    fun `tagged provider returns ancestor with matching tag`() {
        val child = View(context)
        val parent = FrameLayout(context).apply { addView(child) }
        val tagged = FrameLayout(context).apply {
            tag = BACKDROP_ID
            addView(parent)
        }

        val provider = TaggedBackdropViewProvider(child, BACKDROP_ID)

        assertSame(tagged, provider.backdropView)
    }

    @Test
    fun `tagged provider returns null when no ancestor has the tag`() {
        val child = View(context)
        val parent = FrameLayout(context).apply { addView(child) }
        FrameLayout(context).apply { addView(parent) }

        val provider = TaggedBackdropViewProvider(child, BACKDROP_ID)

        assertNull(provider.backdropView)
    }

    @Test
    fun `tagged provider returns sibling with matching tag within DivView scope`() {
        val child = View(context)
        val tagged = View(context).apply {
            tag = BACKDROP_ID
        }
        val parent = FrameLayout(context).apply {
            addView(child)
            addView(tagged)
        }
        Div2View(divContext).apply { addView(parent) }

        val provider = TaggedBackdropViewProvider(child, BACKDROP_ID)

        assertSame(tagged, provider.backdropView)
    }

    @Test
    fun `tagged provider returns null when sibling is not scoped by DivView`() {
        val child = View(context)
        val tagged = View(context).apply {
            tag = BACKDROP_ID
        }
        val parent = FrameLayout(context).apply {
            addView(child)
            addView(tagged)
        }

        val provider = TaggedBackdropViewProvider(child, BACKDROP_ID)

        assertNull(provider.backdropView)
    }

    @Test
    fun `tagged provider does not search beyond a Div2View ancestor`() {
        val child = View(context)
        val parent = FrameLayout(context).apply { addView(child) }
        val divView = Div2View(divContext).apply { addView(parent) }
        FrameLayout(context).apply {
            tag = BACKDROP_ID
            addView(divView)
        }

        val provider = TaggedBackdropViewProvider(child, BACKDROP_ID)

        assertNull(provider.backdropView)
    }

    @Test
    fun `tagged provider finds tag located below the Div2View boundary`() {
        val child = View(context)
        val tagged = FrameLayout(context).apply {
            tag = BACKDROP_ID
            addView(child)
        }
        val divView = Div2View(divContext).apply { addView(tagged) }
        FrameLayout(context).apply {
            tag = BACKDROP_ID
            addView(divView)
        }

        val provider = TaggedBackdropViewProvider(child, BACKDROP_ID)

        assertSame(tagged, provider.backdropView)
    }

    @Test
    fun `tagged provider searching the whole window finds tag above the Div2View`() {
        val child = View(context)
        val parent = FrameLayout(context).apply { addView(child) }
        val divView = Div2View(divContext).apply { addView(parent) }
        val tagged = FrameLayout(context).apply {
            tag = BACKDROP_ID
            addView(divView)
        }

        val provider = TaggedBackdropViewProvider(child, BACKDROP_ID, searchWholeWindow = true)

        assertSame(tagged, provider.backdropView)
    }

    @Test
    fun `tagged provider searching the whole window finds tag in a sibling subtree`() {
        val child = View(context)
        val divView = Div2View(divContext).apply { addView(child) }
        val tagged = View(context).apply {
            tag = BACKDROP_ID
        }
        FrameLayout(context).apply {
            addView(divView)
            addView(tagged)
        }

        val provider = TaggedBackdropViewProvider(child, BACKDROP_ID, searchWholeWindow = true)

        assertSame(tagged, provider.backdropView)
    }

    @Test
    fun `window provider returns the root view`() {
        val child = View(context)
        val parent = FrameLayout(context).apply { addView(child) }
        val root = FrameLayout(context).apply { addView(parent) }

        val provider = WindowBackdropViewProvider(child)

        assertSame(root, provider.backdropView)
    }

    @Test
    fun `window provider collects siblings painted after the decorated view`() {
        val child = View(context)
        val below = View(context)
        val above = View(context)
        FrameLayout(context).apply {
            addView(below)
            addView(child)
            addView(above)
        }

        val provider = WindowBackdropViewProvider(child)

        assertEquals(listOf(above), provider.collectOccludingViews())
    }

    @Test
    fun `window provider collects views painted after the decorated view up the hierarchy`() {
        val child = View(context)
        val parent = FrameLayout(context).apply { addView(child) }
        val aboveParent = View(context)
        FrameLayout(context).apply {
            addView(parent)
            addView(aboveParent)
        }

        val provider = WindowBackdropViewProvider(child)

        assertEquals(listOf(aboveParent), provider.collectOccludingViews())
    }

    @Test
    fun `window provider ignores views that are not visible`() {
        val child = View(context)
        val above = View(context).apply {
            visibility = View.INVISIBLE
        }
        FrameLayout(context).apply {
            addView(child)
            addView(above)
        }

        val provider = WindowBackdropViewProvider(child)

        assertEquals(emptyList<View>(), provider.collectOccludingViews())
    }

    @Test
    fun `window provider collects views elevated above the decorated view`() {
        val child = View(context)
        val elevated = View(context).apply {
            z = 1.0f
        }
        FrameLayout(context).apply {
            addView(elevated)
            addView(child)
        }

        val provider = WindowBackdropViewProvider(child)

        assertEquals(listOf(elevated), provider.collectOccludingViews())
    }

    @Test
    fun `window provider ignores views elevated below the decorated view`() {
        val child = View(context).apply {
            z = 1.0f
        }
        val lowered = View(context)
        FrameLayout(context).apply {
            addView(child)
            addView(lowered)
        }

        val provider = WindowBackdropViewProvider(child)

        assertEquals(emptyList<View>(), provider.collectOccludingViews())
    }

    @Test
    fun `window provider returns the root view of an attached hierarchy`() {
        val child = View(context)
        activity.setContentView(FrameLayout(context).apply { addView(child) })

        val provider = WindowBackdropViewProvider(child)

        assertSame(child.rootView, provider.backdropView)
    }

    @Test
    fun `window provider follows a custom child drawing order`() {
        val child = View(context)
        val drawnFirst = View(context)
        val drawnLast = View(context)
        ReversedDrawingOrderLayout(context).apply {
            addView(drawnLast)
            addView(child)
            addView(drawnFirst)
        }

        val provider = WindowBackdropViewProvider(child)

        assertEquals(listOf(drawnLast), provider.collectOccludingViews())
    }

    @Test
    fun `providers scoped by the card have nothing to hide`() {
        val child = View(context)
        val parent = FrameLayout(context).apply {
            addView(child)
            addView(View(context))
        }
        Div2View(divContext).apply {
            tag = BACKDROP_ID
            addView(parent)
        }

        assertEquals(emptyList<View>(), ParentBackdropViewProvider(child).collectOccludingViews())
        assertEquals(emptyList<View>(), DivViewBackdropViewProvider(child).collectOccludingViews())
        assertEquals(emptyList<View>(), TaggedBackdropViewProvider(child, BACKDROP_ID).collectOccludingViews())
    }

    private class ReversedDrawingOrderLayout(context: Context) : FrameLayout(context) {

        init {
            isChildrenDrawingOrderEnabled = true
        }

        override fun getChildDrawingOrder(childCount: Int, drawingPosition: Int): Int {
            return childCount - 1 - drawingPosition
        }
    }

    private companion object {
        const val BACKDROP_ID = "backdrop"
    }
}
