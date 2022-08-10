package com.yandex.divkit.demo.settings

import androidx.appcompat.app.AppCompatDelegate
import com.yandex.div.core.experiments.Experiment
import com.yandex.divkit.demo.Container
import com.yandex.divkit.demo.settings.dsl.arrayEntries
import com.yandex.divkit.demo.settings.dsl.listPreference
import com.yandex.divkit.demo.settings.dsl.onChanged
import com.yandex.divkit.demo.settings.dsl.preferenceCategory
import com.yandex.divkit.demo.settings.dsl.preferenceFragment
import com.yandex.divkit.demo.settings.dsl.preferenceScreen
import com.yandex.divkit.demo.settings.dsl.stringSetting
import com.yandex.divkit.demo.settings.dsl.switchPreference
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext

internal class SettingsActivityUi : AnkoComponent<SettingsActivity> {

    override fun createView(ui: AnkoContext<SettingsActivity>) =
        ui.preferenceFragment(Container.flagPreferenceProvider) {
            preferenceScreen {
                preferenceCategory {
                    title = "Div experiment settings"

                    switchPreference {
                        experimentFlag = Experiment.VIEW_POOL_ENABLED
                        title = "View Pool enabled"
                    }
                    switchPreference {
                        experimentFlag = Experiment.VIEW_POOL_PROFILING_ENABLED
                        title = "View Pool profiling enabled"
                    }
                    switchPreference {
                        experimentFlag = Experiment.MULTIPLE_STATE_CHANGE_ENABLED
                        title = "Multiple state change enabled"
                    }
                }

                preferenceCategory {
                    title = "Theme"
                    listPreference {
                        title = "Night mode"
                        stringSetting = NightMode.setting
                        arrayEntries = NightMode.entries
                        onChanged {
                            AppCompatDelegate.setDefaultNightMode(Container.preferences.nightMode)
                            ui.owner.delegate.applyDayNight()
                        }
                    }
                }
            }
        }
}
