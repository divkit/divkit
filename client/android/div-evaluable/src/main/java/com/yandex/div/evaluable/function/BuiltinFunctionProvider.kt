package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.FunctionProvider

class BuiltinFunctionProvider : FunctionProvider {

    private val registry = FunctionRegistry()

    init {
        // Arithmetic functions
        registry.register(IntegerSum)
        registry.register(DoubleSum)

        registry.register(IntegerSub)
        registry.register(DoubleSub)

        registry.register(IntegerMul)
        registry.register(DoubleMul)

        registry.register(IntegerDiv)
        registry.register(DoubleDiv)

        registry.register(IntegerMod)
        registry.register(DoubleMod)

        registry.register(IntegerMaxValue)
        registry.register(IntegerMinValue)

        registry.register(DoubleMaxValue)
        registry.register(DoubleMinValue)

        registry.register(IntegerMax)
        registry.register(DoubleMax)

        registry.register(IntegerMin)
        registry.register(DoubleMin)

        registry.register(IntegerAbs)
        registry.register(DoubleAbs)

        registry.register(IntegerSignum)
        registry.register(DoubleSignum)

        registry.register(IntegerCopySign)
        registry.register(DoubleCopySign)

        registry.register(DoubleCeil)
        registry.register(DoubleFloor)
        registry.register(DoubleRound)

        // Color functions
        registry.register(ColorAlphaComponentGetter)
        registry.register(ColorRedComponentGetter)
        registry.register(ColorGreenComponentGetter)
        registry.register(ColorBlueComponentGetter)

        registry.register(ColorAlphaComponentSetter)
        registry.register(ColorRedComponentSetter)
        registry.register(ColorGreenComponentSetter)
        registry.register(ColorBlueComponentSetter)

        registry.register(ColorArgb)
        registry.register(ColorRgb)

        // Datetime functions
        registry.register(ParseUnixTime)
        registry.register(NowLocal)
        registry.register(AddMillis)
        registry.register(SetYear)
        registry.register(SetMonth)
        registry.register(SetDay)
        registry.register(SetHours)
        registry.register(SetMinutes)
        registry.register(SetSeconds)
        registry.register(SetMillis)

        // String functions
        registry.register(StringLength)
        registry.register(StringContains)
        registry.register(StringSubstring)
        registry.register(StringReplaceAll)
        registry.register(StringIndex)
        registry.register(StringLastIndex)
        registry.register(StringEncodeUri)
        registry.register(StringDecodeUri)
        registry.register(ToLowerCase)
        registry.register(ToUpperCase)
        registry.register(Trim)
        registry.register(TrimLeft)
        registry.register(TrimRight)

        // Type cast functions
        registry.register(NumberToInteger)
        registry.register(BooleanToInteger)
        registry.register(StringToInteger)
        registry.register(IntegerToNumber)
        registry.register(StringToNumber)
        registry.register(IntegerToBoolean)
        registry.register(StringToBoolean)
        registry.register(IntegerToString)
        registry.register(NumberToString)
        registry.register(BooleanToString)
    }

    override fun get(name: String, args: List<EvaluableType>): Function {
        return registry.get(name, args)
    }

    internal fun ensureFunctionRegistered(name: String, args: List<FunctionArgument>, resultType: EvaluableType) {
        registry.ensureRegistered(name, args, resultType)
    }
}
