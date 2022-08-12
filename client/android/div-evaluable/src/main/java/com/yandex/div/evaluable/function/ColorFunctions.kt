package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.REASON_CONVERT_TO_COLOR
import com.yandex.div.evaluable.REASON_OUT_OF_RANGE
import com.yandex.div.evaluable.throwExceptionOnFunctionEvaluationFailed

internal abstract class ColorComponentGetter(
    private val componentGetter: (Int) -> Int
) : Function() {

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.STRING)) // color

    override val resultType = EvaluableType.NUMBER

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val colorString = args.first() as String
        val color = try {
            parseColor(colorString)
        } catch (e: IllegalArgumentException) {
            throwExceptionOnFunctionEvaluationFailed(name, args, REASON_CONVERT_TO_COLOR, e)
        }
        return componentGetter(color).toColorFloatComponentValue()
    }
}

internal object ColorAlphaComponentGetter : ColorComponentGetter(
    componentGetter = { color: Int -> Colors.alpha(color) }
) {
    override val name = "getColorAlpha"
}

internal object ColorRedComponentGetter : ColorComponentGetter(
    componentGetter = { color: Int -> Colors.red(color) }
) {
    override val name = "getColorRed"
}

internal object ColorGreenComponentGetter : ColorComponentGetter(
    componentGetter = { color: Int -> Colors.green(color) }
) {
    override val name = "getColorGreen"
}

internal object ColorBlueComponentGetter : ColorComponentGetter(
    componentGetter = { color: Int -> Colors.blue(color) }
) {
    override val name = "getColorBlue"
}

internal abstract class ColorComponentSetter(
    private val componentSetter: (color: Int, value: Double) -> Int
) : Function() {

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // color
        FunctionArgument(type = EvaluableType.NUMBER), // color component value
    )

    override val resultType = EvaluableType.STRING

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val colorString = args[0] as String
        val channelValue = args[1] as Double
        val color = try {
            parseColor(colorString)
        } catch (e: IllegalArgumentException) {
            throwExceptionOnFunctionEvaluationFailed(name, args, REASON_CONVERT_TO_COLOR, e)
        }
        return try {
            colorAsHexString(componentSetter(color, channelValue))
        } catch (e: IllegalArgumentException) {
            throwExceptionOnFunctionEvaluationFailed(name, args, REASON_OUT_OF_RANGE)
        }
    }
}

internal object ColorAlphaComponentSetter : ColorComponentSetter(
    componentSetter = { color: Int, alpha: Double ->
        Colors.argb(
            alpha.toColorIntComponentValue(),
            Colors.red(color),
            Colors.green(color),
            Colors.blue(color)
        )
    }
) {
    override val name = "setColorAlpha"
}

internal object ColorRedComponentSetter : ColorComponentSetter(
    componentSetter = { color: Int, red: Double ->
        Colors.argb(
            Colors.alpha(color),
            red.toColorIntComponentValue(),
            Colors.green(color),
            Colors.blue(color)
        )
    }
) {
    override val name = "setColorRed"
}

internal object ColorGreenComponentSetter : ColorComponentSetter(
    componentSetter = { color: Int, green: Double ->
        Colors.argb(
            Colors.alpha(color),
            Colors.red(color),
            green.toColorIntComponentValue(),
            Colors.blue(color)
        )
    }
) {
    override val name = "setColorGreen"
}

internal object ColorBlueComponentSetter : ColorComponentSetter(
    componentSetter = { color: Int, blue: Double ->
        Colors.argb(
            Colors.alpha(color),
            Colors.red(color),
            Colors.green(color),
            blue.toColorIntComponentValue()
        )
    }
) {
    override val name = "setColorBlue"
}

internal object ColorArgb : Function() {

    override val name = "argb"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.NUMBER), // alpha value
        FunctionArgument(type = EvaluableType.NUMBER), // red value
        FunctionArgument(type = EvaluableType.NUMBER), // green value
        FunctionArgument(type = EvaluableType.NUMBER), // blue value
    )

    override val resultType = EvaluableType.STRING

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        return try {
            val alpha = (args[0] as Double).toColorIntComponentValue()
            val red = (args[1] as Double).toColorIntComponentValue()
            val green = (args[2] as Double).toColorIntComponentValue()
            val blue = (args[3] as Double).toColorIntComponentValue()
            colorAsHexString(Colors.argb(alpha, red, green, blue))
        } catch (e: IllegalArgumentException) {
            throwExceptionOnFunctionEvaluationFailed(name, args, REASON_OUT_OF_RANGE)
        }
    }
}

internal object ColorRgb : Function() {

    override val name = "rgb"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.NUMBER), // red value
        FunctionArgument(type = EvaluableType.NUMBER), // green value
        FunctionArgument(type = EvaluableType.NUMBER), // blue value
    )

    override val resultType = EvaluableType.STRING

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        return try {
            val red = (args[0] as Double).toColorIntComponentValue()
            val green = (args[1] as Double).toColorIntComponentValue()
            val blue = (args[2] as Double).toColorIntComponentValue()
            colorAsHexString(Colors.argb(255, red, green, blue))
        } catch (e: IllegalArgumentException) {
            throwExceptionOnFunctionEvaluationFailed(name, args, REASON_OUT_OF_RANGE)
        }
    }
}

@Throws(IllegalArgumentException::class)
private fun Int.toColorFloatComponentValue(): Double {
    if (this !in 0..255) throw IllegalArgumentException("Value out of channel range 0..255")
    return this.toDouble() / 255f
}

@Throws(IllegalArgumentException::class)
private fun Double.toColorIntComponentValue(): Int {
    if (this < 0f || this > 1f) throw IllegalArgumentException()
    return (this * 255f + 0.5f).toInt()
}

private fun colorAsHexString(color: Int) = String.format("#%08X", color)

@Throws(IllegalArgumentException::class)
private fun parseColor(text: String): Int {
    if (text.isEmpty() || text[0] != '#' || text.length != 9) throw IllegalArgumentException("Not valid format.")
    return Colors.parseColor(text)
}
