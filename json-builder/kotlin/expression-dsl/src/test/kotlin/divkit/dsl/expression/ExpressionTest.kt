package divkit.dsl.expression

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class ExpressionTest {
    @ParameterizedTest
    @MethodSource("simpleExpressionsSource")
    fun simpleExpressions(
        expected: String,
        expression: Expression<*>,
    ) {
        assertEquals(expected, expression.compile())
    }

    @ParameterizedTest
    @MethodSource("infixExpressionsSource")
    fun infixExpressions(
        expected: String,
        expression: Expression<*>,
    ) {
        assertEquals(expected, expression.compile())
    }

    @ParameterizedTest
    @MethodSource("prefixExpressionsSource")
    fun prefixExpressions(
        expected: String,
        expression: Expression<*>,
    ) {
        assertEquals(expected, expression.compile())
    }

    @ParameterizedTest
    @MethodSource("wrapsInBracketsComplexExpressionsSource")
    fun wrapsInBracketsComplexExpressions(
        expected: String,
        expression: Expression<*>,
    ) {
        assertEquals(expected, expression.compile())
    }

    @ParameterizedTest
    @MethodSource("keepsPrecisionPointForIntegersSource")
    fun keepsPrecisionPointForIntegers(
        expected: String,
        expression: Expression<*>,
    ) {
        assertEquals(expected, expression.compile())
    }

    private companion object {
        @JvmStatic
        fun simpleExpressionsSource(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    "@{variable_name}",
                    "variable_name".booleanVariable(),
                ),
                Arguments.of(
                    "@{10}",
                    10.integer(),
                ),
                Arguments.of(
                    "@{(-10)}",
                    (-10).integer(),
                ),
                Arguments.of(
                    "@{10.1}",
                    10.1.number(),
                ),
                Arguments.of(
                    "@{(-10.1)}",
                    (-10.1).number(),
                ),
                Arguments.of(
                    "@{'string'}",
                    "string".string(),
                ),
                Arguments.of(
                    "@{true}",
                    true.boolean(),
                ),
                Arguments.of(
                    "@{false}",
                    false.boolean(),
                ),
            )
        }

        @JvmStatic
        fun infixExpressionsSource(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    "@{variable_name + 1}",
                    "variable_name".integerVariable() + 1,
                ),
                Arguments.of(
                    "@{variable_name - 1}",
                    "variable_name".integerVariable() - 1,
                ),
                Arguments.of(
                    "@{variable_name * 1}",
                    "variable_name".integerVariable() * 1,
                ),
                Arguments.of(
                    "@{variable_name / 1}",
                    "variable_name".integerVariable() / 1,
                ),
                Arguments.of(
                    "@{variable_name % 1}",
                    "variable_name".integerVariable() % 1,
                ),
                Arguments.of(
                    "@{true == false}",
                    true.boolean() equalTo false,
                ),
                Arguments.of(
                    "@{true != false}",
                    true.boolean() notEqualTo false,
                ),
                Arguments.of(
                    "@{1 > 2}",
                    1.integer() moreThan 2,
                ),
                Arguments.of(
                    "@{1 >= 2}",
                    1.integer() moreThanOrEqualTo 2,
                ),
                Arguments.of(
                    "@{1 < 2}",
                    1.integer() lessThan 2,
                ),
                Arguments.of(
                    "@{1 <= 2}",
                    1.integer() lessThanOrEqualTo 2,
                ),
                Arguments.of(
                    "@{true && false}",
                    true.boolean() and false,
                ),
                Arguments.of(
                    "@{true || false}",
                    true.boolean() or false,
                ),
            )
        }

        @JvmStatic
        fun prefixExpressionsSource(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    "@{-variable_name}",
                    -"variable_name".numberVariable(),
                ),
                Arguments.of(
                    "@{!variable_name}",
                    !"variable_name".booleanVariable(),
                ),
            )
        }

        @JvmStatic
        fun wrapsInBracketsComplexExpressionsSource(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    "@{1 + (2 + 3)}",
                    1.integer() + (2.integer() + 3),
                ),
                Arguments.of(
                    "@{1 + (2 + 3)}",
                    1.integer() + "2 + 3".literalExpression(),
                ),
                Arguments.of(
                    "@{(1 + 2) + (2 + 3)}",
                    (1.integer() + 2) + (2.integer() + 3),
                ),
                Arguments.of(
                    "@{1 + (condition ? 2 : 3)}",
                    1.integer() + "condition".booleanVariable().ifElse(2, 3),
                ),
                Arguments.of(
                    "@{!(condition ? true : false)}",
                    !"condition".booleanVariable().ifElse(onMatch = true, onMismatch = false),
                ),
                Arguments.of(
                    "@{-(condition ? 2 : 3)}",
                    -"condition".booleanVariable().ifElse(2, 3),
                ),
                Arguments.of(
                    "@{condition ? 'Levi\\'s' : 'Neuro\\'lu Web Arama'}",
                    "condition".booleanVariable().ifElse("Levi's", "Neuro'lu Web Arama"),
                ),
            )
        }

        @JvmStatic
        fun keepsPrecisionPointForIntegersSource(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    "@{1.0}",
                    1.number()
                ),
                Arguments.of(
                    "@{1.0}",
                    1L.number()
                ),
            )
        }
    }
}
