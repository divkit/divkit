package com.yandex.div.compose

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.insets
import com.yandex.div.test.data.itemBuilder
import com.yandex.div.test.data.jsonArray
import com.yandex.div.test.data.jsonObject
import com.yandex.div.test.data.matchParent
import com.yandex.div.test.data.prototype
import com.yandex.div.test.data.showTooltipAction
import com.yandex.div.test.data.text
import com.yandex.div.test.data.variable
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import com.yandex.div2.DivEvaluableType
import com.yandex.div2.DivFunction
import com.yandex.div2.DivPatch
import com.yandex.div2.DivTooltip
import com.yandex.div2.DivTooltipMode
import com.yandex.div2.DivTooltipModeModal
import com.yandex.div2.DivTooltipModeNonModal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DivViewWithTooltipsTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private val activity: ComponentActivity
        get() = rule.activity

    private val actionHandler = TestExternalActionHandler()
    private val reporter = TestReporter()
    private val divContext by lazy {
        DivContext(
            baseContext = activity,
            configuration = divConfiguration {
                actionHandler = this@DivViewWithTooltipsTest.actionHandler
                reporter = this@DivViewWithTooltipsTest.reporter
            },
        )
    }
    private lateinit var divData: DivData

    @Test
    fun `tooltip is shown`() {
        setContent(
            text(
                text = "Show tooltip",
                action = action(
                    typed = showTooltipAction(id = "tooltip")
                ),
                tooltips = listOf(
                    DivTooltip(
                        id = "tooltip",
                        position = constant(DivTooltip.Position.CENTER),
                        div = text(
                            text = "Tooltip text"
                        )
                    )
                )
            )
        )

        rule.onNodeWithText("Tooltip text").assertDoesNotExist()

        rule.onNodeWithText("Show tooltip").performClick()

        rule.onNodeWithText("Tooltip text").assertIsDisplayed()
    }

    @Test
    fun `tooltip reads and updates the declaring node's local context`() {
        fun scopeName(name: String) = DivFunction(
            name = "scopeName",
            arguments = emptyList(),
            returnType = DivEvaluableType.STRING,
            body = "@{'$name'}",
        )

        val anchor = text(
            id = "anchor",
            variables = listOf(variable("label", "Before")),
            functions = listOf(scopeName("Anchor")),
            text = expression("@{label}"),
            tooltips = listOf(DivTooltip(
                id = "tooltip",
                position = constant(DivTooltip.Position.BOTTOM),
                duration = constant(0L),
                div = text(
                    id = "bubble",
                    text = expression("@{scopeName()} / @{label}"),
                    action = action(url = "div-action://set_variable?name=label&value=After"),
                ),
            )),
        )
        setContent(data(
            content = container(items = listOf(
                text(
                    id = "open",
                    text = expression("@{label}"),
                    action = action(typed = showTooltipAction("tooltip")),
                ),
                anchor,
            )),
            variables = listOf(variable("label", "Card")),
            functions = listOf(scopeName("Root")),
        ))

        rule.onNodeWithTag("open").performClick()
        rule.onNodeWithTag("bubble").assertTextEquals("Anchor / Before")

        rule.onNodeWithTag("bubble").performClick()
        rule.onNodeWithTag("bubble").assertTextEquals("Anchor / After")
        rule.onNodeWithTag("anchor").assertTextEquals("After")
        rule.onNodeWithTag("open").assertTextEquals("Card")
    }

    @Test
    fun `tooltip uses its new local scope after rebinding the card to another context`() {
        val tooltip = DivTooltip(
            id = "tooltip",
            position = constant(DivTooltip.Position.CENTER),
            duration = constant(0L),
            div = text(
                id = "bubble",
                text = expression("@{label}"),
                action = action(url = "div-action://set_variable?name=label&value=Updated"),
            ),
        )
        val card = data(text(
            id = "anchor",
            text = expression("@{label}"),
            variables = listOf(variable("label", "Initial")),
            action = action(typed = showTooltipAction("tooltip")),
            tooltips = listOf(tooltip),
        ))
        val boundContext = mutableStateOf(divContext)
        rule.setContent {
            CompositionLocalProvider(LocalDivContext provides boundContext.value) {
                DivView(card)
            }
        }
        rule.onNodeWithTag("anchor").performClick()
        rule.onNodeWithTag("bubble").performClick()
        rule.onNodeWithTag("anchor").assertTextEquals("Updated")

        rule.runOnIdle {
            boundContext.value = DivContext(activity, divConfiguration { reporter = TestReporter() })
        }
        rule.onNodeWithTag("anchor").assertTextEquals("Initial").performClick()

        rule.onNodeWithTag("bubble").assertTextEquals("Initial").performClick()
        rule.onNodeWithTag("anchor").assertTextEquals("Updated")
    }

    @Test
    fun `tooltip content and substrate read the generated anchor's item index`() {
        val tooltip = DivTooltip(
            id = "tooltip",
            position = constant(DivTooltip.Position.CENTER),
            div = text(id = "bubble", text = expression("Item @{index}")),
            substrateDiv = text(id = "substrate", text = expression("Item @{index}")),
        )
        val anchor = text(
            text = "Show tooltip",
            action = action(typed = showTooltipAction("tooltip")),
            tooltips = listOf(tooltip),
        )
        setContent(data(
            content = container(itemBuilder = itemBuilder(
                data = jsonArray(jsonObject()),
                prototypes = listOf(prototype(div = anchor)),
            )),
            variables = listOf(variable("index", 9L)),
        ))

        rule.onNodeWithText("Show tooltip").performClick()

        rule.onNodeWithTag("bubble").assertTextEquals("Item 0")
        rule.onNodeWithTag("substrate").assertTextEquals("Item 0")
    }

    @Test
    fun `substrate is shown with tooltip`() {
        showTooltip(substrate = text(text = "Substrate", id = "substrate"))

        rule.onNodeWithText("Substrate").assertIsDisplayed()
    }

    @Test
    fun `tap on passive substrate closes tooltip`() {
        showTooltip(substrate = container(id = "substrate", height = matchParent()))

        rule.onNodeWithTag("substrate").performTouchInput { click(bottomRight) }

        rule.onNodeWithText("Tooltip text").assertDoesNotExist()
    }

    @Test
    fun `tap on tooltip does not close it`() {
        showTooltip(substrate = container(id = "substrate", height = matchParent()))

        rule.onNodeWithText("Tooltip text").performTouchInput { click() }

        rule.onNodeWithText("Tooltip text").assertIsDisplayed()
    }

    @Test
    fun `outside actions run when closing is disabled`() {
        showTooltip(
            substrate = container(id = "substrate", height = matchParent()),
            closeByTapOutside = false,
            tapOutsideActions = listOf(action(url = "test://outside")),
        )

        rule.onNodeWithTag("substrate").performTouchInput { click(bottomRight) }

        assertEquals(listOf(actionData(url = "test://outside")), actionHandler.handledActions)
    }

    @Test
    fun `outside tap keeps tooltip when closing is disabled`() {
        showTooltip(
            substrate = container(id = "substrate", height = matchParent()),
            closeByTapOutside = false,
        )

        rule.onNodeWithTag("substrate").performTouchInput { click(bottomRight) }

        rule.onNodeWithText("Tooltip text").assertIsDisplayed()
    }

    @Test
    fun `substrate text observes its local variable`() {
        showTooltip(
            substrate = text(
                text = expression("@{label}"),
                id = "substrate",
                height = matchParent(),
                variables = listOf(variable("label", "Before")),
                action = action(url = "div-action://set_variable?name=label&value=After"),
            ),
        )

        rule.onNodeWithTag("substrate").performTouchInput { click(bottomRight) }

        rule.onNodeWithTag("substrate").assertTextEquals("After")
    }

    @Test
    fun `tap on substrate runs its action instead of outside actions`() {
        showTooltip(
            substrate = text(
                text = constant("Substrate"),
                id = "substrate",
                height = matchParent(),
                action = action(url = "test://substrate"),
            ),
            tapOutsideActions = listOf(action(url = "test://outside")),
        )

        rule.onNodeWithTag("substrate").performTouchInput { click(bottomRight) }

        assertEquals(listOf(actionData(url = "test://substrate")), actionHandler.handledActions)
    }

    @Test
    fun `empty area of interactive substrate does not close tooltip`() {
        showTooltip(
            substrate = container(
                id = "substrate",
                height = matchParent(),
                items = listOf(text(text = "Action", action = action(url = "test://substrate"))),
            ),
        )

        rule.onNodeWithTag("substrate").performTouchInput { click(bottomRight) }

        rule.onNodeWithText("Tooltip text").assertIsDisplayed()
    }

    @Test
    fun `tap on interactive substrate margin closes tooltip`() {
        showTooltip(
            substrate = text(
                text = constant("Substrate"),
                id = "substrate",
                width = fixed(100),
                height = fixed(100),
                margins = insets(start = 100),
                action = action(url = "test://substrate"),
            ),
        )

        rule.onNodeWithTag("substrate").performTouchInput {
            click(Offset(-width / 2f, center.y))
        }

        rule.onNodeWithText("Tooltip text").assertDoesNotExist()
    }

    @Test
    fun `tap on interactive substrate margin runs outside actions when closing is disabled`() {
        showTooltip(
            substrate = text(
                text = constant("Substrate"),
                id = "substrate",
                width = fixed(100),
                height = fixed(100),
                margins = insets(start = 100),
                action = action(url = "test://substrate"),
            ),
            closeByTapOutside = false,
            tapOutsideActions = listOf(action(url = "test://outside")),
        )

        rule.onNodeWithTag("substrate").performTouchInput {
            click(Offset(-width / 2f, center.y))
        }

        assertEquals(listOf(actionData(url = "test://outside")), actionHandler.handledActions)
        rule.onNodeWithText("Tooltip text").assertIsDisplayed()
    }

    @Test
    fun `tap inside substrate with margins runs its action`() {
        showTooltip(
            substrate = text(
                text = constant("Substrate"),
                id = "substrate",
                width = fixed(100),
                height = fixed(100),
                margins = insets(start = 100),
                action = action(url = "test://substrate"),
            ),
            tapOutsideActions = listOf(action(url = "test://outside")),
        )

        rule.onNodeWithTag("substrate").performTouchInput { click() }

        assertEquals(listOf(actionData(url = "test://substrate")), actionHandler.handledActions)
        rule.onNodeWithText("Tooltip text").assertIsDisplayed()
    }

    @Test
    fun `substrate action can hide tooltip`() {
        showTooltip(
            substrate = text(
                text = constant("Close"),
                id = "substrate",
                height = matchParent(),
                action = action(url = "div-action://hide_tooltip?id=tooltip"),
            ),
        )

        rule.onNodeWithTag("substrate").performTouchInput { click(bottomRight) }

        rule.onNodeWithText("Close").assertDoesNotExist()
    }

    @Test
    fun `substrate is removed when tooltip duration expires`() {
        showTooltip(substrate = text(text = "Substrate"), duration = 1000)

        rule.mainClock.advanceTimeBy(2000)

        rule.onNodeWithText("Substrate").assertDoesNotExist()
        rule.onNodeWithText("Tooltip text").assertDoesNotExist()
    }

    @Test
    fun `non-modal substrate is shown`() {
        showTooltip(
            substrate = text(text = "Substrate"),
            mode = DivTooltipMode.NonModal(DivTooltipModeNonModal()),
        )

        rule.onNodeWithText("Substrate").assertIsDisplayed()
    }

    @Test
    fun `tap passes through non-modal substrate when closing is disabled`() {
        showTooltip(
            substrate = container(id = "substrate", height = matchParent()),
            mode = DivTooltipMode.NonModal(DivTooltipModeNonModal()),
            closeByTapOutside = false,
            tapOutsideActions = listOf(action(url = "test://outside")),
            backgroundAction = action(url = "test://background"),
        )

        rule.onNodeWithTag("substrate").performTouchInput { click(bottomRight) }

        assertEquals(
            listOf(actionData(url = "test://background"), actionData(url = "test://outside")),
            actionHandler.handledActions,
        )
        rule.onNodeWithText("Tooltip text").assertIsDisplayed()
    }

    @Test
    fun `non-modal substrate action prevents tap on underlying card`() {
        showTooltip(
            substrate = text(
                text = constant("Substrate"),
                id = "substrate",
                height = matchParent(),
                action = action(url = "test://substrate"),
            ),
            mode = DivTooltipMode.NonModal(DivTooltipModeNonModal()),
            tapOutsideActions = listOf(action(url = "test://outside")),
            backgroundAction = action(url = "test://background"),
        )

        rule.onNodeWithTag("substrate").performTouchInput { click(bottomRight) }

        assertEquals(listOf(actionData(url = "test://substrate")), actionHandler.handledActions)
    }

    @Test
    fun `outside tap closes non-modal tooltip and reaches underlying card`() {
        showTooltip(
            substrate = container(id = "substrate", height = matchParent()),
            mode = DivTooltipMode.NonModal(DivTooltipModeNonModal()),
            backgroundAction = action(url = "test://background"),
        )

        rule.onNodeWithTag("substrate").performTouchInput { click(bottomRight) }

        assertEquals(listOf(actionData(url = "test://background")), actionHandler.handledActions)
        rule.onNodeWithText("Tooltip text").assertDoesNotExist()
    }

    @Test
    fun `cancelled gesture through non-modal substrate does not run actions`() {
        showTooltip(
            substrate = container(id = "substrate", height = matchParent()),
            mode = DivTooltipMode.NonModal(DivTooltipModeNonModal()),
            tapOutsideActions = listOf(action(url = "test://outside")),
            backgroundAction = action(url = "test://background"),
        )

        rule.onNodeWithTag("substrate").performTouchInput {
            down(bottomRight)
            cancel()
        }

        assertEquals(emptyList(), actionHandler.handledActions)
        rule.onNodeWithText("Tooltip text").assertIsDisplayed()
    }

    @Test
    fun `empty area of non-modal interactive substrate passes tap without outside actions`() {
        showTooltip(
            substrate = container(
                id = "substrate",
                height = matchParent(),
                items = listOf(text(text = "Action", action = action(url = "test://substrate"))),
            ),
            mode = DivTooltipMode.NonModal(DivTooltipModeNonModal()),
            tapOutsideActions = listOf(action(url = "test://outside")),
            backgroundAction = action(url = "test://background"),
        )

        rule.onNodeWithTag("substrate").performTouchInput { click(bottomRight) }

        assertEquals(listOf(actionData(url = "test://background")), actionHandler.handledActions)
    }

    @OptIn(ExperimentalApi::class)
    @Test
    fun `patch replaces substrate of visible tooltip`() {
        showTooltip(substrate = text(id = "substrate", text = "Before"))

        val anchor = (divData.states.single().div as Div.Text).value
        val replacement = Div.Text(anchor.copy(tooltips = anchor.tooltips?.map {
            it.copy(substrateDiv = text(id = "substrate", text = "After"))
        }))
        rule.runOnIdle {
            runBlocking {
                divContext.patcher.applyPatch(divData, DivPatch(changes = listOf(
                    DivPatch.Change("anchor", listOf(replacement)),
                )))
            }
        }

        rule.onNodeWithText("Before").assertDoesNotExist()
        rule.onNodeWithText("After").assertIsDisplayed()
        rule.onNodeWithText("Tooltip text").assertIsDisplayed()
    }

    private fun showTooltip(
        substrate: Div,
        closeByTapOutside: Boolean = true,
        tapOutsideActions: List<DivAction>? = null,
        duration: Long = 0,
        mode: DivTooltipMode = DivTooltipMode.Modal(DivTooltipModeModal()),
        backgroundAction: DivAction? = null,
    ) {
        val anchor = text(
            text = "Show tooltip",
            id = "anchor",
            action = action(typed = showTooltipAction(id = "tooltip")),
            tooltips = listOf(
                DivTooltip(
                    id = "tooltip",
                    position = constant(DivTooltip.Position.CENTER),
                    duration = constant(duration),
                    mode = mode,
                    closeByTapOutside = constant(closeByTapOutside),
                    tapOutsideActions = tapOutsideActions,
                    substrateDiv = substrate,
                    div = text(text = constant("Tooltip text"), width = fixed(120)),
                )
            )
        )
        setContent(
            if (backgroundAction == null) anchor else container(
                height = matchParent(),
                action = backgroundAction,
                items = listOf(anchor),
            )
        )
        rule.onNodeWithText("Show tooltip").performClick()
    }

    private fun setContent(content: Div) = setContent(data(content))

    private fun setContent(data: DivData) {
        divData = data
        activity.setContentView(
            ComposeView(context = divContext.component.baseContext).apply {
                setContent {
                    CompositionLocalProvider(LocalDivContext provides divContext) {
                        DivView(divData)
                    }
                }
            }
        )
    }
}
