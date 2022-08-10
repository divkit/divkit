package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.*
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.REASON_CONVERT_TO_COLOR
import com.yandex.div.evaluable.REASON_OUT_OF_RANGE
import com.yandex.div.evaluable.throwExceptionOnFunctionEvaluationFailed
import com.yandex.div.evaluable.types.Color

internal abstract class ColorComponentGetter(
        private val componentGetter: (Color) -> Int
) : Function() {

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.COLOR))

    override val resultType = EvaluableType.NUMBER

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        return componentGetter(args.first() as Color).toColorFloatComponentValue()
    }
}

internal abstract class ColorStringComponentGetter(
    private val componentGetter: ColorComponentGetter
) : Function() {

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.STRING))

    override val resultType = EvaluableType.NUMBER

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val colorString = args.first() as String
        val color = try {
            Color.parse(colorString)
        } catch (e: IllegalArgumentException) {
            throwExceptionOnFunctionEvaluationFailed(name, args, REASON_CONVERT_TO_COLOR, e)
        }
        return componentGetter.invoke(listOf(color))
    }
}

internal object ColorAlphaComponentGetter : ColorComponentGetter(
        componentGetter = { color: Color -> color.alpha() }
) {
    override val name = "getColorAlpha"
}

internal object ColorStringAlphaComponentGetter : ColorStringComponentGetter(
    componentGetter = ColorAlphaComponentGetter
) {
    override val name = "getColorAlpha"
}

internal object ColorRedComponentGetter : ColorComponentGetter(
    componentGetter = { color: Color -> color.red() }
) {
    override val name = "getColorRed"
}

internal object ColorStringRedComponentGetter : ColorStringComponentGetter(
        componentGetter = ColorRedComponentGetter
) {
    override val name = "getColorRed"
}

internal object ColorGreenComponentGetter : ColorComponentGetter(
    componentGetter = { color: Color -> color.green() }
) {
    override val name = "getColorGreen"
}

internal object ColorStringGreenComponentGetter : ColorStringComponentGetter(
        componentGetter = ColorGreenComponentGetter
) {
    override val name = "getColorGreen"
}

internal object ColorBlueComponentGetter : ColorComponentGetter(
    componentGetter = { color: Color -> color.blue() }
) {
    override val name = "getColorBlue"
}

internal object ColorStringBlueComponentGetter : ColorStringComponentGetter(
        componentGetter = ColorBlueComponentGetter
) {
    override val name = "getColorBlue"
}

internal abstract class ColorComponentSetter(
    private val componentSetter: (color: Color, value: Double) -> Color
) : Function() {

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.COLOR),
        FunctionArgument(type = EvaluableType.NUMBER), // color component value
    )

    override val resultType = EvaluableType.COLOR

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val color = args[0] as Color
        val value = args[1] as Double
        return try {
            componentSetter(color, value)
        } catch (e: IllegalArgumentException) {
            throwExceptionOnFunctionEvaluationFailed(name, listOf(color.toString(), value), REASON_OUT_OF_RANGE)
        }
    }
}

internal abstract class ColorStringComponentSetter(
        private val componentSetter: ColorComponentSetter
) : Function() {

    override val declaredArgs = listOf(
            FunctionArgument(type = EvaluableType.STRING),
            FunctionArgument(type = EvaluableType.NUMBER), // color component value
    )

    override val resultType = EvaluableType.COLOR

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val colorString = args[0] as String
        val color = try {
            Color.parse(colorString)
        } catch (e: IllegalArgumentException) {
            throwExceptionOnFunctionEvaluationFailed(name, args, REASON_CONVERT_TO_COLOR, e)
        }
        return componentSetter(listOf(color, args[1]))
    }
}

internal object ColorAlphaComponentSetter : ColorComponentSetter(
    componentSetter = { color: Color, alpha: Double ->
        Color.argb(
            alpha.toColorIntComponentValue(),
            color.red(),
            color.green(),
            color.blue()
        )
    }
) {
    override val name = "setColorAlpha"
}

internal object ColorStringAlphaComponentSetter : ColorStringComponentSetter(
        componentSetter = ColorAlphaComponentSetter
) {
    override val name = "setColorAlpha"
}

internal object ColorRedComponentSetter : ColorComponentSetter(
    componentSetter = { color: Color, red: Double ->
        Color.argb(
            color.alpha(),
            red.toColorIntComponentValue(),
            color.green(),
            color.blue()
        )
    }
) {
    override val name = "setColorRed"
}

internal object ColorStringRedComponentSetter : ColorStringComponentSetter(
        componentSetter = ColorRedComponentSetter
) {
    override val name = "setColorRed"
}

internal object ColorGreenComponentSetter : ColorComponentSetter(
    componentSetter = { color: Color, green: Double ->
        Color.argb(
            color.alpha(),
            color.red(),
            green.toColorIntComponentValue(),
            color.blue()
        )
    }
) {
    override val name = "setColorGreen"
}

internal object ColorStringGreenComponentSetter : ColorStringComponentSetter(
        componentSetter = ColorGreenComponentSetter
) {
    override val name = "setColorGreen"
}

internal object ColorBlueComponentSetter : ColorComponentSetter(
    componentSetter = { color: Color, blue: Double ->
        Color.argb(
            color.alpha(),
            color.red(),
            color.green(),
            blue.toColorIntComponentValue()
        )
    }
) {
    override val name = "setColorBlue"
}

internal object ColorStringBlueComponentSetter : ColorStringComponentSetter(
        componentSetter = ColorBlueComponentSetter
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

    override val resultType = EvaluableType.COLOR

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        return try {
            val alpha = (args[0] as Double).toColorIntComponentValue()
            val red = (args[1] as Double).toColorIntComponentValue()
            val green = (args[2] as Double).toColorIntComponentValue()
            val blue = (args[3] as Double).toColorIntComponentValue()
            Color.argb(alpha, red, green, blue)
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

    override val resultType = EvaluableType.COLOR

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        return try {
            val red = (args[0] as Double).toColorIntComponentValue()
            val green = (args[1] as Double).toColorIntComponentValue()
            val blue = (args[2] as Double).toColorIntComponentValue()
            Color.argb(255, red, green, blue)
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
