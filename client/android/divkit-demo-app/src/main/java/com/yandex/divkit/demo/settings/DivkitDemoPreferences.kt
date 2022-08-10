package com.yandex.divkit.demo.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberProperties


/**
 * Provides access to the shared preferences.
 */
open class DivkitDemoPreferences(protected val context: Context) {
    protected val preferences = context.getSharedPreferences("main", Context.MODE_PRIVATE)

    fun setProperty(name: String, value: String): Boolean {
        val property = DivkitDemoPreferences::class.memberProperties
            .filterIsInstance<KMutableProperty1<DivkitDemoPreferences, Any>>()
            .find { it.name == name }

        if (property == null) {
            when {
                value.isBoolean() -> preferences.setBoolean(name, value.toBoolean())
                else -> preferences.setString(name, value)
            }
            return true
        }

        val valueToSet: Any? = when (property.returnType) {
            Boolean::class.createType() -> value.toBoolean()
            Float::class.createType() -> value.toFloatOrNull()
            Int::class.createType() -> value.toIntOrNull()
            Long::class.createType() -> value.toLongOrNull()
            String::class.createType(), String::class.createType(nullable = true) -> value
            else -> return false
        }
        if (valueToSet != null) {
            property.set(receiver = this, value = valueToSet)
        }

        return true
    }

    fun getProperties(): Map<String, String> {
        val properties = DivkitDemoPreferences::class.memberProperties
            .filter { it.visibility == KVisibility.PUBLIC }
            .filterIsInstance<KMutableProperty1<DivkitDemoPreferences, *>>()
            .mapNotNull { property ->
                try {
                    property.get(this@DivkitDemoPreferences)?.let { value ->
                        property.name to value.toString()
                    }
                } catch (e: Exception) {
                    null
                }
            }
            .toMap()
        val prefs = preferences.all
            .filter { it.value != null }
            .filter { !properties.keys.contains(it.key) }
            .map { it.key to it.value.toString() }
            .toMap()
        return properties + prefs
    }

    fun clear() = preferences.edit { clear() }

    protected fun getString(name: String, defValue: String? = null) = preferences.getString(name, defValue)

    protected class StringPreference(val defValue: String? = null) {

        operator fun getValue(thisRef: DivkitDemoPreferences, property: KProperty<*>): String? =
            thisRef.getString(property.name, defValue)

        operator fun setValue(thisRef: DivkitDemoPreferences, property: KProperty<*>, value: String?) =
            thisRef.preferences.setString(property.name, value)
    }

    protected class BooleanPreference(private val defaultValue: Boolean) {

        operator fun getValue(thisRef: DivkitDemoPreferences, property: KProperty<*>): Boolean =
            thisRef.preferences.getBoolean(property.name, defaultValue)

        operator fun setValue(thisRef: DivkitDemoPreferences, property: KProperty<*>, value: Boolean) =
            thisRef.preferences.edit().putBoolean(property.name, value).apply()
    }

    protected class LongPreference(private val defaultValue: Long) {

        operator fun getValue(thisRef: DivkitDemoPreferences, property: KProperty<*>): Long {
            val preferences = thisRef.preferences
            val name = property.name
            return preferences.getLong(name, defaultValue)
        }

        operator fun setValue(thisRef: DivkitDemoPreferences, property: KProperty<*>, value: Long) {
            val preferences = thisRef.preferences
            val name = property.name
            preferences.edit().putLong(name, value).apply()
        }
    }

    protected class OptionalLongPreference() {

        operator fun getValue(thisRef: DivkitDemoPreferences, property: KProperty<*>): Long? {
            val preferences = thisRef.preferences
            val name = property.name
            return if (preferences.contains(name)) preferences.getLong(name, 0L) else null
        }

        operator fun setValue(thisRef: DivkitDemoPreferences, property: KProperty<*>, value: Long?) {
            val preferences = thisRef.preferences
            val name = property.name
            if (value == null) {
                preferences.edit().remove(name).apply()
            } else {
                preferences.edit().putLong(name, value).apply()
            }
        }
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
            preferences.edit().putInt(name, value).apply()
        }
    }

    protected class StringSetPreference(private val defaultValue: Set<String> = setOf()) {

        operator fun getValue(thisRef: DivkitDemoPreferences, property: KProperty<*>): Set<String> {
            val preferences = thisRef.preferences
            val name = property.name
            return preferences.getStringSet(name, defaultValue) ?: defaultValue
        }

        operator fun setValue(thisRef: DivkitDemoPreferences, property: KProperty<*>, value: Set<String>) {
            val preferences = thisRef.preferences
            val name = property.name
            preferences.edit().putStringSet(name, value).apply()
        }
    }

    protected class EnumPreference<T : Enum<T>>(val defValue: T, val values: () -> Array<T>) {

        operator fun getValue(thisRef: DivkitDemoPreferences, property: KProperty<*>): T {
            val ordinal = thisRef.preferences.getInt(property.name, defValue.ordinal)
            return values().find { it.ordinal == ordinal } ?: defValue
        }

        operator fun setValue(thisRef: DivkitDemoPreferences, property: KProperty<*>, value: T) {
            thisRef.preferences.edit().putInt(property.name, value.ordinal).apply()
        }
    }

    private fun String.isBoolean() = this.equals("false", ignoreCase = true) || toBoolean()
}

fun SharedPreferences.setString(name: String, value: String?) = edit().putString(name, value).apply()

fun SharedPreferences.setBoolean(key: String, value: Boolean) = edit().putBoolean(key, value).apply()

