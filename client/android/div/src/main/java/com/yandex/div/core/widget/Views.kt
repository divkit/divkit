package com.yandex.div.core.widget

import android.view.View
import android.view.ViewGroup
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private class AppearanceAffectingViewProperty<T>(
    private var propertyValue: T,
    private val modifier: ((T) -> T)?
) : ReadWriteProperty<View, T> {

    override fun getValue(thisRef: View, property: KProperty<*>): T {
        return propertyValue
    }

    override fun setValue(thisRef: View, property: KProperty<*>, value: T) {
        val newValue = modifier?.invoke(value) ?: value
        if (propertyValue != newValue) {
            propertyValue = newValue
            thisRef.invalidate()
        }
    }
}

private class DimensionAffectingViewProperty<T>(
    private var propertyValue: T,
    private val modifier: ((T) -> T)?
) : ReadWriteProperty<View, T> {

    override fun getValue(thisRef: View, property: KProperty<*>): T {
        return propertyValue
    }

    override fun setValue(thisRef: View, property: KProperty<*>, value: T) {
        val newValue = modifier?.invoke(value) ?: value
        if (propertyValue != newValue) {
            propertyValue = newValue
            thisRef.requestLayout()
        }
    }
}

internal fun <T> appearanceAffecting(value: T, modifier: ((T) -> T)? = null) : ReadWriteProperty<View, T> {
    return AppearanceAffectingViewProperty(value, modifier)
}

internal fun <T> dimensionAffecting(value: T, modifier: ((T) -> T)? = null) : ReadWriteProperty<View, T> {
    return DimensionAffectingViewProperty(value, modifier)
}

internal inline fun View.invalidateAfter(block: () -> Unit) {
    block()
    invalidate()
}

internal inline fun View.requestLayoutAfter(block: () -> Unit) {
    block()
    requestLayout()
}

internal inline fun ViewGroup.forEach(significantOnly: Boolean = false, action: (View) -> Unit) {
    val childCount = childCount
    for (i in 0 until childCount) {
        val child = getChildAt(i)
        if (significantOnly && child.visibility == View.GONE) continue
        action(child)
    }
}

internal inline fun ViewGroup.forEachIndexed(significantOnly: Boolean = false, action: (View, Int) -> Unit) {
    val childCount = childCount
    for (i in 0 until childCount) {
        val child = getChildAt(i)
        if (significantOnly && child.visibility == View.GONE) continue
        action(child, i)
    }
}
