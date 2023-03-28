package com.yandex.div.evaluable

import com.yandex.div.evaluable.function.BuiltinFunctionProvider
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

private typealias VariableName = String
private typealias NumOfInvokes = Int

class EvaluableTest {

    private val variableProvider = mock<VariableProvider>()
    private val functionProvider = BuiltinFunctionProvider(variableProvider)
    private val evaluator = Evaluator(variableProvider, functionProvider)

    // Ternary Operator Test
    @Test
    fun `ternary # evaluate first expression without second`() {
        setVariable(VAR_A, 1)
        setVariable(VAR_B, 2.0)
        assertExpressionWithInvokeTimes("true ? $VAR_A : $VAR_B", 1, VAR_A to 1, VAR_B to 0)
    }

    @Test
    fun `ternary # evaluate second expression without first`() {
        setVariable(VAR_A, 1)
        setVariable(VAR_B, 2.0)
        assertExpressionWithInvokeTimes("false ? $VAR_A : $VAR_B", 2.0, VAR_A to 0, VAR_B to 1)
    }

    // Constant String Test
    @Test
    fun `constant # plain string without expression`() {
        assertStringTemplate("This is constant string", "This is constant string")
    }

    @Test
    fun `constant # plain string with escaped expression`() {
        assertStringTemplate("This is @{constant} string", "This is \\@{constant} string")
    }

    @Test
    fun `constant # plain string with single quoted part`() {
        assertStringTemplate("This is 'constant' string", "This is 'constant' string")
    }

    // StringTemplate Test
    @Test
    fun `string template # with empty expression`() {
        assertStringTemplate("This is constant string", "This is constant@{} string")
    }

    @Test
    fun `string template # with string expression`() {
        assertStringTemplate("This is constant string", "This is @{'constant'} string")
    }

    @Test
    fun `string template # with String variable`() {
        setVariable(TEST_VARIABLE_1, "constant")
        assertStringTemplate("This is constant string", "This is @{$TEST_VARIABLE_1} string")
    }

    @Test
    fun `string template # with Int expression`() {
        assertStringTemplate("This is 5", "This is @{5}")
    }

    @Test
    fun `string template # with Int variable`() {
        setVariable(TEST_VARIABLE_1, 5)
        assertStringTemplate("This is 5", "This is @{$TEST_VARIABLE_1}")
    }

    @Test
    fun `string template # with Decimal expression`() {
        assertStringTemplate("This is 15.0", "This is @{15.0}")
    }

    @Test
    fun `string template # with Decimal variable`() {
        setVariable(TEST_VARIABLE_1, 5.0)
        assertStringTemplate("This is 5.0", "This is @{$TEST_VARIABLE_1}")
    }

    @Test
    fun `string template # with Boolean variable`() {
        setVariable(TEST_VARIABLE_1, true)
        assertStringTemplate("This is true", "This is @{$TEST_VARIABLE_1}")
        setVariable(TEST_VARIABLE_1, false)
        assertStringTemplate("This is false", "This is @{$TEST_VARIABLE_1}")
    }

    @Test
    fun `string template # with several expressions`() {
        setVariable(TEST_VARIABLE_1, true)
        setVariable(TEST_VARIABLE_2, 1)
        assertStringTemplate("This is true and 1", "This is @{$TEST_VARIABLE_1} and @{$TEST_VARIABLE_2}")
        assertStringTemplate("This is start and this is end", "@{'This is start'} and @{'this is end'}")

    }

    @Test
    fun `string template # with expressions in different positions`() {
        assertStringTemplate("This string template has expression only on start", "@{'This string template'} has expression only on start")
        assertStringTemplate("This string template has expression in the middle", "This string template @{'has expression'} in the middle")
        assertStringTemplate("And this string template has expression only on end", "And this string template has expression @{'only on end'}")
    }

    // Literals Test
    @Test
    fun `literals # positive Int`() {
        assertIntExpression(5, "5")
        assertIntExpression(15, "15")
        assertIntExpression(155, "155")
    }

    @Test
    fun `literals # negative Int`() {
        assertIntExpression(-9, "-9")
        assertIntExpression(-95, "-95")
        assertIntExpression(-958, "-958")
    }

    @Test
    fun `literals # simple positive Decimal`() {
        assertDecimalExpression(5.0, "5.0000")
        assertDecimalExpression(15.0, "15.0")
        assertDecimalExpression(155.0, "155.0000")
    }

    @Test
    fun `literals # simple negative Decimal`() {
        assertDecimalExpression(-9.0, "-9.000")
        assertDecimalExpression(-95.0, "-95.0")
        assertDecimalExpression(-958.0, "-958.0000000")
    }

    @Test
    fun `literals # positive Decimal without leading zero`() {
        assertDecimalExpression(.5, ".5")
        assertDecimalExpression(.12, ".120")
        assertDecimalExpression(.1239, ".1239")
    }

    @Test
    fun `literals # negative Decimal without leading zero`() {
        assertDecimalExpression(-.9, "-.9")
        assertDecimalExpression(-.95, "-.950")
        assertDecimalExpression(-.958, "-.958000")
    }

    @Test
    fun `literals # positive scientific Decimal`() {
        assertDecimalExpression(12e3, "12e3")
        assertDecimalExpression(0.123e+4, "0.123e+4")
        assertDecimalExpression(0.123E+4, "0.123E+4")
        assertDecimalExpression(0.123e-4, "0.123e-4")
        assertDecimalExpression(0.123E-4, "0.123E-4")
    }

    @Test
    fun `literals # negative scientific Decimal`() {
        assertDecimalExpression(-12e3, "-12e3")
        assertDecimalExpression(-0.123e+4, "-0.123e+4")
        assertDecimalExpression(-0.123E+4, "-0.123E+4")
        assertDecimalExpression(-0.123e-4, "-0.123e-4")
        assertDecimalExpression(-0.123E-4, "-0.123E-4")
    }

    @Test
    fun `literals # positive scientific Decimal without leading zero`() {
        assertDecimalExpression(.123e+4, ".123e+4")
        assertDecimalExpression(.123E+4, ".123E+4")
        assertDecimalExpression(.123e-4, ".123e-4")
        assertDecimalExpression(.123E-4, ".123E-4")
    }

    @Test
    fun `literals # negative scientific Decimal without leading zero`() {
        assertDecimalExpression(-.123e+4, "-.123e+4")
        assertDecimalExpression(-.123E+4, "-.123E+4")
        assertDecimalExpression(-.123e-4, "-.123e-4")
        assertDecimalExpression(-.123E-4, "-.123E-4")
    }

    @Test
    fun `literals # String is String`() {
        assertStringExpression("a", "'a'")
        assertStringExpression("asdf87987_/asdsdf", "'asdf87987_/asdsdf'")
        assertStringExpression("as8h)(&(G(*YHiud", "'as8h)(&(G(*YHiud'")
    }

    @Test
    fun `literals # escaping quote in String`() {
        assertStringExpression("sdfh ao 89&(*'9sd ", "'sdfh ao 89&(*\\'9sd ' ")
    }

    @Test
    fun `literals # String escaping`() {
        assertStringExpression("'string'", "'\\'string\\''")
    }

    @Test
    fun `literals # booleans`() {
        assertBooleanExpression(true, "true")
        assertBooleanExpression(false, "false")
    }

    // Variables Test
    @Test
    fun `variables # Int variable is Int`() {
        setVariable(TEST_VARIABLE_1, 1L)
        assertIntExpression(1, TEST_VARIABLE_1)
    }

    @Test
    fun `variables # Decimal variable is Decimal`() {
        setVariable(TEST_VARIABLE_1, 1.0)
        assertDecimalExpression(1.0, TEST_VARIABLE_1)
    }

    @Test
    fun `variables # String variable is String`() {
        setVariable(TEST_VARIABLE_1, TEST_VARIABLE_2)
        assertStringExpression(TEST_VARIABLE_2, TEST_VARIABLE_1)
    }

    @Test
    fun `variables # String variable is Boolean`() {
        setVariable(TEST_VARIABLE_1, true)
        assertBooleanExpression(true, TEST_VARIABLE_1)
    }


    // Int equality tests
    @Test
    fun `equality # Int literal equals the same Int literal`() {
        assertEqualityExpression(true, "5", "5")
        assertEqualityExpression(true, "15", "15")
        assertEqualityExpression(true, "155", "155")
        assertEqualityExpression(true, "-958", "-958")
    }

    @Test
    fun `equality # different Int literals not equals`() {
        assertEqualityExpression(false, "5", "7")
        assertEqualityExpression(false, "15", "27")
        assertEqualityExpression(false, "155", "157")
        assertEqualityExpression(false, "-955", "958")
    }

    @Test
    fun `equality # Int literal equals the same Int variable`() {
        setVariable(TEST_VARIABLE_1, 5L)
        assertEqualityExpression(true, "5", TEST_VARIABLE_1)

        setVariable(TEST_VARIABLE_1, 15L)
        assertEqualityExpression(true, "15", TEST_VARIABLE_1)

        setVariable(TEST_VARIABLE_1, 155L)
        assertEqualityExpression(true, "155", TEST_VARIABLE_1)

        setVariable(TEST_VARIABLE_1, -958L)
        assertEqualityExpression(true, "-958", TEST_VARIABLE_1)
    }

    @Test
    fun `equality # different Int literal and variable not equals`() {
        setVariable(TEST_VARIABLE_1, 7L)
        assertEqualityExpression(false, "5", TEST_VARIABLE_1)

        setVariable(TEST_VARIABLE_1, 25L)
        assertEqualityExpression(false, "15", TEST_VARIABLE_1)

        setVariable(TEST_VARIABLE_1, 15L)
        assertEqualityExpression(false, "155", TEST_VARIABLE_1)

        setVariable(TEST_VARIABLE_1, 958L)
        assertEqualityExpression(false, "-958", TEST_VARIABLE_1)
    }


    // Decimal equality tests
    @Test
    fun `equality # Decimal literal equals the same Decimal literal`() {
        assertEqualityExpression(true, "5.0000", "5.0000")
        assertEqualityExpression(true, "15.0", "15.0")
        assertEqualityExpression(true, "155.000", "155.000")
        assertEqualityExpression(true, "-958.0000000", "-958.0000000")
    }

    @Test
    fun `equality # different Decimal literals not equals`() {
        assertEqualityExpression(false, "5.0000", "7.0000")
        assertEqualityExpression(false, "15.0", "-15.0")
        assertEqualityExpression(false, "155.0000", "5.0000")
        assertEqualityExpression(false, "-958.0000000", "-8.0000000")
    }

    @Test
    fun `equality # Decimal literal equals the same Decimal variable`() {
        setVariable(TEST_VARIABLE_1, 5.0)
        assertEqualityExpression(true, "5.0000", TEST_VARIABLE_1)

        setVariable(TEST_VARIABLE_1, 15.0)
        assertEqualityExpression(true, "15.0", TEST_VARIABLE_1)

        setVariable(TEST_VARIABLE_1, 155.0)
        assertEqualityExpression(true, "155.0000", TEST_VARIABLE_1)

        setVariable(TEST_VARIABLE_1, -958.0)
        assertEqualityExpression(true, "-958.0000000", TEST_VARIABLE_1)
    }

    @Test
    fun `equality # different Decimal literal and variable not equals`() {
        setVariable(TEST_VARIABLE_1, 7.0)
        assertEqualityExpression(false, "5.0000", TEST_VARIABLE_1)

        setVariable(TEST_VARIABLE_1, -15.0)
        assertEqualityExpression(false, "15.0", TEST_VARIABLE_1)

        setVariable(TEST_VARIABLE_1, 5.0)
        assertEqualityExpression(false, "155.0000", TEST_VARIABLE_1)

        setVariable(TEST_VARIABLE_1, -8.0)
        assertEqualityExpression(false, "-958.0000000", TEST_VARIABLE_1)
    }

    // String equality tests
    @Test
    fun `equality # String literal equals the same String literal`() {
        assertEqualityExpression(true, "'a'", "'a'")
        assertEqualityExpression(true, "'asdf87987_/asdsdf'", "'asdf87987_/asdsdf'")
        assertEqualityExpression(true, "'as8h)(&(G(*YHiud'", "'as8h)(&(G(*YHiud'")
        assertEqualityExpression(true, "'sdfh ao 89&(*&9sd '", "'sdfh ao 89&(*&9sd '")
    }

    @Test
    fun `equality # different String literals not equals`() {
        assertEqualityExpression(false, "'a'", "'sa'")
        assertEqualityExpression(false, "'asdf8798dsdf'", "'asdf87987_/asdsdf'")
        assertEqualityExpression(false, "'as8h)(&(G(*YHiud'", "'as8hG(*YHiud'")
        assertEqualityExpression(false, "'sdfh ao89&(*&9sd '", "'sdfh ao 8*&9sd '")
    }

    @Test
    fun `equality # String literal equals the same String variable`() {
        setVariable(TEST_VARIABLE_1, "a")
        assertEqualityExpression(true, "'a'", TEST_VARIABLE_1)

        setVariable(TEST_VARIABLE_1, "asdf87987_/asdsdf")
        assertEqualityExpression(true, "'asdf87987_/asdsdf'", TEST_VARIABLE_1)

        setVariable(TEST_VARIABLE_1, "as8h)(&(G(*YHiud")
        assertEqualityExpression(true, "'as8h)(&(G(*YHiud'", TEST_VARIABLE_1)

        setVariable(TEST_VARIABLE_1, "sdfh ao 89&(*&9sd ")
        assertEqualityExpression(true, "'sdfh ao 89&(*&9sd '", TEST_VARIABLE_1)
    }

    @Test
    fun `equality # different String literal and variable not equals`() {
        setVariable(TEST_VARIABLE_1, "sa")
        assertEqualityExpression(false, "'a'", TEST_VARIABLE_1)

        setVariable(TEST_VARIABLE_1, "asdf8798dsdf")
        assertEqualityExpression(false, "'asdf87987_/asdsdf'", TEST_VARIABLE_1)

        setVariable(TEST_VARIABLE_1, "as8hG(*YHiud")
        assertEqualityExpression(false, "'as8h)(&(G(*YHiud'", TEST_VARIABLE_1)

        setVariable(TEST_VARIABLE_1, "sdfh ao89&(*&9sd")
        assertEqualityExpression(false, "'sdfh ao 89&(*&9sd '", TEST_VARIABLE_1)
    }

    // Int comparison tests
    @Test
    fun `comparison # Int literal comparison test`() {
        assertComparisonExpression(false, false, "1", "0")
        assertComparisonExpression(true, false, "1", "10")
        assertComparisonExpression(false, true, "10", "10")
    }

    @Test
    fun `comparison # Int literal and variable comparison test`() {
        setVariable(TEST_VARIABLE_1, 1L)
        assertComparisonExpression(false, false, TEST_VARIABLE_1, "0")
        assertComparisonExpression(false, true, "1", TEST_VARIABLE_1)
        assertComparisonExpression(true, false, TEST_VARIABLE_1, "10")
    }

    // Decimal comparison tests
    @Test
    fun `comparison # Decimal literal comparison test`() {
        assertComparisonExpression(false, false, "1.0", "-5.00")
        assertComparisonExpression(false, true, "10.000", "10.0")
        assertComparisonExpression(true, false, "-10.000", "0.0")
    }

    @Test
    fun `comparison # Decimal literal and variable comparison test`() {
        setVariable(TEST_VARIABLE_1, 1.0)
        assertComparisonExpression(false, false, TEST_VARIABLE_1, "0.0")
        assertComparisonExpression(false, true, TEST_VARIABLE_1, "1.0")
        assertComparisonExpression(true, false, "-157.0", TEST_VARIABLE_1)
    }

    // Logical
    @Test
    fun `logical # true variable is true`() {
        setVariable(TEST_VARIABLE_1, true)
        assertBooleanExpression(true, TEST_VARIABLE_1)
    }

    @Test
    fun `logical # false variable is false`() {
        setVariable(TEST_VARIABLE_1, false)
        assertBooleanExpression(false, TEST_VARIABLE_1)
    }

    @Test
    fun `logical # true variable AND true variable is true`() {
        setVariable(TEST_VARIABLE_1, true)
        setVariable(TEST_VARIABLE_2, true)
        assertBooleanExpression(true, "$TEST_VARIABLE_1 && $TEST_VARIABLE_2")
    }

    @Test
    fun `logical # true variable AND false variable is false`() {
        setVariable(TEST_VARIABLE_1, true)
        setVariable(TEST_VARIABLE_2, false)
        assertBooleanExpression(false, "$TEST_VARIABLE_1 && $TEST_VARIABLE_2")
    }

    @Test
    fun `logical # false variable AND true variable is false`() {
        setVariable(TEST_VARIABLE_1, false)
        setVariable(TEST_VARIABLE_2, true)
        assertBooleanExpression(false, "$TEST_VARIABLE_1 && $TEST_VARIABLE_2")
    }

    @Test
    fun `logical # false variable AND false variable is false`() {
        setVariable(TEST_VARIABLE_1, false)
        setVariable(TEST_VARIABLE_2, false)
        assertBooleanExpression(false, "$TEST_VARIABLE_1 && $TEST_VARIABLE_2")
    }

    @Test
    fun `logical # true variable OR true variable is true`() {
        setVariable(TEST_VARIABLE_1, true)
        setVariable(TEST_VARIABLE_2, true)
        assertBooleanExpression(true, "$TEST_VARIABLE_1 || $TEST_VARIABLE_2")
    }

    @Test
    fun `logical # true variable OR false variable is true`() {
        setVariable(TEST_VARIABLE_1, true)
        setVariable(TEST_VARIABLE_2, false)
        assertBooleanExpression(true, "$TEST_VARIABLE_1 || $TEST_VARIABLE_2")
    }

    @Test
    fun `logical # false variable OR true variable is true`() {
        setVariable(TEST_VARIABLE_1, false)
        setVariable(TEST_VARIABLE_2, true)
        assertBooleanExpression(true, "$TEST_VARIABLE_1 || $TEST_VARIABLE_2")
    }

    @Test
    fun `logical # false variable OR false variable is false`() {
        setVariable(TEST_VARIABLE_1, false)
        setVariable(TEST_VARIABLE_2, false)
        assertBooleanExpression(false, "$TEST_VARIABLE_1 || $TEST_VARIABLE_2")
    }

    @Test
    fun `logical # logical operations with int`() {
        setVariable(TEST_VARIABLE_1, true)
        assertLogicalExpression(true, TEST_VARIABLE_1, false, "false")
        assertLogicalExpression(true, TEST_VARIABLE_1, true, "true")

        setVariable(TEST_VARIABLE_1, false)
        assertLogicalExpression(false, TEST_VARIABLE_1, false, "false")
        assertLogicalExpression(false, TEST_VARIABLE_1, true, "true")
    }

    @Test
    fun `non pure function is not cacheable`() {
        assertEvaluableIsOrNotCacheable("nowLocal()", false)
    }

    @Test
    fun `pure function with non pure function as argument is not cacheable`() {
        assertEvaluableIsOrNotCacheable("getYear(nowLocal())", false)
    }

    @Test
    fun `unary expression is not cacheable`() {
        assertEvaluableIsOrNotCacheable("-getYear(nowLocal())", false)
    }

    @Test
    fun `binary expression is not cacheable`() {
        assertEvaluableIsOrNotCacheable("getYear(nowLocal())+42", false)
        assertEvaluableIsOrNotCacheable("42+getYear(nowLocal())", false)
    }

    @Test
    fun `ternary expression is not cacheable`() {
        assertEvaluableIsOrNotCacheable("true ? getYear(nowLocal()) : 42", false)
        assertEvaluableIsOrNotCacheable("false ? 42 : getYear(nowLocal())", false)
        assertEvaluableIsOrNotCacheable("getYear(nowLocal()) == 0 ? 10 : 42", false)
    }

    @Test
    fun `string template is not cacheable`() {
        assertStringTemplateIsOrNotCacheable("This is non cacheable @{nowLocal()}", false)
    }

    @Test
    fun `ternary expression is cacheable`() {
        assertEvaluableIsOrNotCacheable("true ? 42 : nowLocal()", true)
        assertEvaluableIsOrNotCacheable("false ? nowLocal() : 42", true)
        assertEvaluableIsOrNotCacheable("true ? 42 : 12", true)
    }

    @Test
    fun `binary expression is cacheable`() {
        assertEvaluableIsOrNotCacheable("true || nowLocal()", true)
    }

    @Test
    fun `unary expression is cacheable`() {
        assertEvaluableIsOrNotCacheable("-sum(2,2)", true)
    }

    @Test
    fun `string template is cacheable`() {
        assertStringTemplateIsOrNotCacheable("This is cacheable @{sum(2,2)}", true)
    }

    @Test
    fun `pure function is cacheable`() {
        assertEvaluableIsOrNotCacheable("mul(4,5)", true)
    }

    @Test
    fun `pure function with pure function as argument is cacheable`() {
        assertEvaluableIsOrNotCacheable("mul(sum(2,2),42)", true)
    }

    @Test
    fun `variables do not affect caching`() {
        setVariable(VAR_A, 1L)
        setVariable(VAR_B, 2L)
        assertEvaluableIsOrNotCacheable("sum($VAR_A,$VAR_B)", true)
        assertEvaluableIsOrNotCacheable("$VAR_A + getYear(nowLocal())", false)
    }

    @Test
    fun `right side of OR not evaluated`() {
        setVariable(TEST_VARIABLE_1, true)
        setVariable(TEST_VARIABLE_2, false)
        assertExpressionWithInvokeTimes("$TEST_VARIABLE_1 || $TEST_VARIABLE_2", true,
            TEST_VARIABLE_1 to 1, TEST_VARIABLE_2 to 0)
    }

    @Test
    fun `right side of AND not evaluated`() {
        setVariable(TEST_VARIABLE_1, false)
        setVariable(TEST_VARIABLE_2, true)
        assertExpressionWithInvokeTimes("$TEST_VARIABLE_1 && $TEST_VARIABLE_2", false,
            TEST_VARIABLE_1 to 1, TEST_VARIABLE_2 to 0)
    }

    // Stress tests
    @Test(expected = EvaluableException::class)
    fun `errors # invalid Int literal`() {
        assertIntExpression(Long.MAX_VALUE, "999999999999999999999")
    }

    @Test(expected = EvaluableException::class)
    fun `errors # invalid underscore in Int literal`() {
        assertIntExpression(99_999, "99_999")
        assertIntExpression(-99_999, "-99_999")
    }

    @Test(expected = EvaluableException::class)
    fun `errors # invalid negative Int literal`() {
        assertIntExpression(Long.MIN_VALUE, "-999999999999999999999")
    }

    @Test(expected = EvaluableException::class)
    fun `errors # invalid Decimal literal`() {
        assertDecimalExpression(123.0e5, "123.e5")
    }

    @Test(expected = EvaluableException::class)
    fun `errors # invalid underscore in Decimal literal`() {
        assertDecimalExpression(99_999.0, "99_999.0")
        assertDecimalExpression(99_999.000_000, "99_999.000_000")
        assertDecimalExpression(-99_999.0, "-99_999.0")
        assertDecimalExpression(-99_999.000_000, "-99_999.000_000")
    }

    @Test(expected = EvaluableException::class)
    fun `errors # invalid couple unary plus usage`() {
        assertIntExpression(1, "++1")
        assertIntExpression(1, "+ +1")
    }

    @Test(expected = EvaluableException::class)
    fun `errors # invalid couple unary minus usage`() {
        assertIntExpression(1, "--1")
        assertIntExpression(1, "- -1")
    }

    @Test(expected = EvaluableException::class)
    fun `errors # invalid couple unary not usage`() {
        setVariable(TEST_VARIABLE_1, true)
        assertBooleanExpression(true, "!!$TEST_VARIABLE_1")
        assertBooleanExpression(true, "! !$TEST_VARIABLE_1")
    }

    @Test(expected = EvaluableException::class)
    fun `errors # logical operation with not valid int`() {
        setVariable(TEST_VARIABLE_1, true)
        assertBooleanExpression(true, "$TEST_VARIABLE_1 && 2")
    }

    @Test(expected = EvaluableException::class)
    fun `errors # invalid Str literal without '`() {
        assertStringExpression("This is not a string", "This is not a string")
    }

    @Test(expected = EvaluableException::class)
    fun `errors # invalid Str literal without one of '`() {
        assertStringExpression("This is not a string", "'This is not a string")
        assertStringExpression("This is not a string", "This is not a string'")
    }

    private fun assertLogicalExpression(leftExpected: Boolean, left: String, rightExpected: Boolean, right: String) {
        assertBooleanExpression(leftExpected && rightExpected, left, "&&", right)
        assertBooleanExpression(leftExpected && rightExpected, right, "&&", left)
        assertBooleanExpression(leftExpected || rightExpected, left, "||", right)
        assertBooleanExpression(leftExpected || rightExpected, right, "||", left)
    }

    private fun assertEqualityExpression(expected: Boolean, left: String, right: String) {
        assertBooleanExpression(expected, left, "==", right)
        assertBooleanExpression(expected, right, "==", left)
        assertBooleanExpression(!expected, left, "!=", right)
        assertBooleanExpression(!expected, right, "!=", left)
    }

    private fun assertComparisonExpression(leftLess: Boolean, equal: Boolean, left: String, right: String) {
        assertBooleanExpression(leftLess && !equal, left, "<", right)
        assertBooleanExpression(leftLess || equal, left, "<=", right)
        assertBooleanExpression(!leftLess && !equal, left, ">", right)
        assertBooleanExpression(!leftLess || equal, left, ">=", right)
    }

    private fun assertBooleanExpression(expected: Boolean, left: String, operator:String, right: String) {
        assertBooleanExpression(expected, "$left $operator $right")
    }

    private fun assertBooleanExpression(expected: Boolean, expr: String) {
        assertBooleanTemplate(expected, "@{$expr}")
    }

    private fun assertBooleanTemplate(expected: Boolean, template: String) {
        val actual: Any = evaluator.eval(Evaluable.prepare(template))
        assert(actual is Boolean)
        assertEquals(expected, actual as Boolean)
    }

    private fun assertIntExpression(expected: Long, expr: String) {
        assertIntTemplate(expected, "@{$expr}")
    }

    private fun assertIntTemplate(expected: Long, template: String) {
        val actual: Any = evaluator.eval(Evaluable.prepare(template))
        assert(actual is Long)
        assertEquals(expected, actual as Long)
    }

    private fun assertDecimalExpression(expected: Double, expr: String) {
        assertDecimalTemplate(expected, "@{$expr}")
    }

    private fun assertDecimalTemplate(expected: Double, template: String) {
        val actual: Any = evaluator.eval(Evaluable.prepare(template))
        assert(actual is Double)
        assertEquals(expected, actual as Double)
    }

    private fun assertStringExpression(expected: String, expr: String) {
        assertStringTemplate(expected, "@{$expr}")
    }

    private fun assertStringTemplate(expected: String, template: String) {
        val actual: Any = evaluator.eval(Evaluable.prepare(template))
        assert(actual is String)
        assertEquals(expected, actual as String)
    }

    private inline fun <reified T> assertExpressionWithInvokeTimes(
        expr: String, expected: T,
        vararg variablesInvokes: Pair<VariableName, NumOfInvokes>
    ) {
        val actual = evaluator.eval<Any>(Evaluable.prepare("@{$expr}"))
        variablesInvokes.forEach { (varName, invokes) ->
            verify(variableProvider, times(invokes)).get(varName)
        }
        assert(actual is T)
        assertEquals(expected, actual as T)
    }

    private fun assertEvaluableIsOrNotCacheable(expr: String, shouldBeCacheable: Boolean) {
        val evaluable = Evaluable.prepare("@{$expr}")
        evaluator.eval<Any>(evaluable)
        assertEquals(evaluable.checkIsCacheable(), shouldBeCacheable)
    }

    private fun assertStringTemplateIsOrNotCacheable(template: String, shouldBeCacheable: Boolean) {
        val evaluable = Evaluable.prepare(template)
        evaluator.eval<Any>(evaluable)
        assertEquals(evaluable.checkIsCacheable(), shouldBeCacheable)
    }

    private fun setVariable(name: String, value: Any) {
        whenever(variableProvider.get(name)).thenReturn(value)
    }

    companion object {
        const val TEST_VARIABLE_1 = "test_1"
        const val TEST_VARIABLE_2 = "test_2"
        const val VAR_A = "var_a"
        const val VAR_B = "var_b"
    }
}
