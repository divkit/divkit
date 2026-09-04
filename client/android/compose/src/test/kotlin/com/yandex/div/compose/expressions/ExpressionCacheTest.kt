package com.yandex.div.compose.expressions

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.createExpressionResolver
import com.yandex.div.core.Disposable
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.colorExpression
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.intExpression
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class ExpressionCacheTest {
    private val variableController = DivVariableController()

    private val expressionResolver = createExpressionResolver(
        variableController = variableController
    )

    private val cache = ExpressionCache(
        expressionResolver = expressionResolver
    )

    @Test
    fun `getOrCreate() returns ref with initial variable value`() {
        variableController.declare(Variable.IntegerVariable("counter", 7))

        val ref = cache.getOrCreate(intExpression("@{counter}"))

        assertEquals(7L, ref.value)
    }

    @Test
    fun `getOrCreate() calls evaluate() only once for two expressions with the same raw string`() {
        val expression1 = CountingExpression("@{counter}")
        val ref1 = cache.getOrCreate(expression1)
        ref1.onRemembered()

        val expression2 = CountingExpression("@{counter}")
        val ref2 = cache.getOrCreate(expression2)
        ref2.onRemembered()

        assertEquals(1, expression1.evaluateCallCount)
        assertEquals(0, expression2.evaluateCallCount)
    }

    @Test
    fun `getOrCreate() keeps separate values for the same raw string with different types`() {
        val rawExpression = "@{'#ffaabbcc'}"

        val colorRef = cache.getOrCreate(colorExpression(rawExpression))
        val stringRef = cache.getOrCreate(expression(rawExpression))

        assertEquals(0xFFAABBCC.toInt(), colorRef.value)
        assertEquals("#ffaabbcc", stringRef.value)
    }

    @Test
    fun `two refs created before either is remembered still share one entry`() {
        val variable = Variable.IntegerVariable("counter", 1)
        variableController.declare(variable)

        val ref1 = cache.getOrCreate(intExpression("@{counter}"))
        val ref2 = cache.getOrCreate(intExpression("@{counter}"))

        ref1.onRemembered()
        ref2.onRemembered()

        variable.set(99)

        assertEquals(99L, ref1.value)
        assertEquals(99L, ref2.value)

        ref1.onForgotten()

        variable.set(100)

        assertEquals(100L, ref2.value)
    }

    @Test
    fun `onRemembered() starts subscription - state updates when variable changes`() {
        val variable = Variable.IntegerVariable("counter", 1)
        variableController.declare(variable)

        val ref = cache.getOrCreate(intExpression("@{counter}"))
        ref.onRemembered()

        variable.set(99)

        assertEquals(99L, ref.value)
    }

    @Test
    fun `state does not update before onRemembered()`() {
        val variable = Variable.IntegerVariable("counter", 1)
        variableController.declare(variable)

        val ref = cache.getOrCreate(intExpression("@{counter}"))

        variable.set(99)

        assertEquals(1L, ref.value)
    }

    @Test
    fun `onAbandoned() removes unretained entry`() {
        val variable = Variable.IntegerVariable("counter", 1)
        variableController.declare(variable)

        val abandonedRef = cache.getOrCreate(intExpression("@{counter}"))
        abandonedRef.onAbandoned()

        variable.set(99)

        val ref = cache.getOrCreate(intExpression("@{counter}"))

        assertEquals(99L, ref.value)
    }

    @Test
    fun `onAbandoned() keeps entry while another ref is pending`() {
        val expression = CountingExpression("@{counter}")
        val ref = cache.getOrCreate(expression)
        cache.getOrCreate(expression)

        ref.onAbandoned()
        cache.getOrCreate(expression)

        assertEquals(1, expression.evaluateCallCount)
    }

    @Test
    fun `pending ref keeps subscription between remembered consumers`() {
        val variable = Variable.IntegerVariable("counter", 1)
        variableController.declare(variable)

        val ref1 = cache.getOrCreate(intExpression("@{counter}"))
        val ref2 = cache.getOrCreate(intExpression("@{counter}"))
        ref1.onRemembered()
        ref1.onForgotten()

        variable.set(99)
        ref2.onRemembered()

        assertEquals(99L, ref2.value)
    }

    @Test
    fun `onForgotten() closes subscription when last consumer leaves`() {
        val variable = Variable.IntegerVariable("counter", 1)
        variableController.declare(variable)

        val ref = cache.getOrCreate(intExpression("@{counter}"))
        ref.onRemembered()
        ref.onForgotten()

        variable.set(99)

        assertEquals(1L, ref.value)
    }

    @Test
    fun `onForgotten() does not close subscription while other consumers remain`() {
        val variable = Variable.IntegerVariable("counter", 1)
        variableController.declare(variable)

        val ref1 = cache.getOrCreate(intExpression("@{counter}"))
        val ref2 = cache.getOrCreate(intExpression("@{counter}"))
        ref1.onRemembered()
        ref2.onRemembered()
        ref1.onForgotten()

        variable.set(42)

        assertEquals(42L, ref2.value)
    }

    @Test
    fun `onForgotten() on non-retained ref is a no-op`() {
        val variable = Variable.IntegerVariable("counter", 1)
        variableController.declare(variable)

        val ref = cache.getOrCreate(intExpression("@{counter}"))
        ref.onForgotten()

        variable.set(99)

        assertEquals(1L, ref.value)
    }
}

private class CountingExpression(
    private val rawExpression: String
) : Expression<String>() {

    var evaluateCallCount = 0
        private set

    override val rawValue: Any
        get() = rawExpression

    override fun evaluate(resolver: ExpressionResolver): String {
        evaluateCallCount++
        return "test"
    }

    override fun observe(resolver: ExpressionResolver, callback: (String) -> Unit): Disposable {
        return Disposable.NULL
    }
}
