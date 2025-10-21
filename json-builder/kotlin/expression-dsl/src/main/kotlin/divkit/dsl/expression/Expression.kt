@file:Suppress("ktlint", "unused")

package divkit.dsl.expression

import divkit.dsl.Url

/**
 * Base class for every expression.
 */
sealed interface Expression<OUTPUT_TYPE> {
    fun build(): String
}

/**
 * Represents a value. Could be div variable or a literal.
 */
internal sealed interface Value<OUTPUT_TYPE> : Expression<OUTPUT_TYPE>

/**
 * Variable expression definition.
 * @see booleanVariable
 * @see stringVariable
 * @see numberVariable
 * @see integerVariable
 * @see urlVariable
 * @see dictVariable
 * @see datetimeVariable
 * @see arrayVariable
 * @see colorVariable
 */
@ExposedCopyVisibility
data class Var<RESULT> internal constructor(
    val name: String,
) : Value<RESULT> {
    override fun build(): String = name
}

/**
 * Creates variable of 'boolean' type.
 */
fun String.booleanVariable(): Var<Boolean> = Var(this)

/**
 * Creates variable of 'string' divkit type.
 */
fun String.stringVariable(): Var<String> = Var(this)

/**
 * Creates variable of 'number' divkit type.
 */
fun String.numberVariable(): Var<Double> = Var(this)

/**
 * Creates variable of 'integer' divkit type.
 */
fun String.integerVariable(): Var<Long> = Var(this)

/**
 * Creates variable of 'url' divkit type.
 */
fun String.urlVariable(): Var<Url> = Var(this)

/**
 * Creates variable of 'dict' divkit type.
 */
fun <VALUE_TYPE> String.dictVariable(): Var<Map<String, VALUE_TYPE>> = Var(this)

/**
 * Creates variable of 'datetime' divkit type.
 */
fun String.datetimeVariable(): Var<Long> = Var(this)

/**
 * Creates variable of 'array' divkit type.
 */
fun <VALUE_TYPE> String.arrayVariable(): Var<List<VALUE_TYPE>> = Var(this)

/**
 * Creates variable of 'color' divkit type.
 */
fun String.colorVariable(): Var<String> = Var(this)

/**
 * Corresponds to 'integer' divkit data type.
 */
internal data class Integer(val value: Number) : Value<Long> {
    override fun build(): String {
        val intValue = value.toInt()
        return if (intValue.shouldWrapInBrackets()) {
            intValue.toString().wrapInBrackets()
        } else {
            intValue.toString()
        }
    }

    private fun Int.shouldWrapInBrackets() = this < 0
}

fun Int.integer(): Expression<Long> = Integer(this)
fun Long.integer(): Expression<Long> = Integer(this)

/**
 * Corresponds to 'number' divkit data type.
 */
internal data class Num(val value: Number) : Value<Double> {
    override fun build(): String {
        val doubleValue = value.toDouble()
        return if (doubleValue.shouldWrapInBrackets()) {
            doubleValue.toString().wrapInBrackets()
        } else {
            doubleValue.toString()
        }.let { serialized ->
            // Ensure dot is presented to avoid type casting in DivKit engine.
            if (serialized.contains(char = '.')) {
                serialized
            } else {
                "$serialized.0"
            }
        }
    }

    private fun Double.shouldWrapInBrackets() = this < 0.0
}

fun Int.number(): Expression<Double> = Num(this)
fun Long.number(): Expression<Double> = Num(this)
fun Double.number(): Expression<Double> = Num(this)
fun Float.number(): Expression<Double> = Num(this)

/**
 * Corresponds to 'string' divkit data type.
 */
internal data class Str(val value: String) : Value<String> {
    override fun build() = "'${value.shieldedString()}'"
}

private fun String.shieldedString(): String = this.replace("'", "\\'")

fun String.string(): Expression<String> = Str(this)

/**
 * Corresponds to 'boolean' divkit data type
 */
internal data class Bool(val value: Boolean) : Value<Boolean> {
    override fun build(): String = if (value) "true" else "false"
}

fun Boolean.boolean(): Expression<Boolean> = Bool(this)

class FunctionExpression<OUTPUT_TYPE>(
    private val functionName: String,
    private val arguments: List<Expression<*>>,
) : Expression<OUTPUT_TYPE> {
    constructor(
        functionName: String,
        vararg arguments: Expression<*>,
    ) : this(functionName, arguments.asList())

    override fun build(): String {
        return StringBuilder().apply {
            append(functionName)
            append('(')
            arguments.forEachIndexed { index, expression ->
                if (index != 0) {
                    append(", ")
                }
                append(expression.build())
            }
            append(')')
        }.toString()
    }
}

/**
 * Defines custom function expression.
 * @param functionName name of called function.
 * @param expressionArguments arguments to pass to function call.
 */
fun <OUTPUT_TYPE> function(
    functionName: String,
    vararg expressionArguments: Expression<*>,
): Expression<OUTPUT_TYPE> {
    return FunctionExpression(functionName, *expressionArguments)
}

/**
 * Literal typed expression (e.g., 2 + 3).
 */
internal data class LiteralExpression<OUTPUT_TYPE>(val expression: String) : Expression<OUTPUT_TYPE> {
    override fun build() = expression
}

/**
 * Converts given string to [Expression] with given result type [OUTPUT_TYPE]. Note that given string
 * must be a valid divkit expression.
 */
fun <OUTPUT_TYPE> String.literalExpression(): Expression<OUTPUT_TYPE> = LiteralExpression(this)

/**
 * Expression that takes single expression and applies prefix operation to it (e.g., unary minus).
 */
internal data class PrefixExpression<INPUT_TYPE, OUTPUT_TYPE>(
    val expression: Expression<INPUT_TYPE>,
    val operation: PrefixOperation,
) : Expression<OUTPUT_TYPE> {
    override fun build(): String {
        return StringBuilder()
            .append(operation.code)
            .addExpression(expression)
            .toString()
    }

    enum class PrefixOperation(val code: String) {
        NOT("!"),
        UNARY_MINUS("-"),
    }
}

/**
 * Expression that takes two expressions and applies infix operation to it (e.g., minus operation).
 */
internal sealed class InfixExpression<LHS_INPUT_TYPE, RHS_INPUT_TYPE, OUTPUT_TYPE>(
    private val lhsExpression: Expression<out LHS_INPUT_TYPE>,
    private val rhsExpression: Expression<out RHS_INPUT_TYPE>,
    private val operation: String,
) : Expression<OUTPUT_TYPE> {
    override fun build(): String {
        return StringBuilder()
            .addExpression(lhsExpression)
            .append(" $operation ")
            .addExpression(rhsExpression)
            .toString()
    }
}

internal class CompareExpression<INPUT_TYPE>(
    lhsExpression: Expression<out INPUT_TYPE>,
    rhsExpression: Expression<out INPUT_TYPE>,
    operation: CompareOperation,
) : InfixExpression<INPUT_TYPE, INPUT_TYPE, Boolean>(
    lhsExpression, rhsExpression, operation.code,
) {
    enum class CompareOperation(
        val code: String,
    ) {
        EQUAL("=="),
        NOT_EQUAL("!="),
        MORE(">"),
        MORE_OR_EQUAL(">="),
        LESS("<"),
        LESS_OR_EQUAL("<="),
    }
}

internal class ArithmeticExpression<T : Number>(
    lhsExpression: Expression<out Number>,
    rhsExpression: Expression<out Number>,
    operation: ArithmeticOperation,
) : InfixExpression<Number, Number, T>(
    lhsExpression, rhsExpression, operation.code,
) {
    enum class ArithmeticOperation(
        val code: String,
    ) {
        PLUS("+"),
        MINUS("-"),
        TIMES("*"),
        DIV("/"),
        REM("%"),
    }
}

internal class ConcatenationExpression(
    lhsExpression: Expression<String>,
    rhsExpression: Expression<String>,
) : InfixExpression<String, String, String>(
    lhsExpression, rhsExpression, "+"
)

internal class BooleanExpression(
    lhsExpression: Expression<Boolean>,
    rhsExpression: Expression<Boolean>,
    operation: BooleanOperation,
) : InfixExpression<Boolean, Boolean, Boolean>(
    lhsExpression, rhsExpression, operation.code,
) {
    enum class BooleanOperation(
        val code: String,
    ) {
        AND("&&"),
        OR("||"),
    }
}

/**
 * Expression that takes condition expression that resolves to 'boolean' type and two other expressions
 * and applies ternary operation logic:
 * ```
 *   if (condition) {
 *     onMatch
 *   } else {
 *     onMismatch
 *   }
 * ```
 */
internal data class TernaryExpression<INPUT_TYPE>(
    val condition: Expression<Boolean>,
    val onMatch: Expression<INPUT_TYPE>,
    val onMismatch: Expression<INPUT_TYPE>,
) : Expression<INPUT_TYPE> {
    override fun build(): String {
        return StringBuilder()
            .addExpression(condition)
            .append(" ? ")
            .addExpression(onMatch)
            .append(" : ")
            .addExpression(onMismatch)
            .toString()
    }
}

/**
 * Compiles expression into DivKit expression string.
 *
 * Do not use inside of expressions, creating nested expressions. It'll break divkit expressions apostrophe escaping.
 */
fun Expression<*>.compile() = "@{${this.build()}}"

/**
 * Assembles ternary expression with given expression as condition and [onMatch] and [onMismatch]
 * as match and mismatch results of condition evaluation. Note that [onMatch] and [onMismatch] should resolve
 * into the same divkit type.
 * @param onMatch result if condition evaluates to true statement.
 * @param onMismatch result if condition evaluates to false statement.
 */
fun <INPUT_TYPE> Expression<Boolean>.ifElse(
    onMatch: Expression<INPUT_TYPE>,
    onMismatch: Expression<INPUT_TYPE>,
): Expression<INPUT_TYPE> = TernaryExpression(this, onMatch, onMismatch)

private fun Expression<*>.shouldWrapInBrackets(): Boolean {
    return when (this) {
        is InfixExpression<*, *, *>,
        is LiteralExpression,
        is TernaryExpression,
        -> true

        is FunctionExpression<*>,
        is PrefixExpression<*, *>,
        is Value,
        -> false
    }
}

private fun StringBuilder.addExpression(expression: Expression<*>): StringBuilder {
    val wrap = expression.shouldWrapInBrackets()
    if (wrap) {
        append(expression.build().wrapInBrackets())
    } else {
        append(expression.build())
    }
    return this
}

private fun String.wrapInBrackets(): String = buildString {
    append('(')
    append(this@wrapInBrackets)
    append(')')
}

internal class TryExpression<INPUT_TYPE>(
    lhsExpression: Expression<out INPUT_TYPE>,
    rhsExpression: Expression<out INPUT_TYPE>,
) : InfixExpression<INPUT_TYPE, INPUT_TYPE, INPUT_TYPE>(
    lhsExpression, rhsExpression, "!:",
)
