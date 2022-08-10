package com.yandex.div.core.expression.triggers

import com.yandex.div.json.ParsingException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ConditionPartTest(
    private val input: String,
    private val errorText: String?,
    private val expected: List<ConditionPart>
) {

    @Test
    fun test() {
        try {
            assertEquals(expected, ConditionPart.parse(input))
            assertNull(errorText)
        } catch (expected: ParsingException) {
            assertEquals(errorText, expected.message)
            assertEquals(input, expected.jsonSummary)
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data() = listOf(
            testArgs(
                "",
            ),
            testArgs(
                "test > 1",
                "test".v, " > 1".r),
            testArgs(
                "test",
                "test".v),
            testArgs(
                "1 > 2",
                "1 > 2".r),
            testArgs(
                "1 > 2 && test1...__3 > 3",
                "1 > 2 && ".r, "test1...__3".v, " > 3".r
            ),
            testArgs(
                "func(x) > func2(y)",
                "func(".r, "x".v, ") > ".r, "func2(".r, "y".v, ")".r
            ),
            testArgs(
                "func(func3(x), y) > func2(y)",
                "func(".r, "func3(".r, "x".v, "), ".r, "y".v, ") > ".r, "func2(".r, "y".v, ")".r
            ),
            testArgs(
                "!test",
                "!".r, "test".v,
            ),
            testArgs(
                "1test",
                "1".r, "test".v
            ),
            testArgs(
                "foo.bar.baz",
                "foo.bar.baz".v
            ),
            testArgs(
                "te111....___st111",
                "te111....___st111".v
            ),
            testArgs(
                "_test",
                "_test".v
            ),
            testArgs(
                ".test",
                ".".r, "test".v
            ),
            testArgs(
                "foo.bar.baz()",
                "foo.bar.baz(".r, ")".r
            ),
            testArgs(
                "total_likes > 100 || user_name == 'John'",
                "total_likes".v, " > 100 || ".r, "user_name".v, " == ".r, "'John'".r
            ),
            testArgs(
                "test == 'string with \\'quotes\\''",
                "test".v, " == ".r, "'string with \\'quotes\\''".r
            ),
            testArgs(
                "test'quoted'",
                "test".v, "'quoted'".r
            ),
            testArgs(
                "many == 'strings' && 'some' != more",
                "many".v, " == ".r, "'strings'".r, " && ".r, "'some'".r, " != ".r, "more".v
            ),
            errorArgs(
                "missing quote'",
                "Invalid quoted string"
            ),
            errorArgs(
                "missing quote' in the center",
                "Invalid quoted string"
            ),
            errorArgs(
                "missing quote' with escaped \\'",
                "Invalid quoted string"
            ),
            errorArgs(
                "Invalid escape 'sequence\\",
                "Invalid escape sequence"
            ),
            errorArgs(
                "'\\",
                "Invalid escape sequence"
            ),
        )
    }
}

private fun testArgs(input: String, vararg parsed: ConditionPart): Array<Any?> {
    return arrayOf(input, null, listOf(*parsed))
}

private fun errorArgs(input: String, errorText: String): Array<Any?> {
    return arrayOf(input, errorText, emptyList<ConditionPart>())
}

private val String.r: ConditionPart get() = ConditionPart.RawString(this)

private val String.v: ConditionPart get() = ConditionPart.Variable(this)
