package com.yandex.div.core.expression.local

import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.internal.Assert
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivVariable
import com.yandex.div2.IntegerVariable
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

private const val PATH = "0/div1"

private const val CHILD_VARIABLE = "child_variable"

class RuntimeStoreImplTest {
    private val rootResolver = mock<ExpressionResolverImpl>()
    private val rootRuntime = mock<ExpressionsRuntime> {
        on { expressionResolver } doReturn rootResolver
    }
    private val resolver = mock<ExpressionResolverImpl> {
        on { variableController } doReturn mock()
    }
    private val childRuntime = mock<ExpressionsRuntime> {
        on { expressionResolver } doReturn resolver
    }
    private val runtimeProvider = mock<ExpressionsRuntimeProvider> {
        on { createRootRuntime(any(), any(), any()) } doReturn rootRuntime
        on { createChildRuntime(any(), any(), any(), any()) } doReturn childRuntime
    }
    private val underTest = RuntimeStoreImpl(mock(), runtimeProvider, mock())

    private val divBase = mock<DivBase>()
    private val div = mock<Div> {
        on { value() } doReturn divBase
    }
    private val path = DivStatePath(0, path = listOf("0", "div1"))

    @Test
    fun `root resolver registered in store`() {
        Assert.assertNotNull(underTest.getRuntimeWithOrNull(rootResolver))
    }

    @Test
    fun `resolveRuntimeWith links path to created runtime`() {
        underTest.resolveRuntimeWith(path, div, resolver, rootResolver)

        Assert.assertNotNull(underTest.getRuntimeWithOrNull(resolver))
    }

    @Test
    fun `getOrCreateRuntime returns runtime for path if exist`() {
        underTest.putRuntime(childRuntime, PATH, rootRuntime)

        Assert.assertEquals(childRuntime, underTest.getOrCreateRuntime(path, div, resolver))
        Assert.assertEquals(childRuntime, underTest.getRuntimeWithOrNull(resolver))
    }

    @Test
    fun `getOrCreateRuntime returns parent runtime for path if no variables provided`() {
        Assert.assertEquals(rootRuntime, underTest.getOrCreateRuntime(path, div, rootResolver))
    }

    @Test
    fun `getOrCreateRuntime returns new runtime if new variables provided`() {
        setVariable()

        val runtime = underTest.getOrCreateRuntime(path, div, rootResolver)

        Assert.assertNotNull(runtime)
        Assert.assertNotSame(rootRuntime, runtime)
    }

    @Test
    fun `getOrCreateRuntime returns root runtime if parent runtime is not found`() {
        val runtime = underTest.getOrCreateRuntime(path, div, mock<ExpressionResolverImpl>())

        Assert.assertEquals(rootRuntime, runtime)
        Assert.assertNotNull(underTest.getRuntimeWithOrNull(resolver))
    }

    @Test
    fun `resolveRuntimeWith creates new runtime if new variables provided`() {
        setVariable()

        val runtime = underTest.resolveRuntimeWith(path, div, resolver, rootResolver)

        Assert.assertNotNull(runtime)
        Assert.assertNotSame(rootRuntime, runtime)
    }

    private fun setVariable() {
        val variables = listOf(DivVariable.Integer(IntegerVariable(CHILD_VARIABLE, Expression.constant(123))))
        whenever(divBase.variables).doReturn(variables)
    }
}
