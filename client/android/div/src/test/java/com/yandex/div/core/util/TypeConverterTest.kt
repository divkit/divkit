package com.yandex.div.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

internal class TypeConverterTest {
    @Test
    fun `check zero`() {
        val result = 0L.toIntSafely()
        assertEquals(0, result)
    }

    @Test
    fun `check positive value`() {
        val result = 10L.toIntSafely()
        assertEquals(10, result)
    }

    @Test
    fun `check negative value`() {
        val result = -10L.toIntSafely()
        assertEquals(-10, result)
    }

    @Test
    fun `check max value`() {
        val result = Int.MAX_VALUE.toLong().toIntSafely()
        assertEquals(Int.MAX_VALUE, result)
    }

    @Test
    fun `check min value`() {
        val result = Int.MIN_VALUE.toLong().toIntSafely()
        assertEquals(Int.MIN_VALUE, result)
    }

    @Test
    fun `check positive overflow value`() {
        val result = (Int.MAX_VALUE.toLong() + 1L).toIntSafely()
        assertEquals(Int.MAX_VALUE, result)
    }

    @Test
    fun `check negative overflow value`() {
        val result = (Int.MIN_VALUE.toLong() - 1L).toIntSafely()
        assertEquals(Int.MIN_VALUE, result)
    }
}