package com.yandex.div.core.type

import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.asTimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class DateTimeTest {

    private val moscow = TimeZone.of("Europe/Moscow")

    @Test
    fun `string is zero padded date and time without time zone`() {
        val dateTime = DateTime.create(epochMillis = 5000)

        assertEquals("1970-01-01 00:00:05", dateTime.toString())
    }

    @Test
    fun `string shows local time of its time zone`() {
        val dateTime = DateTime.create(epochMillis = 5000, timeZone = moscow)

        assertEquals("1970-01-01 03:00:05", dateTime.toString())
    }

    @Test
    fun `string uses daylight saving offset when it is in effect`() {
        val dateTime = DateTime.create(epochMillis = 1719835200000, timeZone = TimeZone.of("America/New_York"))

        assertEquals("2024-07-01 08:00:00", dateTime.toString())
    }

    @Test
    fun `string shows previous day when timestamp is before epoch`() {
        val dateTime = DateTime.create(epochMillis = -1000)

        assertEquals("1969-12-31 23:59:59", dateTime.toString())
    }

    @Test
    fun `string truncates milliseconds`() {
        val dateTime = DateTime.create(epochMillis = 5999)

        assertEquals("1970-01-01 00:00:05", dateTime.toString())
    }

    @Test
    fun `dates are equal when timestamp and time zone match`() {
        assertEquals(
            DateTime.create(epochMillis = 101010, timeZone = moscow),
            DateTime.create(epochMillis = 101010, timeZone = moscow),
        )
    }

    @Test
    fun `dates are not equal when timestamps differ`() {
        assertNotEquals(
            DateTime.create(epochMillis = 101010),
            DateTime.create(epochMillis = 101011),
        )
    }

    @Test
    fun `dates are equal when timestamps match in different time zones`() {
        assertEquals(
            DateTime.create(epochMillis = 101010),
            DateTime.create(epochMillis = 101010, timeZone = moscow),
        )
    }

    @Test
    fun `hash codes match when dates are equal`() {
        assertEquals(
            DateTime.create(epochMillis = 101010).hashCode(),
            DateTime.create(epochMillis = 101010, timeZone = moscow).hashCode(),
        )
    }

    @Test
    fun `factory creates date with given timestamp and time zone`() {
        val dateTime = DateTime.create(epochMillis = 101010, timeZone = moscow)

        assertEquals(101010, dateTime.epochMillis)
        assertEquals(moscow, dateTime.timeZone)
    }

    @Test
    fun `string uses gregorian calendar from cutover date`() {
        val dateTime = DateTime.create(epochMillis = GREGORIAN_CUTOVER_MILLIS)

        assertEquals("1582-10-15 00:00:00", dateTime.toString())
    }

    @Test
    fun `string uses julian calendar before cutover date`() {
        val dateTime = DateTime.create(epochMillis = GREGORIAN_CUTOVER_MILLIS - 1)

        assertEquals("1582-10-04 23:59:59", dateTime.toString())
    }

    @Test
    fun `string chooses calendar by local date`() {
        val beforeCutoverInUtc = DateTime.create(
            epochMillis = GREGORIAN_CUTOVER_MILLIS - 1,
            timeZone = UtcOffset(hours = 3).asTimeZone(),
        )
        val afterCutoverInUtc = DateTime.create(
            epochMillis = GREGORIAN_CUTOVER_MILLIS,
            timeZone = UtcOffset(hours = -5).asTimeZone(),
        )

        assertEquals("1582-10-15 02:59:59", beforeCutoverInUtc.toString())
        assertEquals("1582-10-04 19:00:00", afterCutoverInUtc.toString())
    }

    @Test
    fun `string shows julian leap day`() {
        val dateTime = DateTime.create(epochMillis = -14825851200000)

        assertEquals("1500-02-29 12:00:00", dateTime.toString())
    }

    @Test
    fun `string shows year without zero padding`() {
        val dateTime = DateTime.create(epochMillis = -30610224000000)

        assertEquals("999-12-27 00:00:00", dateTime.toString())
    }

    @Test
    fun `string shows first year of common era`() {
        val dateTime = DateTime.create(epochMillis = -62135596800000)

        assertEquals("1-01-03 00:00:00", dateTime.toString())
    }

    @Test
    fun `string shows era year before common era`() {
        val secondYearBce = DateTime.create(epochMillis = -62198755200000)
        val distantPast = DateTime.create(epochMillis = -100000000000000)

        assertEquals("2-01-03 00:00:00", secondYearBce.toString())
        assertEquals("1200-02-26 14:13:20", distantPast.toString())
    }

    @Test
    fun `dates compare as same when timestamps match in different time zones`() {
        val inUtc = DateTime.create(epochMillis = 0)
        val inMoscow = DateTime.create(epochMillis = 0, timeZone = moscow)

        assertEquals(0, inUtc.compareTo(inMoscow))
    }

    @Test
    fun `earlier timestamp compares as less even when its local time is later`() {
        val earlierWithLaterLocalTime = DateTime.create(epochMillis = 0, timeZone = moscow)
        val laterWithEarlierLocalTime = DateTime.create(epochMillis = 3_600_000)

        assertTrue(earlierWithLaterLocalTime < laterWithEarlierLocalTime)
    }

    private companion object {
        const val GREGORIAN_CUTOVER_MILLIS = -12219292800000
    }
}
