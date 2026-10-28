package com.yandex.div.compose.expressions

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.collection.ItemVariableProvider
import com.yandex.div.compose.createExpressionResolver
import com.yandex.div.compose.rootVariableProvider
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.expression
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class ItemExpressionResolverTest {
    private val controller = DivVariableController()
    private val provider = ItemVariableProvider(
        rootVariableProvider(controller),
        mapOf("index" to 0L),
    )
    private val resolver = createExpressionResolver(variableProvider = provider)

    // `other` makes the resolver observe the controller, so only the constant check in
    // onVariableChanged keeps the shadowed `index` from notifying.
    @Test
    fun `update of a shadowed controller variable is ignored`() {
        val index = Variable.IntegerVariable("index", 10)
        val other = Variable.StringVariable("other", "a")
        controller.declare(index, other)
        val values = mutableListOf<String>()
        expression("@{index}:@{other}").observeAndGet(resolver, values::add)

        index.set(20)
        other.set("b")

        assertEquals(listOf("0:a", "0:b"), values)
    }

    @Test
    fun `declaration of a shadowed controller variable is ignored`() {
        val other = Variable.StringVariable("other", "a")
        controller.declare(other)
        val values = mutableListOf<String>()
        expression("@{index}:@{other}").observeAndGet(resolver, values::add)

        controller.declare(Variable.IntegerVariable("index", 10))
        other.set("b")

        assertEquals(listOf("0:a", "0:b"), values)
    }

    @Test
    fun `item provider updates notify item expression`() {
        val values = mutableListOf<String>()
        expression("@{index}").observeAndGet(resolver, values::add)

        provider.update(mapOf("index" to 1L))
        provider.notifyChanges()

        assertEquals(listOf("0", "1"), values)
    }

    @Test
    fun `clearObservers closes item provider subscription`() {
        val values = mutableListOf<String>()
        expression("@{index}").observeAndGet(resolver, values::add)
        resolver.clearObservers()

        provider.update(mapOf("index" to 1L))
        provider.notifyChanges()

        assertEquals(listOf("0"), values)
    }
}
