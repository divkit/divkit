package com.yandex.div.core

import android.view.View
import com.yandex.div.DivDataTag
import com.yandex.div.core.actions.DivActionTypedHandlerProxy
import com.yandex.div.core.expression.evaluation.DictEvaluator
import com.yandex.div.core.extension.DivExtensionActionHandler
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.player.DivVideoPlaybackState
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivVideoViewState
import com.yandex.div.core.view2.DivViewStateStore
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.state
import com.yandex.div.test.data.text
import com.yandex.div.test.data.video
import com.yandex.div.test.data.visibilityAction
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionExtensionAction
import com.yandex.div2.DivActionTyped
import com.yandex.div2.DivActionVideo
import com.yandex.div2.DivBase
import com.yandex.div2.DivCollectionItemBuilder
import com.yandex.div2.DivExtension
import com.yandex.div2.DivState
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class DivExtensionActionTest {

    private val actionHandler = RecordingActionHandler()
    private val extensionHandler = TestExtensionHandler(actionHandler)
    private val divView = createDivView(extensionHandler = extensionHandler).also { view ->
        view.setData(defaultData(), DivDataTag("extension-action"))
    }

    @Test
    fun `action is handled by path before target view is bound`() {
        // Arrange
        val unboundHandler = RecordingActionHandler()
        val unboundView = createDivView(
            bindOnAttach = true,
            extensionHandler = TestExtensionHandler(unboundHandler),
        )
        unboundView.setData(defaultData(), DivDataTag("unbound-extension-action"))

        // Act
        val handled = unboundView.handleActionWithResult(
            action(JSONObject().put("type", "start")),
            reason = "click",
        )

        // Assert
        val call = unboundHandler.actionCalls.single()
        assertTrue(handled)
        assertEquals(unboundView.currentRootPath.appendDiv("scope").appendDiv("target"), call.path)
        assertEquals("start", call.payload?.get()?.getOrThrow()?.get("type"))
        assertSame(unboundView.expressionResolver, call.resolver)
        assertTrue(unboundHandler.boundViews.isEmpty())
    }

    @Test
    fun `bound target view is unsubscribed with the same path`() {
        // Arrange
        val binding = actionHandler.boundViews.single()

        // Act
        divView.cleanup()

        // Assert
        assertEquals(listOf(binding), actionHandler.unboundViews)
    }

    @Test
    fun `visibility action uses scoped extension target`() {
        // Arrange
        val visibilityAction = visibilityAction(typed = DivActionTyped.ExtensionAction(
            DivActionExtensionAction(
                divId = constant("target"),
                extensionId = constant("animation"),
            )
        )).copy(
            scopeId = constant("scope"),
            payload = JSONObject().put("type", "start"),
        )

        // Act
        val handled = DivActionTypedHandlerProxy.handleVisibilityAction(
            visibilityAction,
            divView,
            divView.expressionResolver,
        )

        // Assert
        val call = actionHandler.actionCalls.single()
        assertTrue(handled)
        assertEquals("start", call.payload?.get()?.getOrThrow()?.get("type"))
        assertEquals(divView.currentRootPath.appendDiv("scope").appendDiv("target"), call.path)
    }

    @Test
    fun `action for an undeclared extension is rejected`() {
        val handled = divView.handleActionWithResult(action(payload = null, extensionId = "other"))

        assertFalse(handled)
        assertTrue(actionHandler.actionCalls.isEmpty())
    }

    @Test
    fun `disabled extension target remains addressable`() {
        // Arrange
        val disabledHandler = RecordingActionHandler()
        val disabledView = createDivView(extensionHandler = TestExtensionHandler(disabledHandler))
        disabledView.setData(data(text(
            id = "target",
            text = "Disabled",
            extensions = listOf(DivExtension(id = "animation", isEnabled = constant(false))),
        )), DivDataTag("disabled-extension-action"))

        // Act
        val handled = disabledView.handleActionWithResult(
            action(payload = null, scopeId = null),
            reason = DivActionHandler.DivActionReason.TIMER,
        )

        // Assert
        assertTrue(handled)
        assertEquals(disabledView.currentRootPath, disabledHandler.actionCalls.single().path)
    }

    @Test
    fun `dispatch stops after the first handler accepts action`() {
        // Arrange
        val calls = mutableListOf<String>()
        val first = RecordingActionHandler(name = "first", calls = calls)
        val second = RecordingActionHandler(name = "second", calls = calls)
        val cardView = createDivView(
            extensionHandlers = listOf(TestExtensionHandler(first), TestExtensionHandler(second)),
        )
        cardView.setData(defaultData(), DivDataTag("first-extension-handler"))

        // Act
        val handled = cardView.handleActionWithResult(action(payload = null))

        // Assert
        assertTrue(handled)
        assertEquals(listOf("first"), calls)
    }

    @Test
    fun `dispatch continues when the first handler declines action`() {
        // Arrange
        val calls = mutableListOf<String>()
        val first = RecordingActionHandler(result = false, name = "first", calls = calls)
        val second = RecordingActionHandler(name = "second", calls = calls)
        val cardView = createDivView(
            extensionHandlers = listOf(TestExtensionHandler(first), TestExtensionHandler(second)),
        )
        cardView.setData(defaultData(), DivDataTag("next-extension-handler"))

        // Act
        val handled = cardView.handleActionWithResult(action(payload = null))

        // Assert
        assertTrue(handled)
        assertEquals(listOf("first", "second"), calls)
    }

    @Test
    fun `scope selects its copy of a repeated target id`() {
        // Arrange
        val scopedHandler = RecordingActionHandler()
        val scopedView = createDivView(extensionHandler = TestExtensionHandler(scopedHandler))
        scopedView.setData(data(container(id = "root", items = listOf(
            container(id = "left", items = listOf(extensionTarget("Left"))),
            container(id = "right", items = listOf(extensionTarget("Right"))),
        ))), DivDataTag("scoped-extension-action"))

        // Act
        val handled = scopedView.handleActionWithResult(action(payload = null, scopeId = "right"))

        // Assert
        assertTrue(handled)
        assertEquals(
            scopedView.currentRootPath.appendDiv("right").appendDiv("target"),
            scopedHandler.actionCalls.single().path,
        )
    }

    @Test
    fun `action source selects its copy in a repeated collection`() {
        // Arrange
        val repeatedHandler = RecordingActionHandler()
        val repeatedExtension = TestExtensionHandler(repeatedHandler)
        val repeatedView = createDivView(extensionHandler = repeatedExtension)
        val item = container(id = "scope", items = listOf(extensionTarget()))
        val builder = DivCollectionItemBuilder(
            data = constant(JSONArray().put(JSONObject()).put(JSONObject())),
            prototypes = listOf(DivCollectionItemBuilder.Prototype(item, id = constant("scope"))),
        )
        repeatedView.setData(data(Div.Container((container(id = "root") as Div.Container).value.copy(
            itemBuilder = builder,
        ))), DivDataTag("repeated-extension-action"))
        val sourceResolver = repeatedExtension.bindings.last().resolver
        val expectedPath = repeatedHandler.boundViews.last().path

        // Act
        val handled = repeatedView.handleActionWithResult(
            action(payload = null),
            resolver = sourceResolver,
        )

        // Assert
        assertTrue(handled)
        assertEquals(expectedPath, repeatedHandler.actionCalls.single().path)
    }

    @Test
    fun `ambiguous target id is rejected`() {
        // Arrange
        val ambiguousHandler = RecordingActionHandler()
        val ambiguousView = createDivView(extensionHandler = TestExtensionHandler(ambiguousHandler))
        ambiguousView.setData(data(container(items = listOf(
            extensionTarget("First"),
            extensionTarget("Second"),
        ))), DivDataTag("ambiguous-extension-action"))

        // Act
        val handled = ambiguousView.handleActionWithResult(action(payload = null, scopeId = null))

        // Assert
        assertFalse(handled)
        assertTrue(ambiguousHandler.actionCalls.isEmpty())
    }

    @Test
    fun `active state selects its copy of a repeated target id`() {
        // Arrange
        val stateHandler = RecordingActionHandler()
        val stateView = createDivView(extensionHandler = TestExtensionHandler(stateHandler))
        stateView.setData(data(Div.State(state(id = "state", states = listOf(
            DivState.State(stateId = "active", div = extensionTarget()),
            DivState.State(stateId = "inactive", div = extensionTarget()),
        )))), DivDataTag("state-extension-action"))

        // Act
        val handled = stateView.handleActionWithResult(action(payload = null, scopeId = null))

        // Assert
        assertTrue(handled)
        assertEquals("0/state/active", stateHandler.actionCalls.single().path.statesString)
    }

    @Test
    fun `missing scope falls back to a unique target`() {
        val handled = divView.handleActionWithResult(action(payload = null, scopeId = "missing"))

        assertTrue(handled)
        assertEquals(
            divView.currentRootPath.appendDiv("scope").appendDiv("target"),
            actionHandler.actionCalls.single().path,
        )
    }

    @Test
    fun `video action uses the same repeated runtime branch as its source`() {
        // Arrange
        val item = container(id = "scope", items = listOf(extensionTarget(), video(id = "video")))
        val builder = DivCollectionItemBuilder(
            data = constant(JSONArray().put(JSONObject()).put(JSONObject())),
            prototypes = listOf(DivCollectionItemBuilder.Prototype(item, id = constant("scope"))),
        )
        divView.setData(data(Div.Container((container(id = "root") as Div.Container).value.copy(
            itemBuilder = builder,
        ))), DivDataTag("repeated-video-action"))
        val sourceResolver = extensionHandler.bindings.last().resolver
        val store = mock<DivViewStateStore>()
        divView.viewStateStore = store

        // Act
        val handled = divView.handleActionWithResult(
            DivAction(scopeId = constant("scope"), typed = DivActionTyped.Video(DivActionVideo(
                id = constant("video"),
                action = constant(DivActionVideo.Action.START),
            ))),
            resolver = sourceResolver,
        )

        // Assert
        assertTrue(handled)
        verify(store).put(
            divView.currentRootPath.appendDiv("scope#1").appendDiv("video").fullPath,
            DivVideoViewState(DivVideoPlaybackState.PLAYING),
        )
    }

    @Test
    fun `view released during extension bind is not subscribed to action state`() {
        // Arrange
        val reentrantHandler = RecordingActionHandler()
        val reentrantExtension = TestExtensionHandler(reentrantHandler) { view, extensionView ->
            view.div2Component.extensionController.unbindView(extensionView, view)
        }
        val reentrantView = createDivView(extensionHandler = reentrantExtension)

        // Act
        reentrantView.setData(data(extensionTarget()), DivDataTag("reentrant-extension-action"))

        // Assert
        assertTrue(reentrantHandler.boundViews.isEmpty())
    }

    private fun createDivView(
        bindOnAttach: Boolean = false,
        extensionHandler: DivExtensionHandler? = null,
        extensionHandlers: List<DivExtensionHandler> = emptyList(),
    ): Div2View {
        val configuration = DivConfiguration.Builder(mock())
            .enableBindOnAttach(bindOnAttach)
        if (extensionHandler != null) {
            configuration.extension(extensionHandler)
        }
        extensionHandlers.forEach { configuration.extension(it) }
        return Div2View(Div2Context(testContextThemeWrapper(), configuration.build()))
    }

    private fun defaultData() = data(container(id = "root", items = listOf(
        container(id = "scope", items = listOf(extensionTarget())),
    )))

    private fun extensionTarget(text: String = "Target"): Div = text(
        id = "target",
        text = text,
        extensions = listOf(DivExtension(id = "animation")),
    )

    private fun action(
        payload: JSONObject?,
        extensionId: String = "animation",
        scopeId: String? = "scope",
    ): DivAction = DivAction(
        scopeId = scopeId?.let { constant(it) },
        typed = DivActionTyped.ExtensionAction(DivActionExtensionAction(
            divId = constant("target"),
            extensionId = constant(extensionId),
        )),
        payload = payload,
    )

    private class RecordingActionHandler(
        private val result: Boolean = true,
        private val name: String? = null,
        private val calls: MutableList<String>? = null,
    ) : DivExtensionActionHandler {

        val actionCalls = mutableListOf<ActionCall>()
        val boundViews = mutableListOf<ViewBinding>()
        val unboundViews = mutableListOf<ViewBinding>()

        override fun onViewBind(divView: Div2View, path: DivStatePath, view: View) {
            boundViews += ViewBinding(divView, path, view)
        }

        override fun onViewUnbind(divView: Div2View, path: DivStatePath, view: View) {
            unboundViews += ViewBinding(divView, path, view)
        }

        override fun handleAction(
            divView: Div2View,
            path: DivStatePath,
            payload: DictEvaluator?,
            resolver: ExpressionResolver,
        ): Boolean {
            actionCalls += ActionCall(divView, path, payload, resolver)
            if (name != null) {
                calls?.add(name)
            }
            return result
        }
    }

    private class TestExtensionHandler(
        override val actionHandler: DivExtensionActionHandler,
        private val onBind: (Div2View, View) -> Unit = { _, _ -> },
    ) : DivExtensionHandler {

        val bindings = mutableListOf<ExtensionBinding>()

        override fun matches(div: DivBase): Boolean = true

        override fun bindView(
            divView: Div2View,
            expressionResolver: ExpressionResolver,
            view: View,
            div: DivBase,
        ) {
            bindings += ExtensionBinding(expressionResolver, view)
            onBind(divView, view)
        }

        override fun unbindView(
            divView: Div2View,
            expressionResolver: ExpressionResolver,
            view: View,
            div: DivBase,
        ) = Unit
    }

    private data class ActionCall(
        val divView: Div2View,
        val path: DivStatePath,
        val payload: DictEvaluator?,
        val resolver: ExpressionResolver,
    )

    private data class ViewBinding(
        val divView: Div2View,
        val path: DivStatePath,
        val view: View,
    )

    private data class ExtensionBinding(
        val resolver: ExpressionResolver,
        val view: View,
    )
}
