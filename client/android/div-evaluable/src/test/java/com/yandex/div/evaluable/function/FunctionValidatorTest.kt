package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.FunctionArgument
import org.junit.Assert.assertEquals
import org.junit.Test

class FunctionValidatorTest {

    @Test(expected = EvaluableException::class)
    fun `multiple variadic arguments causes exception`() {
        val function = FunctionImpl(
            name = "func",
            declaredArgs = listOf(
                FunctionArgument(type = EvaluableType.INTEGER, isVariadic = true),
                FunctionArgument(type = EvaluableType.STRING, isVariadic = true)
            )
        )

        FunctionValidator.validateFunction(function)
    }

    @Test(expected = EvaluableException::class)
    fun `non-trailing variadic arguments causes exception`() {
        val function = FunctionImpl(
            name = "func",
            declaredArgs = listOf(
                FunctionArgument(type = EvaluableType.INTEGER, isVariadic = true),
                FunctionArgument(type = EvaluableType.STRING)
            )
        )

        FunctionValidator.validateFunction(function)
    }

    @Test
    fun `function with different argument does not conflict with known one`() {
        val function1 = FunctionImpl(
            name = "func",
            declaredArgs = listOf(FunctionArgument(type = EvaluableType.INTEGER))
        )

        val function2 = FunctionImpl(
            name = "func",
            declaredArgs = listOf(FunctionArgument(type = EvaluableType.NUMBER))
        )

        assertEquals(function1, FunctionValidator.validateOverloading(function1, listOf(function2)))
    }

    @Test
    fun `function with different name does not conflict with known one`() {
        val function1 = FunctionImpl(
            name = "func1",
            declaredArgs = listOf(FunctionArgument(type = EvaluableType.INTEGER))
        )

        val function2 = FunctionImpl(
            name = "func2",
            declaredArgs = listOf(FunctionArgument(type = EvaluableType.INTEGER))
        )

        assertEquals(function1, FunctionValidator.validateOverloading(function1, listOf(function2)))
    }

    @Test(expected = EvaluableException::class)
    fun `functions with the same arguments conflicts with known one`() {
        val function1 = FunctionImpl(
            name = "func",
            declaredArgs = listOf(FunctionArgument(type = EvaluableType.INTEGER))
        )

        val function2 = FunctionImpl(
            name = "func",
            declaredArgs = listOf(FunctionArgument(type = EvaluableType.INTEGER))
        )

        FunctionValidator.validateOverloading(function1, listOf(function2))
    }

    @Test(expected = EvaluableException::class)
    fun `variadic function conflicts with explicit argument function`() {
        val function1 = FunctionImpl(
            name = "func",
            declaredArgs = listOf(FunctionArgument(type = EvaluableType.INTEGER, isVariadic = true))
        )

        val function2 = FunctionImpl(
            name = "func",
            declaredArgs = listOf(
                FunctionArgument(type = EvaluableType.INTEGER)
            )
        )

        FunctionValidator.validateOverloading(function1, listOf(function2))
    }

    @Test(expected = EvaluableException::class)
    fun `variadic function conflicts with multiple explicit argument function`() {
        val function1 = FunctionImpl(
            name = "func",
            declaredArgs = listOf(FunctionArgument(type = EvaluableType.INTEGER, isVariadic = true))
        )

        val function2 = FunctionImpl(
            name = "func",
            declaredArgs = listOf(
                FunctionArgument(type = EvaluableType.INTEGER),
                FunctionArgument(type = EvaluableType.INTEGER)
            )
        )

        FunctionValidator.validateOverloading(function1, listOf(function2))
    }

    @Test(expected = EvaluableException::class)
    fun `variadic function conflicts with variadic function`() {
        val function1 = FunctionImpl(
            name = "func",
            declaredArgs = listOf(FunctionArgument(type = EvaluableType.INTEGER, isVariadic = true))
        )

        val function2 = FunctionImpl(
            name = "func",
            declaredArgs = listOf(
                FunctionArgument(type = EvaluableType.INTEGER),
                FunctionArgument(type = EvaluableType.INTEGER, isVariadic = true)
            )
        )

        FunctionValidator.validateOverloading(function1, listOf(function2))
    }

    @Test(expected = EvaluableException::class)
    fun `variadic function conflicts with empty argument list function`() {
        val function1 = FunctionImpl(
            name = "func",
            declaredArgs = listOf(FunctionArgument(type = EvaluableType.INTEGER, isVariadic = true))
        )

        val function2 = FunctionImpl(
            name = "func",
            declaredArgs = emptyList()
        )

        FunctionValidator.validateOverloading(function1, listOf(function2))
    }

    @Test
    fun `variadic function does not conflict with multiple explicit argument function`() {
        val function1 = FunctionImpl(
            name = "func",
            declaredArgs = listOf(FunctionArgument(type = EvaluableType.INTEGER, isVariadic = true))
        )

        val function2 = FunctionImpl(
            name = "func",
            declaredArgs = listOf(
                FunctionArgument(type = EvaluableType.INTEGER),
                FunctionArgument(type = EvaluableType.NUMBER)
            )
        )

        assertEquals(function1, FunctionValidator.validateOverloading(function1, listOf(function2)))
    }
}
