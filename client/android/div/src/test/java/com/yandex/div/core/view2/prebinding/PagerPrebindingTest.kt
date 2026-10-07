package com.yandex.div.core.view2.prebinding

import android.os.Looper
import android.view.View
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.widget.makeExactSpec
import com.yandex.div.core.widget.makeUnspecifiedSpec
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.DivBase
import com.yandex.div2.DivData
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.json.JSONObject
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.annotation.LooperMode

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.INSTRUMENTATION_TEST)
class PagerPrebindingTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val preparations = CopyOnWriteArrayList<Pair<Thread, View>>()
    private val releasedPages = CopyOnWriteArrayList<String>()
    private var failBinding = false
    private lateinit var divView: Div2View

    @BeforeTest
    fun setup() = instrumentation.runOnMainSync {
        val extension = object : DivExtensionHandler {
            override fun matches(div: DivBase) = div.extensions?.any { it.id == "record-binding" } == true

            override fun beforeBindView(
                divView: Div2View,
                expressionResolver: ExpressionResolver,
                view: View,
                div: DivBase,
            ) {
                preparations += Thread.currentThread() to view
            }

            override fun bindView(
                divView: Div2View,
                expressionResolver: ExpressionResolver,
                view: View,
                div: DivBase,
            ) {
                if (failBinding) throw Error("Extension binding failed")
            }

            override fun unbindView(
                divView: Div2View,
                expressionResolver: ExpressionResolver,
                view: View,
                div: DivBase,
            ) {
                releasedPages += requireNotNull(div.id)
                if (failBinding) throw Error("Extension cleanup failed")
            }
        }
        val configuration = DivConfiguration.Builder(mock())
            .enableBindOnAttach(false)
            .extension(extension)
            .divErrorsReporter(mock())
            .build()
        divView = Div2View(Div2Context(testContextThemeWrapper(), configuration))
    }

    @AfterTest
    fun cleanup() = instrumentation.runOnMainSync { divView.cleanup() }

    @Test
    fun `infinite pager first layout reuses the page bound once on a worker`() {
        bindAsync()
        instrumentation.runOnMainSync {
            divView.measure(makeExactSpec(300), makeUnspecifiedSpec())
            divView.layout(0, 0, divView.measuredWidth, divView.measuredHeight)
            val displayed = divView.findViewWithTag<View>("page0")

            assertEquals(
                listOf(false to displayed),
                preparations.filter { it.second.tag == "page0" }.map { (thread, view) ->
                    (thread === Looper.getMainLooper().thread) to view
                },
            )
        }
    }

    @Test
    fun `failed binding releases every prepared page exactly once`() {
        failBinding = true
        bindAsync(successful = false)
        instrumentation.runOnMainSync { divView.cleanup() }

        assertEquals(mapOf("page0" to 1, "page1" to 1, "page2" to 1), releasedPages.groupingBy { it }.eachCount())
    }

    private fun bindAsync(successful: Boolean = true) {
        val completed = CountDownLatch(1)
        var result: Boolean? = null
        val document = DivData(
            DivParsingEnvironment(logger = { throw AssertionError("Invalid pager fixture", it) }),
            JSONObject(DOCUMENT).getJSONObject("card"),
        )
        instrumentation.runOnMainSync {
            divView.setDataAsync(document, DivDataTag("pager-prebinding")) {
                result = it
                completed.countDown()
            }
        }
        check(completed.await(5, TimeUnit.SECONDS) && result == successful) { "Unexpected pager binding result" }
    }

    private companion object {
        const val DOCUMENT = """
            {
              "card": {
                "log_id": "pager-prebinding",
                "states": [{
                  "state_id": 0,
                  "div": {
                    "type": "pager", "id": "pager", "default_item": 0, "infinite_scroll": 1,
                    "width": {"type": "fixed", "value": 120, "unit": "px"},
                    "height": {"type": "wrap_content"},
                    "layout_mode": {"type": "percentage", "page_width": {"type": "percentage", "value": 100}},
                    "items": [
                      {
                        "type": "text", "id": "page0", "text": "Page 0",
                        "width": {"type": "fixed", "value": 120, "unit": "px"},
                        "height": {"type": "fixed", "value": 48, "unit": "px"},
                        "extensions": [{"id": "record-binding"}]
                      },
                      {
                        "type": "text", "id": "page1", "text": "Page 1",
                        "width": {"type": "fixed", "value": 120, "unit": "px"},
                        "height": {"type": "fixed", "value": 48, "unit": "px"},
                        "extensions": [{"id": "record-binding"}]
                      },
                      {
                        "type": "text", "id": "page2", "text": "Page 2",
                        "width": {"type": "fixed", "value": 120, "unit": "px"},
                        "height": {"type": "fixed", "value": 48, "unit": "px"},
                        "extensions": [{"id": "record-binding"}]
                      }
                    ]
                  }
                }]
              }
            }
        """
    }
}
