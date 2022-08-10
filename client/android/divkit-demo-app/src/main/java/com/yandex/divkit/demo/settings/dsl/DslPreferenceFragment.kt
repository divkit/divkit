package com.yandex.divkit.demo.settings.dsl

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.TwoStatePreference
import com.yandex.div.core.experiments.Experiment
import com.yandex.divkit.demo.settings.FlagPreferenceProvider
import com.yandex.divkit.demo.utils.noGetter

class DslPreferenceFragment(
    private val flagPreferenceProvider: FlagPreferenceProvider,
    private val init: DslPreferenceFragment.() -> Unit = {}
) : PreferenceFragmentCompat() {

    var onPreferencesCreated: ((DslPreferenceFragment) -> Unit)? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        init()
        onPreferencesCreated?.invoke(this)
    }

    var TwoStatePreference.experimentFlag: Experiment
        get() = noGetter()
        set(experiment) {
            key = experiment.key
            setting = Setting(
                getter = { flagPreferenceProvider.getExperimentFlag(experiment) },
                setter = { value -> flagPreferenceProvider.setExperimentFlag(experiment, value) }
            )
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
}
