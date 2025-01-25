// Copyright 2024 Yandex LLC. All rights reserved.

package com.yandex.div.shine

import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.parser.ANY_TO_BOOLEAN
import com.yandex.div.internal.parser.JsonParser
import com.yandex.div.internal.parser.NUMBER_TO_DOUBLE
import com.yandex.div.internal.parser.NUMBER_TO_INT
import com.yandex.div.internal.parser.STRING_TO_COLOR_INT
import com.yandex.div.internal.parser.TYPE_HELPER_COLOR
import com.yandex.div.internal.parser.TYPE_HELPER_DOUBLE
import com.yandex.div.internal.parser.TYPE_HELPER_INT
import com.yandex.div.internal.parser.TYPE_HELPER_BOOLEAN
import com.yandex.div.json.expressions.ConstantExpressionList
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionList
import com.yandex.div.internal.parser.ValueValidator
import com.yandex.div.internal.parser.ListValidator
import com.yandex.div2.DivAction
import org.json.JSONObject

private const val SHINE_PARAM_IS_ENABLED = "is_enabled"
private const val SHINE_PARAM_COLORS = "colors"
private const val SHINE_PARAM_LOCATIONS = "locations"
private const val SHINE_PARAM_ANGLE = "angle"
private const val SHINE_PARAM_DURATION = "duration"
private const val SHINE_PARAM_CYCLE_COUNT = "cycle_count"
private const val SHINE_PARAM_REPEAT_DELAY = "interval"
private const val SHINE_PARAM_START_DELAY = "delay"
private const val SHINE_PARAM_ON_CYCLE_ACTIONS = "on_cycle_start_actions"
private const val FROM_COLOR: Int = 0xFF5EDB82.toInt()
private const val TO_COLOR: Int = 0x00000000.toInt()

private val DEFAULT_ENABLED = Expression.constant(true)
private val DEFAULT_ANGLE = Expression.constant(45.0)
private val DEFAULT_DURATION = Expression.constant(1600L)
private val DEFAULT_REPEAT_DELAY = Expression.constant(0L)
private val DEFAULT_START_DELAY = Expression.constant(0L)
private val DEFAULT_LOCATIONS = ConstantExpressionList(listOf(0.3, 0.5, 0.7))
private val DEFAULT_COLORS = ConstantExpressionList(listOf(FROM_COLOR, TO_COLOR, FROM_COLOR))
private val DEFAULT_CYCLE_COUNT = Expression.constant(0L)
private val DEFAULT_ON_CYCLE_ACTIONS = (emptyList<DivAction>())

/**
 * Class that holds shine config data from json response.
 */
internal data class ShineData(
    val isEnabled: Expression<Boolean> = DEFAULT_ENABLED,
    val angle: Expression<Double> = DEFAULT_ANGLE,
    val duration: Expression<Long> = DEFAULT_DURATION,
    val colors: ExpressionList<Int> = DEFAULT_COLORS,
    val locations: ExpressionList<Double> = DEFAULT_LOCATIONS,
    val maxViews: Expression<Long> = DEFAULT_CYCLE_COUNT,
    val repeatDelay: Expression<Long> = DEFAULT_REPEAT_DELAY,
    val startDelay: Expression<Long> = DEFAULT_START_DELAY,
    val onCycleStartActions: List<DivAction> = DEFAULT_ON_CYCLE_ACTIONS,
    val tintColor: Expression<Int>? = null
) {
    companion object {

        private fun <T> alwaysValid() = ValueValidator<T> { true }
        private fun <T> alwaysValidList() = ListValidator<T> { true }
        private fun positiveLong() = ValueValidator<Long> { it >= 0 }

        fun fromJson(
            parsingErrorLogger: DivShineLogger,
            params: JSONObject?,
            tintColorExpression: Expression<Int>?,
        ): ShineData {
            val json = params ?: return ShineData()
            val env = DivParsingEnvironment(parsingErrorLogger)

            return ShineData(
                isEnabled = JsonParser.readOptionalExpression(
                    json,
                    SHINE_PARAM_IS_ENABLED,
                    ANY_TO_BOOLEAN,
                    env.logger,
                    env,
                    TYPE_HELPER_BOOLEAN,
                ) ?: DEFAULT_ENABLED,
                angle = JsonParser.readOptionalExpression(
                    json,
                    SHINE_PARAM_ANGLE,
                    NUMBER_TO_DOUBLE,
                    alwaysValid(),
                    env.logger,
                    env,
                    TYPE_HELPER_DOUBLE
                ) ?: DEFAULT_ANGLE,
                duration = JsonParser.readOptionalExpression(
                    json,
                    SHINE_PARAM_DURATION,
                    NUMBER_TO_INT,
                    positiveLong(),
                    env.logger,
                    env,
                    TYPE_HELPER_INT
                ) ?: DEFAULT_DURATION,
                colors = JsonParser.readOptionalExpressionList<String, Int>(
                    json,
                    SHINE_PARAM_COLORS,
                    STRING_TO_COLOR_INT,
                    alwaysValidList(),
                    alwaysValid(),
                    env.logger,
                    env,
                    TYPE_HELPER_COLOR
                ) ?: DEFAULT_COLORS,
                locations = JsonParser.readOptionalExpressionList(
                    json,
                    SHINE_PARAM_LOCATIONS,
                    NUMBER_TO_DOUBLE,
                    alwaysValidList(),
                    alwaysValid(),
                    env.logger,
                    env,
                    TYPE_HELPER_DOUBLE
                ) ?: DEFAULT_LOCATIONS,
                maxViews = JsonParser.readOptionalExpression(
                    json,
                    SHINE_PARAM_CYCLE_COUNT,
                    NUMBER_TO_INT,
                    positiveLong(),
                    env.logger,
                    env,
                    TYPE_HELPER_INT
                ) ?: DEFAULT_CYCLE_COUNT,
                repeatDelay = JsonParser.readOptionalExpression(
                    json,
                    SHINE_PARAM_REPEAT_DELAY,
                    NUMBER_TO_INT,
                    positiveLong(),
                    env.logger,
                    env,
                    TYPE_HELPER_INT
                ) ?: DEFAULT_REPEAT_DELAY,
                startDelay = JsonParser.readOptionalExpression(
                    json,
                    SHINE_PARAM_START_DELAY,
                    NUMBER_TO_INT,
                    positiveLong(),
                    env.logger,
                    env,
                    TYPE_HELPER_INT
                ) ?: DEFAULT_START_DELAY,
                onCycleStartActions = JsonParser.readOptionalList(
                    json,
                    SHINE_PARAM_ON_CYCLE_ACTIONS,
                    DivAction.CREATOR,
                    env.logger,
                    env
                ) ?: DEFAULT_ON_CYCLE_ACTIONS,
                tintColor = tintColorExpression
            )
        }
    }
}
