package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.FunctionProvider

@Deprecated(
    "Use `GeneratedBuiltinFunctionProvider` instead",
    ReplaceWith(
        "GeneratedBuiltinFunctionProvider",
        "com.yandex.div.evaluable.function.GeneratedBuiltinFunctionProvider"
    )
)
object BuiltinFunctionProvider : FunctionProvider {

    private val registry = FunctionRegistry()
    internal val exposedFunctions
        get() = registry.exposedFunctions

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
        registry.register(UrlToString)
        registry.register(StringToColor)
        registry.register(StringToUrl)

        // Variable functions
        registry.register(GetIntegerValue)
        registry.register(GetNumberValue)
        registry.register(GetStringValue)
        registry.register(GetColorValueString)
        registry.register(GetColorValue)
        registry.register(GetUrlValueWithStringFallback)
        registry.register(GetUrlValueWithUrlFallback)
        registry.register(GetBooleanValue)

        // Stored value functions
        registry.register(GetStoredIntegerValue)
        registry.register(GetStoredNumberValue)
        registry.register(GetStoredStringValue)
        registry.register(GetStoredColorValueString)
        registry.register(GetStoredColorValue)
        registry.register(GetStoredBooleanValue)
        registry.register(GetStoredUrlValueWithStringFallback)
        registry.register(GetStoredUrlValueWithUrlFallback)

        // Dict functions legacy
        registry.register(GetDictInteger)
        registry.register(GetDictNumber)
        registry.register(GetDictString)
        registry.register(GetDictColor)
        registry.register(GetDictUrl)
        registry.register(GetDictBoolean)
        registry.register(GetArrayFromDict)
        registry.register(GetDictFromDict)
        registry.register(GetDictOptInteger)
        registry.register(GetDictOptNumber)
        registry.register(GetDictOptString)
        registry.register(GetDictOptColorWithColorFallback)
        registry.register(GetDictOptColorWithStringFallback)
        registry.register(GetDictOptUrlWithStringFallback)
        registry.register(GetDictOptUrlWithUrlFallback)
        registry.register(GetDictOptBoolean)
        registry.register(GetOptArrayFromDict)
        registry.register(GetOptDictFromDict)

        // Dict functions
        registry.register(GetIntegerFromDict)
        registry.register(GetNumberFromDict)
        registry.register(GetStringFromDict)
        registry.register(GetColorFromDict)
        registry.register(GetUrlFromDict)
        registry.register(GetBooleanFromDict)
        registry.register(GetOptIntegerFromDict)
        registry.register(GetOptNumberFromDict)
        registry.register(GetOptStringFromDict)
        registry.register(GetOptColorFromDictWithColorFallback)
        registry.register(GetOptColorFromDictWithStringFallback)
        registry.register(GetOptUrlFromDictWithStringFallback)
        registry.register(GetOptUrlFromDictWithUrlFallback)
        registry.register(GetOptBooleanFromDict)

        // Array functions legacy
        registry.register(GetArrayInteger)
        registry.register(GetArrayNumber)
        registry.register(GetArrayString)
        registry.register(GetArrayColor)
        registry.register(GetArrayUrl)
        registry.register(GetArrayBoolean)
        registry.register(GetArrayOptInteger)
        registry.register(GetArrayOptNumber)
        registry.register(GetArrayOptString)
        registry.register(GetArrayOptColorWithColorFallback)
        registry.register(GetArrayOptColorWithStringFallback)
        registry.register(GetArrayOptUrlWithUrlFallback)
        registry.register(GetArrayOptUrlWithStringFallback)
        registry.register(GetArrayOptBoolean)

        // Array functions
        registry.register(GetIntegerFromArray)
        registry.register(GetNumberFromArray)
        registry.register(GetStringFromArray)
        registry.register(GetColorFromArray)
        registry.register(GetUrlFromArray)
        registry.register(GetBooleanFromArray)
        registry.register(GetArrayFromArray)
        registry.register(GetDictFromArray)
        registry.register(GetOptIntegerFromArray)
        registry.register(GetOptNumberFromArray)
        registry.register(GetOptStringFromArray)
        registry.register(GetOptColorFromArrayWithColorFallback)
        registry.register(GetOptColorFromArrayWithStringFallback)
        registry.register(GetOptUrlFromArrayWithUrlFallback)
        registry.register(GetOptUrlFromArrayWithStringFallback)
        registry.register(GetOptBooleanFromArray)
        registry.register(GetOptArrayFromArray)
        registry.register(GetOptDictFromArray)
        registry.register(GetArrayLength)
    }

    override fun get(name: String, args: List<EvaluableType>): Function {
        return registry.get(name, args)
    }

    internal fun ensureFunctionRegistered(name: String, args: List<FunctionArgument>, resultType: EvaluableType) {
        registry.ensureRegistered(name, args, resultType)
    }
}
