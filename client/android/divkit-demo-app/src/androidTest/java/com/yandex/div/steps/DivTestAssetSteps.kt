package com.yandex.div.steps

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.annotation.MainThread
import androidx.test.espresso.Espresso
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.utils.contentView
import com.yandex.div.zoom.DivPinchToZoomConfiguration
import com.yandex.div.zoom.DivPinchToZoomExtensionHandler
import com.yandex.div2.DivData
import com.yandex.divkit.demo.div.divContext
import com.yandex.divkit.demo.screenshot.DivAssetReader
import com.yandex.test.util.StepsDsl
import ru.tinkoff.allure.step
import java.util.UUID

@StepsDsl
abstract class DivTestAssetSteps {

    lateinit var testAsset: String
    lateinit var div2View: Div2View
    lateinit var container: FrameLayout

    private val parsingEnvironment = DivParsingEnvironment(ParsingErrorLogger.ASSERT)

    @MainThread
    fun ActivityTestRule<*>.setTestData(dataTag: DivDataTag? = null) {
        val divJson = DivAssetReader(activity).read(testAsset)
        val cardJson = if (divJson.has("card")) {
            val templates = divJson.optJSONObject("templates")
            templates?.let {
                parsingEnvironment.parseTemplates(it)
            }
            divJson.getJSONObject("card")
        } else {
            divJson
        }
        div2View.setData(
            DivData(parsingEnvironment, cardJson),
            dataTag ?: DivDataTag(UUID.randomUUID().toString())
        )
    }

    fun ActivityTestRule<*>.buildContainer(
        width: Int,
        height: Int,
        isScrollable: Boolean = false,
        divActionHandler: DivActionHandler? = null
    ): Unit =
        step("Build container w=$width, h=$height, isScrollable=$isScrollable") {
            runOnUiThread {
                val divContext = divContext(activity) {
                    extension(
                        DivPinchToZoomExtensionHandler(
                            DivPinchToZoomConfiguration.Builder(activity).build()
                        )
                    ).also {
                        if (divActionHandler != null) {
                            actionHandler(divActionHandler)
                        }
                    }
                }
                div2View = Div2View(divContext)
                setTestData()
                container = if (isScrollable) ScrollView(activity) else FrameLayout(activity)
                container.layoutParams = FrameLayout.LayoutParams(width, height)
                container.addView(div2View)
                (activity.contentView as ViewGroup).addView(container)
            }
            Espresso.onIdle()
        }

    fun Div2Context.buildContainer(
        width: Int,
        height: Int,
        tag: String? = null
    ): Unit = step("Build container w=$width, h=$height") {
            runOnUiThread {
                div2View = Div2View(this)
                setTestData(tag?.let { DivDataTag(it) })
                container = FrameLayout(this)
                container.layoutParams = LayoutParams(width, height)
                container.addView(div2View)
                activity.setContentView(container)
            }
            Espresso.onIdle()
        }

    @MainThread
    fun Div2Context.setTestData(dataTag: DivDataTag? = null) {
        val divJson = DivAssetReader(this).read(testAsset)
        val cardJson = if (divJson.has("card")) {
            val templates = divJson.optJSONObject("templates")
            templates?.let {
                parsingEnvironment.parseTemplates(it)
            }
            divJson.getJSONObject("card")
        } else {
            divJson
        }
        div2View.setData(
            DivData(parsingEnvironment, cardJson),
            dataTag ?: DivDataTag(UUID.randomUUID().toString())
        )
    }

    private val Div2Context.activity: Activity
        get() {
            var context: ContextWrapper = this
            var baseContext: Context? = context.baseContext
            while (baseContext is ContextWrapper) {
                if (baseContext is Activity) return baseContext
                context = baseContext
                baseContext = context.baseContext
            }
            throw IllegalStateException("Activity not found")
        }

    fun runOnMainSync(block: () -> Unit) =
        InstrumentationRegistry.getInstrumentation().runOnMainSync(block)
}
