package com.yandex.div.evaluable.types

import org.junit.Assert
import org.junit.Test

/**
 * Tests for [DateTime].
 */
class DateTimeTest {
    @Test
    fun `dates compared by timestamp in UTC timezone`() {
        Assert.assertTrue(
            DateTime(timestampMillis = 0, timezoneMinutes = +2 * 60) >
                    DateTime(timestampMillis = 0, timezoneMinutes = +3 * 60)
        )
    }

    @Test
    fun `dates equality`() {
        Assert.assertEquals(
            DateTime(timestampMillis = 101010, +3 * 60),
            DateTime(timestampMillis = 101010, +3 * 60))
    }

    @Test
    fun `parsing initial date from string`() {
        val dateTime = DateTime.parseAsUTC("1970-01-01 00:00:00")
        Assert.assertEquals(0, dateTime.timezoneMinutes)
        Assert.assertEquals(0, dateTime.timestampMillis)
    }

    @Test
    fun `timestamp stored in millis`() {
        val dateTime = DateTime.parseAsUTC("1970-01-01 00:00:01")
        Assert.assertEquals(1000, dateTime.timestampMillis)
    }

    @Test
    fun `string representation does not include timezone in it`() {
        val dateTime = DateTime(timestampMillis = 5000, timezoneMinutes = +1 * 60)
        Assert.assertEquals("1970-01-01 00:00:05", dateTime.toString())
    }
}
