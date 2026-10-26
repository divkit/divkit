package com.yandex.div.core.view2

import android.app.Activity
import android.view.View
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivKit
import com.yandex.div.test.data.data
import com.yandex.div.test.data.text
import com.yandex.div.test.data.variable
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.DivAppearanceTransition
import com.yandex.div2.DivFadeTransition
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
class Div2ViewVisibilityIntegrationTest {
    private val activity = Robolectric.buildActivity(Activity::class.java).setup()
    private val divView = Div2View(Div2Context(
        testContextThemeWrapper(),
        DivConfiguration.Builder(mock()).build(),
    ))

    @AfterTest
    fun tearDown() {
        divView.cleanup()
        activity.pause().stop().destroy()
        DivKit.resetSingletonForTesting()
    }

    @Test
    fun `view stays visible when animated show supersedes pending hide`() {
        bindTarget(DivAppearanceTransition.Fade(DivFadeTransition()))

        divView.setVariable("visibility", "invisible")
        divView.setVariable("visibility", "visible")
        ShadowLooper.idleMainLooper(1, TimeUnit.SECONDS)

        assertEquals(View.VISIBLE, divView.findViewWithTag<View>("target").visibility)
    }

    @Test
    fun `view stays visible when immediate show supersedes pending hide`() {
        bindTarget(transitionIn = null)

        divView.setVariable("visibility", "invisible")
        divView.setVariable("visibility", "visible")
        ShadowLooper.idleMainLooper(1, TimeUnit.SECONDS)

        assertEquals(View.VISIBLE, divView.findViewWithTag<View>("target").visibility)
    }

    @Test
    fun `view becomes invisible when pending hide is not superseded`() {
        bindTarget(transitionIn = null)

        divView.setVariable("visibility", "invisible")
        ShadowLooper.idleMainLooper(1, TimeUnit.SECONDS)

        assertEquals(View.INVISIBLE, divView.findViewWithTag<View>("target").visibility)
    }

    private fun bindTarget(transitionIn: DivAppearanceTransition?) {
        val content = text(
            id = "target", text = "Weather", visibility = visibilityExpression("@{visibility}"),
            transitionIn = transitionIn,
            transitionOut = DivAppearanceTransition.Fade(DivFadeTransition()),
        )
        divView.setData(
            data(content, variables = listOf(variable("visibility", "visible"))),
            DivDataTag("pending-visibility"),
        )
        activity.get().setContentView(divView)
        ShadowLooper.idleMainLooper(1, TimeUnit.SECONDS)
    }
}
