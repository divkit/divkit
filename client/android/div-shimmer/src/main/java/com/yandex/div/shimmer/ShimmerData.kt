package com.yandex.div.shimmer

import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.parser.JsonParser
import com.yandex.div.internal.parser.ListValidator
import com.yandex.div.internal.parser.NUMBER_TO_DOUBLE
import com.yandex.div.internal.parser.STRING_TO_COLOR_INT
import com.yandex.div.internal.parser.TYPE_HELPER_COLOR
import com.yandex.div.internal.parser.TYPE_HELPER_DOUBLE
import com.yandex.div.internal.parser.ValueValidator
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.ConstantExpressionList
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionList
import org.json.JSONObject

private const val SHIMMER_PARAM_COLORS = "colors"
private const val SHIMMER_PARAM_LOCATIONS = "locations"
private const val SHIMMER_PARAM_ANGLE = "angle"
private const val SHIMMER_PARAM_DURATION = "duration"
private const val FROM_COLOR: Int = 0xFF7F7F7F.toInt()
private const val TO_COLOR: Int = 0xFFAAAAAA.toInt()

private val DEFAULT_ANGLE = Expression.constant(0.0)
private val DEFAULT_DURATION = Expression.constant(1.6)
private val DEFAULT_LOCATIONS = ConstantExpressionList(listOf(0.3, 0.5, 0.7))
private val DEFAULT_COLORS = ConstantExpressionList(listOf(FROM_COLOR, TO_COLOR, FROM_COLOR))

/**
 * Class that holds shimmer config data from json response.
 */
internal data class ShimmerData(
        val angle: Expression<Double> = DEFAULT_ANGLE,
        val duration: Expression<Double> = DEFAULT_DURATION,
        val colors: ExpressionList<Int> = DEFAULT_COLORS,
        val locations: ExpressionList<Double> = DEFAULT_LOCATIONS
) {
    companion object {

        private fun <T> alwaysValid() = ValueValidator<T> { true }
        private fun <T> alwaysValidList() = ListValidator<T> { true }
        private fun positive() = ValueValidator<Double> { it > 0 }

        fun fromJson(params: JSONObject?): ShimmerData {
            val json = params ?: return ShimmerData()
            val logger = ParsingErrorLogger.LOG
            val env = DivParsingEnvironment(logger)
            return ShimmerData(
                    angle = JsonParser.readOptionalExpression(
                            json,
                            SHIMMER_PARAM_ANGLE,
                            NUMBER_TO_DOUBLE,
                            alwaysValid(),
                            logger,
                            env,
                            TYPE_HELPER_DOUBLE
                    ) ?: DEFAULT_ANGLE,
                    duration = JsonParser.readOptionalExpression(
                            json,
                            SHIMMER_PARAM_DURATION,
                            NUMBER_TO_DOUBLE,
                            positive(),
                            logger,
                            env,
                            TYPE_HELPER_DOUBLE
                    ) ?: DEFAULT_DURATION,
                    colors = JsonParser.readOptionalExpressionList<String, Int>(
                            json,
                            SHIMMER_PARAM_COLORS,
                            STRING_TO_COLOR_INT,
                            alwaysValidList(),
                            alwaysValid(),
                            logger,
                            env,
                            TYPE_HELPER_COLOR
                    ) ?: DEFAULT_COLORS,
                    locations = JsonParser.readOptionalExpressionList(
                            json,
                            SHIMMER_PARAM_LOCATIONS,
                            NUMBER_TO_DOUBLE,
                            alwaysValidList(),
                            alwaysValid(),
                            logger,
                            env,
                            TYPE_HELPER_DOUBLE
                    ) ?: DEFAULT_LOCATIONS
            )
        }
    }
}
