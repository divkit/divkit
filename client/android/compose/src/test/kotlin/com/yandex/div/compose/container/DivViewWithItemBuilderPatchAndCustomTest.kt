package com.yandex.div.compose.container

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.actionData
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.custom.DivCustomEnvironment
import com.yandex.div.compose.custom.DivCustomViewFactory
import com.yandex.div.compose.setContent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.custom
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.itemBuilder
import com.yandex.div.test.data.jsonArray
import com.yandex.div.test.data.jsonObject
import com.yandex.div.test.data.prototype
import com.yandex.div.test.data.text
import com.yandex.div.test.data.trigger
import com.yandex.div.test.data.variable
import com.yandex.div2.DivData
import com.yandex.div2.DivPatch
import com.yandex.div2.DivVariable
import com.yandex.div2.StrVariable
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DivViewWithItemBuilderPatchAndCustomTest {
    @get:Rule
    val rule = createComposeRule()

    private val variableController = DivVariableController()
    private val actionHandler = TestExternalActionHandler()
    private val divContext = DivContext(
        getApplicationContext(),
        DivConfiguration(
            reporter = TestReporter(),
            actionHandler = actionHandler,
            variableController = variableController,
            customViewFactories = mapOf("children" to ChildrenFactory())
        )
    )

    @Test
    fun `builder trigger remains active after patching a sibling`() {
        val fire = Variable.BooleanVariable("fire", false)
        variableController.declare(fire)
        val card = data(container(items = listOf(
            container(itemBuilder = itemBuilder(
                data = jsonArray(jsonObject()),
                prototypes = listOf(prototype(
                    id = constant("item"),
                    div = text(
                        text = expression("@{local}"),
                        variables = listOf(variable("local", "initial")),
                        triggers = listOf(trigger(
                            condition = "@{fire}",
                            action = action(
                                url = "div-action://set_variable?name=local&value=changed"
                            )
                        ))
                    )
                ))
            )),
            text(id = "target", text = "before")
        )))
        setContent(card)
        applyPatch(card, DivPatch.Change("target", listOf(text(text = "after"))))

        fire.set(true)

        rule.onNodeWithTag("item").assertTextEquals("changed")
    }

    @Test
    fun `builder stops triggering after its container is removed by a patch`() {
        val fire = Variable.BooleanVariable("fire", false)
        val result = Variable.StringVariable("result", "initial")
        variableController.declare(fire, result)
        val card = data(container(items = listOf(
            container(id = "target", itemBuilder = itemBuilder(
                data = jsonArray(jsonObject()),
                prototypes = listOf(prototype(div = text(
                    text = "item",
                    variables = listOf(variable("local", "item")),
                    triggers = listOf(trigger(
                        condition = "@{fire}",
                        action = action(url = "div-action://set_variable?name=result&value=changed")
                    ))
                )))
            )),
            text(text = "sibling")
        )))
        setContent(card)
        applyPatch(card, DivPatch.Change("target"))

        fire.set(true)
        rule.waitForIdle()

        assertEquals("initial", result.getValue())
    }

    @Test
    fun `custom children have independent local state in builder instances`() {
        val child = text(
            text = expression("@{local}"),
            variables = listOf(DivVariable.Str(StrVariable(
                name = "local", value = expression("@{it.getString('title')}")
            ))),
            action = action(url = "div-action://set_variable?name=local&value=changed")
        )
        setContent(data(container(itemBuilder = itemBuilder(
            data = jsonArray(jsonObject("title" to "First"), jsonObject("title" to "Second")),
            prototypes = listOf(prototype(div = custom(type = "children", items = listOf(child))))
        ))))
        rule.onNodeWithText("First").performClick()

        rule.onNodeWithText("changed").assertExists()
        rule.onNodeWithText("Second").assertExists()
    }

    @Test
    fun `nested item builders remain reactive after patching a sibling`() {
        val value = Variable.StringVariable("value", "before")
        variableController.declare(value)
        val nested = container(itemBuilder = itemBuilder(
            data = jsonArray(jsonObject()),
            prototypes = listOf(prototype(id = constant("nested"), div = text(
                text = expression("@{local}:@{value}"),
                variables = listOf(variable("local", "item"))
            )))
        ))
        val card = data(container(items = listOf(
            container(itemBuilder = itemBuilder(
                data = jsonArray(jsonObject()), prototypes = listOf(prototype(div = nested))
            )),
            text(id = "target", text = "before")
        )))
        setContent(card)
        applyPatch(card, DivPatch.Change("target", listOf(text(text = "after"))))

        value.set("after")

        rule.onNodeWithTag("nested").assertTextEquals("item:after")
    }

    @Test
    fun `builder items keep local state when a patch copies their container`() {
        // Patching an element of the ignored `items` copies the container with the same builder.
        val card = data(container(
            itemBuilder = itemBuilder(
                data = jsonArray(jsonObject()),
                prototypes = listOf(prototype(
                    id = constant("item"),
                    div = text(
                        text = expression("@{local}"),
                        variables = listOf(variable("local", "initial")),
                        action = action(url = "div-action://set_variable?name=local&value=changed")
                    )
                ))
            ),
            items = listOf(text(id = "target", text = "before"))
        ))
        setContent(card)
        rule.onNodeWithTag("item").performClick()
        rule.onNodeWithTag("item").assertTextEquals("changed")

        applyPatch(card, DivPatch.Change("target", listOf(text(text = "after"))))

        rule.onNodeWithTag("item").assertTextEquals("changed")
    }

    private fun setContent(card: DivData) {
        rule.setContent {
            CompositionLocalProvider(LocalContext provides divContext) {
                DivView(card)
            }
        }
    }

    private fun applyPatch(card: DivData, change: DivPatch.Change) {
        rule.runOnIdle {
            assertTrue(runBlocking {
                divContext.patcher.applyPatch(card, DivPatch(
                    changes = listOf(change),
                    onAppliedActions = listOf(action(id = "applied"))
                ))
            })
        }
        rule.waitForIdle()
        // onAppliedActions run right after the patch cleanup, so the cleanup has happened.
        assertEquals(
            listOf(actionData(id = "applied", source = DivActionSource.PATCH)),
            actionHandler.handledActions
        )
    }

    private class ChildrenFactory : DivCustomViewFactory {
        @Composable
        override fun Content(modifier: Modifier, environment: DivCustomEnvironment) {
            Column(modifier) { environment.items() }
        }
    }
}
