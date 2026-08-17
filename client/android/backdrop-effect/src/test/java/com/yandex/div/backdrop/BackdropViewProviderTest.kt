package com.yandex.div.backdrop

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.view2.Div2View
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

    private companion object {
        const val BACKDROP_ID = "backdrop"
    }
}
