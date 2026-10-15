package com.yandex.div.core.state

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.data.Variable
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.state
import com.yandex.div.test.data.text
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivStateManagerTest {

    private val variable = Variable.StringVariable("state_var", "first")
    private val resolver = mock<ExpressionResolver> {
        on { getVariable("state_var") } doReturn variable
    }
    private val div = state(
        id = "state",
        stateIdVariable = "state_var",
        states = listOf(
            state(stateId = "first", div = text(text = "first")),
            state(stateId = "second", div = text(text = "second")),
        ),
    )
    private val tooltipPath = DivStatePath.fromState(0).withNamespace("tooltip#1#t").appendDiv("tooltip#t")
    private val path = tooltipPath.appendDiv("state")
    private val block = DivBlock.State(div, resolver, path)

    private val updates = mutableListOf<String>()
    private val updater: (String) -> Unit = { updates += it }

    private val underTest = DivStateManager("tag", mock())

    @Test
    fun `updateStates sets state_id_variable of collected block`() {
        underTest.collectStateVariables(block)

        underTest.updateStates(path.append("state", "second", "second"), temporary = true)

        assertEquals("second", variable.getValue())
    }

    @Test
    fun `updateStates sets state_id_variable of collected root block`() {
        val rootPath = DivStatePath.fromState(0).withNamespace("tooltip#1#t").appendDiv("tooltip#t#state")
        underTest.collectStateVariables(DivBlock.State(div, resolver, rootPath))

        underTest.updateStates(rootPath.append("state", "second", "second"), temporary = true)

        assertEquals("second", variable.getValue())
    }

    @Test
    fun `updateStates sets state_id_variable of state nested in state without variable`() {
        val outer = state(id = "outer", states = listOf(state(stateId = "s1", div = div)))
        val outerPath = tooltipPath.appendDiv("outer")
        underTest.collectStateVariables(DivBlock.State(outer, resolver, outerPath))

        underTest.updateStates(
            outerPath.append("outer", "s1", "s1").append("state", "second", "second"),
            temporary = true,
        )

        assertEquals("second", variable.getValue())
    }

    @Test
    fun `updateStates notifies bound variable updater`() {
        underTest.collectStateVariables(block)
        underTest.bindVariable(block, updater)

        underTest.updateStates(path.append("state", "second", "second"), temporary = true)

        assertEquals(listOf("second"), updates)
    }

    @Test
    fun `updateStates does not touch variable of not collected block`() {
        underTest.updateStates(path.append("state", "second", "second"), temporary = true)

        assertEquals("first", variable.getValue())
    }
}
