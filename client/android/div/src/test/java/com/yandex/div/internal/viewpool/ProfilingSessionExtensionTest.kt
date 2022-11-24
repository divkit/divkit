package com.yandex.div.internal.viewpool

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class ProfilingSessionExtensionTest(private val value: Long, private val grouped: Long) {

    companion object {
        @JvmStatic
        @Parameters
        fun data() : Collection<Array<Any>> {
            return listOf(
                    arrayOf(-20L, 0L),
                    arrayOf(0L, 0L),
                    arrayOf(10L, 0L),
                    arrayOf(24L, 20L),
                    arrayOf(57L, 40L),
                    arrayOf(101L, 100L),
                    arrayOf(128L, 100L),
                    arrayOf(467L, 400L),
                    arrayOf(1337L, 1200L),
                    arrayOf(1800L, 1800L),
                    arrayOf(2737L, 2500L),
                    arrayOf(4040L, 4000L),
                    arrayOf(6536L, 6000L),
                    arrayOf(9999L, 9000L),
                    arrayOf(13370L, 12000L),
                    arrayOf(17423L, 16000L),
                    arrayOf(9999L, 9000L),
                    arrayOf(29486L, 25000L),
                    arrayOf(42000L, 40000L),
                    arrayOf(65536L, 50000L),
                    arrayOf(262144L, 50000L)
            )
        }
    }

    @Test
    fun `time rounded correctly`() {
        assertEquals(grouped, value.roundRoughly())
    }
}
