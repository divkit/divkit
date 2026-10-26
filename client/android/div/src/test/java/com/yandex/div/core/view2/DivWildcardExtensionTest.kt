package com.yandex.div.core.view2

import android.view.View
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivCustomContainerViewAdapter
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.booleanExpression
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.separator
import com.yandex.div.test.data.text
import com.yandex.div.test.data.variable
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivCustom
import com.yandex.div2.DivExtension
import com.yandex.div2.DivText
import com.yandex.div2.DivVariable
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
class DivWildcardExtensionTest {

    private val events = mutableListOf<String>()
    private val declared = RecordingHandler("declared", events) { div ->
        div.extensions?.any { it.id == EXTENSION } == true
    }
    private val wildcard = RecordingHandler("wildcard", events)
    private val textOnly = RecordingHandler("text", events) { it is DivText }
    private val customView = View(testContextThemeWrapper())
    private val customAdapter = object : DivCustomContainerViewAdapter {
        override fun createView(
            div: DivCustom,
            divView: Div2View,
            expressionResolver: ExpressionResolver,
            path: DivStatePath,
        ) = customView

        override fun bindView(
            view: View,
            div: DivCustom,
            divView: Div2View,
            expressionResolver: ExpressionResolver,
            path: DivStatePath,
        ) = Unit

        override fun isCustomTypeSupported(type: String) = true

        override fun release(view: View, div: DivCustom) = Unit
    }
    private val configuration = DivConfiguration.Builder(mock())
        .enableBindOnAttach(false)
        .divCustomContainerViewAdapter(customAdapter)
        .extension(declared)
        .wildcardExtension(wildcard)
        .wildcardExtension(textOnly)
        .build()
    private val divView = Div2View(Div2Context(testContextThemeWrapper(), configuration))

    @Test
    fun `wildcard handler is bound to divs without extensions and unbound on cleanup`() {
        setData(container(id = "root", items = listOf(
            text(id = "first", text = "First"),
            text(id = "second", text = "Second"),
        )))

        assertEquals(setOf("root", "first", "second"), wildcard.bound)

        divView.cleanup()

        assertEquals(emptySet(), wildcard.bound)
    }

    @Test
    fun `wildcard handler is bound only to matching divs`() {
        setData(container(id = "root", items = listOf(
            text(id = "first", text = "First"),
            separator(id = "separator"),
        )))

        assertEquals(setOf("first"), textOnly.bound)
    }

    @Test
    fun `wildcard handler runs after declared extension handlers`() {
        setData(separator(id = "target", extensions = listOf(DivExtension(id = EXTENSION))))

        assertEquals(
            listOf(
                "declared:beforeBind:target",
                "wildcard:beforeBind:target",
                "declared:bind:target",
                "wildcard:bind:target",
            ),
            events,
        )
    }

    @Test
    fun `toggling declared extensions keeps prepared wildcard handler bound`() {
        setData(
            separator(id = "target", extensions = listOf(
                DivExtension(id = EXTENSION, isEnabled = booleanExpression("@{enabled}")),
            )),
            variables = listOf(variable("enabled", true)),
        )
        events.clear()

        divView.setVariable("enabled", "false")
        divView.setVariable("enabled", "true")

        assertEquals(listOf("declared:unbind:target", "declared:bind:target"), events)
        assertEquals(setOf("target"), wildcard.bound)
    }

    @Test
    fun `wildcard handler is bound to the view created for a custom div`() {
        setData(Div.Custom(DivCustom(customType = "custom", id = "custom")))

        assertSame(customView, wildcard.views["custom"])

        divView.cleanup()

        assertEquals(emptySet(), wildcard.bound)
    }

    @Test
    fun `wildcard media callbacks receive the bound custom view`() {
        setData(Div.Custom(DivCustom(customType = "custom", id = "custom")))

        divView.releaseMedia()
        divView.loadMedia()

        assertEquals(listOf(customView, customView), wildcard.mediaViews)
    }

    @Test
    fun `wildcard handler is rebound once when data is replaced`() {
        setData(text(id = "first", text = "First"))

        divView.setData(data(text(id = "second", text = "Second")), DivDataTag("replacement"))

        assertEquals(setOf("second"), wildcard.bound)
        assertEquals(1, events.count { it == "wildcard:bind:second" })
    }

    private fun setData(content: Div, variables: List<DivVariable>? = null) {
        divView.setData(data(content, variables = variables), DivDataTag("wildcard-extension"))
    }

    private class RecordingHandler(
        private val name: String,
        private val events: MutableList<String>,
        private val filter: (DivBase) -> Boolean = { true },
    ) : DivExtensionHandler {

        private val prepared = mutableSetOf<String>()
        val views = mutableMapOf<String, View>()
        val bound: Set<String> get() = views.keys
        val mediaViews = mutableListOf<View>()

        override fun matches(div: DivBase) = filter(div)

        override fun beforeBindView(
            divView: Div2View,
            expressionResolver: ExpressionResolver,
            view: View,
            div: DivBase,
        ) {
            prepared += div.id.orEmpty()
            events += "$name:beforeBind:${div.id}"
        }

        override fun bindView(
            divView: Div2View,
            expressionResolver: ExpressionResolver,
            view: View,
            div: DivBase,
        ) {
            if (div.id.orEmpty() in prepared) views[div.id.orEmpty()] = view
            events += "$name:bind:${div.id}"
        }

        override fun unbindView(
            divView: Div2View,
            expressionResolver: ExpressionResolver,
            view: View,
            div: DivBase,
        ) {
            prepared -= div.id.orEmpty()
            views.remove(div.id.orEmpty(), view)
            events += "$name:unbind:${div.id}"
        }

        override fun loadMedia(
            divView: Div2View,
            expressionResolver: ExpressionResolver,
            view: View,
            div: DivBase,
        ) {
            mediaViews += view
        }

        override fun releaseMedia(
            divView: Div2View,
            expressionResolver: ExpressionResolver,
            view: View,
            div: DivBase,
        ) {
            mediaViews += view
        }
    }

    private companion object {
        const val EXTENSION = "test-extension"
    }
}
