package com.yandex.divkit.demo.settings

import android.content.Context
import com.yandex.div.core.experiments.Experiment

class FlagPreferenceProvider(context: Context) {
    private val preferences = context.getSharedPreferences("main", Context.MODE_PRIVATE)

    fun getExperimentFlag(experiment: Experiment) =
        preferences.getBoolean(experiment.key, experiment.defaultValue)

    fun setExperimentFlag(experiment: Experiment, value: Boolean) =
        preferences.setBoolean(experiment.key, value)
}
