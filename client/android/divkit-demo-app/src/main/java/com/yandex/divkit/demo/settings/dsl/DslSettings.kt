package com.yandex.divkit.demo.settings.dsl

import android.content.Context
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceGroup
import androidx.preference.PreferenceScreen
import androidx.preference.SwitchPreference
import androidx.preference.TwoStatePreference
import com.yandex.divkit.demo.utils.noGetter

/**
 * Common code to use for preferences DSL.
 */
class Setting<T>(val getter: () -> T, val setter: (T) -> Unit)

val PreferenceFragmentCompat.preferenceContext: Context get() = preferenceManager.context

fun PreferenceFragmentCompat.preferenceScreen(init: PreferenceScreen.() -> Unit) {
    preferenceScreen = preferenceManager.createPreferenceScreen(preferenceContext)
    preferenceScreen.init()
}

fun PreferenceGroup.preferenceCategory(init: PreferenceCategory.() -> Unit): PreferenceCategory {
    val category = PreferenceCategory(context)
    addPreference(category)
    category.init()
    return category
}

fun Preference.onChanged(listener: (Any) -> Unit) {
    val wrapped = onPreferenceChangeListener
    onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
        (wrapped?.onPreferenceChange(preference, newValue) ?: true).also {
            listener(newValue)
        }
    }
}

fun PreferenceGroup.switchPreference(dependencyKey: String? = null, init: SwitchPreference.() -> Unit): SwitchPreference =
    with(SwitchPreference(context)) {
        isPersistent = false
        init()
        addPreference(this)
        dependencyKey?.let { dependency = it }
        this
    }

var TwoStatePreference.setting: Setting<Boolean>
    get() = noGetter()
    set(value) {
        isChecked = value.getter()
        onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            value.setter(newValue as Boolean)
            true
        }
    }

fun PreferenceGroup.listPreference(init: ListPreference.() -> Unit): ListPreference {
    val preference = ListPreference(context).apply {
        init()
        if (key == null) {
            key = title?.toString()
        }
    }
    addPreference(preference)
    return preference
}

var ListPreference.arrayEntries: Array<*>
    get() = noGetter()
    set(value) {
        entries = value.map { it.toString() }.toTypedArray()
        entryValues = entries
    }

var ListPreference.stringSetting: Setting<String?>
    get() = noGetter()
    set(value) {
        val initialValue = value.getter().toString()
        summary = initialValue
        setValue(initialValue)
        onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            val stringValue = newValue as String
            value.setter(stringValue)
            summary = stringValue
            true
        }
    }
