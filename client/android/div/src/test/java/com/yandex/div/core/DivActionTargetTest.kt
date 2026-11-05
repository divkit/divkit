package com.yandex.div.core

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.DivDataTag
import com.yandex.div.core.actions.actionTargetBlockLocator
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.custom
import com.yandex.div.test.data.data
import com.yandex.div.test.data.state
import com.yandex.div.test.data.text
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.Div
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import org.junit.runner.RunWith
import org.mockito.kotlin.mock

@RunWith(AndroidJUnit4::class)
class DivActionTargetTest {
    private val divView = Div2View(Div2Context(
        testContextThemeWrapper(),
        DivConfiguration.Builder(mock()).enableBindOnAttach(true).build(),
    ))

    @Test
    fun `View locator does not enter nested custom elements`() {
        bindCard(custom(type = "nested", items = listOf(
            custom(type = "nested", items = listOf(text(id = "target", text = "Target"))),
        )))

        val target = findTarget()

        assertEquals("Element with id 'target' not found", target.exceptionOrNull()?.message)
    }

    @Test
    fun `View locator does not enter a custom scope`() {
        bindCard(container(items = listOf(
            custom(type = "nested", id = "left", items = listOf(text(id = "target", text = "Left"))),
            custom(type = "nested", id = "right", items = listOf(text(id = "target", text = "Right"))),
        )))

        val target = findTarget(scopeId = "right")

        assertEquals("Element with id 'target' not found in scope", target.exceptionOrNull()?.message)
    }

    @Test
    fun `View locator does not enter active states inside a custom element`() {
        bindCard(custom(type = "nested", items = listOf(state(
            id = "state",
            defaultStateId = constant("selected"),
            states = listOf(
                state(stateId = "other", div = text(id = "target", text = "Other")),
                state(stateId = "selected", div = text(id = "target", text = "Selected")),
            ),
        ))))

        val target = findTarget()

        assertEquals("Element with id 'target' not found", target.exceptionOrNull()?.message)
    }

    @Test
    fun `View locator reads the current card after it was created`() {
        bindCard(text(id = "target", text = "Initial"))
        val locator = divView.actionTargetBlockLocator()!!
        val updatedTarget = text(id = "target", text = "Updated")
        bindCard(updatedTarget)

        val result = locator.findTarget(
            divId = "target",
            scopeId = null,
            actionResolver = divView.expressionResolver,
            matches = { true },
        )

        assertSame(updatedTarget, result.getOrThrow().div)
    }

    private fun bindCard(div: Div) {
        divView.setData(data(div), DivDataTag("custom"))
    }

    private fun findTarget(scopeId: String? = null): Result<DivBlock> =
        divView.actionTargetBlockLocator()!!.findTarget(
            divId = "target", scopeId = scopeId, actionResolver = divView.expressionResolver, matches = { true },
        )
}
