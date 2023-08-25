package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.*
import com.yandex.div.evaluable.Function

class BuiltinFunctionProvider(
    variableProvider: VariableProvider,
    storedValueProvider: StoredValueProvider,
) : FunctionProvider {

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
        registry.register(ColorStringAlphaComponentGetter)
        registry.register(ColorRedComponentGetter)
        registry.register(ColorStringRedComponentGetter)
        registry.register(ColorGreenComponentGetter)
        registry.register(ColorStringGreenComponentGetter)
        registry.register(ColorBlueComponentGetter)
        registry.register(ColorStringBlueComponentGetter)

        registry.register(ColorAlphaComponentSetter)
        registry.register(ColorStringAlphaComponentSetter)
        registry.register(ColorRedComponentSetter)
        registry.register(ColorStringRedComponentSetter)
        registry.register(ColorGreenComponentSetter)
        registry.register(ColorStringGreenComponentSetter)
        registry.register(ColorBlueComponentSetter)
        registry.register(ColorStringBlueComponentSetter)

        registry.register(ColorArgb)
        registry.register(ColorRgb)

        // Datetime functions
        registry.register(ParseUnixTime)
        registry.register(ParseUnixTimeAsLocal)
        registry.register(NowLocal)
        registry.register(AddMillis)
        registry.register(SetYear)
        registry.register(SetMonth)
        registry.register(SetDay)
        registry.register(SetHours)
        registry.register(SetMinutes)
        registry.register(SetSeconds)
        registry.register(SetMillis)
        registry.register(GetYear)
        registry.register(GetMonth)
        registry.register(GetDay)
        registry.register(GetDayOfWeek)
        registry.register(GetHours)
        registry.register(GetMinutes)
        registry.register(GetSeconds)
        registry.register(GetMillis)
        registry.register(FormatDateAsLocal)
        registry.register(FormatDateAsUTC)
        registry.register(FormatDateAsLocalWithLocale)
        registry.register(FormatDateAsUTCWithLocale)

        // Interval functions
        registry.register(GetIntervalTotalWeeks)
        registry.register(GetIntervalTotalDays)
        registry.register(GetIntervalTotalHours)
        registry.register(GetIntervalHours)
        registry.register(GetIntervalTotalMinutes)
        registry.register(GetIntervalMinutes)
        registry.register(GetIntervalTotalSeconds)
        registry.register(GetIntervalSeconds)

        // String functions
        registry.register(StringLength)
        registry.register(StringContains)
        registry.register(StringSubstring)
        registry.register(StringReplaceAll)
        registry.register(StringIndex)
        registry.register(StringLastIndex)
        registry.register(StringEncodeUri)
        registry.register(StringDecodeUri)
        registry.register(TestRegex)
        registry.register(ToLowerCase)
        registry.register(ToUpperCase)
        registry.register(Trim)
        registry.register(TrimLeft)
        registry.register(TrimRight)
        registry.register(PadStartString)
        registry.register(PadStartInteger)
        registry.register(PadEndString)
        registry.register(PadEndInteger)

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
        registry.register(ColorToString)

        // Variable functions
        registry.register(GetIntegerValue(variableProvider))
        registry.register(GetNumberValue(variableProvider))
        registry.register(GetStringValue(variableProvider))
        registry.register(GetColorValueString(variableProvider))
        registry.register(GetColorValue(variableProvider))
        registry.register(GetBooleanValue(variableProvider))

        // Dict functions
        registry.register(GetDictInteger(variableProvider))
        registry.register(GetDictNumber(variableProvider))
        registry.register(GetDictString(variableProvider))
        registry.register(GetDictColor(variableProvider))
        registry.register(GetDictBoolean(variableProvider))
        registry.register(GetDictOptInteger(variableProvider))
        registry.register(GetDictOptNumber(variableProvider))
        registry.register(GetDictOptString(variableProvider))
        registry.register(GetDictOptColor(variableProvider))
        registry.register(GetDictOptBoolean(variableProvider))

        // Array functions
        registry.register(GetArrayInteger(variableProvider))
        registry.register(GetArrayNumber(variableProvider))
        registry.register(GetArrayString(variableProvider))
        registry.register(GetArrayColor(variableProvider))
        registry.register(GetArrayBoolean(variableProvider))
        registry.register(GetArrayOptInteger(variableProvider))
        registry.register(GetArrayOptNumber(variableProvider))
        registry.register(GetArrayOptString(variableProvider))
        registry.register(GetArrayOptColorWithColorFallback(variableProvider))
        registry.register(GetArrayOptColorWithStringFallback(variableProvider))
        registry.register(GetArrayOptBoolean(variableProvider))
    }

    override fun get(name: String, args: List<EvaluableType>): Function {
        return registry.get(name, args)
    }

    internal fun ensureFunctionRegistered(name: String, args: List<FunctionArgument>, resultType: EvaluableType) {
        registry.ensureRegistered(name, args, resultType)
    }
}
