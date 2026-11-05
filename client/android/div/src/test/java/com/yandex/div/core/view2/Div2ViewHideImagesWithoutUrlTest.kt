package com.yandex.div.core.view2

import android.app.Activity
import android.view.View
import android.view.View.MeasureSpec
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivCompatibilityFeatures
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivKit
import com.yandex.div.core.DivViewFacade
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.gallery
import com.yandex.div.test.data.gifImage
import com.yandex.div.test.data.image
import com.yandex.div.test.data.text
import com.yandex.div.test.data.visibilityAction
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.DivSightAction
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.Robolectric
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowLooper
import java.util.concurrent.TimeUnit
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class Div2ViewHideImagesWithoutUrlTest {
    private val activity = Robolectric.buildActivity(Activity::class.java).setup()
    private val sightActions = mutableListOf<String?>()
    private var divView: Div2View? = null

    @AfterTest
    fun tearDown() {
        divView?.cleanup()
        activity.pause().stop().destroy()
        DivKit.resetSingletonForTesting()
    }

    @Test
    fun `image without url is gone when hideImagesWithoutUrl is set`() {
        assertEquals(View.GONE, bind(hideImagesWithoutUrl = true).findViewWithTag<View>("image").visibility)
    }

    @Test
    fun `image without url is visible by default`() {
        assertEquals(View.VISIBLE, bind(hideImagesWithoutUrl = false).findViewWithTag<View>("image").visibility)
    }

    @Test
    fun `gallery item without url is excluded when hideImagesWithoutUrl is set`() {
        val gallery = bind(hideImagesWithoutUrl = true).findViewWithTag<RecyclerView>("gallery")

        assertEquals(1, gallery.adapter?.itemCount)
    }

    @Test
    fun `gallery item without url is kept by default`() {
        val gallery = bind(hideImagesWithoutUrl = false).findViewWithTag<RecyclerView>("gallery")

        assertEquals(2, gallery.adapter?.itemCount)
    }

    @Test
    fun `visibility actions of image without url are not dispatched when hideImagesWithoutUrl is set`() {
        bind(hideImagesWithoutUrl = true)

        assertEquals(emptyList(), sightActions)
    }

    @Test
    fun `visibility actions of image with empty scheme url are dispatched when hideImagesWithoutUrl is set`() {
        bind(hideImagesWithoutUrl = true, imageUrl = "empty://")

        assertEquals(listOf<String?>("visible"), sightActions)
    }

    private fun bind(hideImagesWithoutUrl: Boolean, imageUrl: String? = null): Div2View {
        val features = DivCompatibilityFeatures(hideImagesWithoutUrl = hideImagesWithoutUrl)
        val configuration = DivConfiguration.Builder(mock()).compatibilityFeatures(features).build()
        val view = Div2View(Div2Context(testContextThemeWrapper(), configuration))
        divView = view
        view.actionHandler = object : DivActionHandler() {
            override fun handleAction(
                action: DivSightAction, view: DivViewFacade, resolver: ExpressionResolver,
            ): Boolean {
                sightActions += action.logId?.evaluate(resolver)
                return true
            }
        }
        val image = image(
            height = fixed(40),
            id = "image",
            imageUrl = imageUrl,
            visibilityActions = listOf(visibilityAction(delayMs = 0, id = "visible")),
        )
        val gallery = gallery(id = "gallery", items = listOf(gifImage(), text(text = "Text")))
        view.setData(data(container(items = listOf(image, gallery))), DivDataTag("test"))
        activity.get().setContentView(view)
        val spec = MeasureSpec.makeMeasureSpec(300, MeasureSpec.EXACTLY)
        view.measure(spec, spec)
        view.layout(0, 0, 300, 300)
        ShadowLooper.idleMainLooper(1, TimeUnit.SECONDS)
        return view
    }
}
