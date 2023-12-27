package com.yandex.div.evaluable.internal

import com.yandex.div.evaluable.EvaluableException
import org.junit.Assert.assertEquals
import org.junit.Test

class TokenizerTest {

    @Test
    fun `string # simple string`() {
        assertTemplate("This is a string", s("This is a string"))
    }

    @Test
    fun `string # simple string with empty expression`() {
        assertTemplate("This is@{} a string",
                ts, s("This is"), `@{`, `}`, s(" a string"), te)
    }

    @Test
    fun `string # two expression without any string`() {
        assertTemplate("@{}@{}",
                ts, `@{`, `}`, `@{`, `}`, te)
    }

    @Test
    fun `string # simple string with single quoted part`() {
        assertTemplate("This is a 'string'",
                s("This is a 'string'"))
    }

    @Test
    fun `string # just expression without any string`() {
        assertTemplate("@{}")
    }

    @Test
    fun `value # empty value`() {
        assertExpression("")
    }

    @Test
    fun `operands # unsigned Int operands`() {
        assertUnsignedNumberLiteral("7", 7)
        assertUnsignedNumberLiteral("19", 19)
        assertUnsignedNumberLiteral("150", 150)
    }

    @Test
    fun `operands # unsigned Decimal operands`() {
        assertUnsignedNumberLiteral("7.0", 7.0)
        assertUnsignedNumberLiteral("19.000", 19.0)
        assertUnsignedNumberLiteral("150.00", 150.0)
    }

    @Test
    fun `operands # positive Int operands`() {
        assertPositiveNumberLiteral("+7", 7)
        assertPositiveNumberLiteral("+19", 19)
        assertPositiveNumberLiteral("+150", 150)
    }

    @Test
    fun `operands # positive Decimal operands`() {
        assertPositiveNumberLiteral("+7.0", 7.0)
        assertPositiveNumberLiteral("+19.000", 19.0)
        assertPositiveNumberLiteral("+150.00", 150.0)
    }

    @Test
    fun `operands # negative Int operands`() {
        assertNegativeNumberLiteral("-7", -7)
        assertNegativeNumberLiteral("-19", -19)
        assertNegativeNumberLiteral("-150", -150)
    }

    @Test
    fun `operands # negative Decimal operands`() {
        assertNegativeNumberLiteral("-7.0", -7.0)
        assertNegativeNumberLiteral("-19.000", -19.0)
        assertNegativeNumberLiteral("-150.00", -150.0)
    }

    @Test
    fun `operands # empty Str operands`() {
        assertStringLiteral("''", "")
    }

    @Test
    fun `operands # simple Str operands`() {
        assertStringLiteral("'This is a string'", "This is a string")
    }

    @Test
    fun `operands # simple Str operands with single quote`() {
        assertStringLiteral("'This\\'s a string'", "This's a string")
    }

    @Test
    fun `operands # low case variable name`() {
        assertVariableName("a", "a")
        assertVariableName("index", "index")
        assertVariableName("state", "state")
    }

    @Test
    fun `operands # variable name with digits`() {
        assertVariableName("a1", "a1")
        assertVariableName("index123", "index123")
        assertVariableName("st1t2", "st1t2")
    }

    @Test
    fun `operands # camel case variable name`() {
        assertVariableName("aA", "aA")
        assertVariableName("isAtEnd","isAtEnd")
        assertVariableName("startOfExpression","startOfExpression")
    }

    @Test
    fun `operands # variable name with underline`() {
        assertVariableName("_aA","_aA")
        assertVariableName("is_AtEnd","is_AtEnd")
        assertVariableName("__is_AtEnd","__is_AtEnd")
        assertVariableName("start__OfExpression","start__OfExpression")
    }

    @Test
    fun `operands # variable name with dot`() {
        assertVariableName("a.A","a.A")
        assertVariableName("isAt.End", "isAt.End")
        assertVariableName("startOf.Expression", "startOf.Expression")
    }

    @Test
    fun `operands # crazy variable name`() {
        assertVariableName("_a.__A", "_a.__A")
        assertVariableName("isAt.._2__..E3nd", "isAt.._2__..E3nd")
        assertVariableName("_..._S6tarOf.___.Expres2ion", "_..._S6tarOf.___.Expres2ion")
    }

    @Test
    fun `operators # ternary operator`() {
        assertExpression(
                "true ? 1 : 0",
                b(true),
                ternIf, n(1), ternElse, n(0)
        )
        assertExpression(
                "true ? false : 'str'",
                b(true),
                ternIf, b(false), ternElse, s("str")
        )
        assertExpression(
                "2 > 0 ? 1 : 0",
                n(2), greater, n(0),
                ternIf, n(1), ternElse, n(0)
        )
        assertExpression(
                "2 > (3 + 1) ? 1 : 'some string'",
                n(2), greater, `(`, n(3), plus, n(1), `)`,
                ternIf, n(1), ternElse, s("some string")
        )
    }

    @Test
    fun `operators # arithmetic operators`() {
        assertExpression("1 - 1", n(1), minus, n(1))
        assertExpression("1 + 1", n(1), plus, n(1))
        assertExpression("1 * 1", n(1), mul, n(1))
        assertExpression("1 / 1", n(1), div, n(1))
        assertExpression("1 % 1", n(1), mod, n(1))
    }

    @Test
    fun `operators # logical operators`() {
        assertExpression("&&", and)
        assertExpression("||", or)
    }

    @Test
    fun `operators # equality operators`() {
        assertExpression("==", equal)
        assertExpression("!=", notEqual)
    }

    @Test
    fun `operators # comparison operators`() {
        assertExpression("<", less)
        assertExpression("<=", lessOrEqual)
        assertExpression(">", greater)
        assertExpression(">=", greaterOrEqual)
    }

    @Test
    fun `brackets # simple brackets`() {
        assertExpression("(", `(`)
        assertExpression(")", `)`)
        assertExpression("()", `(`, `)`)
        assertExpression("(())", `(`, `(`, `)`, `)`)
        assertExpression("()()", `(`, `)`, `(`, `)`)
    }

    @Test
    fun `whitespaces # just ignore whitespaces`() {
        assertExpression(" ")
        assertExpression("\t")
        assertExpression("\r")
    }

    @Test
    fun `functions # low case function name`() {
        assertFunctionName("a()", "a")
        assertFunctionName("index()","index")
        assertFunctionName("state()", "state")
    }

    @Test
    fun `functions # function name with digits`() {
        assertFunctionName("a1()","a1")
        assertFunctionName("index123()","index123")
        assertFunctionName("st1t2()","st1t2")
    }

    @Test
    fun `functions # camel case function name`() {
        assertFunctionName("aA()","aA")
        assertFunctionName("isAtEnd()","isAtEnd")
        assertFunctionName("startOfExpression()","startOfExpression")
    }

    @Test
    fun `functions # function name with underline`() {
        assertFunctionName("_aA()","_aA")
        assertFunctionName("is_AtEnd()", "is_AtEnd")
        assertFunctionName("__is_AtEnd()","__is_AtEnd")
        assertFunctionName("start__OfExpression()","start__OfExpression")
    }

    @Test
    fun `functions # functions call with simple args`() {
        assertExpression("a(1)", f("a"), `(`, n(1), `)`)
        assertExpression("a(2.0)", f("a"), `(`, n(2.0), `)`)
        assertExpression("a(2.0, 1)", f("a"), `(`, n(2.0), `,`, n(1), `)`)
        assertExpression("a('str')", f("a"), `(`, s("str"), `)`)
        assertExpression("a(2.0, 'str', 1)", f("a"), `(`, n(2.0), `,`, s("str"), `,`, n(1), `)`)
        assertExpression("a(variable)", f("a"), `(`, v("variable"), `)`)
        assertExpression("a('str', 2.0, variable, 1)", f("a"), `(`,
                s("str"), `,`, n(2.0), `,`, v("variable"), `,`, n(1), `)`)
    }

    @Test
    fun `functions # hierarchy functions call`() {
        assertExpression("a(c())", f("a"), `(`, f("c"), `(`, `)`, `)`)
        assertExpression("a(b, c())", f("a"), `(`, v("b"), `,`, f("c"), `(`, `)`, `)`)
        assertExpression("a(b(), c)", f("a"), `(`, f("b"), `(`, `)`, `,`, v("c"), `)`)
        assertExpression("a(b, c(e, f()))", f("a"), `(`, v("b"), `,`,
                f("c"), `(`, v("e"), `,`, f("f"), `(`, `)`, `)`, `)`)
        assertExpression("a(b(e, f()), c)", f("a"), `(`, f("b"), `(`,
                v("e"), `,`, f("f"), `(`, `)`, `)`, `,`, v("c"), `)`)
    }

    @Test
    fun `equality # Int literal equality`() {
        assertSignedEquality("1", n(1), "1", n(1))
        assertSignedEquality("15", n(15), "192", n(192))
    }

    @Test
    fun `equality # Decimal literal equality`() {
        assertSignedEquality("1.0", n(1.0), "1.00", n(1.0))
        assertSignedEquality("15.000", n(15.0), "192.00", n(192.0))
    }

    @Test
    fun `equality # Int with Decimal literal equality`() {
        assertSignedEquality("1", n(1), "1.00", n(1.0))
        assertSignedEquality("15.000", n(15.0), "192", n(192))
    }

    @Test
    fun `equality # variable equality`() {
        assertSignedEquality("a", v("a"), "b", v("b"))
    }

    @Test
    fun `equality # function call equality`() {
        assertSignedEquality("a()", listOf(f("a"), `(`, `)`), "b()", listOf(f("b"), `(`, `)`))
    }

    @Test
    fun `equality # variable with Int equality`() {
        assertSignedEquality("1", n(1), "b", v("b"))
    }

    @Test
    fun `equality # function call with Int equality`() {
        assertSignedEquality("1", listOf(n(1)), "b()", listOf(f("b"), `(`, `)`))
    }

    @Test
    fun `equality # variable with Decimal equality`() {
        assertSignedEquality("1.0", n(1.0), "b", v("b"))
    }

    @Test
    fun `equality # function call with Decimal equality`() {
        assertSignedEquality("1.0", listOf(n(1.0)), "b()", listOf(f("b"), `(`, `)`))
    }

    @Test
    fun `equality # function call with variable equality`() {
        assertSignedEquality("a", listOf(v("a")), "b()", listOf(f("b"), `(`, `)`))
    }

    @Test
    fun `equality # Str literal equality`() {
        assertUnsignedEquality("'a'", s("a"), "'a'", s("a"))
        assertUnsignedEquality("'abc'", s("abc"), "'abc'", s("abc"))
        assertUnsignedEquality("'ab'", s("ab"), "'abc'", s("abc"))
    }

    @Test
    fun `comparison # Int literal`() {
        assertSignedComparison("1", n(1), "1", n(1))
        assertSignedComparison("15", n(15), "19", n(19))
        assertSignedComparison("154", n(154), "192", n(192))
    }

    @Test
    fun `comparison # Decimal literal`() {
        assertSignedComparison("1.0", n(1.0), "1.0", n(1.0))
        assertSignedComparison("15.000", n(15.0), "19.0000", n(19.0))
        assertSignedComparison("154.00", n(154.0), "192.0", n(192.0))
    }

    @Test
    fun `comparison # Str literal`() {
        assertUnsignedComparison("'a'", s("a"), "'a'", s("a"))
        assertUnsignedComparison("'abc'", s("abc"), "'abc'", s("abc"))
        assertUnsignedComparison("'ab'", s("ab"), "'abc'", s("abc"))
    }

    @Test
    fun `comparison # variables`() {
        assertSignedComparison("a", v("a"), "b", v("b"))
    }
    @Test
    fun `comparison # function call`() {
        assertSignedComparison("a()", listOf(f("a"), `(`, `)`), "b()", listOf(f("b"), `(`, `)`))
    }

    @Test
    fun `comparison # Int with Decimal literal`() {
        assertSignedComparison("1", n(1), "1.0", n(1.0))
        assertSignedComparison("15.000", n(15.0), "19", n(19))
        assertSignedComparison("154", n(154), "192.00", n(192.0))
    }

    @Test
    fun `comparison # Int with Str literal`() {
        assertSignedComparison("1", n(1), "'str'", s("str"))
        assertUnsignedComparison("-1", n(-1), "'str'", s("str"))
        assertUnsignedComparison("+1", listOf(uplus, n(1)), "'str'", listOf(s("str")))
    }

    @Test
    fun `comparison # Int with variable`() {
        assertSignedComparison("1", n(1), "a", v("a"))
        assertSignedComparison("b", v("b"), "19", n(19))
        assertSignedComparison("154", n(154), "c", v("c"))
    }

    @Test
    fun `comparison # Int with function call`() {
        assertSignedComparison("1", listOf(n(1)), "a()", listOf(f("a"), `(`, `)`))
    }

    @Test
    fun `comparison # Decimal with Str`() {
        assertUnsignedComparison("1.0", n(1.0), "'str'", s("str"))
        assertUnsignedComparison("-1.0", n(-1.0), "'str'", s("str"))
        assertUnsignedComparison("+1.0", listOf(uplus, n(1.0)), "'str'", listOf(s("str")))
    }

    @Test
    fun `comparison # Decimal with variable`() {
        assertSignedComparison("1.0", n(1.0), "a", v("a"))
        assertSignedComparison("b", v("b"), "19.000", n(19.0))
        assertSignedComparison("154.00", n(154.0), "c", v("c"))
    }

    @Test
    fun `comparison # Decimal with function call`() {
        assertSignedComparison("1.0", listOf(n(1.0)), "a()", listOf(f("a"), `(`, `)`))
    }

    @Test
    fun `comparison # variable with function call`() {
        assertSignedComparison("var", listOf(v("var")), "a()", listOf(f("a"), `(`, `)`))
    }

    @Test
    fun `logical # unary not`() {
        assertExpression("!var", not, v("var"))
    }

    @Test
    fun `logical # and with variables`() {
        assertOperation("var_1", listOf(v("var_1")), "&&", and, "var_2", listOf(v("var_2")))
        assertOperation("!var_1", listOf(not, v("var_1")), "&&", and, "var_2", listOf(v("var_2")))
        assertOperation("!var_1", listOf(not, v("var_1")), "&&", and, "!var_2", listOf(not, v("var_2")))
    }

    @Test
    fun `logical # or with variables`() {
        assertOperation("var_1", listOf(v("var_1")), "||", or, "var_2", listOf(v("var_2")))
        assertOperation("!var_1", listOf(not, v("var_1")), "||", or, "var_2", listOf(v("var_2")))
        assertOperation("!var_1", listOf(not, v("var_1")), "||", or, "!var_2", listOf(not, v("var_2")))
    }

    @Test(expected = EvaluableException::class)
    fun `unsupported # Power operator is unsupported`() {
        assertExpression("^")
    }

    @Test(expected = EvaluableException::class)
    fun `unsupported # Semicolon is unsupported`() {
        assertExpression(";")
    }

    @Test(expected = EvaluableException::class)
    fun `unsupported # and symbol is unsupported`() {
        assertExpression("&")
    }

    @Test(expected = EvaluableException::class)
    fun `unsupported # or symbol is unsupported`() {
        assertExpression("|")
    }

    @Test(expected = EvaluableException::class)
    fun `unsupported # sharp symbol is unsupported`() {
        assertExpression("#")
    }

    @Test(expected = EvaluableException::class)
    fun `unsupported # dollar symbol is unsupported`() {
        assertExpression("$")
    }

    @Test(expected = EvaluableException::class)
    fun `unsupported # slash symbol is unsupported`() {
        assertExpression("\\")
    }

    @Test(expected = EvaluableException::class)
    fun `unsupported # tilda symbol is unsupported`() {
        assertExpression("~")
    }

    @Test(expected = EvaluableException::class)
    fun `unsupported # back tick symbol is unsupported`() {
        assertExpression("`")
    }

    @Test(expected = EvaluableException::class)
    fun `unsupported # dot symbol is unsupported`() {
        assertExpression(".")
    }

    @Test(expected = EvaluableException::class)
    fun `unsupported # simple curly brackets`() {
        assertExpression("{")
        assertExpression("}")
        assertExpression("{}")
    }

    @Test(expected = EvaluableException::class)
    fun `unsupported # simple square brackets`() {
        assertExpression("[")
        assertExpression("]")
        assertExpression("[]")
    }

    @Test(expected = EvaluableException::class)
    fun `unsupported # function name with dot`() {
        assertExpression("_A.b()", f("_A.b()"), `(`, `)`)
    }

    @Test(expected = EvaluableException::class)
    fun `errors # String without closing quote`() {
        assertExpression("'string", s("string"))
    }

    private fun assertUnsignedNumberLiteral(expr: String, value: Number) = assertExpression(expr, n(value))
    private fun assertPositiveNumberLiteral(expr: String, value: Number) = assertExpression(expr, uplus, n(value))
    private fun assertNegativeNumberLiteral(expr: String, value: Int) = assertExpression(expr, n(value))
    private fun assertNegativeNumberLiteral(expr: String, value: Double) = assertExpression(expr, n(value))
    private fun assertStringLiteral(expr: String, expected: String) = assertExpression(expr, s(expected))
    private fun assertVariableName(expr: String, expected: String) = assertExpression(expr, v(expected))
    private fun assertFunctionName(expr: String, expected: String) = assertExpression(expr, f(expected), `(`, `)`)
    private fun assertSignedEquality(leftExpr: String, leftExpected: Token,
                                     rightExpr: String, rightExpected: Token) =
            assertSignedEquality(leftExpr, listOf(leftExpected), rightExpr, listOf(rightExpected))

    private fun assertSignedEquality(leftExpr: String, leftExpected: List<Token>,
                                     rightExpr: String, rightExpected: List<Token>) {
        assertSignedOperation(leftExpr, leftExpected, "==", equal, rightExpr, rightExpected)
        assertSignedOperation(leftExpr, leftExpected, "!=", notEqual, rightExpr, rightExpected)
    }

    private fun assertUnsignedEquality(leftExpr: String, leftExpected: Token,
                                       rightExpr: String, rightExpected: Token) =
            assertUnsignedEquality(leftExpr, listOf(leftExpected), rightExpr, listOf(rightExpected))

    private fun assertUnsignedEquality(leftExpr: String, leftExpected: List<Token>,
                                       rightExpr: String, rightExpected: List<Token>) {
        assertOperation(leftExpr, leftExpected, "==", equal, rightExpr, rightExpected)
        assertOperation(leftExpr, leftExpected, "!=", notEqual, rightExpr, rightExpected)
    }

    private fun assertSignedOperation(leftExpr: String, leftExpected: List<Token>,
                                      operationExpr: String, operationExpected: Token,
                                      rightExpr: String, rightExpected: List<Token>) {
        assertOperation(leftExpr, leftExpected, operationExpr, operationExpected, rightExpr, rightExpected)
        assertOperation(
                "+$leftExpr", listOf(uplus) + leftExpected,
                operationExpr, operationExpected,
                rightExpr, rightExpected
        )
        assertOperation(
                leftExpr, leftExpected,
                operationExpr, operationExpected,
                "+$rightExpr", listOf(uplus) + rightExpected
        )
        assertOperation(
                "+$leftExpr", listOf(uplus) + leftExpected,
                operationExpr, operationExpected,
                "+$rightExpr", listOf(uplus) + rightExpected
        )

        val leftExpectedWithUnaryMinus = addUnaryMinus(leftExpected)
        val rightExpectedWithUnaryMinus = addUnaryMinus(rightExpected)

        assertOperation(
                "-$leftExpr", leftExpectedWithUnaryMinus,
                operationExpr, operationExpected,
                rightExpr, rightExpected
        )
        assertOperation(
                leftExpr, leftExpected,
                operationExpr, operationExpected,
                "-$rightExpr", rightExpectedWithUnaryMinus
        )
        assertOperation(
                "-$leftExpr", leftExpectedWithUnaryMinus,
                operationExpr, operationExpected,
                "-$rightExpr", rightExpectedWithUnaryMinus
        )

        assertOperation(
                "-$leftExpr", leftExpectedWithUnaryMinus,
                operationExpr, operationExpected,
                "+$rightExpr", listOf(uplus) + rightExpected
        )
        assertOperation(
                "+$leftExpr", listOf(uplus) + leftExpected,
                operationExpr, operationExpected,
                "-$rightExpr", rightExpectedWithUnaryMinus
        )
    }

    private fun addUnaryMinus(tokens: List<Token>) : List<Token> {
        val first = tokens.firstOrNull()
                ?: throw EvaluableException("Non empty token list is expected for adding unary minus.")
        return if (first is Token.Operand.Literal.Num) {
            val negativeValue = when (first.value) {
                is Long -> first.value.unaryMinus()
                is Double -> first.value.unaryMinus()
                else -> throw EvaluableException("Unknown type of number ${first.value}.")
            }
            listOf(n(negativeValue)) + tokens.drop(1)
        } else {
            listOf(uminus) + tokens
        }
    }

    private fun assertOperation(leftExpr: String, leftExpected: List<Token>,
                                operationExpr: String, operationExpected: Token,
                                rightExpr: String, rightExpected: List<Token>) {
        assertExpression("$leftExpr $operationExpr $rightExpr", (leftExpected + operationExpected + rightExpected))
        assertExpression("$rightExpr $operationExpr $leftExpr", (rightExpected + operationExpected + leftExpected))
    }

    private fun assertSignedComparison(leftExpr: String, leftExpected: Token,
                                       rightExpr: String, rightExpected: Token) =
            assertSignedComparison(leftExpr, listOf(leftExpected), rightExpr, listOf(rightExpected))

    private fun assertSignedComparison(leftExpr: String, leftExpected: List<Token>,
                                       rightExpr: String, rightExpected: List<Token>) {
        assertSignedOperation(leftExpr, leftExpected, "<", less, rightExpr, rightExpected)
        assertSignedOperation(leftExpr, leftExpected, "<=", lessOrEqual, rightExpr, rightExpected)
        assertSignedOperation(leftExpr, leftExpected, ">", greater, rightExpr, rightExpected)
        assertSignedOperation(leftExpr, leftExpected, ">=", greaterOrEqual, rightExpr, rightExpected)
    }

    private fun assertUnsignedComparison(leftExpr: String, leftExpected: Token,
                                         rightExpr: String, rightExpected: Token) =
            assertUnsignedComparison(leftExpr, listOf(leftExpected), rightExpr, listOf(rightExpected))

    private fun assertUnsignedComparison(leftExpr: String, leftExpected: List<Token>,
                                         rightExpr: String, rightExpected: List<Token>) {
        assertOperation(leftExpr, leftExpected, "<", less, rightExpr, rightExpected)
        assertOperation(leftExpr, leftExpected, "<=", lessOrEqual, rightExpr, rightExpected)
        assertOperation(leftExpr, leftExpected, ">", greater, rightExpr, rightExpected)
        assertOperation(leftExpr, leftExpected, ">=", greaterOrEqual, rightExpr, rightExpected)
    }

    private fun assertExpression(expr: String, vararg expected: Token) {
        assertExpression(expr, expected.toList())
    }

    private fun assertExpression(expr: String, expected: List<Token>) {
        val actual = expr("@{$expr}")
        assertEquals(expected.toList(), actual)
    }

    private fun assertTemplate(tmpl: String, vararg expected: Token) {
        val actual = expr(tmpl)
        assertEquals(expected.toList(), actual)
    }

    private fun expr(expr: String): List<Token> {
        return Tokenizer.tokenize(expr)
    }

    companion object {
        private val ternIf = Token.Operator.TernaryIf
        private val ternElse = Token.Operator.TernaryElse

        private val plus = Token.Operator.Binary.Sum.Plus
        private val minus = Token.Operator.Binary.Sum.Minus
        private val mul = Token.Operator.Binary.Factor.Multiplication
        private val mod = Token.Operator.Binary.Factor.Modulo
        private val div = Token.Operator.Binary.Factor.Division

        private val uplus = Token.Operator.Unary.Plus
        private val uminus = Token.Operator.Unary.Minus
        private val not = Token.Operator.Unary.Not

        private val and = Token.Operator.Binary.Logical.And
        private val or = Token.Operator.Binary.Logical.Or

        private val equal = Token.Operator.Binary.Equality.Equal
        private val notEqual = Token.Operator.Binary.Equality.NotEqual

        private val less = Token.Operator.Binary.Comparison.Less
        private val lessOrEqual = Token.Operator.Binary.Comparison.LessOrEqual
        private val greater = Token.Operator.Binary.Comparison.Greater
        private val greaterOrEqual = Token.Operator.Binary.Comparison.GreaterOrEqual

        private val `,` = Token.Function.ArgumentDelimiter
        private val `(` = Token.Bracket.LeftRound
        private val `)` = Token.Bracket.RightRound
        private val `@{` = Token.StringTemplate.StartOfExpression
        private val `}` = Token.StringTemplate.EndOfExpression

        private val ts = Token.StringTemplate.Start
        private val te = Token.StringTemplate.End

        private fun n(value: Number): Token.Operand.Literal {
            return if (value is Int) {
                Token.Operand.Literal.Num(value.toLong())
            } else {
                Token.Operand.Literal.Num(value)
            }
        }
        private fun s(value: String) = Token.Operand.Literal.Str(value)
        private fun b(value: Boolean) = Token.Operand.Literal.Bool(value)
        private fun v(value: String) = Token.Operand.Variable(value)
        private fun f(value: String) = Token.Function(value)
    }
}