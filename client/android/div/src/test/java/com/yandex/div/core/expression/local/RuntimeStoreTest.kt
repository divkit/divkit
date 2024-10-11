package com.yandex.div.core.expression.local

import com.yandex.div.core.Div2Logger
import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.expression.variables.VariableController
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.divs.DivActionBinder
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.data.Variable
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.internal.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner

private const val PARENT_PATH = "0"
private const val PATH = "0/div1"

private const val CHILD_VARIABLE = "child_variable"
private const val PARENT_VARIABLE = "parent_variable"

@RunWith(RobolectricTestRunner::class)
class RuntimeStoreTest {
    private val evaluationContext = EvaluationContext(mock(), mock(), mock(), mock())
    private val evaluator = mock<Evaluator> {
        on { evaluationContext } doReturn evaluationContext
    }
    private val errorCollector = mock<ErrorCollector>()

    private val div2Logger = Div2Logger.STUB
    private val divActionBinder = mock<DivActionBinder>()
    private val path = DivStatePath(0, emptyList(), mutableListOf("0", "div1"))
    private val underTest = RuntimeStore(evaluator, errorCollector, div2Logger, divActionBinder)

    private var runtimeFromCallback: ExpressionsRuntime? = null
    private val callback = ExpressionResolverImpl.OnCreateCallback { resolver, variableController ->
        runtimeFromCallback = ExpressionsRuntime(
            resolver, variableController, null, underTest
        )
        underTest.putRuntime(runtimeFromCallback!!)
    }

    private val resolver = ExpressionResolverImpl(mock(), evaluator, errorCollector, callback)
    private val rootVariableController: VariableController = mock<VariableController>()
    private val rootResolver = ExpressionResolverImpl(rootVariableController, evaluator, errorCollector, callback)
    private val rootRuntime: ExpressionsRuntime =
        ExpressionsRuntime(rootResolver, rootVariableController, null, underTest)

    @Before
    fun putRootResolver() {
        underTest.rootRuntime = rootRuntime
    }

    @Test
    fun `new resolver registered in store`() {
        Assert.assertNotNull(underTest.getRuntimeWithOrNull(resolver))
    }

    @Test
    fun `setPathToRuntimeWith links path to created runtime`() {
        val newResolver = ExpressionResolverImpl(mock(), mock(), errorCollector, callback)
        underTest.resolveRuntimeWith(path.fullPath, null, null, newResolver, newResolver)

        Assert.assertNotNull(underTest.getRuntimeWithOrNull(newResolver))
        Assert.assertEquals(
            runtimeFromCallback,
            underTest.getOrCreateRuntime(path.fullPath)
        )
    }

    @Test
    fun `setPathToRuntimeWith creates runtime with new variables if variables provided`() {
        val variables = listOf(Variable.IntegerVariable(CHILD_VARIABLE, 123))
        underTest.resolveRuntimeWith(path.fullPath, variables, null, resolver, resolver)

        val runtime = underTest.getOrCreateRuntime(path.fullPath, null)
        Assert.assertNotNull(underTest.getRuntimeWithOrNull(resolver))
        Assert.assertNotNull(runtime)
        Assert.assertEquals(
            123L, runtime?.variableController?.getMutableVariable(CHILD_VARIABLE)?.getValue()
        )
    }

    @Test
    fun `getOrCreateRuntime returns runtime for path if exist`() {
        val resolver = ExpressionResolverImpl(mock(), evaluator, errorCollector, callback)
        val runtime = ExpressionsRuntime(resolver, mock(), null, underTest)
        underTest.putRuntime(runtime, PATH, rootRuntime)

        Assert.assertEquals(runtime, underTest.getOrCreateRuntime(path.fullPath, null, null))
        Assert.assertNotSame(runtime, rootRuntime)
        Assert.assertNotNull(underTest.getRuntimeWithOrNull(resolver))
    }

    @Test
    fun `getOrCreateRuntime returns parent runtime for path if no variables provided`() {
        val resolver = ExpressionResolverImpl(mock(), evaluator, errorCollector, callback)
        val parentVariableController = mock<VariableController>()
        val runtime = ExpressionsRuntime(resolver, parentVariableController, null, underTest)
        underTest.putRuntime(runtime, PARENT_PATH, rootRuntime)

        Assert.assertEquals(runtime, underTest.getOrCreateRuntime(path.fullPath, parentResolver = resolver))
        Assert.assertEquals(runtime, underTest.getRuntimeWithOrNull(resolver))
    }

    @Test
    fun `getOrCreateRuntime returns wrapped parent runtime with new variables if new variables provided`() {
        val variables = listOf(Variable.IntegerVariable(CHILD_VARIABLE, 123))
        val resolver = ExpressionResolverImpl(mock(), evaluator, errorCollector, callback)
        val parentVariableController = mock<VariableController> {
            on { getMutableVariable(PARENT_VARIABLE) } doReturn Variable.StringVariable(PARENT_VARIABLE, "123")
        }
        val runtime = ExpressionsRuntime(resolver, parentVariableController, null, underTest)

        underTest.putRuntime(runtime, PARENT_PATH, rootRuntime)

        val childRuntime = underTest.getOrCreateRuntime(path.fullPath, variables, parentResolver = resolver)
        val variableController = childRuntime?.variableController

        Assert.assertNotNull(childRuntime)
        Assert.assertEquals(
            "123",
            variableController?.getMutableVariable(PARENT_VARIABLE)?.getValue()
        )
        Assert.assertEquals(
            123L,
            variableController?.getMutableVariable(CHILD_VARIABLE)?.getValue()
        )
    }

    @Test
    fun `getOrCreateRuntime returns root runtime if parent runtime is not found`() {
        val runtime = underTest.getOrCreateRuntime(path.fullPath, null, null)
        Assert.assertEquals(rootRuntime, runtime)
        Assert.assertNotNull(underTest.getRuntimeWithOrNull(resolver))
    }

    @Test
    fun `setPathToRuntimeWith links path to runtime`() {
        val resolver = ExpressionResolverImpl(mock(), evaluator, errorCollector, callback)
        underTest.resolveRuntimeWith(path.fullPath, null, null, resolver, resolver)

        Assert.assertNotNull(runtimeFromCallback)
        Assert.assertEquals(
            runtimeFromCallback,
            underTest.getRuntimeWithOrNull(resolver)
        )
        Assert.assertEquals(
            runtimeFromCallback,
            underTest.getOrCreateRuntime(path.fullPath, null, null)
        )
    }

    @Test
    fun `setPathToRuntimeWith creates new runtime if new variables provided`() {
        val variables = listOf(Variable.IntegerVariable(CHILD_VARIABLE, 123))
        val parentVariableController = mock<VariableController> {
            on { getMutableVariable(PARENT_VARIABLE) } doReturn Variable.StringVariable(PARENT_VARIABLE, "123")
        }
        val resolver = ExpressionResolverImpl(
            parentVariableController, evaluator, errorCollector, callback
        )

        underTest.resolveRuntimeWith(path.fullPath, variables, null, resolver, resolver)
        val newRuntime = underTest.getOrCreateRuntime(path.fullPath, null, null)

        Assert.assertNotNull(newRuntime)
        Assert.assertEquals(
            "123",
            newRuntime?.variableController?.getMutableVariable(PARENT_VARIABLE)?.getValue()
        )
        Assert.assertEquals(
            123L,
            newRuntime?.variableController?.getMutableVariable(CHILD_VARIABLE)?.getValue()
        )
    }
}
