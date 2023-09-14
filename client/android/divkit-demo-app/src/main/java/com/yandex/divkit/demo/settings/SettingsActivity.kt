package com.yandex.divkit.demo.settings

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.yandex.div.core.experiments.Experiment
import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivAction
import com.yandex.divkit.demo.BuildConfig
import com.yandex.divkit.demo.Container
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.databinding.SettingsActivityBinding
import com.yandex.divkit.demo.ui.UIDiv2ViewCreator
import com.yandex.divkit.regression.ScenarioLogDelegate

private const val ACTION_SET_SWITCHER_STATE = "div-action://set_state?state_id=0/"
private const val ACTION_SET_VARIABLE = "div-action://set_variable"

private const val NESTED_SCROLL_VIEW_POSITION = "nested scroll view position"
private const val SCROLL_VIEW_POSITION = "scroll view position"

private const val APP_VERSION = "app_version"

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: SettingsActivityBinding
    private lateinit var div2View: Div2View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null) // Pass null here to avoid trying to restore settings fragment

        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = getString(R.string.settings)
        val config: Configuration = resources.configuration
        val icon = if (config.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            R.drawable.ic_back_light_rtl
        } else {
            R.drawable.ic_back_light
        }
        binding.toolbar.setNavigationIcon(icon)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        div2View = UIDiv2ViewCreator(this).createDiv2View(
            this,
            "application/settings.json",
            binding.scrollView,
            ScenarioLogDelegate.Stub
        )

        binding.scrollView.addView(div2View)
        initSettings()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntArray(NESTED_SCROLL_VIEW_POSITION,
        intArrayOf(binding.scrollView.scrollX, binding.scrollView.scrollY))
        outState.putIntArray(SCROLL_VIEW_POSITION,
            intArrayOf(binding.coordinator.scrollX, binding.coordinator.scrollY))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val nestedPosition = savedInstanceState.getIntArray(NESTED_SCROLL_VIEW_POSITION)

        val position = savedInstanceState.getIntArray(SCROLL_VIEW_POSITION)
        if (position != null) binding.coordinator.post{
            binding.scrollView.scrollTo(position[0], position[1])
        }

        if (nestedPosition != null) binding.scrollView.post{
            binding.scrollView.scrollTo(nestedPosition[0], nestedPosition[1])
        }
        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun initSettings() {
        setPreferenceState(DIV2_VIEW_POOL, Experiment.VIEW_POOL_ENABLED)
        setPreferenceState(DIV2_VIEW_POOL_PROFILING, Experiment.VIEW_POOL_PROFILING_ENABLED)
        setPreferenceState(DIV2_MULTIPLE_STATE_CHANGE, Experiment.MULTIPLE_STATE_CHANGE_ENABLED)
        setPreferenceState(DIV2_DEMO_SHOW_RENDERING_TIME, Experiment.SHOW_RENDERING_TIME)
        val nightMode = when (Container.preferences.nightMode) {
            AppCompatDelegate.MODE_NIGHT_YES -> NIGHT_MODE_NIGHT
            AppCompatDelegate.MODE_NIGHT_NO -> NIGHT_MODE_DAY
            else -> NIGHT_MODE_AUTO
        }
        div2View.setVariable(NIGHT_MODE, nightMode)
        setPreferenceState(IMAGE_LOADER, Container.preferences.imageLoader)

        val appName = resources.getText(R.string.app_name)
        val appVersion = "$appName ${BuildConfig.VERSION_NAME}.${BuildConfig.BUILD_NUMBER} ${BuildConfig.BUILD_TYPE}"
        div2View.setVariable(APP_VERSION, appVersion)
    }

    private fun setPreferenceState(name: String, flag: Experiment) {
        setPreferenceState(name, Container.flagPreferenceProvider.getExperimentFlag(flag))
    }

    private fun setPreferenceState(name: String, value: Boolean) {
        val state = if (value) "active" else "inactive"
        val url = "$ACTION_SET_SWITCHER_STATE$name/$state"
        div2View.handleActionWithResult(
            DivAction(
                logId = "init preferences",
                url = Expression.constant(Uri.parse(url))
            )
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
