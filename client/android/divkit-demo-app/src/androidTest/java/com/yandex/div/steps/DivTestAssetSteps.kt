package com.yandex.div.steps

import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.annotation.MainThread
import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.yandex.div.DivDataTag
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
import java.util.*

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

    fun runOnMainSync(block: () -> Unit) =
        InstrumentationRegistry.getInstrumentation().runOnMainSync(block)
}
