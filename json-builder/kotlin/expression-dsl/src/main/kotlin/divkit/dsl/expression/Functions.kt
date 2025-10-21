@file:Suppress("ktlint", "UNUSED_PARAMETER", "unused")

package divkit.dsl.expression

/**
 * Returns a string value from string.
 *
 * @param param0 String.
 * @return function expression
 */

@JvmName("toStringString")
fun toString(
    param0: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "toString",
        param0,
    )

/**
 * Returns a string representation from number.
 *
 * @param param0 Number.
 * @return function expression
 */

@JvmName("toStringDouble")
fun toString(
    param0: Expression<Double>,
): Expression<String> =
    FunctionExpression(
        "toString",
        param0,
    )

/**
 * Returns a string value from boolean.
 *
 * @param param0 Boolean.
 * @return function expression
 */

@JvmName("toStringBoolean")
fun toString(
    param0: Expression<Boolean>,
): Expression<String> =
    FunctionExpression(
        "toString",
        param0,
    )

/**
 * Returns a string value from array.
 *
 * @param param0 Array.
 * @return function expression
 */

@JvmName("toStringList")
fun toString(
    param0: Expression<out List<*>>,
): Expression<String> =
    FunctionExpression(
        "toString",
        param0,
    )

/**
 * Returns a string value from dict.
 *
 * @param param0 Dict.
 * @return function expression
 */

@JvmName("toStringMap")
fun toString(
    param0: Expression<out Map<*, *>>,
): Expression<String> =
    FunctionExpression(
        "toString",
        param0,
    )

/**
 * Returns a string value from array.
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @return function expression
 */

@JvmName("getArrayStringListInt")
fun getArrayString(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
): Expression<String> =
    FunctionExpression(
        "getArrayString",
        param0,
        param1,
    )

/**
 * Returns a number value from array.
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @return function expression
 */

@JvmName("getArrayNumberListInt")
fun getArrayNumber(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
): Expression<Double> =
    FunctionExpression(
        "getArrayNumber",
        param0,
        param1,
    )

/**
 * Returns an integer value from array.
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @return function expression
 */

@JvmName("getArrayIntegerListInt")
fun getArrayInteger(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "getArrayInteger",
        param0,
        param1,
    )

/**
 * Returns a boolean property from array.
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @return function expression
 */

@JvmName("getArrayBooleanListInt")
fun getArrayBoolean(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
): Expression<Boolean> =
    FunctionExpression(
        "getArrayBoolean",
        param0,
        param1,
    )

/**
 * Returns a color property from array.
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @return function expression
 */

@JvmName("getArrayColorListInt")
fun getArrayColor(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
): Expression<String> =
    FunctionExpression(
        "getArrayColor",
        param0,
        param1,
    )

/**
 * Returns an optional string property from array.
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @param param2 Fallback value if value by index not exists or it's is not a string.
 * @return function expression
 */

@JvmName("getArrayOptStringListIntString")
fun getArrayOptString(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
    param2: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getArrayOptString",
        param0,
        param1,
        param2,
    )

/**
 * Returns an optional number property from array.
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @param param2 Fallback value if value by index not exists or it's is not a number.
 * @return function expression
 */

@JvmName("getArrayOptNumberListIntDouble")
fun getArrayOptNumber(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
    param2: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "getArrayOptNumber",
        param0,
        param1,
        param2,
    )

/**
 * Returns an optional integer property from array.
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @param param2 Fallback value if property does not exist or a property value is not an integer.
 * @return function expression
 */

@JvmName("getArrayOptIntegerListIntInt")
fun getArrayOptInteger(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
    param2: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "getArrayOptInteger",
        param0,
        param1,
        param2,
    )

/**
 * Returns an optional boolean value from array.
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @param param2 Fallback value if value by index not exists or it's is not a boolean.
 * @return function expression
 */

@JvmName("getArrayOptBooleanListIntBoolean")
fun getArrayOptBoolean(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
    param2: Expression<Boolean>,
): Expression<Boolean> =
    FunctionExpression(
        "getArrayOptBoolean",
        param0,
        param1,
        param2,
    )

/**
 * Returns an optional color value from array.
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @param param2 Fallback value if value by index not exists or it's is not a color.
 * @return function expression
 */

@JvmName("getArrayOptColorListIntString")
fun getArrayOptColor(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
    param2: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getArrayOptColor",
        param0,
        param1,
        param2,
    )

/**
 * Returns an url value from array.
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @return function expression
 */

@JvmName("getArrayUrlListInt")
fun getArrayUrl(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
): Expression<String> =
    FunctionExpression(
        "getArrayUrl",
        param0,
        param1,
    )

/**
 * Returns an optional url value from array.
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @param param2 Fallback value if value by index not exists or it's is not a url.
 * @return function expression
 */

@JvmName("getArrayOptUrlListIntString")
fun getArrayOptUrl(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
    param2: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getArrayOptUrl",
        param0,
        param1,
        param2,
    )

/**
 * Returns a string value from array.
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @return function expression
 */

@JvmName("getStringFromArrayListInt")
fun getStringFromArray(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
): Expression<String> =
    FunctionExpression(
        "getStringFromArray",
        param0,
        param1,
    )

/**
 * Returns a number value from array.
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @return function expression
 */

@JvmName("getNumberFromArrayListInt")
fun getNumberFromArray(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
): Expression<Double> =
    FunctionExpression(
        "getNumberFromArray",
        param0,
        param1,
    )

/**
 * Returns an integer value from array.
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @return function expression
 */

@JvmName("getIntegerFromArrayListInt")
fun getIntegerFromArray(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "getIntegerFromArray",
        param0,
        param1,
    )

/**
 * Returns a boolean property from array.
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @return function expression
 */

@JvmName("getBooleanFromArrayListInt")
fun getBooleanFromArray(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
): Expression<Boolean> =
    FunctionExpression(
        "getBooleanFromArray",
        param0,
        param1,
    )

/**
 * Returns a color property from array.
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @return function expression
 */

@JvmName("getColorFromArrayListInt")
fun getColorFromArray(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
): Expression<String> =
    FunctionExpression(
        "getColorFromArray",
        param0,
        param1,
    )

/**
 * Returns an optional string property from array.
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @param param2 Fallback value if value by index not exists or it's is not a string.
 * @return function expression
 */

@JvmName("getOptStringFromArrayListIntString")
fun getOptStringFromArray(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
    param2: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getOptStringFromArray",
        param0,
        param1,
        param2,
    )

/**
 * Returns an optional number property from array.
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @param param2 Fallback value if value by index not exists or it's is not a number.
 * @return function expression
 */

@JvmName("getOptNumberFromArrayListIntDouble")
fun getOptNumberFromArray(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
    param2: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "getOptNumberFromArray",
        param0,
        param1,
        param2,
    )

/**
 * Returns an optional integer property from array.
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @param param2 Fallback value if property does not exist or a property value is not an integer.
 * @return function expression
 */

@JvmName("getOptIntegerFromArrayListIntInt")
fun getOptIntegerFromArray(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
    param2: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "getOptIntegerFromArray",
        param0,
        param1,
        param2,
    )

/**
 * Returns an optional boolean value from array.
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @param param2 Fallback value if value by index not exists or it's is not a boolean.
 * @return function expression
 */

@JvmName("getOptBooleanFromArrayListIntBoolean")
fun getOptBooleanFromArray(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
    param2: Expression<Boolean>,
): Expression<Boolean> =
    FunctionExpression(
        "getOptBooleanFromArray",
        param0,
        param1,
        param2,
    )

/**
 * Returns an optional color value from array.
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @param param2 Fallback value if value by index not exists or it's is not a color.
 * @return function expression
 */

@JvmName("getOptColorFromArrayListIntString")
fun getOptColorFromArray(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
    param2: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getOptColorFromArray",
        param0,
        param1,
        param2,
    )

/**
 * Returns an url value from array.
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @return function expression
 */

@JvmName("getUrlFromArrayListInt")
fun getUrlFromArray(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
): Expression<String> =
    FunctionExpression(
        "getUrlFromArray",
        param0,
        param1,
    )

/**
 * Returns an optional url value from array.
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @param param2 Fallback value if value by index not exists or it's is not a url.
 * @return function expression
 */

@JvmName("getOptUrlFromArrayListIntString")
fun getOptUrlFromArray(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
    param2: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getOptUrlFromArray",
        param0,
        param1,
        param2,
    )

/**
 * Returns an array value from array
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @return function expression
 */

@JvmName("getArrayFromArrayListInt")
fun getArrayFromArray(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
): Expression<List<*>> =
    FunctionExpression(
        "getArrayFromArray",
        param0,
        param1,
    )

/**
 * Returns an array value from array if exist or empty array otherwise
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @return function expression
 */

@JvmName("getOptArrayFromArrayListInt")
fun getOptArrayFromArray(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
): Expression<List<*>> =
    FunctionExpression(
        "getOptArrayFromArray",
        param0,
        param1,
    )

/**
 * Returns an dict value from array
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @return function expression
 */

@JvmName("getDictFromArrayListInt")
fun getDictFromArray(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
): Expression<Map<*, *>> =
    FunctionExpression(
        "getDictFromArray",
        param0,
        param1,
    )

/**
 * Returns an dict value from array if exist or empty dict otherwise
 *
 * @param param0 Array.
 * @param param1 Index at array.
 * @return function expression
 */

@JvmName("getOptDictFromArrayListInt")
fun getOptDictFromArray(
    param0: Expression<out List<*>>,
    param1: Expression<Long>,
): Expression<Map<*, *>> =
    FunctionExpression(
        "getOptDictFromArray",
        param0,
        param1,
    )

/**
 * Gets the length of array. Returns integer value.
 *
 * @param param0 Array value to get length of.
 * @return function expression
 */

@JvmName("lenList")
fun len(
    param0: Expression<out List<*>>,
): Expression<Long> =
    FunctionExpression(
        "len",
        param0,
    )

/**
 * Gets the length of array. Returns integer value.
 *
 * @receiver Array value to get length of.
 * @return function expression
 */

@JvmName("extension_lenList")
fun Expression<out List<*>>.len(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = len(this)

/**
 * Gets alpha value of argument color. Returns number value in range 0.0 to 1.0 of alpha value of color.
 *
 * @param param0 String value of color in hex format.
 * @return function expression
 */

@JvmName("getColorAlphaString")
fun getColorAlpha(
    param0: Expression<String>,
): Expression<Double> =
    FunctionExpression(
        "getColorAlpha",
        param0,
    )

/**
 * Gets alpha value of argument color. Returns number value in range 0.0 to 1.0 of alpha value of color.
 *
 * @receiver String value of color in hex format.
 * @return function expression
 */

@JvmName("extension_getColorAlphaString")
fun Expression<String>.getColorAlpha(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getColorAlpha(this)

/**
 * Gets red value of argument color. Returns number value in range 0.0 to 1.0 of red value of color.
 *
 * @param param0 String value of color in hex format.
 * @return function expression
 */

@JvmName("getColorRedString")
fun getColorRed(
    param0: Expression<String>,
): Expression<Double> =
    FunctionExpression(
        "getColorRed",
        param0,
    )

/**
 * Gets red value of argument color. Returns number value in range 0.0 to 1.0 of red value of color.
 *
 * @receiver String value of color in hex format.
 * @return function expression
 */

@JvmName("extension_getColorRedString")
fun Expression<String>.getColorRed(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getColorRed(this)

/**
 * Gets green value of argument color. Returns number value in range 0.0 to 1.0 of green value of color.
 *
 * @param param0 String value of color in hex format.
 * @return function expression
 */

@JvmName("getColorGreenString")
fun getColorGreen(
    param0: Expression<String>,
): Expression<Double> =
    FunctionExpression(
        "getColorGreen",
        param0,
    )

/**
 * Gets green value of argument color. Returns number value in range 0.0 to 1.0 of green value of color.
 *
 * @receiver String value of color in hex format.
 * @return function expression
 */

@JvmName("extension_getColorGreenString")
fun Expression<String>.getColorGreen(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getColorGreen(this)

/**
 * Gets blue value of argument color. Returns number value in range 0.0 to 1.0 of blue value of color.
 *
 * @param param0 String value of color in hex format.
 * @return function expression
 */

@JvmName("getColorBlueString")
fun getColorBlue(
    param0: Expression<String>,
): Expression<Double> =
    FunctionExpression(
        "getColorBlue",
        param0,
    )

/**
 * Gets blue value of argument color. Returns number value in range 0.0 to 1.0 of blue value of color.
 *
 * @receiver String value of color in hex format.
 * @return function expression
 */

@JvmName("extension_getColorBlueString")
fun Expression<String>.getColorBlue(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getColorBlue(this)

/**
 * Sets alpha value of argument color. Returns string value of color in hex format '#FFAABBCC'.
 *
 * @param param0 String value of color in hex format.
 * @param param1 Number value of alpha in range 0.0 to 1.0.
 * @return function expression
 */

@JvmName("setColorAlphaStringDouble")
fun setColorAlpha(
    param0: Expression<String>,
    param1: Expression<Double>,
): Expression<String> =
    FunctionExpression(
        "setColorAlpha",
        param0,
        param1,
    )

/**
 * Sets red value of argument color. Returns string value of color in hex format '#FFAABBCC'.
 *
 * @param param0 String value of color in hex format.
 * @param param1 Number value of red in range 0.0 to 1.0.
 * @return function expression
 */

@JvmName("setColorRedStringDouble")
fun setColorRed(
    param0: Expression<String>,
    param1: Expression<Double>,
): Expression<String> =
    FunctionExpression(
        "setColorRed",
        param0,
        param1,
    )

/**
 * Sets green value of argument color. Returns string value of color in hex format '#FFAABBCC'.
 *
 * @param param0 String value of color in hex format.
 * @param param1 Number value of green in range 0.0 to 1.0.
 * @return function expression
 */

@JvmName("setColorGreenStringDouble")
fun setColorGreen(
    param0: Expression<String>,
    param1: Expression<Double>,
): Expression<String> =
    FunctionExpression(
        "setColorGreen",
        param0,
        param1,
    )

/**
 * Sets blue value of argument color. Returns string value of color in hex format '#FFAABBCC'.
 *
 * @param param0 String value of color in hex format.
 * @param param1 Number value of blue in range 0.0 to 1.0.
 * @return function expression
 */

@JvmName("setColorBlueStringDouble")
fun setColorBlue(
    param0: Expression<String>,
    param1: Expression<Double>,
): Expression<String> =
    FunctionExpression(
        "setColorBlue",
        param0,
        param1,
    )

/**
 * Creates color from values of alpha, red, green, blue values. Returns string value of color in hex format '#FFAABBCC'.
 *
 * @param param0 Number value of alpha in range 0.0 to 1.0.
 * @param param1 Number value of red in range 0.0 to 1.0.
 * @param param2 Number value of green in range 0.0 to 1.0.
 * @param param3 Number value of blue in range 0.0 to 1.0.
 * @return function expression
 */

@JvmName("argbDoubleDoubleDoubleDouble")
fun argb(
    param0: Expression<Double>,
    param1: Expression<Double>,
    param2: Expression<Double>,
    param3: Expression<Double>,
): Expression<String> =
    FunctionExpression(
        "argb",
        param0,
        param1,
        param2,
        param3,
    )

/**
 * Creates color from values of red, green, blue values. Sets alpha value as 1.0. Returns color value of color in hex format '#FFAABBCC'.
 *
 * @param param0 Number value of red in range 0.0 to 1.0.
 * @param param1 Number value of green in range 0.0 to 1.0.
 * @param param2 Number value of blue in range 0.0 to 1.0.
 * @return function expression
 */

@JvmName("rgbDoubleDoubleDouble")
fun rgb(
    param0: Expression<Double>,
    param1: Expression<Double>,
    param2: Expression<Double>,
): Expression<String> =
    FunctionExpression(
        "rgb",
        param0,
        param1,
        param2,
    )

/**
 * Creates datetime from integer value of unix time. Returns datetime value.
 *
 * @param param0 Time in UTC seconds from the epoch.
 * @return function expression
 */

@JvmName("parseUnixTimeInt")
fun parseUnixTime(
    param0: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "parseUnixTime",
        param0,
    )

/**
 * Creates datetime from integer value of unix time. Returns datetime value.
 *
 * @receiver Time in UTC seconds from the epoch.
 * @return function expression
 */

@JvmName("extension_parseUnixTimeInt")
fun Expression<Long>.parseUnixTime(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = parseUnixTime(this)

/**
 * Creates datetime in local timezone from integer value of unix time. Returns datetime value.
 *
 * @param param0 Time in local time zone seconds from the epoch.
 * @return function expression
 */

@JvmName("parseUnixTimeAsLocalInt")
fun parseUnixTimeAsLocal(
    param0: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "parseUnixTimeAsLocal",
        param0,
    )

/**
 * Creates datetime in local timezone from integer value of unix time. Returns datetime value.
 *
 * @receiver Time in local time zone seconds from the epoch.
 * @return function expression
 */

@JvmName("extension_parseUnixTimeAsLocalInt")
fun Expression<Long>.parseUnixTimeAsLocal(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = parseUnixTimeAsLocal(this)

/**
 * Creates datetime from time of now. Returns datetime value.
 *
 * @return function expression
 */

@JvmName("nowLocal")
fun nowLocal(
): Expression<Long> =
    FunctionExpression(
        "nowLocal",
    )

/**
 * Adds milliseconds to original date. Returns datetime with added time in milliseconds.
 *
 * @param param0 Datetime value, date to add milliseconds.
 * @param param1 Integer value, count of milliseconds to add.
 * @return function expression
 */

@JvmName("addMillisIntInt")
fun addMillis(
    param0: Expression<Long>,
    param1: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "addMillis",
        param0,
        param1,
    )

/**
 * Sets year number to original date. Returns datetime with year set.
 *
 * @param param0 Datetime value, date to set year.
 * @param param1 Integer value, year to set to date.
 * @return function expression
 */

@JvmName("setYearIntInt")
fun setYear(
    param0: Expression<Long>,
    param1: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "setYear",
        param0,
        param1,
    )

/**
 * Sets month number to original date. Returns datetime with month set.
 *
 * @param param0 Datetime value, date to set month.
 * @param param1 Integer value, month to set to date. Expected values from 1 to 12.
 * @return function expression
 */

@JvmName("setMonthIntInt")
fun setMonth(
    param0: Expression<Long>,
    param1: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "setMonth",
        param0,
        param1,
    )

/**
 * Sets day of month number to original date. Returns datetime with day of month set.
 *
 * @param param0 Datetime value, date to set day of month.
 * @param param1 Integer value, day of month to set to date.
 * @return function expression
 */

@JvmName("setDayIntInt")
fun setDay(
    param0: Expression<Long>,
    param1: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "setDay",
        param0,
        param1,
    )

/**
 * Sets hours number to original date. Returns datetime with hours set.
 *
 * @param param0 Datetime value, date to set hours.
 * @param param1 Integer value, hours to set to date.
 * @return function expression
 */

@JvmName("setHoursIntInt")
fun setHours(
    param0: Expression<Long>,
    param1: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "setHours",
        param0,
        param1,
    )

/**
 * Sets minutes number to original date. Returns datetime with minutes set.
 *
 * @param param0 Datetime value, date to set minutes.
 * @param param1 Integer value, minutes to set to date.
 * @return function expression
 */

@JvmName("setMinutesIntInt")
fun setMinutes(
    param0: Expression<Long>,
    param1: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "setMinutes",
        param0,
        param1,
    )

/**
 * Sets seconds number to original date. Returns datetime with seconds set.
 *
 * @param param0 Datetime value, date to set seconds.
 * @param param1 Integer value, seconds to set to date.
 * @return function expression
 */

@JvmName("setSecondsIntInt")
fun setSeconds(
    param0: Expression<Long>,
    param1: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "setSeconds",
        param0,
        param1,
    )

/**
 * Sets milliseconds number to original date. Returns datetime with milliseconds set.
 *
 * @param param0 Datetime value, date to set milliseconds.
 * @param param1 Integer value, milliseconds to set to date.
 * @return function expression
 */

@JvmName("setMillisIntInt")
fun setMillis(
    param0: Expression<Long>,
    param1: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "setMillis",
        param0,
        param1,
    )

/**
 * Returns year number of original date.
 *
 * @param param0 Datetime value, date to get year.
 * @return function expression
 */

@JvmName("getYearInt")
fun getYear(
    param0: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "getYear",
        param0,
    )

/**
 * Returns year number of original date.
 *
 * @receiver Datetime value, date to get year.
 * @return function expression
 */

@JvmName("extension_getYearInt")
fun Expression<Long>.getYear(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getYear(this)

/**
 * Returns month number of original date.
 *
 * @param param0 Datetime value, date to get month.
 * @return function expression
 */

@JvmName("getMonthInt")
fun getMonth(
    param0: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "getMonth",
        param0,
    )

/**
 * Returns month number of original date.
 *
 * @receiver Datetime value, date to get month.
 * @return function expression
 */

@JvmName("extension_getMonthInt")
fun Expression<Long>.getMonth(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getMonth(this)

/**
 * Returns day of month of original date.
 *
 * @param param0 Datetime value, date to get day of month.
 * @return function expression
 */

@JvmName("getDayInt")
fun getDay(
    param0: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "getDay",
        param0,
    )

/**
 * Returns day of month of original date.
 *
 * @receiver Datetime value, date to get day of month.
 * @return function expression
 */

@JvmName("extension_getDayInt")
fun Expression<Long>.getDay(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getDay(this)

/**
 * Returns day of week of original date, where Sunday is 7.
 *
 * @param param0 Datetime value, date to get day of week.
 * @return function expression
 */

@JvmName("getDayOfWeekInt")
fun getDayOfWeek(
    param0: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "getDayOfWeek",
        param0,
    )

/**
 * Returns day of week of original date, where Sunday is 7.
 *
 * @receiver Datetime value, date to get day of week.
 * @return function expression
 */

@JvmName("extension_getDayOfWeekInt")
fun Expression<Long>.getDayOfWeek(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getDayOfWeek(this)

/**
 * Returns hours number of original date.
 *
 * @param param0 Datetime value, date to get hours.
 * @return function expression
 */

@JvmName("getHoursInt")
fun getHours(
    param0: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "getHours",
        param0,
    )

/**
 * Returns hours number of original date.
 *
 * @receiver Datetime value, date to get hours.
 * @return function expression
 */

@JvmName("extension_getHoursInt")
fun Expression<Long>.getHours(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getHours(this)

/**
 * Returns minutes number of original date.
 *
 * @param param0 Datetime value, date to get minutes.
 * @return function expression
 */

@JvmName("getMinutesInt")
fun getMinutes(
    param0: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "getMinutes",
        param0,
    )

/**
 * Returns minutes number of original date.
 *
 * @receiver Datetime value, date to get minutes.
 * @return function expression
 */

@JvmName("extension_getMinutesInt")
fun Expression<Long>.getMinutes(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getMinutes(this)

/**
 * Returns seconds number of original date.
 *
 * @param param0 Datetime value, date to get seconds.
 * @return function expression
 */

@JvmName("getSecondsInt")
fun getSeconds(
    param0: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "getSeconds",
        param0,
    )

/**
 * Returns seconds number of original date.
 *
 * @receiver Datetime value, date to get seconds.
 * @return function expression
 */

@JvmName("extension_getSecondsInt")
fun Expression<Long>.getSeconds(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getSeconds(this)

/**
 * Returns milliseconds number of original date.
 *
 * @param param0 Datetime value, date to get milliseconds.
 * @return function expression
 */

@JvmName("getMillisInt")
fun getMillis(
    param0: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "getMillis",
        param0,
    )

/**
 * Returns milliseconds number of original date.
 *
 * @receiver Datetime value, date to get milliseconds.
 * @return function expression
 */

@JvmName("extension_getMillisInt")
fun Expression<Long>.getMillis(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getMillis(this)

/**
 * Formatting dates in a local timezone in users locale. Used default locale. Look https://en.wikipedia.org/wiki/ISO_8601 for more. `Z` for timezones not supported.
 *
 * @param param0 Datetime value, date to get milliseconds.
 * @param param1 Date and time formats are specified by date and time pattern strings.
 * @return function expression
 */

@JvmName("formatDateAsLocalIntString")
fun formatDateAsLocal(
    param0: Expression<Long>,
    param1: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "formatDateAsLocal",
        param0,
        param1,
    )

/**
 * Formatting dates in a UTC timezone in users locale. Used default locale. Look https://en.wikipedia.org/wiki/ISO_8601 for more. `Z` for timezones not supported.
 *
 * @param param0 Datetime value, date to get milliseconds.
 * @param param1 Date and time formats are specified by date and time pattern strings.
 * @return function expression
 */

@JvmName("formatDateAsUTCIntString")
fun formatDateAsUTC(
    param0: Expression<Long>,
    param1: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "formatDateAsUTC",
        param0,
        param1,
    )

/**
 * Formatting dates in a local timezone with localization. Look https://en.wikipedia.org/wiki/ISO_8601 for more. `Z` for timezones not supported.
 *
 * @param param0 Datetime value, date to get milliseconds.
 * @param param1 Date and time formats are specified by date and time pattern strings.
 * @param param2 Language for specified date and time pattern strings.
 * @return function expression
 */

@JvmName("formatDateAsLocalWithLocaleIntStringString")
fun formatDateAsLocalWithLocale(
    param0: Expression<Long>,
    param1: Expression<String>,
    param2: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "formatDateAsLocalWithLocale",
        param0,
        param1,
        param2,
    )

/**
 * Formatting dates in a UTC timezone with localization. Look https://en.wikipedia.org/wiki/ISO_8601 for more. `Z` for timezones not supported.
 *
 * @param param0 Datetime value, date to get milliseconds.
 * @param param1 Date and time formats are specified by date and time pattern strings.
 * @param param2 Language for specified date and time pattern strings.
 * @return function expression
 */

@JvmName("formatDateAsUTCWithLocaleIntStringString")
fun formatDateAsUTCWithLocale(
    param0: Expression<Long>,
    param1: Expression<String>,
    param2: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "formatDateAsUTCWithLocale",
        param0,
        param1,
        param2,
    )

/**
 * Returns a string value from dictionary.
 *
 * @param param0 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getDictStringMapString")
fun getDictString(
    param0: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getDictString",
        param0,
        *varargs,
    )

/**
 * Returns a number value from dictionary.
 *
 * @param param0 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getDictNumberMapString")
fun getDictNumber(
    param0: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<Double> =
    FunctionExpression(
        "getDictNumber",
        param0,
        *varargs,
    )

/**
 * Returns an integer value from dictionary.
 *
 * @param param0 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getDictIntegerMapString")
fun getDictInteger(
    param0: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<Long> =
    FunctionExpression(
        "getDictInteger",
        param0,
        *varargs,
    )

/**
 * Returns a boolean property from dictionary.
 *
 * @param param0 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getDictBooleanMapString")
fun getDictBoolean(
    param0: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<Boolean> =
    FunctionExpression(
        "getDictBoolean",
        param0,
        *varargs,
    )

/**
 * Returns a color property from dictionary.
 *
 * @param param0 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getDictColorMapString")
fun getDictColor(
    param0: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getDictColor",
        param0,
        *varargs,
    )

/**
 * Returns an url property from dictionary.
 *
 * @param param0 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getDictUrlMapString")
fun getDictUrl(
    param0: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getDictUrl",
        param0,
        *varargs,
    )

/**
 * Returns an optional string property from dictionary.
 *
 * @param param0 Fallback value if property does not exist or a property value is not a string.
 * @param param1 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getDictOptStringStringMapString")
fun getDictOptString(
    param0: Expression<String>,
    param1: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getDictOptString",
        param0,
        param1,
        *varargs,
    )

/**
 * Returns an optional number property from dictionary.
 *
 * @param param0 Fallback value if property does not exist or a property value is not a number.
 * @param param1 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getDictOptNumberDoubleMapString")
fun getDictOptNumber(
    param0: Expression<Double>,
    param1: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<Double> =
    FunctionExpression(
        "getDictOptNumber",
        param0,
        param1,
        *varargs,
    )

/**
 * Returns an optional integer property from dictionary.
 *
 * @param param0 Fallback value if property does not exist or a property value is not an integer.
 * @param param1 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getDictOptIntegerIntMapString")
fun getDictOptInteger(
    param0: Expression<Long>,
    param1: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<Long> =
    FunctionExpression(
        "getDictOptInteger",
        param0,
        param1,
        *varargs,
    )

/**
 * Returns an optional boolean property from dictionary.
 *
 * @param param0 Fallback value if property does not exist or a property value is not a boolean.
 * @param param1 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getDictOptBooleanBooleanMapString")
fun getDictOptBoolean(
    param0: Expression<Boolean>,
    param1: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<Boolean> =
    FunctionExpression(
        "getDictOptBoolean",
        param0,
        param1,
        *varargs,
    )

/**
 * Returns an optional color property from dictionary.
 *
 * @param param0 Fallback value if property does not exist or a property value is not a color.
 * @param param1 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getDictOptColorStringMapString")
fun getDictOptColor(
    param0: Expression<String>,
    param1: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getDictOptColor",
        param0,
        param1,
        *varargs,
    )

/**
 * Returns an optional url property from dictionary.
 *
 * @param param0 Fallback value if property does not exist or a property value is not an url.
 * @param param1 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getDictOptUrlStringMapString")
fun getDictOptUrl(
    param0: Expression<String>,
    param1: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getDictOptUrl",
        param0,
        param1,
        *varargs,
    )

/**
 * Returns a string value from dictionary.
 *
 * @param param0 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getStringFromDictMapString")
fun getStringFromDict(
    param0: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getStringFromDict",
        param0,
        *varargs,
    )

/**
 * Returns a number value from dictionary.
 *
 * @param param0 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getNumberFromDictMapString")
fun getNumberFromDict(
    param0: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<Double> =
    FunctionExpression(
        "getNumberFromDict",
        param0,
        *varargs,
    )

/**
 * Returns an integer value from dictionary.
 *
 * @param param0 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getIntegerFromDictMapString")
fun getIntegerFromDict(
    param0: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<Long> =
    FunctionExpression(
        "getIntegerFromDict",
        param0,
        *varargs,
    )

/**
 * Returns a boolean property from dictionary.
 *
 * @param param0 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getBooleanFromDictMapString")
fun getBooleanFromDict(
    param0: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<Boolean> =
    FunctionExpression(
        "getBooleanFromDict",
        param0,
        *varargs,
    )

/**
 * Returns a color property from dictionary.
 *
 * @param param0 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getColorFromDictMapString")
fun getColorFromDict(
    param0: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getColorFromDict",
        param0,
        *varargs,
    )

/**
 * Returns an url property from dictionary.
 *
 * @param param0 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getUrlFromDictMapString")
fun getUrlFromDict(
    param0: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getUrlFromDict",
        param0,
        *varargs,
    )

/**
 * Returns an dict property from dictionary.
 *
 * @param param0 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getDictFromDictMapString")
fun getDictFromDict(
    param0: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<Map<*, *>> =
    FunctionExpression(
        "getDictFromDict",
        param0,
        *varargs,
    )

/**
 * Returns an array property from dictionary.
 *
 * @param param0 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getArrayFromDictMapString")
fun getArrayFromDict(
    param0: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<List<*>> =
    FunctionExpression(
        "getArrayFromDict",
        param0,
        *varargs,
    )

/**
 * Returns an optional string property from dictionary.
 *
 * @param param0 Fallback value if property does not exist or a property value is not a string.
 * @param param1 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getOptStringFromDictStringMapString")
fun getOptStringFromDict(
    param0: Expression<String>,
    param1: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getOptStringFromDict",
        param0,
        param1,
        *varargs,
    )

/**
 * Returns an optional number property from dictionary.
 *
 * @param param0 Fallback value if property does not exist or a property value is not a number.
 * @param param1 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getOptNumberFromDictDoubleMapString")
fun getOptNumberFromDict(
    param0: Expression<Double>,
    param1: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<Double> =
    FunctionExpression(
        "getOptNumberFromDict",
        param0,
        param1,
        *varargs,
    )

/**
 * Returns an optional integer property from dictionary.
 *
 * @param param0 Fallback value if property does not exist or a property value is not an integer.
 * @param param1 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getOptIntegerFromDictIntMapString")
fun getOptIntegerFromDict(
    param0: Expression<Long>,
    param1: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<Long> =
    FunctionExpression(
        "getOptIntegerFromDict",
        param0,
        param1,
        *varargs,
    )

/**
 * Returns an optional boolean property from dictionary.
 *
 * @param param0 Fallback value if property does not exist or a property value is not a boolean.
 * @param param1 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getOptBooleanFromDictBooleanMapString")
fun getOptBooleanFromDict(
    param0: Expression<Boolean>,
    param1: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<Boolean> =
    FunctionExpression(
        "getOptBooleanFromDict",
        param0,
        param1,
        *varargs,
    )

/**
 * Returns an optional color property from dictionary.
 *
 * @param param0 Fallback value if property does not exist or a property value is not a color.
 * @param param1 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getOptColorFromDictStringMapString")
fun getOptColorFromDict(
    param0: Expression<String>,
    param1: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getOptColorFromDict",
        param0,
        param1,
        *varargs,
    )

/**
 * Returns an optional url property from dictionary.
 *
 * @param param0 Fallback value if property does not exist or a property value is not an url.
 * @param param1 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getOptUrlFromDictStringMapString")
fun getOptUrlFromDict(
    param0: Expression<String>,
    param1: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getOptUrlFromDict",
        param0,
        param1,
        *varargs,
    )

/**
 * Returns an array value from dict if exist or empty array otherwise.
 *
 * @param param0 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getOptArrayFromDictMapString")
fun getOptArrayFromDict(
    param0: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<List<*>> =
    FunctionExpression(
        "getOptArrayFromDict",
        param0,
        *varargs,
    )

/**
 * Returns an dict value from dict if exist or empty dict otherwise.
 *
 * @param param0 Dictionary.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getOptDictFromDictMapString")
fun getOptDictFromDict(
    param0: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<Map<*, *>> =
    FunctionExpression(
        "getOptDictFromDict",
        param0,
        *varargs,
    )

/**
 * Counts the number of elements in a dictionary.
 *
 * @param param0 Dictionary.
 * @return function expression
 */

@JvmName("lenMap")
fun len(
    param0: Expression<out Map<*, *>>,
): Expression<Long> =
    FunctionExpression(
        "len",
        param0,
    )

/**
 * Counts the number of elements in a dictionary.
 *
 * @receiver Dictionary.
 * @return function expression
 */

@JvmName("extension_lenMap")
fun Expression<out Map<*, *>>.len(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = len(this)

/**
 * Return a collection containing just the keys of the dictionary.
 *
 * @param param0 Dictionary.
 * @return function expression
 */

@JvmName("getDictKeysMap")
fun getDictKeys(
    param0: Expression<out Map<*, *>>,
): Expression<List<*>> =
    FunctionExpression(
        "getDictKeys",
        param0,
    )

/**
 * Return a collection containing just the keys of the dictionary.
 *
 * @receiver Dictionary.
 * @return function expression
 */

@JvmName("extension_getDictKeysMap")
fun Expression<out Map<*, *>>.getDictKeys(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getDictKeys(this)

/**
 * Return a collection containing just the values of the dictionary.
 *
 * @param param0 Dictionary.
 * @return function expression
 */

@JvmName("getDictValuesMap")
fun getDictValues(
    param0: Expression<out Map<*, *>>,
): Expression<List<*>> =
    FunctionExpression(
        "getDictValues",
        param0,
    )

/**
 * Return a collection containing just the values of the dictionary.
 *
 * @receiver Dictionary.
 * @return function expression
 */

@JvmName("extension_getDictValuesMap")
fun Expression<out Map<*, *>>.getDictValues(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getDictValues(this)

/**
 * Gets the seconds component of the duration. Expressed as a value between 0 and 59.
 *
 * @param param0 Milliseconds count
 * @return function expression
 */

@JvmName("getIntervalSecondsInt")
fun getIntervalSeconds(
    param0: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "getIntervalSeconds",
        param0,
    )

/**
 * Gets the seconds component of the duration. Expressed as a value between 0 and 59.
 *
 * @receiver Milliseconds count
 * @return function expression
 */

@JvmName("extension_getIntervalSecondsInt")
fun Expression<Long>.getIntervalSeconds(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getIntervalSeconds(this)

/**
 * Gets the number of seconds in duration.
 *
 * @param param0 Milliseconds count
 * @return function expression
 */

@JvmName("getIntervalTotalSecondsInt")
fun getIntervalTotalSeconds(
    param0: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "getIntervalTotalSeconds",
        param0,
    )

/**
 * Gets the number of seconds in duration.
 *
 * @receiver Milliseconds count
 * @return function expression
 */

@JvmName("extension_getIntervalTotalSecondsInt")
fun Expression<Long>.getIntervalTotalSeconds(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getIntervalTotalSeconds(this)

/**
 * Gets the minutes component of the duration. Expressed as a value between 0 and 59.
 *
 * @param param0 Milliseconds count
 * @return function expression
 */

@JvmName("getIntervalMinutesInt")
fun getIntervalMinutes(
    param0: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "getIntervalMinutes",
        param0,
    )

/**
 * Gets the minutes component of the duration. Expressed as a value between 0 and 59.
 *
 * @receiver Milliseconds count
 * @return function expression
 */

@JvmName("extension_getIntervalMinutesInt")
fun Expression<Long>.getIntervalMinutes(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getIntervalMinutes(this)

/**
 * Gets the number of minutes in duration.
 *
 * @param param0 Milliseconds count
 * @return function expression
 */

@JvmName("getIntervalTotalMinutesInt")
fun getIntervalTotalMinutes(
    param0: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "getIntervalTotalMinutes",
        param0,
    )

/**
 * Gets the number of minutes in duration.
 *
 * @receiver Milliseconds count
 * @return function expression
 */

@JvmName("extension_getIntervalTotalMinutesInt")
fun Expression<Long>.getIntervalTotalMinutes(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getIntervalTotalMinutes(this)

/**
 * Gets the hours component of the duration. Expressed as a value between 0 and 23.
 *
 * @param param0 Milliseconds count
 * @return function expression
 */

@JvmName("getIntervalHoursInt")
fun getIntervalHours(
    param0: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "getIntervalHours",
        param0,
    )

/**
 * Gets the hours component of the duration. Expressed as a value between 0 and 23.
 *
 * @receiver Milliseconds count
 * @return function expression
 */

@JvmName("extension_getIntervalHoursInt")
fun Expression<Long>.getIntervalHours(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getIntervalHours(this)

/**
 * Gets the number of hours in duration.
 *
 * @param param0 Milliseconds count
 * @return function expression
 */

@JvmName("getIntervalTotalHoursInt")
fun getIntervalTotalHours(
    param0: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "getIntervalTotalHours",
        param0,
    )

/**
 * Gets the number of hours in duration.
 *
 * @receiver Milliseconds count
 * @return function expression
 */

@JvmName("extension_getIntervalTotalHoursInt")
fun Expression<Long>.getIntervalTotalHours(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getIntervalTotalHours(this)

/**
 * Gets the number of days in duration.
 *
 * @param param0 Milliseconds count
 * @return function expression
 */

@JvmName("getIntervalTotalDaysInt")
fun getIntervalTotalDays(
    param0: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "getIntervalTotalDays",
        param0,
    )

/**
 * Gets the number of days in duration.
 *
 * @receiver Milliseconds count
 * @return function expression
 */

@JvmName("extension_getIntervalTotalDaysInt")
fun Expression<Long>.getIntervalTotalDays(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getIntervalTotalDays(this)

/**
 * Gets the number of weeks in duration.
 *
 * @param param0 Milliseconds count
 * @return function expression
 */

@JvmName("getIntervalTotalWeeksInt")
fun getIntervalTotalWeeks(
    param0: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "getIntervalTotalWeeks",
        param0,
    )

/**
 * Gets the number of weeks in duration.
 *
 * @receiver Milliseconds count
 * @return function expression
 */

@JvmName("extension_getIntervalTotalWeeksInt")
fun Expression<Long>.getIntervalTotalWeeks(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getIntervalTotalWeeks(this)

/**
 * Calculates division between the first number and the second. Returns integer quotient of division.
 *
 * @param param0 Divisible integer value.
 * @param param1 Divisor integer value.
 * @return function expression
 */

@JvmName("divIntInt")
fun div(
    param0: Expression<Long>,
    param1: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "div",
        param0,
        param1,
    )

/**
 * Calculates division between the first number and the second. Returns number quotient of division.
 *
 * @param param0 Divisible number value.
 * @param param1 Divisor number value.
 * @return function expression
 */

@JvmName("divDoubleDouble")
fun div(
    param0: Expression<Double>,
    param1: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "div",
        param0,
        param1,
    )

/**
 * Calculates remainder of division the first number and the second. Returns integer remainder of division.
 *
 * @param param0 Divisible integer value.
 * @param param1 Divisor integer value.
 * @return function expression
 */

@JvmName("modIntInt")
fun mod(
    param0: Expression<Long>,
    param1: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "mod",
        param0,
        param1,
    )

/**
 * Calculates remainder of division the first number and the second. Returns number remainder of division.
 *
 * @param param0 Divisible number value.
 * @param param1 Divisor number value.
 * @return function expression
 */

@JvmName("modDoubleDouble")
fun mod(
    param0: Expression<Double>,
    param1: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "mod",
        param0,
        param1,
    )

/**
 * Calculates multiplication between argument values. Returns integer result of multiplication.
 *
 * @param varargs Integer value to multiply.
 * @return function expression
 */

@JvmName("mulInt")
fun mul(
    vararg varargs: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "mul",
        *varargs,
    )

/**
 * Calculates multiplication between argument values. Returns number result of multiplication.
 *
 * @param varargs Number value to multiply.
 * @return function expression
 */

@JvmName("mulDouble")
fun mul(
    vararg varargs: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "mul",
        *varargs,
    )

/**
 * Calculates subtraction between argument values. Returns integer result of subtraction.
 *
 * @param varargs Integer value to subtract.
 * @return function expression
 */

@JvmName("subInt")
fun sub(
    vararg varargs: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "sub",
        *varargs,
    )

/**
 * Calculates subtraction between argument values. Returns number result of subtraction.
 *
 * @param varargs Number value to subtract.
 * @return function expression
 */

@JvmName("subDouble")
fun sub(
    vararg varargs: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "sub",
        *varargs,
    )

/**
 * Calculates sum between argument values. Returns integer result of sum.
 *
 * @param varargs Integer value to sum.
 * @return function expression
 */

@JvmName("sumInt")
fun sum(
    vararg varargs: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "sum",
        *varargs,
    )

/**
 * Calculates sum between argument values. Returns number result of sum.
 *
 * @param varargs Number value to sum.
 * @return function expression
 */

@JvmName("sumDouble")
fun sum(
    vararg varargs: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "sum",
        *varargs,
    )

/**
 * Gets max value of integer type. Returns max integer value.
 *
 * @return function expression
 */

@JvmName("maxInteger")
fun maxInteger(
): Expression<Long> =
    FunctionExpression(
        "maxInteger",
    )

/**
 * Gets max value of number type. Returns max number value.
 *
 * @return function expression
 */

@JvmName("maxNumber")
fun maxNumber(
): Expression<Double> =
    FunctionExpression(
        "maxNumber",
    )

/**
 * Gets min value of integer type. Returns min integer value.
 *
 * @return function expression
 */

@JvmName("minInteger")
fun minInteger(
): Expression<Long> =
    FunctionExpression(
        "minInteger",
    )

/**
 * Gets min value of number type. Returns min number value.
 *
 * @return function expression
 */

@JvmName("minNumber")
fun minNumber(
): Expression<Double> =
    FunctionExpression(
        "minNumber",
    )

/**
 * Gets max value of argument values. Returns max integer value.
 *
 * @param varargs Integer value.
 * @return function expression
 */

@JvmName("maxInt")
fun max(
    vararg varargs: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "max",
        *varargs,
    )

/**
 * Gets max value of argument values. Returns max number value.
 *
 * @param varargs Number value.
 * @return function expression
 */

@JvmName("maxDouble")
fun max(
    vararg varargs: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "max",
        *varargs,
    )

/**
 * Gets min value of argument values. Returns min integer value.
 *
 * @param varargs Integer value.
 * @return function expression
 */

@JvmName("minInt")
fun min(
    vararg varargs: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "min",
        *varargs,
    )

/**
 * Gets min value of argument values. Returns min number value.
 *
 * @param varargs Number value.
 * @return function expression
 */

@JvmName("minDouble")
fun min(
    vararg varargs: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "min",
        *varargs,
    )

/**
 * Gets absolute value of argument. Returns integer value.
 *
 * @param param0 Integer value to get absolute.
 * @return function expression
 */

@JvmName("absInt")
fun abs(
    param0: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "abs",
        param0,
    )

/**
 * Gets absolute value of argument. Returns integer value.
 *
 * @receiver Integer value to get absolute.
 * @return function expression
 */

@JvmName("extension_absInt")
fun Expression<Long>.abs(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = abs(this)

/**
 * Gets absolute value of argument. Returns number value.
 *
 * @param param0 Number value to get absolute.
 * @return function expression
 */

@JvmName("absDouble")
fun abs(
    param0: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "abs",
        param0,
    )

/**
 * Gets absolute value of argument. Returns number value.
 *
 * @receiver Number value to get absolute.
 * @return function expression
 */

@JvmName("extension_absDouble")
fun Expression<Double>.abs(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = abs(this)

/**
 * Gets sign value from argument. Returns -1 if value is negative, 0 if value is equal to 0, 1 if value is positive.
 *
 * @param param0 Integer value to get sign value.
 * @return function expression
 */

@JvmName("signumInt")
fun signum(
    param0: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "signum",
        param0,
    )

/**
 * Gets sign value from argument. Returns -1 if value is negative, 0 if value is equal to 0, 1 if value is positive.
 *
 * @receiver Integer value to get sign value.
 * @return function expression
 */

@JvmName("extension_signumInt")
fun Expression<Long>.signum(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = signum(this)

/**
 * Gets sign value from argument. Returns -1.0 if value is negative, 0.0 if value is equal to 0, 1.0 if value is positive.
 *
 * @param param0 Number value to get sign value.
 * @return function expression
 */

@JvmName("signumDouble")
fun signum(
    param0: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "signum",
        param0,
    )

/**
 * Gets sign value from argument. Returns -1.0 if value is negative, 0.0 if value is equal to 0, 1.0 if value is positive.
 *
 * @receiver Number value to get sign value.
 * @return function expression
 */

@JvmName("extension_signumDouble")
fun Expression<Double>.signum(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = signum(this)

/**
 * Rounds argument value to the closest integer value. 1.49 -> 1.0, 1.5 -> 2.0. Returns number value.
 *
 * @param param0 Number value to round.
 * @return function expression
 */

@JvmName("roundDouble")
fun round(
    param0: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "round",
        param0,
    )

/**
 * Rounds argument value to the closest integer value. 1.49 -> 1.0, 1.5 -> 2.0. Returns number value.
 *
 * @receiver Number value to round.
 * @return function expression
 */

@JvmName("extension_roundDouble")
fun Expression<Double>.round(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = round(this)

/**
 * Rounds argument value down. 1.49 -> 1.0, 1.5 -> 1.0. Returns number value.
 *
 * @param param0 Number value to round.
 * @return function expression
 */

@JvmName("floorDouble")
fun floor(
    param0: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "floor",
        param0,
    )

/**
 * Rounds argument value down. 1.49 -> 1.0, 1.5 -> 1.0. Returns number value.
 *
 * @receiver Number value to round.
 * @return function expression
 */

@JvmName("extension_floorDouble")
fun Expression<Double>.floor(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = floor(this)

/**
 * Rounds argument value up. 1.49 -> 2.0, 1.5 -> 2.0. Returns number value.
 *
 * @param param0 Number value to round.
 * @return function expression
 */

@JvmName("ceilDouble")
fun ceil(
    param0: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "ceil",
        param0,
    )

/**
 * Rounds argument value up. 1.49 -> 2.0, 1.5 -> 2.0. Returns number value.
 *
 * @receiver Number value to round.
 * @return function expression
 */

@JvmName("extension_ceilDouble")
fun Expression<Double>.ceil(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = ceil(this)

/**
 * Gets value of first argument with sign of the second argument. Returns integer value.
 *
 * @param param0 Integer value to copy value from.
 * @param param1 Integer value to copy sign from.
 * @return function expression
 */

@JvmName("copySignIntInt")
fun copySign(
    param0: Expression<Long>,
    param1: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "copySign",
        param0,
        param1,
    )

/**
 * Gets value of first argument with sign of the second argument. Returns number value.
 *
 * @param param0 Number value to copy value from.
 * @param param1 Number value to copy sign from.
 * @return function expression
 */

@JvmName("copySignDoubleDouble")
fun copySign(
    param0: Expression<Double>,
    param1: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "copySign",
        param0,
        param1,
    )

/**
 * Converts argument value to boolean type. Returns true if argument is 1, false if argument is 0, error if argument is other.
 *
 * @param param0 Integer value to convert.
 * @return function expression
 */

@JvmName("toBooleanInt")
fun toBoolean(
    param0: Expression<Long>,
): Expression<Boolean> =
    FunctionExpression(
        "toBoolean",
        param0,
    )

/**
 * Converts argument value to boolean type. Returns true if argument is 1, false if argument is 0, error if argument is other.
 *
 * @receiver Integer value to convert.
 * @return function expression
 */

@JvmName("extension_toBooleanInt")
fun Expression<Long>.toBoolean(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = toBoolean(this)

/**
 * Converts argument value to boolean type. Returns true if argument is "true", false if argument is "false", error if argument is other.
 *
 * @param param0 String value to convert.
 * @return function expression
 */

@JvmName("toBooleanString")
fun toBoolean(
    param0: Expression<String>,
): Expression<Boolean> =
    FunctionExpression(
        "toBoolean",
        param0,
    )

/**
 * Converts argument value to boolean type. Returns true if argument is "true", false if argument is "false", error if argument is other.
 *
 * @receiver String value to convert.
 * @return function expression
 */

@JvmName("extension_toBooleanString")
fun Expression<String>.toBoolean(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = toBoolean(this)

/**
 * Converts argument value to integer type. Returns 1 if argument is true, 0 if argument is false.
 *
 * @param param0 Boolean value to convert.
 * @return function expression
 */

@JvmName("toIntegerBoolean")
fun toInteger(
    param0: Expression<Boolean>,
): Expression<Long> =
    FunctionExpression(
        "toInteger",
        param0,
    )

/**
 * Converts argument value to integer type. Returns 1 if argument is true, 0 if argument is false.
 *
 * @receiver Boolean value to convert.
 * @return function expression
 */

@JvmName("extension_toIntegerBoolean")
fun Expression<Boolean>.toInteger(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = toInteger(this)

/**
 * Converts argument value to integer type. Rounds number value down. Returns integer value.
 *
 * @param param0 Number value to convert.
 * @return function expression
 */

@JvmName("toIntegerDouble")
fun toInteger(
    param0: Expression<Double>,
): Expression<Long> =
    FunctionExpression(
        "toInteger",
        param0,
    )

/**
 * Converts argument value to integer type. Rounds number value down. Returns integer value.
 *
 * @receiver Number value to convert.
 * @return function expression
 */

@JvmName("extension_toIntegerDouble")
fun Expression<Double>.toInteger(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = toInteger(this)

/**
 * Converts argument value to integer type. Returns integer value.
 *
 * @param param0 String value to convert.
 * @return function expression
 */

@JvmName("toIntegerString")
fun toInteger(
    param0: Expression<String>,
): Expression<Long> =
    FunctionExpression(
        "toInteger",
        param0,
    )

/**
 * Converts argument value to integer type. Returns integer value.
 *
 * @receiver String value to convert.
 * @return function expression
 */

@JvmName("extension_toIntegerString")
fun Expression<String>.toInteger(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = toInteger(this)

/**
 * Converts argument value to number type. Returns number value.
 *
 * @param param0 Integer value to convert.
 * @return function expression
 */

@JvmName("toNumberInt")
fun toNumber(
    param0: Expression<Long>,
): Expression<Double> =
    FunctionExpression(
        "toNumber",
        param0,
    )

/**
 * Converts argument value to number type. Returns number value.
 *
 * @receiver Integer value to convert.
 * @return function expression
 */

@JvmName("extension_toNumberInt")
fun Expression<Long>.toNumber(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = toNumber(this)

/**
 * Converts argument value to number type. Returns number value.
 *
 * @param param0 String value to convert.
 * @return function expression
 */

@JvmName("toNumberString")
fun toNumber(
    param0: Expression<String>,
): Expression<Double> =
    FunctionExpression(
        "toNumber",
        param0,
    )

/**
 * Converts argument value to number type. Returns number value.
 *
 * @receiver String value to convert.
 * @return function expression
 */

@JvmName("extension_toNumberString")
fun Expression<String>.toNumber(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = toNumber(this)

/**
 * Converts argument value to color type. Returns color value.
 *
 * @param param0 String value to convert.
 * @return function expression
 */

@JvmName("toColorString")
fun toColor(
    param0: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "toColor",
        param0,
    )

/**
 * Converts argument value to color type. Returns color value.
 *
 * @receiver String value to convert.
 * @return function expression
 */

@JvmName("extension_toColorString")
fun Expression<String>.toColor(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = toColor(this)

/**
 * Converts argument value to url type. Returns url value.
 *
 * @param param0 String value to convert.
 * @return function expression
 */

@JvmName("toUrlString")
fun toUrl(
    param0: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "toUrl",
        param0,
    )

/**
 * Converts argument value to url type. Returns url value.
 *
 * @receiver String value to convert.
 * @return function expression
 */

@JvmName("extension_toUrlString")
fun Expression<String>.toUrl(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = toUrl(this)

/**
 * Returns the value of a variable by its name. If the variable doesn't exist or has incorrect type, the default value would be returned.
 *
 * @param param0 Variable name.
 * @param param1 Fallback value.
 * @return function expression
 */

@JvmName("getIntegerValueStringInt")
fun getIntegerValue(
    param0: Expression<String>,
    param1: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "getIntegerValue",
        param0,
        param1,
    )

/**
 * Returns the value of a variable by its name. If the variable doesn't exist or has incorrect type, the default value would be returned.
 *
 * @param param0 Variable name.
 * @param param1 Fallback value.
 * @return function expression
 */

@JvmName("getNumberValueStringDouble")
fun getNumberValue(
    param0: Expression<String>,
    param1: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "getNumberValue",
        param0,
        param1,
    )

/**
 * Returns the value of a variable by its name. If the variable doesn't exist or has incorrect type, the default value would be returned.
 *
 * @param param0 Variable name.
 * @param param1 Fallback value.
 * @return function expression
 */

@JvmName("getStringValueStringString")
fun getStringValue(
    param0: Expression<String>,
    param1: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getStringValue",
        param0,
        param1,
    )

/**
 * Returns the value of a variable by its name. If the variable doesn't exist or has incorrect type, the default value would be returned.
 *
 * @param param0 Variable name.
 * @param param1 Fallback value.
 * @return function expression
 */

@JvmName("getUrlValueStringString")
fun getUrlValue(
    param0: Expression<String>,
    param1: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getUrlValue",
        param0,
        param1,
    )

/**
 * Returns the value of a variable by its name. If the variable doesn't exist or has incorrect type, the default value would be returned.
 *
 * @param param0 Variable name.
 * @param param1 Fallback value.
 * @return function expression
 */

@JvmName("getColorValueStringString")
fun getColorValue(
    param0: Expression<String>,
    param1: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getColorValue",
        param0,
        param1,
    )

/**
 * Returns the value of a variable by its name. If the variable doesn't exist or has incorrect type, the default value would be returned.
 *
 * @param param0 Variable name.
 * @param param1 Fallback value.
 * @return function expression
 */

@JvmName("getBooleanValueStringBoolean")
fun getBooleanValue(
    param0: Expression<String>,
    param1: Expression<Boolean>,
): Expression<Boolean> =
    FunctionExpression(
        "getBooleanValue",
        param0,
        param1,
    )

/**
 * Returns the stored value by its name. If the value doesn't exist or has incorrect type, the default value would be returned.
 *
 * @param param0 Stored value name.
 * @param param1 Fallback value.
 * @return function expression
 */

@JvmName("getStoredIntegerValueStringInt")
fun getStoredIntegerValue(
    param0: Expression<String>,
    param1: Expression<Long>,
): Expression<Long> =
    FunctionExpression(
        "getStoredIntegerValue",
        param0,
        param1,
    )

/**
 * Returns the stored value by its name. If the value doesn't exist or has incorrect type, the default value would be returned.
 *
 * @param param0 Stored value name.
 * @param param1 Fallback value.
 * @return function expression
 */

@JvmName("getStoredNumberValueStringDouble")
fun getStoredNumberValue(
    param0: Expression<String>,
    param1: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "getStoredNumberValue",
        param0,
        param1,
    )

/**
 * Returns the stored value by its name. If the value doesn't exist or has incorrect type, the default value would be returned.
 *
 * @param param0 Stored value name.
 * @param param1 Fallback value.
 * @return function expression
 */

@JvmName("getStoredStringValueStringString")
fun getStoredStringValue(
    param0: Expression<String>,
    param1: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getStoredStringValue",
        param0,
        param1,
    )

/**
 * Returns the stored value by its name. If the value doesn't exist or has incorrect type, the default value would be returned.
 *
 * @param param0 Stored value name.
 * @param param1 Fallback value.
 * @return function expression
 */

@JvmName("getStoredUrlValueStringString")
fun getStoredUrlValue(
    param0: Expression<String>,
    param1: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getStoredUrlValue",
        param0,
        param1,
    )

/**
 * Returns the stored value by its name. If the value doesn't exist or has incorrect type, the default value would be returned.
 *
 * @param param0 Stored value name.
 * @param param1 Fallback value.
 * @return function expression
 */

@JvmName("getStoredColorValueStringString")
fun getStoredColorValue(
    param0: Expression<String>,
    param1: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getStoredColorValue",
        param0,
        param1,
    )

/**
 * Returns the stored value by its name. If the value doesn't exist or has incorrect type, the default value would be returned.
 *
 * @param param0 Stored value name.
 * @param param1 Fallback value.
 * @return function expression
 */

@JvmName("getStoredBooleanValueStringBoolean")
fun getStoredBooleanValue(
    param0: Expression<String>,
    param1: Expression<Boolean>,
): Expression<Boolean> =
    FunctionExpression(
        "getStoredBooleanValue",
        param0,
        param1,
    )

/**
 * Returns the stored value by its name. If the value doesn't exist or has incorrect type, the exception will be thrown.
 *
 * @param param0 Stored value name.
 * @return function expression
 */

@JvmName("getStoredArrayValueString")
fun getStoredArrayValue(
    param0: Expression<String>,
): Expression<List<*>> =
    FunctionExpression(
        "getStoredArrayValue",
        param0,
    )

/**
 * Returns the stored value by its name. If the value doesn't exist or has incorrect type, the exception will be thrown.
 *
 * @receiver Stored value name.
 * @return function expression
 */

@JvmName("extension_getStoredArrayValueString")
fun Expression<String>.getStoredArrayValue(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getStoredArrayValue(this)

/**
 * Returns the stored value by its name. If the value doesn't exist or has incorrect type, the exception will be thrown.
 *
 * @param param0 Stored value name.
 * @return function expression
 */

@JvmName("getStoredDictValueString")
fun getStoredDictValue(
    param0: Expression<String>,
): Expression<Map<*, *>> =
    FunctionExpression(
        "getStoredDictValue",
        param0,
    )

/**
 * Returns the stored value by its name. If the value doesn't exist or has incorrect type, the exception will be thrown.
 *
 * @receiver Stored value name.
 * @return function expression
 */

@JvmName("extension_getStoredDictValueString")
fun Expression<String>.getStoredDictValue(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getStoredDictValue(this)

/**
 * Gets the length of string argument. Returns integer value.
 *
 * @param param0 String value to get length of.
 * @return function expression
 */

@JvmName("lenString")
fun len(
    param0: Expression<String>,
): Expression<Long> =
    FunctionExpression(
        "len",
        param0,
    )

/**
 * Gets the length of string argument. Returns integer value.
 *
 * @receiver String value to get length of.
 * @return function expression
 */

@JvmName("extension_lenString")
fun Expression<String>.len(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = len(this)

/**
 * Gets if second string argument contains in first string argument. Returns boolean value.
 *
 * @param param0 String value to find substring.
 * @param param1 String value substring to find.
 * @return function expression
 */

@JvmName("containsStringString")
fun contains(
    param0: Expression<String>,
    param1: Expression<String>,
): Expression<Boolean> =
    FunctionExpression(
        "contains",
        param0,
        param1,
    )

/**
 * Gets substring of first argument in range of last arguments. Returns string value.
 *
 * @param param0 String value to get substring from.
 * @param param1 Integer value, left index of substring.
 * @param param2 Integer value, right index of substring.
 * @return function expression
 */

@JvmName("substringStringIntInt")
fun substring(
    param0: Expression<String>,
    param1: Expression<Long>,
    param2: Expression<Long>,
): Expression<String> =
    FunctionExpression(
        "substring",
        param0,
        param1,
        param2,
    )

/**
 * Replaces all occurrences of the second argument in the first argument with the third argument. Returns string value of replaced string.
 *
 * @param param0 String value, original string.
 * @param param1 String value, string to find.
 * @param param2 String value, string to replace.
 * @return function expression
 */

@JvmName("replaceAllStringStringString")
fun replaceAll(
    param0: Expression<String>,
    param1: Expression<String>,
    param2: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "replaceAll",
        param0,
        param1,
        param2,
    )

/**
 * Gets first index of second argument in first argument. Returns integer value, index of occurrence or -1 if substring is not found.
 *
 * @param param0 String value, original string.
 * @param param1 String value, string to find.
 * @return function expression
 */

@JvmName("indexStringString")
fun index(
    param0: Expression<String>,
    param1: Expression<String>,
): Expression<Long> =
    FunctionExpression(
        "index",
        param0,
        param1,
    )

/**
 * Gets last index of second argument in first argument. Returns integer value, index of occurrence or -1 if substring is not found.
 *
 * @param param0 String value, original string.
 * @param param1 String value, string to find.
 * @return function expression
 */

@JvmName("lastIndexStringString")
fun lastIndex(
    param0: Expression<String>,
    param1: Expression<String>,
): Expression<Long> =
    FunctionExpression(
        "lastIndex",
        param0,
        param1,
    )

/**
 * Translates a string into application/x-www-form-urlencoded format using a specific encoding scheme. Returns string value, encoded string.
 *
 * @param param0 String value to translate.
 * @return function expression
 */

@JvmName("encodeUriString")
fun encodeUri(
    param0: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "encodeUri",
        param0,
    )

/**
 * Translates a string into application/x-www-form-urlencoded format using a specific encoding scheme. Returns string value, encoded string.
 *
 * @receiver String value to translate.
 * @return function expression
 */

@JvmName("extension_encodeUriString")
fun Expression<String>.encodeUri(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = encodeUri(this)

/**
 * Decodes a application/x-www-form-urlencoded string using a specific encoding scheme. Returns string value, decoded string.
 *
 * @param param0 String value to decode.
 * @return function expression
 */

@JvmName("decodeUriString")
fun decodeUri(
    param0: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "decodeUri",
        param0,
    )

/**
 * Decodes a application/x-www-form-urlencoded string using a specific encoding scheme. Returns string value, decoded string.
 *
 * @receiver String value to decode.
 * @return function expression
 */

@JvmName("extension_decodeUriString")
fun Expression<String>.decodeUri(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = decodeUri(this)

/**
 * Returns a string having leading and trailing whitespace removed.
 *
 * @param param0 String value to trim.
 * @return function expression
 */

@JvmName("trimString")
fun trim(
    param0: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "trim",
        param0,
    )

/**
 * Returns a string having leading and trailing whitespace removed.
 *
 * @receiver String value to trim.
 * @return function expression
 */

@JvmName("extension_trimString")
fun Expression<String>.trim(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = trim(this)

/**
 * Returns a string having leading whitespace removed.
 *
 * @param param0 String value to trim.
 * @return function expression
 */

@JvmName("trimLeftString")
fun trimLeft(
    param0: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "trimLeft",
        param0,
    )

/**
 * Returns a string having leading whitespace removed.
 *
 * @receiver String value to trim.
 * @return function expression
 */

@JvmName("extension_trimLeftString")
fun Expression<String>.trimLeft(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = trimLeft(this)

/**
 * Returns a string having trailing whitespace removed.
 *
 * @param param0 String value to trim.
 * @return function expression
 */

@JvmName("trimRightString")
fun trimRight(
    param0: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "trimRight",
        param0,
    )

/**
 * Returns a string having trailing whitespace removed.
 *
 * @receiver String value to trim.
 * @return function expression
 */

@JvmName("extension_trimRightString")
fun Expression<String>.trimRight(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = trimRight(this)

/**
 * Returns converted to upper case string.
 *
 * @param param0 String value to transform.
 * @return function expression
 */

@JvmName("toUpperCaseString")
fun toUpperCase(
    param0: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "toUpperCase",
        param0,
    )

/**
 * Returns converted to upper case string.
 *
 * @receiver String value to transform.
 * @return function expression
 */

@JvmName("extension_toUpperCaseString")
fun Expression<String>.toUpperCase(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = toUpperCase(this)

/**
 * Returns converted to lower case string.
 *
 * @param param0 String value to transform.
 * @return function expression
 */

@JvmName("toLowerCaseString")
fun toLowerCase(
    param0: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "toLowerCase",
        param0,
    )

/**
 * Returns converted to lower case string.
 *
 * @receiver String value to transform.
 * @return function expression
 */

@JvmName("extension_toLowerCaseString")
fun Expression<String>.toLowerCase(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = toLowerCase(this)

/**
 * Pads a string to a given string length, inserting at start
 *
 * @param param0 String value to pad.
 * @param param1 String length
 * @param param2 String character to pad with
 * @return function expression
 */

@JvmName("padStartStringIntString")
fun padStart(
    param0: Expression<String>,
    param1: Expression<Long>,
    param2: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "padStart",
        param0,
        param1,
        param2,
    )

/**
 * Converts given integer to a string and pads it to a given string length, inserting at start
 *
 * @param param0 Integer value to pad.
 * @param param1 String length
 * @param param2 String character to pad with
 * @return function expression
 */

@JvmName("padStartIntIntString")
fun padStart(
    param0: Expression<Long>,
    param1: Expression<Long>,
    param2: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "padStart",
        param0,
        param1,
        param2,
    )

/**
 * Pads a string to a given string length, inserting at end
 *
 * @param param0 String value to pad.
 * @param param1 String length
 * @param param2 String character to pad with
 * @return function expression
 */

@JvmName("padEndStringIntString")
fun padEnd(
    param0: Expression<String>,
    param1: Expression<Long>,
    param2: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "padEnd",
        param0,
        param1,
        param2,
    )

/**
 * Converts given integer to a string and pads it to a given string length, inserting at end
 *
 * @param param0 Integer value to pad.
 * @param param1 String length
 * @param param2 String character to pad with
 * @return function expression
 */

@JvmName("padEndIntIntString")
fun padEnd(
    param0: Expression<Long>,
    param1: Expression<Long>,
    param2: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "padEnd",
        param0,
        param1,
        param2,
    )

/**
 * Checks whether the string matches the regular expression.
 *
 * @param param0 A string to test.
 * @param param1 Regex string.
 * @return function expression
 */

@JvmName("testRegexStringString")
fun testRegex(
    param0: Expression<String>,
    param1: Expression<String>,
): Expression<Boolean> =
    FunctionExpression(
        "testRegex",
        param0,
        param1,
    )

/**
 * Encodes a string for safe use in regular expressions.
 *
 * @param param0 A string to encode.
 * @return function expression
 */

@JvmName("encodeRegexString")
fun encodeRegex(
    param0: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "encodeRegex",
        param0,
    )

/**
 * Encodes a string for safe use in regular expressions.
 *
 * @receiver A string to encode.
 * @return function expression
 */

@JvmName("extension_encodeRegexString")
fun Expression<String>.encodeRegex(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = encodeRegex(this)

/**
 * Returns a string representation of given value.
 *
 * @param param0 Value.
 * @return function expression
 */

@JvmName("toStringInt")
fun toString(
    param0: Expression<Long>,
): Expression<String> =
    FunctionExpression(
        "toString",
        param0,
    )

/**
 * Returns value of the `π`: ratio of the circumference of a circle to its diameter
 *
 * @return function expression
 */

@JvmName("pi")
fun pi(
): Expression<Double> =
    FunctionExpression(
        "pi",
    )

/**
 * Converts the argument value from degrees to radians
 *
 * @param param0 Number value representing degrees.
 * @return function expression
 */

@JvmName("toRadiansDouble")
fun toRadians(
    param0: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "toRadians",
        param0,
    )

/**
 * Converts the argument value from degrees to radians
 *
 * @receiver Number value representing degrees.
 * @return function expression
 */

@JvmName("extension_toRadiansDouble")
fun Expression<Double>.toRadians(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = toRadians(this)

/**
 * Converts the argument value from radians to degrees
 *
 * @param param0 Number value representing radians.
 * @return function expression
 */

@JvmName("toDegreesDouble")
fun toDegrees(
    param0: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "toDegrees",
        param0,
    )

/**
 * Converts the argument value from radians to degrees
 *
 * @receiver Number value representing radians.
 * @return function expression
 */

@JvmName("extension_toDegreesDouble")
fun Expression<Double>.toDegrees(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = toDegrees(this)

/**
 * Calculates the sine of the given angle in radians
 *
 * @param param0 Angle in radians.
 * @return function expression
 */

@JvmName("sinDouble")
fun sin(
    param0: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "sin",
        param0,
    )

/**
 * Calculates the sine of the given angle in radians
 *
 * @receiver Angle in radians.
 * @return function expression
 */

@JvmName("extension_sinDouble")
fun Expression<Double>.sin(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = sin(this)

/**
 * Calculates the cosine of the given angle in radians
 *
 * @param param0 Angle in radians.
 * @return function expression
 */

@JvmName("cosDouble")
fun cos(
    param0: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "cos",
        param0,
    )

/**
 * Calculates the cosine of the given angle in radians
 *
 * @receiver Angle in radians.
 * @return function expression
 */

@JvmName("extension_cosDouble")
fun Expression<Double>.cos(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = cos(this)

/**
 * Calculates the tangent of the given angle in radians
 *
 * @param param0 Angle in radians.
 * @return function expression
 */

@JvmName("tanDouble")
fun tan(
    param0: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "tan",
        param0,
    )

/**
 * Calculates the tangent of the given angle in radians
 *
 * @receiver Angle in radians.
 * @return function expression
 */

@JvmName("extension_tanDouble")
fun Expression<Double>.tan(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = tan(this)

/**
 * Calculates the angle in radians of the given sine value
 *
 * @param param0 Sine value.
 * @return function expression
 */

@JvmName("asinDouble")
fun asin(
    param0: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "asin",
        param0,
    )

/**
 * Calculates the angle in radians of the given sine value
 *
 * @receiver Sine value.
 * @return function expression
 */

@JvmName("extension_asinDouble")
fun Expression<Double>.asin(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = asin(this)

/**
 * Calculates the angle in radians of the given cosine value
 *
 * @param param0 Cosine value.
 * @return function expression
 */

@JvmName("acosDouble")
fun acos(
    param0: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "acos",
        param0,
    )

/**
 * Calculates the angle in radians of the given cosine value
 *
 * @receiver Cosine value.
 * @return function expression
 */

@JvmName("extension_acosDouble")
fun Expression<Double>.acos(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = acos(this)

/**
 * Calculates the angle in radians for a given tangent value
 *
 * @param param0 Tangent value.
 * @return function expression
 */

@JvmName("atanDouble")
fun atan(
    param0: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "atan",
        param0,
    )

/**
 * Calculates the angle in radians for a given tangent value
 *
 * @receiver Tangent value.
 * @return function expression
 */

@JvmName("extension_atanDouble")
fun Expression<Double>.atan(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = atan(this)

/**
 * Calculates the angle in radians for given values (y,x)
 *
 * @param param0 Y coordinate of the point
 * @param param1 X coordinate of the point
 * @return function expression
 */

@JvmName("atan2DoubleDouble")
fun atan2(
    param0: Expression<Double>,
    param1: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "atan2",
        param0,
        param1,
    )

/**
 * Calculates the cotangent of the given angle in radians
 *
 * @param param0 Angle in radians.
 * @return function expression
 */

@JvmName("cotDouble")
fun cot(
    param0: Expression<Double>,
): Expression<Double> =
    FunctionExpression(
        "cot",
        param0,
    )

/**
 * Calculates the cotangent of the given angle in radians
 *
 * @receiver Angle in radians.
 * @return function expression
 */

@JvmName("extension_cotDouble")
fun Expression<Double>.cot(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = cot(this)

/**
 * Returns an array value from dict by key.
 *
 * @param param0 Dict.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getArrayMapString")
fun getArray(
    param0: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<List<*>> =
    FunctionExpression(
        "getArray",
        param0,
        *varargs,
    )

/**
 * Returns a boolean value from dict by key.
 *
 * @param param0 Dict.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getBooleanMapString")
fun getBoolean(
    param0: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<Boolean> =
    FunctionExpression(
        "getBoolean",
        param0,
        *varargs,
    )

/**
 * Returns a color value from dict by key.
 *
 * @param param0 Dict.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getColorMapString")
fun getColor(
    param0: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getColor",
        param0,
        *varargs,
    )

/**
 * Returns a dict value from dict by key.
 *
 * @param param0 Dict.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getDictMapString")
fun getDict(
    param0: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<Map<*, *>> =
    FunctionExpression(
        "getDict",
        param0,
        *varargs,
    )

/**
 * Returns an integer value from dict by key.
 *
 * @param param0 Dict.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getIntegerMapString")
fun getInteger(
    param0: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<Long> =
    FunctionExpression(
        "getInteger",
        param0,
        *varargs,
    )

/**
 * Returns a number value from dict by key.
 *
 * @param param0 Dict.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getNumberMapString")
fun getNumber(
    param0: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<Double> =
    FunctionExpression(
        "getNumber",
        param0,
        *varargs,
    )

/**
 * Returns a string value from dict by key.
 *
 * @param param0 Dict.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getStringMapString")
fun getString(
    param0: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getString",
        param0,
        *varargs,
    )

/**
 * Returns a url value from dict by key.
 *
 * @param param0 Dict.
 * @param varargs Path in dictionary.
 * @return function expression
 */

@JvmName("getUrlMapString")
fun getUrl(
    param0: Expression<out Map<*, *>>,
    vararg varargs: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "getUrl",
        param0,
        *varargs,
    )

/**
 * Returns true if the given key is present in the dictionary.
 *
 * @param param0 Dict.
 * @param param1 Key in dictionary.
 * @return function expression
 */

@JvmName("containsKeyMapString")
fun containsKey(
    param0: Expression<out Map<*, *>>,
    param1: Expression<String>,
): Expression<Boolean> =
    FunctionExpression(
        "containsKey",
        param0,
        param1,
    )

/**
 * The method returns boolean true if dict is empty else false.
 *
 * @param param0 Dict.
 * @return function expression
 */

@JvmName("isEmptyMap")
fun isEmpty(
    param0: Expression<out Map<*, *>>,
): Expression<Boolean> =
    FunctionExpression(
        "isEmpty",
        param0,
    )

/**
 * The method returns boolean true if dict is empty else false.
 *
 * @receiver Dict.
 * @return function expression
 */

@JvmName("extension_isEmptyMap")
fun Expression<out Map<*, *>>.isEmpty(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = isEmpty(this)

/**
 * Return a collection containing just the keys of the dictionary.
 *
 * @param param0 Dictionary.
 * @return function expression
 */

@JvmName("getKeysMap")
fun getKeys(
    param0: Expression<out Map<*, *>>,
): Expression<List<*>> =
    FunctionExpression(
        "getKeys",
        param0,
    )

/**
 * Return a collection containing just the keys of the dictionary.
 *
 * @receiver Dictionary.
 * @return function expression
 */

@JvmName("extension_getKeysMap")
fun Expression<out Map<*, *>>.getKeys(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getKeys(this)

/**
 * Return a collection containing just the values of the dictionary.
 *
 * @param param0 Dictionary.
 * @return function expression
 */

@JvmName("getValuesMap")
fun getValues(
    param0: Expression<out Map<*, *>>,
): Expression<List<*>> =
    FunctionExpression(
        "getValues",
        param0,
    )

/**
 * Return a collection containing just the values of the dictionary.
 *
 * @receiver Dictionary.
 * @return function expression
 */

@JvmName("extension_getValuesMap")
fun Expression<out Map<*, *>>.getValues(@Suppress("UNUSED_PARAMETER", "LocalVariableName") `do not use`: Unit = Unit) = getValues(this)

/**
 * Returns a string value from an integer using format.
 *
 * @param param0 Integer value to format.
 * @param param1 Number format.
 * @return function expression
 */

@JvmName("decimalFormatIntString")
fun decimalFormat(
    param0: Expression<Long>,
    param1: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "decimalFormat",
        param0,
        param1,
    )

/**
 * Returns a string value from an number using format.
 *
 * @param param0 Number value to format.
 * @param param1 Number format.
 * @return function expression
 */

@JvmName("decimalFormatDoubleString")
fun decimalFormat(
    param0: Expression<Double>,
    param1: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "decimalFormat",
        param0,
        param1,
    )

/**
 * Returns a string value from an integer using format and locale.
 *
 * @param param0 Integer value to format.
 * @param param1 Number format.
 * @param param2 Locale.
 * @return function expression
 */

@JvmName("decimalFormatIntStringString")
fun decimalFormat(
    param0: Expression<Long>,
    param1: Expression<String>,
    param2: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "decimalFormat",
        param0,
        param1,
        param2,
    )

/**
 * Returns a string value from an number using format and locale.
 *
 * @param param0 Number value to format.
 * @param param1 Number format.
 * @param param2 Locale.
 * @return function expression
 */

@JvmName("decimalFormatDoubleStringString")
fun decimalFormat(
    param0: Expression<Double>,
    param1: Expression<String>,
    param2: Expression<String>,
): Expression<String> =
    FunctionExpression(
        "decimalFormat",
        param0,
        param1,
        param2,
    )

