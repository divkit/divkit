package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument

private const val MILLIS_IN_SECONDS = 1000
private const val SECONDS_IN_MINUTE = 60
private const val MINUTES_IN_HOUR = 60
private const val HOURS_IN_DAY = 24
private const val DAYS_IN_WEEK = 7


internal object GetIntervalSeconds : Function() {

    override val name = "getIntervalSeconds"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.INTEGER)
    )
    override val resultType = EvaluableType.INTEGER
    override val isPure = true

    @Throws(EvaluableException::class)
    override fun evaluate(args: List<Any>): Any {
        val duration = args[0] as Long

        if (duration < 0) {
            throw EvaluableException("Failed to evaluate [getIntervalSeconds(-1)]. Expecting non-negative number of milliseconds.")
        }

        return duration / MILLIS_IN_SECONDS % SECONDS_IN_MINUTE
    }
}

internal object GetIntervalTotalSeconds : Function() {

    override val name = "getIntervalTotalSeconds"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.INTEGER)
    )
    override val resultType = EvaluableType.INTEGER
    override val isPure = true

    @Throws(EvaluableException::class)
    override fun evaluate(args: List<Any>): Any {
        val duration = args[0] as Long

        if (duration < 0) {
            throw EvaluableException("Failed to evaluate [getIntervalTotalSeconds(-1)]. Expecting non-negative number of milliseconds.")
        }

        return duration / MILLIS_IN_SECONDS
    }
}

internal object GetIntervalMinutes : Function() {

    override val name = "getIntervalMinutes"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.INTEGER)
    )
    override val resultType = EvaluableType.INTEGER
    override val isPure = true

    @Throws(EvaluableException::class)
    override fun evaluate(args: List<Any>): Any {
        val duration = args[0] as Long

        if (duration < 0) {
            throw EvaluableException("Failed to evaluate [getIntervalMinutes(-1)]. Expecting non-negative number of milliseconds.")
        }

        return duration / MILLIS_IN_SECONDS / SECONDS_IN_MINUTE % MINUTES_IN_HOUR
    }
}

internal object GetIntervalTotalMinutes : Function() {

    override val name = "getIntervalTotalMinutes"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.INTEGER)
    )
    override val resultType = EvaluableType.INTEGER
    override val isPure = true

    @Throws(EvaluableException::class)
    override fun evaluate(args: List<Any>): Any {
        val duration = args[0] as Long

        if (duration < 0) {
            throw EvaluableException("Failed to evaluate [getIntervalTotalMinutes(-1)]. Expecting non-negative number of milliseconds.")
        }

        return duration / MILLIS_IN_SECONDS / SECONDS_IN_MINUTE
    }
}

internal object GetIntervalHours : Function() {

    override val name = "getIntervalHours"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.INTEGER)
    )
    override val resultType = EvaluableType.INTEGER
    override val isPure = true

    @Throws(EvaluableException::class)
    override fun evaluate(args: List<Any>): Any {
        val duration = args[0] as Long

        if (duration < 0) {
            throw EvaluableException("Failed to evaluate [getIntervalHours(-1)]. Expecting non-negative number of milliseconds.")
        }

        return duration / MILLIS_IN_SECONDS / SECONDS_IN_MINUTE / MINUTES_IN_HOUR % HOURS_IN_DAY
    }
}

internal object GetIntervalTotalHours : Function() {

    override val name = "getIntervalTotalHours"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.INTEGER)
    )
    override val resultType = EvaluableType.INTEGER
    override val isPure = true

    @Throws(EvaluableException::class)
    override fun evaluate(args: List<Any>): Any {
        val duration = args[0] as Long

        if (duration < 0) {
            throw EvaluableException("Failed to evaluate [getIntervalTotalHours(-1)]. Expecting non-negative number of milliseconds.")
        }

        return duration / MILLIS_IN_SECONDS / SECONDS_IN_MINUTE / MINUTES_IN_HOUR
    }
}

internal object GetIntervalTotalDays : Function() {

    override val name = "getIntervalTotalDays"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.INTEGER)
    )
    override val resultType = EvaluableType.INTEGER
    override val isPure = true

    @Throws(EvaluableException::class)
    override fun evaluate(args: List<Any>): Any {
        val duration = args[0] as Long

        if (duration < 0) {
            throw EvaluableException("Failed to evaluate [getIntervalTotalDays(-1)]. Expecting non-negative number of milliseconds.")
        }

        return duration / MILLIS_IN_SECONDS / SECONDS_IN_MINUTE / MINUTES_IN_HOUR / HOURS_IN_DAY
    }
}

internal object GetIntervalTotalWeeks : Function() {

    override val name = "getIntervalTotalWeeks"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.INTEGER)
    )
    override val resultType = EvaluableType.INTEGER
    override val isPure = true

    @Throws(EvaluableException::class)
    override fun evaluate(args: List<Any>): Any {
        val duration = args[0] as Long

        if (duration < 0) {
            throw EvaluableException("Failed to evaluate [getIntervalTotalWeeks(-1)]. Expecting non-negative number of milliseconds.")
        }

        return duration / MILLIS_IN_SECONDS / SECONDS_IN_MINUTE / MINUTES_IN_HOUR / HOURS_IN_DAY / DAYS_IN_WEEK
    }
}
