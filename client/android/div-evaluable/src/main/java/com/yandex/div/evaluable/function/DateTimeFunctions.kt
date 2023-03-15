package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.throwExceptionOnFunctionEvaluationFailed
import com.yandex.div.evaluable.types.DateTime
import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale

internal object ParseUnixTime : Function() {

    override val name = "parseUnixTime"

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.INTEGER))
    override val resultType = EvaluableType.DATETIME
    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val first = args.first()
        val timestampInSeconds = first as Int
        return DateTime(
            timestampMillis = timestampInSeconds * 1000L,
            timezone = TimeZone.getTimeZone("UTC"),
        )
    }
}

internal object ParseUnixTimeAsLocal : Function() {

    override val name = "parseUnixTimeAsLocal"

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.INTEGER))
    override val resultType = EvaluableType.DATETIME
    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val first = args.first()
        val timestampInSeconds = first as Int
        return DateTime(
            timestampMillis = timestampInSeconds * 1000L,
            timezone = TimeZone.getDefault(),
        )
    }
}

internal object NowLocal : Function() {

    override val name = "nowLocal"

    override val declaredArgs = emptyList<FunctionArgument>()
    override val resultType = EvaluableType.DATETIME
    override val isPure = false

    override fun evaluate(args: List<Any>): Any {
        return DateTime(
            timestampMillis = System.currentTimeMillis(),
            timezone = TimeZone.getDefault(),
        )
    }
}

internal object AddMillis : Function() {

    override val name = "addMillis"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME),
        FunctionArgument(type = EvaluableType.INTEGER)
    )
    override val resultType = EvaluableType.DATETIME
    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val datetime = args[0] as DateTime
        val millis = args[1] as Int

        return DateTime(
            timestampMillis = datetime.timestampMillis + millis,
            timezone = datetime.timezone,
        )
    }
}

internal object SetYear : Function() {

    override val name = "setYear"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME),
        FunctionArgument(type = EvaluableType.INTEGER)
    )
    override val resultType = EvaluableType.DATETIME
    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val datetime = args[0] as DateTime
        val value = args[1] as Int

        val calendar = datetime.toCalendar()
        calendar.set(Calendar.YEAR, value)

        return DateTime(
            timestampMillis = calendar.timeInMillis,
            timezone = datetime.timezone,
        )
    }
}

internal object SetMonth : Function() {

    override val name = "setMonth"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME),
        FunctionArgument(type = EvaluableType.INTEGER)
    )
    override val resultType = EvaluableType.DATETIME
    override val isPure = true

    @Throws(EvaluableException::class)
    override fun evaluate(args: List<Any>): Any {
        val datetime = args[0] as DateTime
        val value = args[1] as Int

        if (value > 12 || value < 1) {
            throwExceptionOnFunctionEvaluationFailed(name, args, "Expecting month in [1..12], instead got $value.")
        }
        val calendar = datetime.toCalendar()
        calendar.set(Calendar.MONTH, value - 1)

        return DateTime(
            timestampMillis = calendar.timeInMillis,
            timezone = datetime.timezone,
        )
    }
}

internal object SetDay : Function() {

    override val name = "setDay"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME),
        FunctionArgument(type = EvaluableType.INTEGER)
    )
    override val resultType = EvaluableType.DATETIME
    override val isPure = true

    @Throws(EvaluableException::class)
    override fun evaluate(args: List<Any>): Any {
        val datetime = args[0] as DateTime
        val value = args[1] as Int

        val calendar = datetime.toCalendar()
        val daysInMonth: Int = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        when (value) {
            in 1..daysInMonth -> calendar.set(Calendar.DAY_OF_MONTH, value)
            -1 -> calendar.set(Calendar.DAY_OF_MONTH, 0)
            else -> throwExceptionOnFunctionEvaluationFailed(name, args, "Unable to set day $value for date $datetime.")
        }

        return DateTime(
            timestampMillis = calendar.timeInMillis,
            timezone = datetime.timezone,
        )
    }
}

internal object SetHours : Function() {

    override val name = "setHours"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME),
        FunctionArgument(type = EvaluableType.INTEGER)
    )
    override val resultType = EvaluableType.DATETIME
    override val isPure = true

    @Throws(EvaluableException::class)
    override fun evaluate(args: List<Any>): Any {
        val datetime = args[0] as DateTime
        val value = args[1] as Int

        if (value > 23 || value < 0) {
            throwExceptionOnFunctionEvaluationFailed(name, args, "Expecting hours in [0..23], instead got $value.")
        }
        val calendar = datetime.toCalendar()
        calendar.timeInMillis = datetime.timestampMillis
        calendar.set(Calendar.HOUR_OF_DAY, value)

        return DateTime(
            timestampMillis = calendar.timeInMillis,
            timezone = datetime.timezone,
        )
    }
}

internal object SetMinutes : Function() {

    override val name = "setMinutes"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME),
        FunctionArgument(type = EvaluableType.INTEGER)
    )
    override val resultType = EvaluableType.DATETIME
    override val isPure = true

    @Throws(EvaluableException::class)
    override fun evaluate(args: List<Any>): Any {
        val datetime = args[0] as DateTime
        val value = args[1] as Int

        if (value > 59 || value < 0) {
            throwExceptionOnFunctionEvaluationFailed(name, args, "Expecting minutes in [0..59], instead got $value.")
        }
        val calendar = datetime.toCalendar()
        calendar.set(Calendar.MINUTE, value)

        return DateTime(
            timestampMillis = calendar.timeInMillis,
            timezone = datetime.timezone,
        )
    }
}

internal object SetSeconds : Function() {

    override val name = "setSeconds"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME),
        FunctionArgument(type = EvaluableType.INTEGER)
    )
    override val resultType = EvaluableType.DATETIME
    override val isPure = true

    @Throws(EvaluableException::class)
    override fun evaluate(args: List<Any>): Any {
        val datetime = args[0] as DateTime
        val value = args[1] as Int

        if (value > 59 || value < 0) {
            throwExceptionOnFunctionEvaluationFailed(name, args, "Expecting seconds in [0..59], instead got $value.")
        }
        val calendar = datetime.toCalendar()
        calendar.set(Calendar.SECOND, value)

        return DateTime(
            timestampMillis = calendar.timeInMillis,
            timezone = datetime.timezone,
        )
    }
}

internal object SetMillis : Function() {

    override val name = "setMillis"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME),
        FunctionArgument(type = EvaluableType.INTEGER)
    )
    override val resultType = EvaluableType.DATETIME
    override val isPure = true

    @Throws(EvaluableException::class)
    override fun evaluate(args: List<Any>): Any {
        val datetime = args[0] as DateTime
        val value = args[1] as Int

        if (value > 999 || value < 0) {
            throwExceptionOnFunctionEvaluationFailed(name, args, "Expecting millis in [0..999], instead got $value.")
        }

        val calendar = datetime.toCalendar()
        calendar.set(Calendar.MILLISECOND, value)

        return DateTime(
            timestampMillis = calendar.timeInMillis,
            timezone = datetime.timezone,
        )
    }
}

internal object GetYear : Function() {

    override val name = "getYear"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME)
    )
    override val resultType = EvaluableType.INTEGER
    override val isPure = true

    @Throws(EvaluableException::class)
    override fun evaluate(args: List<Any>): Any {
        val datetime = args[0] as DateTime

        val calendar = datetime.toCalendar()

        return calendar.get(Calendar.YEAR)
    }
}

internal object GetMonth : Function() {

    override val name = "getMonth"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME)
    )
    override val resultType = EvaluableType.INTEGER
    override val isPure = true

    @Throws(EvaluableException::class)
    override fun evaluate(args: List<Any>): Any {
        val datetime = args[0] as DateTime

        val calendar = datetime.toCalendar()

        return calendar.get(Calendar.MONTH) + 1
    }
}

internal object GetDay : Function() {

    override val name = "getDay"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME)
    )
    override val resultType = EvaluableType.INTEGER
    override val isPure = true

    @Throws(EvaluableException::class)
    override fun evaluate(args: List<Any>): Any {
        val datetime = args[0] as DateTime

        val calendar = datetime.toCalendar()

        return calendar.get(Calendar.DAY_OF_MONTH)
    }
}

internal object GetDayOfWeek : Function() {

    override val name = "getDayOfWeek"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME)
    )
    override val resultType = EvaluableType.INTEGER
    override val isPure = true

    @Throws(EvaluableException::class)
    override fun evaluate(args: List<Any>): Any {
        val datetime = args[0] as DateTime

        val calendar = datetime.toCalendar()

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

        return if (dayOfWeek == 0) 7 else dayOfWeek
    }
}

internal object GetHours : Function() {

    override val name = "getHours"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME)
    )
    override val resultType = EvaluableType.INTEGER
    override val isPure = true

    @Throws(EvaluableException::class)
    override fun evaluate(args: List<Any>): Any {
        val datetime = args[0] as DateTime

        val calendar = datetime.toCalendar()

        return calendar.get(Calendar.HOUR_OF_DAY)
    }
}

internal object GetMinutes : Function() {

    override val name = "getMinutes"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME)
    )
    override val resultType = EvaluableType.INTEGER
    override val isPure = true

    @Throws(EvaluableException::class)
    override fun evaluate(args: List<Any>): Any {
        val datetime = args[0] as DateTime

        val calendar = datetime.toCalendar()

        return calendar.get(Calendar.MINUTE)
    }
}

internal object GetSeconds : Function() {

    override val name = "getSeconds"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME)
    )
    override val resultType = EvaluableType.INTEGER
    override val isPure = true

    @Throws(EvaluableException::class)
    override fun evaluate(args: List<Any>): Any {
        val datetime = args[0] as DateTime

        val calendar = datetime.toCalendar()

        return calendar.get(Calendar.SECOND)
    }
}

internal object GetMillis : Function() {

    override val name = "getMillis"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME)
    )
    override val resultType = EvaluableType.INTEGER
    override val isPure = true

    @Throws(EvaluableException::class)
    override fun evaluate(args: List<Any>): Any {
        val datetime = args[0] as DateTime

        val calendar = datetime.toCalendar()

        return calendar.get(Calendar.MILLISECOND)
    }
}

internal object FormatDateAsLocal : Function() {
    override val name = "formatDateAsLocal"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME),
        FunctionArgument(type = EvaluableType.STRING)
    )
    override val resultType = EvaluableType.STRING

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val datetime = args[0] as DateTime
        val pattern = args[1] as String

        throwExceptionIfZInTimezone(pattern)

        val date = datetime.toDate()
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()

        return sdf.format(date)
    }
}

internal object FormatDateAsUTC : Function() {
    override val name = "formatDateAsUTC"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME),
        FunctionArgument(type = EvaluableType.STRING)
    )
    override val resultType = EvaluableType.STRING

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val datetime = args[0] as DateTime
        val pattern = args[1] as String

        throwExceptionIfZInTimezone(pattern)

        val date = datetime.toDate()
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")

        return sdf.format(date)
    }
}

internal object FormatDateAsLocalWithLocale : Function() {
    override val name = "formatDateAsLocalWithLocale"

    override val declaredArgs = listOf(
            FunctionArgument(type = EvaluableType.DATETIME),
            FunctionArgument(type = EvaluableType.STRING),
            FunctionArgument(type = EvaluableType.STRING)
    )
    override val resultType = EvaluableType.STRING

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val datetime = args[0] as DateTime
        val pattern = args[1] as String
        val locale = args[2] as String

        throwExceptionIfZInTimezone(pattern)

        val date = datetime.toDate()
        val sdf = SimpleDateFormat(pattern, Locale.Builder().setLanguageTag(locale).build())
        sdf.timeZone = TimeZone.getDefault()

        return sdf.format(date)
    }
}

internal object FormatDateAsUTCWithLocale : Function() {
    override val name = "formatDateAsUTCWithLocale"

    override val declaredArgs = listOf(
            FunctionArgument(type = EvaluableType.DATETIME),
            FunctionArgument(type = EvaluableType.STRING),
            FunctionArgument(type = EvaluableType.STRING)
    )
    override val resultType = EvaluableType.STRING

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val datetime = args[0] as DateTime
        val pattern = args[1] as String
        val locale = args[2] as String

        throwExceptionIfZInTimezone(pattern)

        val date = datetime.toDate()
        val sdf = SimpleDateFormat(pattern, Locale.Builder().setLanguageTag(locale).build())
        sdf.timeZone = TimeZone.getTimeZone("UTC")

        return sdf.format(date)
    }
}

private fun throwExceptionIfZInTimezone(pattern: String): Unit {
    if (pattern.toLowerCase().contains("z")) {
        throw EvaluableException("z/Z not supported in [$pattern]")
    }
}

private fun DateTime.toCalendar(): Calendar {
    val calendar = GregorianCalendar.getInstance()
    calendar.timeZone = timezone
    calendar.timeInMillis = timestampMillis

    return calendar
}

private fun DateTime.toDate(): Date {
    return Date(timestampMillis  - timezone.rawOffset)
}
