package com.yandex.div.compose

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTouchInput
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.pager.PagerItemWindow
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.gallery
import com.yandex.div.test.data.state
import com.yandex.div.test.data.text
import com.yandex.div.test.data.throwingErrorLogger
import com.yandex.div.test.data.trigger
import com.yandex.div.test.data.variable
import com.yandex.div.test.data.wrapContent
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivData
import com.yandex.div2.DivPageContentSize
import com.yandex.div2.DivPager
import com.yandex.div2.DivPagerLayoutMode
import com.yandex.div2.DivPatch
import com.yandex.div2.DivState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DivViewWithPatchTest {
    @get:Rule
    val rule = createComposeRule()

    private val reporter = TestReporter()
    private val actionHandler = TestExternalActionHandler()
    private val divContext = DivContext(
        getApplicationContext(),
        DivConfiguration(reporter = reporter, actionHandler = actionHandler)
    )

    @Test
    fun `patch replaces a child in the rendered container`() {
        val data = data(container(items = listOf(text(id = "target", text = "Before"))))
        setContent(data)

        assertTrue(applyPatch(data, DivPatch(changes = listOf(
            DivPatch.Change("target", listOf(text(id = "target", text = "After")))
        ))))

        rule.onNodeWithTag("target").assertTextEquals("After")
    }

    @Test
    fun `omitted patch items remove a child`() {
        val data = data(container(items = listOf(
            text(id = "target", text = "Remove"), text(id = "sibling", text = "Keep")
        )))
        setContent(data)

        applyPatch(data, DivPatch(changes = listOf(DivPatch.Change("target"))))

        rule.onNodeWithTag("target").assertDoesNotExist()
        rule.onNodeWithTag("sibling").assertTextEquals("Keep")
    }

    @Test
    fun `patch removal of active state falls back to the first remaining state`() {
        val data = data(Div.State(state(
            id = "switcher",
            states = listOf(
                DivState.State(stateId = "first", div = text(
                    id = "first",
                    text = "First",
                    action = action(url = "div-action://set_state?state_id=0/switcher/second")
                )),
                DivState.State(stateId = "second", div = text(id = "target", text = "Second"))
            )
        )))
        setContent(data)
        rule.onNodeWithTag("first").performClick()
        rule.onNodeWithTag("target").assertTextEquals("Second")
        reporter.failOnError = false

        applyPatch(data, DivPatch(changes = listOf(DivPatch.Change("target"))))

        rule.onNodeWithTag("first").assertTextEquals("First")
        assertEquals(listOf("State with id 'second' not found"), reporter.errors)
    }

    @Test
    fun `multiple patch items expand a child slot`() {
        val data = data(container(items = listOf(text(id = "target", text = "Before"))))
        setContent(data)

        applyPatch(data, DivPatch(changes = listOf(DivPatch.Change("target", listOf(
            text(id = "first", text = "First"), text(id = "second", text = "Second")
        )))))

        rule.onNodeWithTag("first").assertTextEquals("First")
        rule.onNodeWithTag("second").assertTextEquals("Second")
        rule.onNodeWithTag("target").assertDoesNotExist()
    }

    @Test
    fun `partial patch applies found changes and runs only success actions`() {
        val data = data(container(items = listOf(text(id = "target", text = "Before"))))
        setContent(data)

        assertTrue(applyPatch(data, DivPatch(
            changes = listOf(
                DivPatch.Change("target", listOf(text(id = "target", text = "After"))),
                DivPatch.Change("missing")
            ),
            onAppliedActions = listOf(action(id = "applied")),
            onFailedActions = listOf(action(id = "failed"))
        )))

        rule.onNodeWithTag("target").assertTextEquals("After")
        assertEquals(listOf(actionData(id = "applied", source = DivActionSource.PATCH)), actionHandler.handledActions)
    }

    @Test
    fun `transactional patch rolls back found changes and runs only failure actions`() {
        val data = data(container(items = listOf(text(id = "target", text = "Before"))))
        setContent(data)

        assertFalse(applyPatch(data, DivPatch(
            changes = listOf(
                DivPatch.Change("target", listOf(text(id = "target", text = "After"))),
                DivPatch.Change("missing")
            ),
            mode = constant(DivPatch.Mode.TRANSACTIONAL),
            onAppliedActions = listOf(action(id = "applied")),
            onFailedActions = listOf(action(id = "failed"))
        )))

        rule.onNodeWithTag("target").assertTextEquals("Before")
        assertEquals(listOf(actionData(id = "failed", source = DivActionSource.PATCH)), actionHandler.handledActions)
    }

    @Test
    fun `patch mode expression uses the card variable scope`() {
        val data = data(text(id = "target", text = "Before"), variables = listOf(variable("mode", "transactional")))
        setContent(data)
        val patch = DivPatch(DivParsingEnvironment(throwingErrorLogger), JSONObject("""
            {"mode":"@{mode}","changes":[{"id":"missing"}]}
        """))

        assertFalse(applyPatch(data, patch))

        rule.onNodeWithTag("target").assertTextEquals("Before")
    }

    @Test
    fun `success actions update card variables in order`() {
        val data = data(text(id = "target", text = expression("@{result}")), variables = listOf(variable("result", "Before")))
        setContent(data)

        applyPatch(data, DivPatch(
            changes = listOf(DivPatch.Change("missing")),
            onAppliedActions = listOf(
                action(url = "div-action://set_variable?name=result&value=First"),
                action(url = "div-action://set_variable?name=result&value=Ignored", isEnabled = false),
                action(url = "div-action://set_variable?name=result&value=Last")
            )
        ))

        rule.onNodeWithTag("target").assertTextEquals("Last")
    }

    @Test
    fun `subsequent patch targets the previously inserted node`() {
        val data = data(text(id = "original", text = "Before"))
        setContent(data)
        applyPatch(data, DivPatch(changes = listOf(DivPatch.Change("original", listOf(text(id = "inserted", text = "First"))))))

        assertTrue(applyPatch(data, DivPatch(
            changes = listOf(DivPatch.Change("inserted", listOf(text(id = "final", text = "Second")))),
            mode = constant(DivPatch.Mode.TRANSACTIONAL)
        )))

        rule.onNodeWithTag("final").assertTextEquals("Second")
    }

    @Test
    fun `patch preserves changed ancestor local variables for new descendants`() {
        val data = data(container(
            variables = listOf(variable("counter", 1)),
            items = listOf(
                text(id = "counter", text = expression("@{counter}"), action = action(url = "div-action://set_variable?name=counter&value=7")),
                text(id = "target", text = "Before")
            )
        ))
        setContent(data)
        rule.onNodeWithTag("counter").performClick()

        applyPatch(data, DivPatch(changes = listOf(DivPatch.Change("target", listOf(
            text(id = "inserted", text = expression("@{counter}"))
        )))))

        rule.onNodeWithTag("inserted").assertTextEquals("7")
    }

    @Test
    fun `patch preserves local variables of unchanged descendants`() {
        val data = data(container(items = listOf(
            text(id = "target", text = "Before"),
            text(id = "counter", text = expression("@{counter}"), variables = listOf(variable("counter", 1)),
                action = action(url = "div-action://set_variable?name=counter&value=7"))
        )))
        setContent(data)
        rule.onNodeWithTag("counter").performClick()

        applyPatch(data, DivPatch(changes = listOf(DivPatch.Change("target"))))

        rule.onNodeWithTag("counter").assertTextEquals("7")
    }

    @Test
    fun `patch survives leaving and returning to composition`() {
        val data = data(text(id = "target", text = "Before"))
        val visible = mutableStateOf(true)
        rule.setContent {
            CompositionLocalProvider(LocalContext provides divContext) {
                if (visible.value) DivView(data)
            }
        }
        applyPatch(data, DivPatch(changes = listOf(DivPatch.Change("target", listOf(text(id = "target", text = "After"))))))
        rule.runOnIdle { visible.value = false }
        rule.waitForIdle()

        rule.runOnIdle { visible.value = true }

        rule.onNodeWithTag("target").assertTextEquals("After")
    }

    @Test
    fun `unchanged gallery keeps its scroll when a preceding child expands`() {
        val data = data(container(items = listOf(
            text(id = "target", text = "Before"),
            gallery(id = "gallery", width = fixed(200), height = fixed(100), items = List(10) {
                text(id = "page$it", text = constant("Page $it"), width = fixed(100))
            })
        )))
        setContent(data)
        rule.onNode(hasScrollToIndexAction() and hasAnyAncestor(hasTestTag("gallery"))).performScrollToIndex(5)
        rule.onNodeWithTag("page5").assertIsDisplayed()

        applyPatch(data, DivPatch(changes = listOf(DivPatch.Change("target", listOf(
            text(text = "First"), text(text = "Second")
        )))))

        rule.onNodeWithTag("page5").assertIsDisplayed()
    }

    @Test
    fun `nested gallery keeps its scroll when a preceding non-scrollable gallery item expands`() {
        val data = data(gallery(width = wrapContent(), height = fixed(100), items = listOf(
            text(id = "target", text = constant("Before"), width = fixed(20)),
            gallery(id = "gallery", width = fixed(200), height = fixed(100), items = List(10) {
                text(id = "page$it", text = constant("Page $it"), width = fixed(100))
            })
        )))
        setContent(data)
        rule.onNode(hasScrollToIndexAction() and hasAnyAncestor(hasTestTag("gallery"))).performScrollToIndex(5)
        rule.onNodeWithTag("page5").assertIsDisplayed()

        applyPatch(data, DivPatch(changes = listOf(DivPatch.Change("target", listOf(
            text(text = constant("First"), width = fixed(20)), text(text = constant("Second"), width = fixed(20))
        )))))

        rule.onNodeWithTag("page5").assertIsDisplayed()
    }

    @Test
    fun `root deletion is rejected and invokes failure actions`() {
        reporter.failOnError = false
        val data = data(text(id = "root", text = "Keep"))
        setContent(data)

        assertFalse(applyPatch(data, DivPatch(
            changes = listOf(DivPatch.Change("root")),
            onFailedActions = listOf(action(id = "failed"))
        )))

        rule.onNodeWithText("Keep").assertIsDisplayed()
        assertEquals("Patch contains empty or invalid div for state '0'!", reporter.lastError)
        assertEquals(listOf(actionData(id = "failed", source = DivActionSource.PATCH)), actionHandler.handledActions)
    }

    @Test
    fun `patch preserves the visible gallery item when preceding items are removed`() {
        val data = data(gallery(id = "gallery", width = fixed(200), height = fixed(100), items = List(10) {
            text(id = "page$it", text = constant("Page $it"), width = fixed(100))
        }))
        setContent(data)
        rule.onNode(hasScrollToIndexAction() and hasAnyAncestor(hasTestTag("gallery"))).performScrollToIndex(5)

        applyPatch(data, DivPatch(changes = listOf(DivPatch.Change("page0"))))

        rule.onNodeWithTag("page5").assertIsDisplayed()
    }

    @Test
    fun `explicitly replaced node gets new local variables`() {
        val data = data(text(id = "counter", text = expression("@{counter}"), variables = listOf(variable("counter", 1)),
            action = action(url = "div-action://set_variable?name=counter&value=7")))
        setContent(data)
        rule.onNodeWithTag("counter").performClick()

        applyPatch(data, DivPatch(changes = listOf(DivPatch.Change("counter", listOf(
            text(id = "counter", text = expression("@{counter}"), variables = listOf(variable("counter", 2)))
        )))))

        rule.onNodeWithTag("counter").assertTextEquals("2")
    }

    @Test
    fun `patches are isolated between cards sharing a context`() {
        val first = data(text(id = "target", text = "First"))
        val second = data(text(id = "target", text = "Second"))
        setContent(second)

        applyPatch(first, DivPatch(changes = listOf(DivPatch.Change("target", listOf(text(id = "target", text = "Patched"))))))

        rule.onNodeWithTag("target").assertTextEquals("Second")
    }

    @Test
    fun `patch applied before composition is rendered`() {
        val data = data(text(id = "target", text = "Before"))
        applyPatch(data, DivPatch(changes = listOf(DivPatch.Change("target", listOf(text(id = "target", text = "After"))))))

        setContent(data)

        rule.onNodeWithTag("target").assertTextEquals("After")
    }

    @Test
    fun `removed local trigger stops observing card variables`() {
        val data = data(
            container(items = listOf(
                text(
                    id = "target",
                    text = "Remove",
                    triggers = listOf(trigger(action(id = "triggered"), condition = "@{condition == 1}"))
                ),
                text(id = "button", text = "Trigger", action = action(url = "div-action://set_variable?name=condition&value=1"))
            )),
            variables = listOf(variable("condition", 0))
        )
        setContent(data)

        applyPatch(data, DivPatch(changes = listOf(DivPatch.Change("target"))))
        rule.onNodeWithTag("target").assertDoesNotExist()
        rule.onNodeWithTag("button").performClick()

        assertEquals(emptyList(), actionHandler.handledActions)
    }

    @Test
    fun `ancestor trigger does not restart after patching its child`() {
        val data = data(Div.Container(DivContainer(
            items = listOf(text(id = "target", text = "Before")),
            variableTriggers = listOf(trigger(action(id = "triggered"), condition = "@{true}"))
        )))
        setContent(data)
        rule.waitForIdle()
        assertEquals(listOf(actionData(id = "triggered", source = DivActionSource.TRIGGER)), actionHandler.handledActions)

        applyPatch(data, DivPatch(changes = listOf(DivPatch.Change("target", listOf(text(text = "After"))))))

        rule.onNodeWithText("After").assertIsDisplayed()
        assertEquals(listOf(actionData(id = "triggered", source = DivActionSource.TRIGGER)), actionHandler.handledActions)
    }

    @Test
    fun `removed local trigger stops before success actions run`() {
        val data = data(
            container(items = listOf(text(
                id = "target",
                text = "Remove",
                triggers = listOf(trigger(action(id = "triggered"), condition = "@{condition == 1}"))
            ))),
            variables = listOf(variable("condition", 0))
        )
        setContent(data)
        rule.waitForIdle()

        applyPatch(data, DivPatch(
            changes = listOf(DivPatch.Change("target")),
            onAppliedActions = listOf(
                action(url = "div-action://set_variable?name=condition&value=1"),
                action(id = "applied")
            )
        ))
        rule.waitForIdle()

        assertEquals(listOf(actionData(id = "applied", source = DivActionSource.PATCH)), actionHandler.handledActions)
    }

    @Test
    fun `pager keeps its visible page when a preceding page is removed`() {
        val data = data(Div.Pager(DivPager(
            id = "pager",
            width = fixed(200),
            height = fixed(100),
            defaultItem = constant(5L),
            layoutMode = DivPagerLayoutMode.PageContentSize(DivPageContentSize()),
            items = List(10) { text(id = "page$it", text = constant("Page $it"), width = fixed(200)) }
        )))
        setContent(data)
        rule.onNodeWithTag("page5").assertIsDisplayed()

        applyPatch(data, DivPatch(changes = listOf(DivPatch.Change("page0"))))

        rule.onNodeWithTag("page5").assertIsDisplayed()
    }

    @Test
    fun `the same patch can expand a gallery repeatedly`() {
        val data = data(gallery(
            items = listOf(text(id = "target", text = "Before")),
            height = fixed(100)
        ))
        val patch = DivPatch(changes = listOf(DivPatch.Change("target", listOf(
            text(id = "target", text = "After"),
            text(text = "Added")
        ))))
        setContent(data)

        applyPatch(data, patch)
        rule.onNodeWithTag("target").assertTextEquals("After")
        applyPatch(data, patch)

        rule.onNodeWithTag("target").assertTextEquals("After")
    }

    @Test
    fun `infinite pager keeps its visible page when a preceding page is removed`() {
        val data = data(Div.Pager(DivPager(
            id = "pager",
            width = fixed(200),
            height = fixed(100),
            defaultItem = constant(5L),
            infiniteScroll = constant(true),
            layoutMode = DivPagerLayoutMode.PageContentSize(DivPageContentSize()),
            items = List(10) { text(id = "page$it", text = constant("Page $it"), width = fixed(200)) }
        )))
        setContent(data)
        rule.onNodeWithTag("page5").assertIsDisplayed()

        applyPatch(data, DivPatch(changes = listOf(DivPatch.Change("page0"))))

        rule.onNodeWithTag("page5").assertIsDisplayed()
    }

    @Test
    fun `offscreen patch preserves nested gallery scroll in the next pager cycle`() {
        val data = data(Div.Pager(DivPager(
            id = "pager",
            width = fixed(200),
            height = fixed(100),
            infiniteScroll = constant(true),
            layoutMode = DivPagerLayoutMode.PageContentSize(DivPageContentSize()),
            items = listOf(
                text(id = "target", text = constant("Before"), width = fixed(200)),
                text(text = constant("Page 1"), width = fixed(200)),
                gallery(id = "gallery", width = fixed(200), height = fixed(100), items = List(10) {
                    text(id = "item$it", text = constant("Item $it"), width = fixed(100))
                }),
                text(text = constant("Page 3"), width = fixed(200)),
                text(text = constant("Page 4"), width = fixed(200)),
            )
        )))
        setContent(data)
        val window = PagerItemWindow.virtuallyUnbounded(realItemCount = 5)
        rule.onNode(
            hasScrollToIndexAction() and hasAnyAncestor(hasTestTag("pager")) and
                !hasAnyAncestor(hasTestTag("gallery"))
        ).performScrollToIndex(window.rawIndex(2) + 5)
        rule.onNode(hasScrollToIndexAction() and hasAnyAncestor(hasTestTag("gallery"))).performScrollToIndex(5)
        rule.onNodeWithTag("item5").assertIsDisplayed()

        applyPatch(data, DivPatch(changes = listOf(DivPatch.Change("target", listOf(
            text(id = "target", text = constant("After"), width = fixed(200))
        )))))

        rule.onNodeWithTag("item5").assertIsDisplayed()
    }

    @Test
    fun `reusing a patch resets variables of replaced descendants`() {
        val data = data(text(id = "target", text = "Before"))
        val patch = DivPatch(changes = listOf(DivPatch.Change("target", listOf(container(
            id = "target",
            items = listOf(text(
                id = "counter",
                text = expression("@{counter}"),
                variables = listOf(variable("counter", 1)),
                action = action(url = "div-action://set_variable?name=counter&value=7")
            ))
        )))))
        setContent(data)
        applyPatch(data, patch)
        rule.onNodeWithTag("counter").performClick()
        rule.onNodeWithTag("counter").assertTextEquals("7")

        applyPatch(data, patch)

        rule.onNodeWithTag("counter").assertTextEquals("1")
    }

    @Test
    fun `patch replaces every gallery item with the same id`() {
        val data = data(gallery(
            width = fixed(200),
            height = fixed(100),
            items = List(2) { text(id = "target", text = constant("Before"), width = fixed(100)) }
        ))
        setContent(data)

        applyPatch(data, DivPatch(changes = listOf(DivPatch.Change("target", listOf(
            text(text = constant("After"), width = fixed(100))
        )))))

        rule.onAllNodesWithText("After").assertCountEquals(2)
    }

    @Test
    fun `patch occurrences have independent local variables`() {
        val data = data(container(items = List(2) { text(id = "target", text = "Before") }))
        setContent(data)
        applyPatch(data, DivPatch(changes = listOf(DivPatch.Change("target", listOf(
            text(
                id = "counter",
                text = expression("@{counter}"),
                variables = listOf(variable("counter", 1)),
                action = action(url = "div-action://set_variable?name=counter&value=7")
            )
        )))))

        rule.onAllNodesWithTag("counter")[0].performClick()

        rule.onAllNodesWithTag("counter")[0].assertTextEquals("7")
        rule.onAllNodesWithTag("counter")[1].assertTextEquals("1")
    }

    @Test
    fun `copied ancestor keeps its key before recomposition`() {
        val parent = container(items = listOf(text(id = "target", text = "Before")))
        val data = data(parent)
        setContent(data)

        rule.runOnIdle {
            val viewContext = divContext.getViewContext(data)
            val key = viewContext.compositionKeyStorage.get(parent.value())
            runBlocking {
                divContext.patcher.applyPatch(data, DivPatch(changes = listOf(DivPatch.Change(
                    "target", listOf(text(text = "After"))
                ))))
            }

            assertEquals(key, viewContext.compositionKeyStorage.get(parent.value()))
        }
    }

    @Test
    fun `stale ancestor lookup does not restart its trigger before recomposition`() {
        val parent = Div.Container(DivContainer(
            items = listOf(text(id = "target", text = "Before")),
            variableTriggers = listOf(trigger(action(id = "triggered"), condition = "@{true}"))
        ))
        val data = data(parent)
        setContent(data)

        rule.runOnIdle {
            val viewContext = divContext.getViewContext(data)
            runBlocking {
                divContext.patcher.applyPatch(data, DivPatch(changes = listOf(DivPatch.Change(
                    "target", listOf(text(text = "After"))
                ))))
            }
            viewContext.getLocalComponent(parent.value(), viewContext.rootLocalComponent)
        }

        rule.waitForIdle()
        assertEquals(listOf(actionData(id = "triggered", source = DivActionSource.TRIGGER)), actionHandler.handledActions)
    }

    @Test
    fun `patched state occurrences have independent triggers`() {
        val data = data(container(items = List(2) { index ->
            Div.State(state(
                id = "state$index",
                states = listOf(DivState.State(div = text(id = "target", text = "Before"), stateId = "initial"))
            ))
        }))
        setContent(data)

        applyPatch(data, DivPatch(changes = listOf(DivPatch.Change("target", listOf(text(
            text = "After",
            triggers = listOf(trigger(action(id = "triggered"), condition = "@{true}"))
        ))))))

        rule.waitForIdle()
        assertEquals(List(2) { actionData(id = "triggered", source = DivActionSource.TRIGGER) }, actionHandler.handledActions)
    }

    @Test
    fun `ancestor first composed from stale data keeps its component in the updated tree`() {
        val parent = Div.Container(DivContainer(
            items = listOf(text(id = "target", text = "Before")),
            variableTriggers = listOf(trigger(action(id = "triggered"), condition = "@{true}"))
        ))
        val data = data(parent)
        applyPatch(data, DivPatch(changes = listOf(DivPatch.Change("target", listOf(text(text = "After"))))))
        rule.runOnIdle {
            val viewContext = divContext.getViewContext(data)
            viewContext.getLocalComponent(parent.value(), viewContext.rootLocalComponent)
        }

        setContent(data)

        rule.onNodeWithText("After").assertIsDisplayed()
        assertEquals(listOf(actionData(id = "triggered", source = DivActionSource.TRIGGER)), actionHandler.handledActions)
    }

    @Test
    fun `patch during gallery drag preserves the visible card scope`() {
        val card = Div.Container(DivContainer(
            width = fixed(100),
            variables = listOf(variable("counter", 1)),
            variableTriggers = listOf(trigger(action(id = "triggered"), condition = "@{true}")),
            items = listOf(
                text(id = "counter", text = expression("@{counter}"),
                    action = action(url = "div-action://set_variable?name=counter&value=7")),
                text(id = "target", text = "Before")
            )
        ))
        val data = data(gallery(
            id = "gallery", width = fixed(200), height = fixed(100),
            items = List(30) { text(text = constant("Before $it"), width = fixed(100)) } + card +
                List(5) { text(text = constant("After $it"), width = fixed(100)) }
        ))
        val patch = DivPatch(changes = listOf(DivPatch.Change("target", listOf(text(text = "Patched")))))
        var patchOnMove = false
        var applied = false
        rule.setContent {
            CompositionLocalProvider(LocalContext provides divContext) {
                DivView(data, Modifier.pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            if (patchOnMove && event.changes.any { it.positionChanged() }) {
                                val viewContext = divContext.getViewContext(data)
                                applied = viewContext.patchCoordinator.applyPatch(patch, viewContext.rootLocalComponent)
                                patchOnMove = false
                            }
                        }
                    }
                })
            }
        }
        rule.onNode(hasScrollToIndexAction() and hasAnyAncestor(hasTestTag("gallery"))).performScrollToIndex(29)
        rule.onNodeWithTag("counter").performClick()
        rule.onNodeWithTag("gallery").performTouchInput {
            down(Offset(width - 1f, height / 2f))
            moveBy(Offset(-70f, 0f))
        }
        val previousPosition = rule.onNodeWithTag("counter").fetchSemanticsNode().positionInRoot.x
        rule.runOnIdle { patchOnMove = true }

        rule.onNodeWithTag("gallery").performTouchInput {
            moveBy(Offset(-70f, 0f))
            cancel()
        }

        assertTrue(applied)
        rule.onNodeWithTag("counter").assertTextEquals("7")
        assertTrue(rule.onNodeWithTag("counter").fetchSemanticsNode().positionInRoot.x < previousPosition)
        assertEquals(listOf(actionData(id = "triggered", source = DivActionSource.TRIGGER)), actionHandler.handledActions)
    }

    @Test
    fun `pager does not repeat selected actions when a preceding page is removed`() {
        val data = pagerWithSelectedActions()
        setContent(data)
        rule.onNodeWithTag("page5").assertIsDisplayed()

        applyPatch(data, DivPatch(changes = listOf(DivPatch.Change("page0"))))
        rule.waitForIdle()

        assertEquals(listOf(actionData(id = "page5", source = DivActionSource.SELECTION)), actionHandler.handledActions)
    }

    @Test
    fun `infinite pager does not repeat selected actions when a preceding page expands`() {
        val data = pagerWithSelectedActions(infiniteScroll = true)
        setContent(data)
        rule.onNodeWithTag("page5").assertIsDisplayed()

        applyPatch(data, DivPatch(changes = listOf(DivPatch.Change("page0", listOf(
            text(text = constant("First"), width = fixed(200)),
            text(text = constant("Second"), width = fixed(200))
        )))))
        rule.waitForIdle()

        assertEquals(listOf(actionData(id = "page5", source = DivActionSource.SELECTION)), actionHandler.handledActions)
    }

    @Test
    fun `pager runs selected actions when the selected page is replaced`() {
        val data = pagerWithSelectedActions()
        setContent(data)
        rule.onNodeWithTag("page5").assertIsDisplayed()

        applyPatch(data, DivPatch(changes = listOf(DivPatch.Change("page5", listOf(text(
            text = constant("Replacement"), width = fixed(200), selectedActions = listOf(action(id = "replacement"))
        ))))))
        rule.waitForIdle()

        assertEquals(listOf(
            actionData(id = "page5", source = DivActionSource.SELECTION),
            actionData(id = "replacement", source = DivActionSource.SELECTION)
        ), actionHandler.handledActions)
    }

    private fun pagerWithSelectedActions(infiniteScroll: Boolean = false): DivData = data(Div.Pager(DivPager(
        id = "pager",
        width = fixed(200),
        height = fixed(100),
        defaultItem = constant(5L),
        infiniteScroll = constant(infiniteScroll),
        layoutMode = DivPagerLayoutMode.PageContentSize(DivPageContentSize()),
        items = List(10) { text(
            id = "page$it", text = constant("Page $it"), width = fixed(200),
            selectedActions = listOf(action(id = "page$it"))
        ) }
    )))

    private fun setContent(data: DivData) {
        rule.setContent {
            CompositionLocalProvider(LocalContext provides divContext) {
                DivView(data)
            }
        }
    }

    private fun applyPatch(data: DivData, patch: DivPatch): Boolean = rule.runOnIdle {
        runBlocking { divContext.patcher.applyPatch(data, patch) }
    }
}
