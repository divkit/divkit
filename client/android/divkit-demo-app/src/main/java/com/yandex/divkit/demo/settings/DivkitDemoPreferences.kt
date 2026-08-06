package com.yandex.divkit.demo.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.reflect.KProperty

/**
 * Provides access to the shared preferences.
 */
open class DivkitDemoPreferences(protected val context: Context) {
    protected val preferences: SharedPreferences =
        context.getSharedPreferences("main", Context.MODE_PRIVATE)

    fun clear() = preferences.edit { clear() }

    protected fun getString(name: String, defValue: String? = null): String? {
        return preferences.getString(name, defValue)
    }

    protected class BooleanPreference(private val defaultValue: Boolean) {

        operator fun getValue(thisRef: DivkitDemoPreferences, property: KProperty<*>): Boolean =
            thisRef.preferences.getBoolean(property.name, defaultValue)

        operator fun setValue(thisRef: DivkitDemoPreferences, property: KProperty<*>, value: Boolean) =
            thisRef.preferences.edit { putBoolean(property.name, value) }
    }

    protected class IntPreference(private val defaultValue: Int) {

        operator fun getValue(thisRef: DivkitDemoPreferences, property: KProperty<*>): Int {
            val preferences = thisRef.preferences
            val name = property.name
            return preferences.getInt(name, defaultValue)
        }

        operator fun setValue(thisRef: DivkitDemoPreferences, property: KProperty<*>, value: Int) {
            val preferences = thisRef.preferences
            val name = property.name
            preferences.edit { putInt(name, value) }
        }
    }

    protected class EnumPreference<T : Enum<T>>(val defValue: T, val values: () -> Array<T>) {

        operator fun getValue(thisRef: DivkitDemoPreferences, property: KProperty<*>): T {
            val ordinal = thisRef.preferences.getInt(property.name, defValue.ordinal)
            return values().find { it.ordinal == ordinal } ?: defValue
        }

        operator fun setValue(thisRef: DivkitDemoPreferences, property: KProperty<*>, value: T) {
            thisRef.preferences.edit { putInt(property.name, value.ordinal) }
        }
    }
}
