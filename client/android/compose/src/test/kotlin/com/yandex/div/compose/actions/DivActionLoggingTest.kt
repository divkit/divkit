package com.yandex.div.compose.actions

import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.actionData
import com.yandex.div.data.Variable
import com.yandex.div.internal.actions.DivUntypedAction
import com.yandex.div.test.data.action
import com.yandex.div.test.data.booleanExpression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.customAction
import com.yandex.div.test.data.disappearAction
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.setVariableAction
import com.yandex.div.test.data.typedValue
import com.yandex.div.test.data.uriExpression
import com.yandex.div.test.data.visibilityAction
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionTyped
import org.json.JSONObject
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivActionLoggingTest {
    private val environment = ActionHandlerEnvironment()
    private val logger: DivActionLogger = mock()
    private val externalHandler: DivExternalActionHandler = mock()
    private val setVariableHandler: SetVariableActionHandler = mock()

    @BeforeTest
    fun setUp() {
        environment.init(
            actionLogger = logger,
            externalActionHandler = externalHandler,
            setVariableActionHandler = setVariableHandler
        )
    }

    @Test
    fun `external action is logged before dispatch`() {
        val payload = JSONObject(mapOf("key" to "value"))

        environment.handle(action(id = "external", payload = payload, url = "custom://action"))

        inOrder(logger, externalHandler) {
            verify(logger).logAction(
                environment.context,
                event(id = "external", payload = payload, url = "custom://action")
            )
            verify(externalHandler).handle(
                environment.context,
                actionData(
                    id = "external",
                    payload = payload,
                    source = DivActionSource.EXTERNAL,
                    url = "custom://action"
                )
            )
        }
    }

    @Test
    fun `typed action is logged before internal dispatch`() {
        val typed = setVariableAction("variable", typedValue("value")) as DivActionTyped.SetVariable

        environment.handle(action(typed = typed))

        inOrder(logger, setVariableHandler) {
            verify(logger).logAction(environment.context, event(typed = typed))
            verify(setVariableHandler).handle(environment.context, typed.value)
        }
    }

    @Test
    fun `legacy action is logged before internal dispatch`() {
        val url = "div-action://set_variable?name=variable&value=value"

        environment.handle(action(url = url))

        inOrder(logger, setVariableHandler) {
            verify(logger).logAction(environment.context, event(url = url))
            verify(setVariableHandler).handle(eq(environment.context), any<DivUntypedAction.SetVariable>())
        }
    }

    @Test
    fun `custom action is logged before dispatch`() {
        val typed = customAction()
        val payload = JSONObject(mapOf("key" to "value"))

        environment.handle(action(id = "custom", payload = payload, typed = typed))

        inOrder(logger, externalHandler) {
            verify(logger).logAction(environment.context, event(id = "custom", payload = payload, typed = typed))
            verify(externalHandler).handleCustomAction(
                environment.context,
                DivCustomActionData(id = "custom", payload = payload, source = DivActionSource.EXTERNAL)
            )
        }
    }

    @Test
    fun `disabled actions are not logged or dispatched`() {
        environment.actionHandler.handle(
            context = environment.context,
            actions = listOf(
                action(isEnabled = false, typed = customAction()),
                action(isEnabled = false, url = "custom://action")
            ),
            source = DivActionSource.TAP
        )

        verifyNoInteractions(logger, externalHandler)
    }

    @Test
    fun `disabled visibility actions are not logged or dispatched`() {
        environment.handle(visibilityAction(isEnabled = false, url = "custom://visible"))
        environment.handle(disappearAction(isEnabled = false, url = "custom://disappear"))

        verifyNoInteractions(logger, externalHandler)
    }

    @Test
    fun `disabled action does not evaluate logging expressions`() {
        environment.variableController.declare(Variable.BooleanVariable("enabled", false))

        environment.handle(
            DivAction(
                isEnabled = booleanExpression("@{enabled}"),
                logId = expression("@{missing}"),
                logUrl = uriExpression("https://divkit.tech/log/@{missing}"),
                referer = uriExpression("https://divkit.tech/referer/@{missing}")
            )
        )

        verifyNoInteractions(logger, externalHandler)
    }

    @Test
    fun `logging expressions resolve before action changes their variable`() {
        val variable = Variable.StringVariable("value", "before")
        environment.variableController.declare(variable)
        environment.init(
            actionLogger = logger,
            setVariableActionHandler = SetVariableActionHandler(environment.reporter)
        )
        val typed = setVariableAction("value", typedValue("after"))

        environment.handle(
            DivAction(
                logId = expression("@{value}"),
                logUrl = uriExpression("https://divkit.tech/log/@{value}"),
                referer = uriExpression("https://divkit.tech/referer/@{value}"),
                typed = typed
            ),
            includeLogUrl = true,
        )

        verify(logger).logAction(
            environment.context,
            event(
                id = "before",
                logUrl = "https://divkit.tech/log/before",
                referer = "https://divkit.tech/referer/before",
                typed = typed
            )
        )
        assertEquals("after", variable.getValue())
    }

    @Test
    fun `typed action does not evaluate ignored url`() {
        val typed = customAction()

        environment.handle(action(typed = typed, url = uriExpression("@{missing}")))

        verify(logger).logAction(environment.context, event(typed = typed))
    }

    @Test
    fun `beacon url and referer are logged without navigation url`() {
        environment.handle(
            action(logUrl = "https://divkit.tech/log/tap", referer = "https://divkit.tech/referer/card"),
            source = DivActionSource.TAP,
            includeLogUrl = true,
        )

        verify(logger).logAction(
            environment.context,
            event(
                logUrl = "https://divkit.tech/log/tap",
                referer = "https://divkit.tech/referer/card",
                source = DivActionSource.TAP
            )
        )
    }

    @Test
    fun `visibility action logs metadata with visibility source`() {
        environment.handle(
            visibilityAction(
                id = "visible",
                referer = "https://divkit.tech/referer/card",
                url = "https://divkit.tech/log/visible"
            )
        )

        verify(logger).logAction(
            environment.context,
            event(
                id = "visible",
                referer = "https://divkit.tech/referer/card",
                source = DivActionSource.VISIBILITY,
                url = "https://divkit.tech/log/visible"
            )
        )
    }

    @Test
    fun `disappear action logs metadata with disappear source`() {
        environment.handle(
            disappearAction(
                id = "disappear",
                referer = "https://divkit.tech/referer/card",
                url = "https://divkit.tech/log/disappear"
            )
        )

        verify(logger).logAction(
            environment.context,
            event(
                id = "disappear",
                referer = "https://divkit.tech/referer/card",
                source = DivActionSource.DISAPPEAR,
                url = "https://divkit.tech/log/disappear"
            )
        )
    }

    @Test
    fun `action source is preserved in every logged event`() {
        val sources = DivActionSource.entries

        sources.forEach { environment.handle(action(id = "action"), source = it) }

        val events = argumentCaptor<DivActionEvent>()
        verify(logger, times(sources.size)).logAction(eq(environment.context), events.capture())
        assertEquals(sources.map { event(id = "action", source = it) }, events.allValues)
    }

    @Test
    fun `non-interactive actions omit beacon URL`() {
        val sources = listOf(
            DivActionSource.EXTERNAL,
            DivActionSource.TIMER,
            DivActionSource.TRIGGER,
            DivActionSource.VIDEO
        )

        sources.forEach {
            environment.handle(action(id = "action", logUrl = "https://divkit.tech/log"), source = it)
        }

        val events = argumentCaptor<DivActionEvent>()
        verify(logger, times(sources.size)).logAction(eq(environment.context), events.capture())
        assertEquals(sources.map { event(id = "action", source = it) }, events.allValues)
    }

    @Test
    fun `non-interactive action does not evaluate beacon URL`() {
        environment.handle(
            DivAction(logUrl = uriExpression("https://divkit.tech/log/@{missing}")),
            source = DivActionSource.TIMER
        )

        verify(logger).logAction(environment.context, event(source = DivActionSource.TIMER))
    }

    @Test
    fun `action list logs events in order with values from each dispatch`() {
        environment.variableController.declare(Variable.StringVariable("value", "before"))
        environment.init(
            actionLogger = logger,
            setVariableActionHandler = SetVariableActionHandler(environment.reporter)
        )
        val typed = setVariableAction("value", typedValue("after"))

        environment.actionHandler.handle(
            context = environment.context,
            actions = listOf(
                DivAction(
                    logId = expression("@{value}"),
                    logUrl = uriExpression("https://divkit.tech/log/@{value}"),
                    typed = typed
                ),
                DivAction(
                    logId = expression("@{value}"),
                    logUrl = uriExpression("https://divkit.tech/log/@{value}"),
                    url = uriExpression("https://divkit.tech/action/@{value}")
                )
            ),
            source = DivActionSource.TAP,
            includeLogUrl = true,
        )

        val events = argumentCaptor<DivActionEvent>()
        verify(logger, times(2)).logAction(eq(environment.context), events.capture())
        assertEquals(
            listOf(
                event(
                    id = "before",
                    logUrl = "https://divkit.tech/log/before",
                    source = DivActionSource.TAP,
                    typed = typed
                ),
                event(
                    id = "after",
                    logUrl = "https://divkit.tech/log/after",
                    source = DivActionSource.TAP,
                    url = "https://divkit.tech/action/after"
                )
            ),
            events.allValues
        )
    }

    @Test
    fun `action is logged before menu is shown`() {
        val menuHolder: ActionMenuHolder = mock()
        environment.init(actionLogger = logger, actionMenuHolder = menuHolder)
        val action = action(id = "menu", menuItems = listOf(DivAction.MenuItem(text = constant("Item"))))

        environment.handle(action)

        inOrder(logger, menuHolder) {
            verify(logger).logAction(environment.context, event(id = "menu"))
            verify(menuHolder).showIfNeeded(action, environment.expressionResolver)
        }
    }

    @Test
    fun `disabled action does not show menu`() {
        val menuHolder: ActionMenuHolder = mock()
        environment.init(actionLogger = logger, actionMenuHolder = menuHolder)

        environment.handle(
            action(isEnabled = false, menuItems = listOf(DivAction.MenuItem(text = constant("Item"))))
        )

        verifyNoInteractions(logger, menuHolder)
    }

    private fun event(
        id: String? = null,
        payload: JSONObject? = null,
        source: DivActionSource = DivActionSource.EXTERNAL,
        url: String? = null,
        typed: DivActionTyped? = null,
        logUrl: String? = null,
        referer: String? = null,
    ): DivActionEvent = DivActionEvent(
        id = id,
        payload = payload,
        source = source,
        url = url?.toUri(),
        typed = typed,
        logUrl = logUrl?.toUri(),
        referer = referer?.toUri(),
    )
}
