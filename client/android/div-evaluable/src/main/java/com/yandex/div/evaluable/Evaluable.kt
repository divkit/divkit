package com.yandex.div.evaluable

import com.yandex.div.evaluable.function.GetBooleanValue
import com.yandex.div.evaluable.function.GetColorValue
import com.yandex.div.evaluable.function.GetColorValueString
import com.yandex.div.evaluable.function.GetIntegerValue
import com.yandex.div.evaluable.function.GetNumberValue
import com.yandex.div.evaluable.function.GetStringValue
import com.yandex.div.evaluable.function.GetUrlValueWithStringFallback
import com.yandex.div.evaluable.function.GetUrlValueWithUrlFallback
import com.yandex.div.evaluable.internal.Parser
import com.yandex.div.evaluable.internal.Token
import com.yandex.div.evaluable.internal.Tokenizer

abstract class Evaluable(val rawExpr: String) {

    abstract val variables: List<String>
    abstract val dynamicVariables: List<Evaluable>

    /**
     * Value of this field is valid only after calling the eval method.
     */
    private var isCacheable = true

    private var evalCalled = false

    internal fun updateIsCacheable(value : Boolean) {
        isCacheable = isCacheable && value
    }

    fun checkIsCacheable(): Boolean {
        assert(evalCalled)
        return isCacheable
    }

    @Throws(EvaluableException::class)
    internal fun eval(evaluator: Evaluator): Any {
        return evalImpl(evaluator).also { evalCalled = true }
    }

    @Throws(EvaluableException::class)
    protected abstract fun evalImpl(evaluator: Evaluator): Any

    internal class Lazy(private val expr: String): Evaluable(expr) {
        private val tokens = Tokenizer.tokenize(expr)
        private lateinit var expression: Evaluable
        override val variables: List<String>
            get() =  if (this::expression.isInitialized) {
                expression.variables
            } else {
                tokens.filterIsInstance<Token.Operand.Variable>().map { it.name }
            }
        override val dynamicVariables: List<Evaluable>
            get() = initExpression().let { expression.dynamicVariables }
        override fun evalImpl(evaluator: Evaluator): Any {
            initExpression()
            val res = expression.eval(evaluator)
            updateIsCacheable(expression.isCacheable)
            return res
        }
        override fun toString(): String = expr
        private fun initExpression() {
            if (!this::expression.isInitialized) {
                expression = Parser.parse(tokens, rawExpr)
            }
        }
    }

    internal data class Unary(
        val token: Token.Operator,
        val expression: Evaluable,
        val rawExpression: String,
    ) : Evaluable(rawExpression) {
        override val variables: List<String> = expression.variables
        override val dynamicVariables: List<Evaluable> = expression.dynamicVariables
        override fun evalImpl(evaluator: Evaluator): Any = evaluator.evalUnary(this)
        override fun toString(): String = "$token$expression"
    }

    internal data class Binary(
        val token: Token.Operator.Binary,
        val left: Evaluable,
        val right: Evaluable,
        val rawExpression: String,
    ) : Evaluable(rawExpression) {
        override val variables: List<String> = left.variables + right.variables
        override val dynamicVariables: List<Evaluable> = left.dynamicVariables + right.dynamicVariables
        override fun evalImpl(evaluator: Evaluator): Any = evaluator.evalBinary(this)
        override fun toString(): String = "($left $token $right)"
    }

    internal data class Ternary(
        val token: Token.Operator,
        val firstExpression: Evaluable,
        val secondExpression: Evaluable,
        val thirdExpression: Evaluable,
        val rawExpression: String,
    ) : Evaluable(rawExpression) {
        override val variables: List<String> = firstExpression.variables +
            secondExpression.variables + thirdExpression.variables
        override val dynamicVariables: List<Evaluable> = firstExpression.dynamicVariables +
            secondExpression.dynamicVariables + thirdExpression.dynamicVariables
        override fun evalImpl(evaluator: Evaluator): Any = evaluator.evalTernary(this)
        override fun toString(): String {
            val opIf = Token.Operator.TernaryIf
            val opElse = Token.Operator.TernaryElse
            return "($firstExpression $opIf $secondExpression $opElse $thirdExpression)"
        }
    }

    internal data class Try(
        val token: Token.Operator.Try,
        val tryExpression: Evaluable,
        val fallbackExpression: Evaluable,
        val rawExpression: String,
    ) : Evaluable(rawExpression) {
        override val variables: List<String> = tryExpression.variables + fallbackExpression.variables
        override val dynamicVariables: List<Evaluable> =
            tryExpression.dynamicVariables + fallbackExpression.dynamicVariables
        override fun evalImpl(evaluator: Evaluator): Any = evaluator.evalTry(this)
        override fun toString() = "($tryExpression $token $fallbackExpression)"
    }

    internal data class MethodCall(
        val token: Token.Function,
        val arguments: List<Evaluable>,
        val rawExpression: String,
    ) : Evaluable(rawExpression) {
        override val variables: List<String> =
            arguments.map { it.variables }.reduceOrNull { acc, vars -> acc + vars } ?: emptyList()
        override val dynamicVariables: List<Evaluable> =
            arguments.map { it.dynamicVariables }.reduceOrNull { acc, vars -> acc + vars } ?: emptyList()
        override fun evalImpl(evaluator: Evaluator): Any = evaluator.evalMethodCall(this)
        override fun toString(): String {
            val argsString = if (arguments.size > 1) {
                arguments.subList(1, arguments.size).joinToString(Token.Function.ArgumentDelimiter.toString())
            } else ""
            return "${arguments.first()}.${token.name}($argsString)"
        }
    }

    internal data class FunctionCall(
        val token: Token.Function,
        val arguments: List<Evaluable>,
        val rawExpression: String,
    ) : Evaluable(rawExpression) {
        override val variables: List<String> =
            arguments.map { it.variables }.reduceOrNull { acc, vars -> acc + vars } ?: emptyList()
        override val dynamicVariables: List<Evaluable> = findDynamicVariables()
        override fun evalImpl(evaluator: Evaluator): Any = evaluator.evalFunctionCall(this)
        override fun toString(): String {
            val argsString = arguments.joinToString(separator = Token.Function.ArgumentDelimiter.toString())
            return "${token.name}($argsString)"
        }
        private fun findDynamicVariables(): List<Evaluable> {
            val names = if (token.name in functionsWithVariableName) arguments else emptyList()
            return arguments.map { it.dynamicVariables }
                .plus(listOf(names))
                .reduceOrNull { acc, vars -> acc + vars } ?: emptyList()
        }
    }

    internal data class StringTemplate(
        val arguments: List<Evaluable>,
        val rawExpression: String,
    ) : Evaluable(rawExpression) {
        override val variables: List<String> = arguments.map { it.variables }.reduce { acc, vars -> acc + vars }
        override val dynamicVariables: List<Evaluable> =
            arguments.map { it.dynamicVariables }.reduce { acc, vars -> acc + vars }
        override fun evalImpl(evaluator: Evaluator): Any = evaluator.evalStringTemplate(this)
        override fun toString(): String = arguments.joinToString(separator = "")
    }

    internal data class Variable(
        val token: Token.Operand.Variable,
        val rawExpression: String,
    ) : Evaluable(rawExpression) {
        override val variables: List<String> = listOf(token.name)
        override val dynamicVariables: List<Evaluable> = emptyList()
        override fun evalImpl(evaluator: Evaluator): Any = evaluator.evalVariable(this)
        override fun toString(): String = token.name
    }

    internal data class Value(
        val token: Token.Operand.Literal,
        val rawExpression: String,
    ) : Evaluable(rawExpression) {
        override val variables: List<String> = emptyList()
        override val dynamicVariables: List<Evaluable> = emptyList()
        override fun evalImpl(evaluator: Evaluator): Any = evaluator.evalValue(this)
        override fun toString(): String = when (token) {
            is Token.Operand.Literal.Str -> "'${token.value}'"
            is Token.Operand.Literal.Num -> token.value.toString()
            is Token.Operand.Literal.Bool -> token.value.toString()
        }
    }

    companion object {
        @JvmStatic
        fun prepare(expr: String) : Evaluable {
            return Parser.parse(Tokenizer.tokenize(expr), expr)
        }

        @JvmStatic
        fun lazy(expr: String) : Evaluable {
            return Lazy(expr)
        }

        internal val functionsWithVariableName = setOf(
            GetIntegerValue.name,
            GetNumberValue.name,
            GetStringValue.name,
            GetColorValue.name,
            GetColorValueString.name,
            GetUrlValueWithUrlFallback.name,
            GetUrlValueWithStringFallback.name,
            GetBooleanValue.name,
        )
    }
}
