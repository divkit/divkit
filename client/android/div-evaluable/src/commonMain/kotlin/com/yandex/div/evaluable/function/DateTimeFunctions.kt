package com.yandex.div.evaluable.function

import com.yandex.div.core.util.currentTimeMillis
import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.ExpressionContext
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.throwExceptionOnFunctionEvaluationFailed
import com.yandex.div.evaluable.types.DateTime

internal object ParseUnixTime : Function() {

    override val name = "parseUnixTime"

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.INTEGER))
    override val resultType = EvaluableType.DATETIME
    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val first = args.first()
        val timestampInSeconds = first as Long
        return DateTime(timestampInSeconds * MILLIS_PER_SECOND, isLocal = false)
    }
}

internal object ParseUnixTimeAsLocal : Function() {

    override val name = "parseUnixTimeAsLocal"

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.INTEGER))
    override val resultType = EvaluableType.DATETIME
    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val first = args.first()
        val timestampInSeconds = first as Long
        return DateTime(timestampInSeconds * MILLIS_PER_SECOND, isLocal = true)
    }
}

internal object NowLocal : Function() {

    override val name = "nowLocal"

    override val declaredArgs = emptyList<FunctionArgument>()
    override val resultType = EvaluableType.DATETIME
    override val isPure = false

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        return DateTime(currentTimeMillis(), isLocal = true)
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

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val datetime = args[0] as DateTime
        val millis = args[1] as Long

        return datetime.withTimestampMillis(datetime.timestampMillis + millis)
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

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val datetime = args[0] as DateTime
        val value = args[1] as Long

        val calendar = Calendar(datetime)
        calendar.set(Calendar.Field.YEAR, value.toInt())

        return datetime.withTimestampMillis(calendar.timeInMillis)
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

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val datetime = args[0] as DateTime
        val value = args[1] as Long

        if (value > 12 || value < 1) {
            throwExceptionOnFunctionEvaluationFailed(name, args, "Expecting month in [1..12], instead got $value.")
        }
        val calendar = Calendar(datetime)
        calendar.set(Calendar.Field.MONTH, (value - 1).toInt())

        return datetime.withTimestampMillis(calendar.timeInMillis)
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

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val datetime = args[0] as DateTime
        val value = args[1] as Long

        val calendar = Calendar(datetime)
        val daysInMonth: Int = calendar.daysInMonth()
        when (value) {
            in 1..daysInMonth -> calendar.set(Calendar.Field.DAY_OF_MONTH, value.toInt())
            -1L -> calendar.set(Calendar.Field.DAY_OF_MONTH, 0)
            else -> throwExceptionOnFunctionEvaluationFailed(name, args, "Unable to set day $value for date $datetime.")
        }

        return datetime.withTimestampMillis(calendar.timeInMillis)
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

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val datetime = args[0] as DateTime
        val value = args[1] as Long

        if (value > 23 || value < 0) {
            throwExceptionOnFunctionEvaluationFailed(name, args, "Expecting hours in [0..23], instead got $value.")
        }
        val calendar = Calendar(datetime)
        calendar.timeInMillis = datetime.timestampMillis
        calendar.set(Calendar.Field.HOUR_OF_DAY, value.toInt())

        return datetime.withTimestampMillis(calendar.timeInMillis)
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

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val datetime = args[0] as DateTime
        val value = args[1] as Long

        if (value > 59 || value < 0) {
            throwExceptionOnFunctionEvaluationFailed(name, args, "Expecting minutes in [0..59], instead got $value.")
        }
        val calendar = Calendar(datetime)
        calendar.set(Calendar.Field.MINUTE, value.toInt())

        return datetime.withTimestampMillis(calendar.timeInMillis)
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

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val datetime = args[0] as DateTime
        val value = args[1] as Long

        if (value > 59 || value < 0) {
            throwExceptionOnFunctionEvaluationFailed(name, args, "Expecting seconds in [0..59], instead got $value.")
        }
        val calendar = Calendar(datetime)
        calendar.set(Calendar.Field.SECOND, value.toInt())

        return datetime.withTimestampMillis(calendar.timeInMillis)
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

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val datetime = args[0] as DateTime
        val value = args[1] as Long

        if (value > 999 || value < 0) {
            throwExceptionOnFunctionEvaluationFailed(name, args, "Expecting millis in [0..999], instead got $value.")
        }

        val calendar = Calendar(datetime)
        calendar.set(Calendar.Field.MILLISECOND, value.toInt())

        return datetime.withTimestampMillis(calendar.timeInMillis)
    }
}

internal object GetYear : Function() {

    override val name = "getYear"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME)
    )
    override val resultType = EvaluableType.INTEGER
    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val datetime = args[0] as DateTime

        val calendar = Calendar(datetime)

        return calendar.get(Calendar.Field.YEAR).toLong()
    }
}

internal object GetMonth : Function() {

    override val name = "getMonth"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME)
    )
    override val resultType = EvaluableType.INTEGER
    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val datetime = args[0] as DateTime

        val calendar = Calendar(datetime)

        return (calendar.get(Calendar.Field.MONTH) + 1).toLong()
    }
}

internal object GetDay : Function() {

    override val name = "getDay"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME)
    )
    override val resultType = EvaluableType.INTEGER
    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val datetime = args[0] as DateTime

        val calendar = Calendar(datetime)

        return calendar.get(Calendar.Field.DAY_OF_MONTH).toLong()
    }
}

internal object GetDayOfWeek : Function() {

    override val name = "getDayOfWeek"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME)
    )
    override val resultType = EvaluableType.INTEGER
    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val datetime = args[0] as DateTime

        val calendar = Calendar(datetime)

        val dayOfWeek = calendar.get(Calendar.Field.DAY_OF_WEEK) - 1

        return if (dayOfWeek == 0) 7L else dayOfWeek.toLong()
    }
}

internal object GetHours : Function() {

    override val name = "getHours"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME)
    )
    override val resultType = EvaluableType.INTEGER
    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val datetime = args[0] as DateTime

        val calendar = Calendar(datetime)

        return calendar.get(Calendar.Field.HOUR_OF_DAY).toLong()
    }
}

internal object GetMinutes : Function() {

    override val name = "getMinutes"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME)
    )
    override val resultType = EvaluableType.INTEGER
    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val datetime = args[0] as DateTime

        val calendar = Calendar(datetime)

        return calendar.get(Calendar.Field.MINUTE).toLong()
    }
}

internal object GetSeconds : Function() {

    override val name = "getSeconds"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME)
    )
    override val resultType = EvaluableType.INTEGER
    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val datetime = args[0] as DateTime

        val calendar = Calendar(datetime)

        return calendar.get(Calendar.Field.SECOND).toLong()
    }
}

internal object GetMillis : Function() {

    override val name = "getMillis"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME)
    )
    override val resultType = EvaluableType.INTEGER
    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val datetime = args[0] as DateTime

        val calendar = Calendar(datetime)

        return calendar.get(Calendar.Field.MILLISECOND).toLong()
    }
}

internal object GetTimestamp : Function() {

    override val name = "getTimestamp"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.DATETIME)
    )
    override val resultType = EvaluableType.INTEGER
    override val isPure = true

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val datetime = args[0] as DateTime
        return datetime.timestampMillis
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

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val datetime = args[0] as DateTime
        val pattern = args[1] as String

        return formatDateTime(datetime, pattern, local = true, localeTag = null)
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

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val datetime = args[0] as DateTime
        val pattern = args[1] as String

        return formatDateTime(datetime, pattern, local = false, localeTag = null)
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

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val datetime = args[0] as DateTime
        val pattern = args[1] as String
        val locale = args[2] as String

        return formatDateTime(datetime, pattern, local = true, localeTag = locale)
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

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val datetime = args[0] as DateTime
        val pattern = args[1] as String
        val locale = args[2] as String

        return formatDateTime(datetime, pattern, local = false, localeTag = locale)
    }
}
