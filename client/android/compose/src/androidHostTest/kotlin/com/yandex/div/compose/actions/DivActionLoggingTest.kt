package com.yandex.div.compose.actions

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.actionData
import com.yandex.div.data.Variable
import com.yandex.div.internal.actions.DivUntypedAction
import com.yandex.div.test.data.action
import com.yandex.div.test.data.customAction
import com.yandex.div.test.data.disappearAction
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.menuItem
import com.yandex.div.test.data.setVariableAction
import com.yandex.div.test.data.typedValue
import com.yandex.div.test.data.uriExpression
import com.yandex.div.test.data.visibilityAction
import com.yandex.div2.DivAction
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
    private val context = environment.context
    private val externalHandler: DivExternalActionHandler = mock()
    private val menuHolder: ActionMenuHolder = mock()
    private val setVariableHandler: SetVariableActionHandler = mock()

    @BeforeTest
    fun setUp() {
        environment.init(
            actionMenuHolder = menuHolder,
            externalActionHandler = externalHandler,
            setVariableActionHandler = setVariableHandler
        )
    }

    @Test
    fun `div-action action is logged before dispatch`() {
        val url = "div-action://set_variable?name=variable&value=value"

        environment.handle(
            action(url = url),
            source = DivActionSource.TAP,
        )

        inOrder(externalHandler, setVariableHandler) {
            verify(externalHandler).onActionTriggered(
                context,
                actionData(source = DivActionSource.TAP, url = url)
            )
            verify(setVariableHandler).handle(
                eq(context),
                any<DivUntypedAction.SetVariable>()
            )
        }
    }

    @Test
    fun `external action is logged before dispatch`() {
        environment.handle(
            action(id = "action_id", url = "custom://action"),
            source = DivActionSource.TAP,
        )

        val actionData = actionData(
            id = "action_id",
            source = DivActionSource.TAP,
            url = "custom://action"
        )

        inOrder(externalHandler) {
            verify(externalHandler).onActionTriggered(context, actionData)
            verify(externalHandler).handle(context, actionData)
        }
    }

    @Test
    fun `custom action is logged before dispatch`() {
        environment.handle(
            action(id = "custom", typed = customAction())
        )

        inOrder(externalHandler) {
            verify(externalHandler).onActionTriggered(
                context,
                actionData(id = "custom", source = DivActionSource.EXTERNAL)
            )
            verify(externalHandler).handleCustomAction(
                context,
                DivCustomActionData(
                    id = "custom",
                    payload = null,
                    source = DivActionSource.EXTERNAL
                )
            )
        }
    }

    @Test
    fun `disabled actions are not logged`() {
        environment.handle(
            action(isEnabled = false, url = "custom://action")
        )

        verifyNoInteractions(externalHandler)
    }

    @Test
    fun `action without typed and url is logged`() {
        environment.handle(
            action(
                logUrl = "https://divkit.tech/log/tap",
                referer = "https://divkit.tech/referer/card"
            ),
            source = DivActionSource.TAP,
        )

        verify(externalHandler).onActionTriggered(
            context,
            actionData(
                logUrl = "https://divkit.tech/log/tap",
                referer = "https://divkit.tech/referer/card",
                source = DivActionSource.TAP
            )
        )
    }

    @Test
    fun `visibility action is logged with visibility source`() {
        environment.handle(
            visibilityAction(
                id = "visible",
                referer = "https://divkit.tech/referer/card",
                url = "https://divkit.tech/log/visible"
            )
        )

        verify(externalHandler).onActionTriggered(
            context,
            actionData(
                id = "visible",
                referer = "https://divkit.tech/referer/card",
                source = DivActionSource.VISIBILITY,
                url = "https://divkit.tech/log/visible"
            )
        )
    }

    @Test
    fun `disappear action is logged with disappear source`() {
        environment.handle(
            disappearAction(
                id = "disappear",
                referer = "https://divkit.tech/referer/card",
                url = "https://divkit.tech/log/disappear"
            )
        )

        verify(externalHandler).onActionTriggered(
            context,
            actionData(
                id = "disappear",
                referer = "https://divkit.tech/referer/card",
                source = DivActionSource.DISAPPEAR,
                url = "https://divkit.tech/log/disappear"
            )
        )
    }

    @Test
    fun `action is logged with resolved log_url`() {
        environment.variableController.declare(Variable.StringVariable("value", "timer"))

        environment.handle(
            DivAction(logUrl = uriExpression("https://divkit.tech/log/@{value}")),
            source = DivActionSource.TIMER,
        )

        verify(externalHandler).onActionTriggered(
            context,
            actionData(
                logUrl = "https://divkit.tech/log/timer",
                source = DivActionSource.TIMER,
            )
        )
    }

    @Test
    fun `action list logs events in order with values from each dispatch`() {
        environment.variableController.declare(Variable.StringVariable("value", "before"))
        environment.init(
            externalActionHandler = externalHandler,
            setVariableActionHandler = SetVariableActionHandler(environment.reporter)
        )
        val typed = setVariableAction("value", typedValue("after"))

        environment.actionHandler.handle(
            context = context,
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
        )

        val events = argumentCaptor<DivActionData>()
        verify(externalHandler, times(2)).onActionTriggered(eq(context), events.capture())
        assertEquals(
            listOf(
                actionData(
                    id = "before",
                    logUrl = "https://divkit.tech/log/before",
                    source = DivActionSource.TAP
                ),
                actionData(
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
        val action = action(
            id = "menu",
            menuItems = listOf(menuItem(text = "Item"))
        )

        environment.handle(action, source = DivActionSource.TAP)

        inOrder(externalHandler, menuHolder) {
            verify(externalHandler).onActionTriggered(context, actionData(id = "menu"))
            verify(menuHolder).showIfNeeded(action, environment.expressionResolver)
        }
    }
}
