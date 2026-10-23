package com.yandex.div.core.tooltip

import android.graphics.Rect
import android.view.View
import androidx.activity.ComponentActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivErrorsReporter
import com.yandex.div.core.DivTooltipRestrictor
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.view2.Div2View
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.text
import com.yandex.div.test.data.visibilityAction
import com.yandex.div2.DivData
import com.yandex.div2.DivTooltip
import java.time.Duration
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowLooper

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class DivTooltipLifecycleTest {
    private val activityController = Robolectric.buildActivity(ComponentActivity::class.java).setup()
    private val activity = activityController.get()

    private val errors = mutableListOf<Throwable>()
    private val requestedTooltipIds = mutableListOf<String>()
    private var divView: Div2View? = null

    @AfterTest
    fun cleanup() {
        try {
            divView?.cleanup()
        } finally {
            activityController.pause().stop().destroy()
        }
    }

    @Test
    fun `tooltip is shown when restrictor checks shown tooltips`() {
        // Arrange
        val view = attachCard(data(text(
            text = "Initial card",
            tooltips = listOf(DivTooltip(
                id = "tooltip",
                position = constant(DivTooltip.Position.BOTTOM),
                div = text(text = "Tooltip content"),
            )),
        )), restrictToNoShownTooltips = true)

        // Act
        view.handleAction(action(url = "div-action://show_tooltip?id=tooltip"))
        ShadowLooper.shadowMainLooper().idleFor(Duration.ofMillis(32))

        // Assert
        assertTooltipShown(view)
    }

    @Test
    fun `single tap cannot show two tooltips when restrictor allows only one`() {
        // Arrange
        val view = attachCard(data(text(
            id = "button",
            text = constant("Show tooltips"),
            actions = listOf(
                action(url = "div-action://show_tooltip?id=tooltip"),
                action(url = "div-action://show_tooltip?id=second"),
            ),
            tooltips = listOf(
                DivTooltip(
                    id = "tooltip",
                    position = constant(DivTooltip.Position.BOTTOM),
                    div = text(text = "First tooltip"),
                ),
                DivTooltip(
                    id = "second",
                    position = constant(DivTooltip.Position.TOP),
                    div = text(text = "Second tooltip"),
                ),
            ),
        )), restrictToNoShownTooltips = true)

        // Act
        assertNotNull(view.findViewWithTag<View>("button")).performClick()
        ShadowLooper.shadowMainLooper().idleFor(Duration.ofMillis(32))

        // Assert
        assertTrue("second" in requestedTooltipIds)
        assertTooltipShown(view)
    }

    @Test
    fun `tooltip visibility action cannot bypass shown tooltip restriction`() {
        // Arrange
        val view = attachCard(data(text(
            text = "Initial card",
            tooltips = listOf(
                DivTooltip(
                    id = "tooltip",
                    position = constant(DivTooltip.Position.BOTTOM),
                    div = text(
                        text = "Tooltip content",
                        visibilityActions = listOf(visibilityAction(
                            id = "show_second_tooltip",
                            url = "div-action://show_tooltip?id=second&multiple=true",
                            delayMs = 0,
                        )),
                    ),
                ),
                DivTooltip(
                    id = "second",
                    position = constant(DivTooltip.Position.TOP),
                    div = text(text = "Second tooltip"),
                ),
            ),
        )), restrictToNoShownTooltips = true)

        // Act
        view.handleAction(action(url = "div-action://show_tooltip?id=tooltip"))
        ShadowLooper.shadowMainLooper().idleFor(Duration.ofMillis(32))

        // Assert
        assertTrue("second" in requestedTooltipIds)
        assertTooltipShown(view)
    }

    @Test
    fun `tooltip closed from shown callback does not run visibility action`() {
        // Arrange
        val view = attachCard(data(text(
            text = "Initial card",
            tooltips = listOf(
                DivTooltip(
                    id = "tooltip",
                    position = constant(DivTooltip.Position.BOTTOM),
                    div = text(
                        text = "Tooltip content",
                        visibilityActions = listOf(visibilityAction(
                            id = "show_second_tooltip",
                            url = "div-action://show_tooltip?id=second&multiple=true",
                            delayMs = 0,
                        )),
                    ),
                ),
                DivTooltip(
                    id = "second",
                    position = constant(DivTooltip.Position.TOP),
                    div = text(text = "Second tooltip"),
                ),
            ),
        )), onTooltipShown = { it.hideTooltip("tooltip") })

        // Act
        view.handleAction(action(url = "div-action://show_tooltip?id=tooltip"))
        ShadowLooper.shadowMainLooper().idleFor(Duration.ofMillis(32))

        // Assert
        assertTrue(errors.isEmpty(), errors.joinToString { it.stackTraceToString() })
        val context = view.getContext() as Div2Context
        assertTrue(context.tooltipManager.captureShownTooltips().isEmpty())
        assertFalse(assertNotNull(shadowOf(activity.application).latestPopupWindow).isShowing)
    }

    @Test
    fun `visibility action shows tooltip from replacement data`() {
        // Arrange
        val view = attachCard(data(container(items = listOf(
            text(text = "Initial card"),
        ))))

        // Act
        view.setData(data(text(
            text = "Initial card",
            visibilityActions = listOf(visibilityAction(
                id = "show_tooltip",
                url = "div-action://show_tooltip?id=tooltip",
                delayMs = 0,
            )),
            tooltips = listOf(DivTooltip(
                id = "tooltip",
                position = constant(DivTooltip.Position.BOTTOM),
                div = text(text = "Tooltip content"),
            )),
        )), DivDataTag("replacement"))
        ShadowLooper.shadowMainLooper().idleFor(Duration.ofMillis(32))

        // Assert
        assertTooltipShown(view)
    }

    private fun assertTooltipShown(view: Div2View) {
        assertTrue(errors.isEmpty(), errors.joinToString { it.stackTraceToString() })
        val context = view.getContext() as Div2Context
        assertEquals(listOf("tooltip"), context.tooltipManager.captureShownTooltips().map { it.id })
        val popup = assertNotNull(shadowOf(activity.application).latestPopupWindow)
        assertTrue(popup.isShowing)
    }

    private fun attachCard(
        data: DivData,
        restrictToNoShownTooltips: Boolean = false,
        onTooltipShown: (Div2View) -> Unit = {},
    ): Div2View {
        val restrictor = object : DivTooltipRestrictor {
            override fun canShowTooltip(
                initiator: Div2View, anchor: View, tooltip: DivTooltip, multiple: Boolean,
            ): Boolean {
                requestedTooltipIds += tooltip.id
                val context = initiator.getContext() as Div2Context
                return !restrictToNoShownTooltips ||
                    context.tooltipManager.captureShownTooltips().isEmpty()
            }

            override fun getTooltipShownCallback(): DivTooltipRestrictor.DivTooltipShownCallback =
                object : DivTooltipRestrictor.DivTooltipShownCallback {
                    override fun onDivTooltipShown(view: Div2View, anchor: View, tooltip: DivTooltip) {
                        onTooltipShown(view)
                    }

                    override fun onDivTooltipDismissed(
                        view: Div2View, anchor: View, tooltip: DivTooltip,
                    ) = Unit
                }
        }
        val imageLoader = DivImageLoader { _, _ -> error("Test card must not load images") }
        val configuration = DivConfiguration.Builder(imageLoader)
            .enableBindOnAttach(false)
            .enableComplexRebind(false)
            .tooltipRestrictor(restrictor)
            .divErrorsReporter(object : DivErrorsReporter {
                override fun onRuntimeError(
                    divData: DivData?, divDataTag: DivDataTag, error: Throwable,
                ) {
                    errors += error
                }

                override fun onWarning(
                    divData: DivData?, divDataTag: DivDataTag, warning: Throwable,
                ) = Unit
            })
            .build()
        val context = Div2Context(activity, configuration)
        val view = Div2View(context).also {
            divView = it
            it.setData(data, DivDataTag("initial"))
            activity.setContentView(it)
        }
        ShadowLooper.shadowMainLooper().idleFor(Duration.ofMillis(32))
        assertTrue(view.isAttachedToWindow)
        assertTrue(view.getGlobalVisibleRect(Rect()))
        return view
    }
}
