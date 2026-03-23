package com.yandex.div.evaluable.types

import org.junit.Assert
import org.junit.Test
import java.util.TimeZone

/**
 * Tests for [DateTime].
 */
class DateTimeTest {
    @Test
    fun `dates compared by timestamp in UTC timezone`() {
        val utcPlus2 = DateTime(timestampMillis = 0, TimeZone.getTimeZone("GMT+2"))
        val utcPlus1 = DateTime(timestampMillis = 0, TimeZone.getTimeZone("GMT+1"))
        Assert.assertEquals(0, utcPlus2.compareTo(utcPlus1))
    }

    @Test
    fun `dates equality`() {
        Assert.assertEquals(
            DateTime(timestampMillis = 101010, TimeZone.getDefault()),
            DateTime(timestampMillis = 101010, TimeZone.getDefault()))
    }

    @Test
    fun `string representation does not include timezone in it`() {
        val dateTime = DateTime(timestampMillis = 5000, TimeZone.getTimeZone("UTC"))
        Assert.assertEquals("1970-01-01 00:00:05", dateTime.toString())
    }
}
