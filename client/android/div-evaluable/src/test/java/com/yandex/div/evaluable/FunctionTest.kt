package com.yandex.div.evaluable

import com.yandex.div.evaluable.function.FunctionImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FunctionTest {

    @Test
    fun `function without arguments matches empty argument list`() {
        val function = FunctionImpl("func", emptyList())
        assertMatchesArguments(function, emptyList())
    }

    @Test
    fun `function with only int argument matches single int value`() {
        val function = FunctionImpl("func", listOf(FunctionArgument(EvaluableType.INTEGER)))
        assertMatchesArguments(function, listOf(EvaluableType.INTEGER))
    }

    @Test
    fun `function with only number argument matches single number value`() {
        val function = FunctionImpl("func", listOf(FunctionArgument(EvaluableType.NUMBER)))
        assertMatchesArguments(function, listOf(EvaluableType.NUMBER))
    }

    @Test
    fun `function with only bool argument matches single bool value`() {
        val function = FunctionImpl("func", listOf(FunctionArgument(EvaluableType.BOOLEAN)))
        assertMatchesArguments(function, listOf(EvaluableType.BOOLEAN))
    }

    @Test
    fun `function with only string argument matches single string value`() {
        val function = FunctionImpl("func", listOf(FunctionArgument(EvaluableType.STRING)))
        assertMatchesArguments(function, listOf(EvaluableType.STRING))
    }

    @Test
    fun `too few arguments passed to function`() {
        val function = FunctionImpl(
            name = "func",
            declaredArgs = listOf(FunctionArgument(EvaluableType.INTEGER), FunctionArgument(EvaluableType.INTEGER))
        )
        assertArgCountMismatch(function, listOf(EvaluableType.INTEGER))
    }

    @Test
    fun `too many arguments passed to function`() {
        val function = FunctionImpl("func", emptyList())
        assertArgCountMismatch(function, listOf(EvaluableType.INTEGER))
    }

    @Test
    fun `function has type mismatch with passed argument`() {
        val function = FunctionImpl("func", listOf(FunctionArgument(EvaluableType.INTEGER)))
        assertArgumentTypeMismatch(function, listOf(EvaluableType.NUMBER))
    }

    @Test
    fun `variadic function matches multiple values`() {
        val function = FunctionImpl("func", listOf(FunctionArgument(EvaluableType.INTEGER, isVariadic = true)))
        assertMatchesArguments(
            function,
            listOf(EvaluableType.INTEGER, EvaluableType.INTEGER, EvaluableType.INTEGER, EvaluableType.INTEGER)
        )
    }

    @Test
    fun `no variadic arguments passed to function`() {
        val function = FunctionImpl("func", listOf(FunctionArgument(EvaluableType.INTEGER, isVariadic = true)))
        assertArgCountMismatch(function, emptyList())
    }

    @Test
    fun `variadic function has type mismatch with passed argument`() {
        val function = FunctionImpl("func", listOf(FunctionArgument(EvaluableType.INTEGER, isVariadic = true)))
        assertArgumentTypeMismatch(
            function,
            listOf(EvaluableType.INTEGER, EvaluableType.NUMBER)
        )
    }

    private fun assertMatchesArguments(function: Function, argTypes: List<EvaluableType>) {
        assertEquals(Function.MatchResult.Ok, function.matchesArguments(argTypes))
    }

    private fun assertArgCountMismatch(function: Function, argTypes: List<EvaluableType>) {
        assertTrue(function.matchesArguments(argTypes) is Function.MatchResult.ArgCountMismatch)
    }

    private fun assertArgumentTypeMismatch(function: Function, argTypes: List<EvaluableType>) {
        assertTrue(function.matchesArguments(argTypes) is Function.MatchResult.ArgTypeMismatch)
    }
}
