package com.yandex.div.compose.container

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.actionData
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.arrayExpression
import com.yandex.div.test.data.booleanExpression
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.itemBuilder
import com.yandex.div.test.data.jsonArray
import com.yandex.div.test.data.jsonObject
import com.yandex.div.test.data.prototype
import com.yandex.div.test.data.text
import com.yandex.div.test.data.variable
import com.yandex.div2.Div
import com.yandex.div2.DivCollectionItemBuilder
import com.yandex.div2.DivData
import com.yandex.div2.DivPatch
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import org.robolectric.ParameterizedRobolectricTestRunner
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Observers that built items add to card variables are released together with the items.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
class DivViewWithItemBuilderScopeTest(private val case: Case) {
    @get:Rule
    val rule = createComposeRule()

    private val elements = Variable.ArrayVariable("elements", jsonArray())
    // The spy records the live observers of a card variable, so a leaked item scope stays visible.
    private val parent = spy(Variable.BooleanVariable("parent", false))
    private val observers = mutableSetOf<(Variable) -> Unit>()
    private val variableController = DivVariableController()
    private val actionHandler = TestExternalActionHandler()
    private val divContext = DivContext(
        getApplicationContext(),
        DivConfiguration(
            reporter = TestReporter(),
            actionHandler = actionHandler,
            variableController = variableController
        )
    )

    @BeforeTest
    fun setUp() {
        doAnswer {
            observers.add(it.getArgument(0))
            it.callRealMethod()
        }.whenever(parent).addObserver(any())
        doAnswer {
            observers.remove(it.getArgument<(Variable) -> Unit>(0))
            it.callRealMethod()
        }.whenever(parent).removeObserver(any())
        variableController.declare(elements, parent)
    }

    @Test
    fun `item observers are released with the items`() {
        val card = data(container(items = listOf(
            container(
                id = BUILDER_ID,
                itemBuilder = itemBuilder(
                    data = arrayExpression("@{elements}"),
                    prototypes = listOf(case.prototype())
                )
            ),
            text(text = "sibling")
        )))
        rule.setContent {
            CompositionLocalProvider(LocalContext provides divContext) { DivView(card) }
        }
        rule.waitForIdle()
        val cardObservers = observers.toSet()
        elements.set(jsonArray(jsonObject("version" to 1)))
        rule.waitForIdle()
        assertTrue(observers.size > cardObservers.size)

        when (case.removal) {
            Removal.DATA -> elements.set(jsonArray())
            Removal.PATCH -> patchBuilder(card, items = null)
            Removal.REPLACEMENT -> patchBuilder(card, items = listOf(replacementBuilder()))
        }
        rule.waitForIdle()

        assertEquals(cardObservers, observers)
    }

    private fun patchBuilder(card: DivData, items: List<Div>?) {
        rule.runOnIdle {
            assertTrue(runBlocking {
                divContext.patcher.applyPatch(card, DivPatch(
                    changes = listOf(DivPatch.Change(BUILDER_ID, items)),
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

    enum class Removal { DATA, PATCH, REPLACEMENT }

    class Case(
        private val name: String,
        val removal: Removal,
        val prototype: () -> DivCollectionItemBuilder.Prototype,
    ) {
        override fun toString() = name
    }

    companion object {
        private const val BUILDER_ID = "builder"

        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun cases(): List<Case> = cases

        // The runner calls the provider once per case, so the list is built once.
        private val cases: List<Case> = listOf(
            Case("selector, data emptied", Removal.DATA) {
                prototype(selector = booleanExpression("@{parent}"), div = text(text = "item"))
            },
            Case("item expression, data emptied", Removal.DATA) {
                prototype(div = text(text = expression("@{parent}")))
            },
            Case("item local variable, data emptied", Removal.DATA) {
                prototype(div = localVariableText())
            },
            Case("nested builder, data emptied", Removal.DATA) {
                val nested = container(items = listOf(localVariableText()))
                prototype(div = container(itemBuilder = itemBuilder(
                    data = jsonArray(jsonObject()),
                    prototypes = listOf(prototype(div = nested))
                )))
            },
            Case("item expression, builder removed by a patch", Removal.PATCH) {
                prototype(div = text(text = expression("@{parent}")))
            },
            Case("item expression, builder replaced by a patch", Removal.REPLACEMENT) {
                prototype(div = text(text = expression("@{parent}")))
            },
        )

        private fun replacementBuilder(): Div = container(
            id = BUILDER_ID,
            itemBuilder = itemBuilder(
                data = arrayExpression("@{elements}"),
                prototypes = listOf(prototype(div = text(text = "replaced")))
            )
        )

        private fun localVariableText() = text(
            text = expression("@{local}:@{parent}"),
            variables = listOf(variable("local", "item"))
        )
    }
}
