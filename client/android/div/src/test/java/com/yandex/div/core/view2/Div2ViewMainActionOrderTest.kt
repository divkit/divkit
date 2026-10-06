package com.yandex.div.core.view2

import android.view.View
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.TestComponent
import com.yandex.div.core.TestViewComponentBuilder
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.util.EnableAssertsRule
import com.yandex.div.core.util.binding.BindingCriticalSection
import com.yandex.div.core.util.binding.BindingDispatcher
import com.yandex.div.core.util.binding.ControlledBindingExecutor
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.action
import com.yandex.div.test.data.booleanExpression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.text
import com.yandex.div.test.data.variable
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.DivBase
import com.yandex.div2.DivData
import com.yandex.div2.DivExtension
import com.yandex.div2.DivTrigger
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.robolectric.annotation.LooperMode
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class Div2ViewMainActionOrderTest {
    @get:Rule
    val assertionsEnabled = EnableAssertsRule()

    private val binding = ControlledBindingExecutor()
    private val prepared = mutableListOf<String>()
    private val boundCards = mutableSetOf<String>()
    private val context = run {
        val configuration = DivConfiguration.Builder(mock())
            .enableBindOnAttach(false)
            .extension(PreparationObserver(prepared))
            .build()
        val backing = Div2Context(testContextThemeWrapper(), configuration)
        val component = TestComponent(
            backing.div2Component,
            viewComponentBuilder = TestViewComponentBuilder(
                backing.div2Component.viewComponent(),
                bindingDispatcher = { BindingDispatcher(it, BindingCriticalSection(), binding.executor) },
            ),
        )
        spy(backing) { on { div2Component } doReturn component }
    }
    private val subscribedCard = Div2View(context)
    private val sharedCard = Div2View(context)
    private val unrelatedCard = Div2View(context)
    private val sharedTag = DivDataTag("shared-runtime")
    private val sharedData = sharedCardData()
    private val reboundData = reboundCardData()
    private val unrelatedData = unrelatedCardData()

    @AfterTest
    fun closeBindings() {
        try {
            binding.settle()
            subscribedCard.cleanup()
            sharedCard.cleanup()
            unrelatedCard.cleanup()
        } finally {
            binding.close()
        }
    }

    @Test
    fun `a shared runtime trigger delays its view rebind without delaying another card`() {
        check(subscribedCard.setData(sharedData, sharedTag))
        sharedCard.setDataAsync(sharedData, sharedTag) { if (it) boundCards += "shared" }
        binding.runNext()

        subscribedCard.setDataAsync(reboundData, DivDataTag("rebound-card")) { if (it) boundCards += "rebound" }
        unrelatedCard.setDataAsync(unrelatedData, DivDataTag("unrelated-card")) { if (it) boundCards += "unrelated" }
        binding.runWorker()
        val preparedBeforeUiActions = prepared.toList()
        binding.settle()

        val expected = MainActionOrderResult(
            preparedCardIdsBeforeUiActions = listOf("unrelated"),
            preparedCardIdsAfterUiActions = listOf("unrelated", "rebound"),
            boundCardIds = setOf("shared", "rebound", "unrelated"),
        )
        val actual = MainActionOrderResult(
            preparedCardIdsBeforeUiActions = preparedBeforeUiActions,
            preparedCardIdsAfterUiActions = prepared.toList(),
            boundCardIds = boundCards.toSet(),
        )
        assertEquals(expected, actual)
    }

    private fun sharedCardData(): DivData = data(
        text(id = "shared", text = "Shared card"),
        variables = listOf(variable("ready", 1L)),
        triggers = listOf(DivTrigger(
            actions = listOf(action(id = "shared-trigger", url = "div-action://set_variable?name=ready&value=1")),
            condition = booleanExpression("@{ready == 1}"),
            mode = constant(DivTrigger.Mode.ON_VARIABLE),
        )),
    )

    private fun reboundCardData(): DivData = data(container(
        id = "rebound",
        items = listOf(text(text = "Rebound title"), text(text = "Rebound body")),
        extensions = listOf(DivExtension(id = PREPARATION_EXTENSION)),
    ))

    private fun unrelatedCardData(): DivData = data(text(
        id = "unrelated",
        text = "unrelated",
        extensions = listOf(DivExtension(id = PREPARATION_EXTENSION)),
    ))

    private class PreparationObserver(private val prepared: MutableList<String>) : DivExtensionHandler {
        override fun matches(div: DivBase) = div.extensions?.any { it.id == PREPARATION_EXTENSION } == true

        override fun beforeBindView(divView: Div2View, expressionResolver: ExpressionResolver, view: View, div: DivBase) {
            prepared += checkNotNull(div.id)
        }

        override fun bindView(divView: Div2View, expressionResolver: ExpressionResolver, view: View, div: DivBase) = Unit

        override fun unbindView(divView: Div2View, expressionResolver: ExpressionResolver, view: View, div: DivBase) = Unit
    }

    private data class MainActionOrderResult(
        val preparedCardIdsBeforeUiActions: List<String>,
        val preparedCardIdsAfterUiActions: List<String>,
        val boundCardIds: Set<String>,
    )

    private companion object {
        const val PREPARATION_EXTENSION = "observe-preparation"
    }
}
