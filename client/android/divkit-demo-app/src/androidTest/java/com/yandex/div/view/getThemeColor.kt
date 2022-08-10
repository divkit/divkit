package com.yandex.div.view

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

/**
 * Returns the color int for the provided theme color attribute.
 *
 * @throws IllegalArgumentException if the attribute is not set in the current theme.
 */
@ColorInt
fun Context.getThemeColor(@AttrRes attribute: Int): Int {
    val typedValue = resolveAttributeOrThrow(attribute)
    return typedValue.data
}

fun Context.getThemeBoolean(@AttrRes attribute: Int): Boolean {
    return resolveAttributeOrThrow(attribute).data != 0
}

fun Context.getThemeResource(@AttrRes attribute: Int): Int {
    return resolveAttributeOrThrow(attribute).resourceId
}

internal fun Context.resolveAttributeOrThrow(@AttrRes attributeResId: Int): TypedValue {
    val typedValue = TypedValue()

    val isAttrResolved = theme.resolveAttribute(attributeResId, typedValue, true)
    if (isAttrResolved) {
        return typedValue
    }

    val errorMessage = ("%1\$s requires a value for the %2\$s attribute to be set in your theme.")
    throw java.lang.IllegalArgumentException(
        String.format(
            errorMessage,
            javaClass.canonicalName,
            resources.getResourceName(attributeResId),
        )
    )
}
