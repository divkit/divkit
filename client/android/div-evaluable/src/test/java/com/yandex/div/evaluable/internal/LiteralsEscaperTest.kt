package com.yandex.div.evaluable.internal

import com.yandex.div.evaluable.EvaluableException
import org.junit.Assert
import org.junit.Test

class LiteralsEscaperTest {

    @Test
    fun `escaping # without escaping literals`() {
        assertStringLiteralEscaping("Hello", "Hello")
    }

    @Test
    fun `escaping # two backslashes`() {
        assertStringLiteralEscaping("\\\\Hello", """\Hello""")
    }

    @Test
    fun `escaping # escaping start expression literal`() {
        assertStringLiteralEscaping("\\@{Hello", "@{Hello")
    }

    @Test
    fun `escaping # escaping quote literal`() {
        assertStringLiteralEscaping("\\'Hello", "'Hello")
    }

    @Test
    fun `escaping # backslashes`() {
        assertStringLiteralEscaping("\\\\", """\""")
    }

    @Test
    fun `escaping # escaping quote literal with backslashes`() {
        assertStringLiteralEscaping("\\\\\\'", """\'""")
    }

    @Test(expected = EvaluableException::class)
    fun `escaping # alone backslashes 1`() {
        assertStringLiteralEscaping("\\", """\""")
    }

    @Test(expected = EvaluableException::class)
    fun `escaping # alone backslashes 2`() {
        assertStringLiteralEscaping("\\\\\\ '", """\ '""")
    }

    private fun assertStringLiteralEscaping(stringLiteral: String, expected: String) {
        val actual = LiteralsEscaper.process(stringLiteral)
        Assert.assertEquals(expected, actual)
    }
}
