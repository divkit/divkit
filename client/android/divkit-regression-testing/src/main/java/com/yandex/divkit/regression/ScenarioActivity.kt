package com.yandex.divkit.regression

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.yandex.divkit.regression.data.Scenario
import com.yandex.divkit.regression.databinding.ScenarioActivityBinding
import com.yandex.divkit.regression.di.provideDiv2ViewCreator
import com.yandex.divkit.regression.di.provideScenariosRepository
import com.yandex.divkit.regression.di.provideScreenRecord
import com.yandex.divkit.regression.screenrecord.ScreenRecord
import com.yandex.divkit.regression.utils.shortMessage
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val EXTRA_POSITION = "${BuildConfig.LIBRARY_PACKAGE_NAME}.extra_position"

private const val PREV_ITEM_ID = 200
private const val NEXT_ITEM_ID = 201

private const val INVALID_POSITION = -1

class ScenarioActivity : AppCompatActivity(), MetadataBottomSheet.ScenarioHost {
    private lateinit var binding: ScenarioActivityBinding
    private val repository by lazy(LazyThreadSafetyMode.NONE) { provideScenariosRepository() }
    private val div2ViewCreator by lazy(LazyThreadSafetyMode.NONE) { provideDiv2ViewCreator() }
    private val logging = mutableListOf<String>()
    private val logDelegate = ScenarioLogDelegate { message -> logging += message }

    private val screenRecord by lazy(LazyThreadSafetyMode.NONE) {
        provideScreenRecord(activityResultRegistry)
    }

    private lateinit var _scenario: Scenario
    override val scenario: Scenario
        get() = _scenario

    private var position = INVALID_POSITION
    private var itemCount = 0

    private lateinit var metadataBottomSheet: MetadataBottomSheet

    override val logMessages: List<String>
        get() = logging

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ScenarioActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        position = intent.getIntExtra(EXTRA_POSITION, -1)
        lifecycle.addObserver(screenRecord)
        metadataBottomSheet = MetadataBottomSheet()
        if (position >= 0) {
            lifecycleScope.launchWhenCreated {
                _scenario = repository.loadScenarioByPosition(position)
                bindScenario(_scenario)
                itemCount = repository.itemCount()
                invalidateOptionsMenu()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (position > 0) {
            menu?.add(Menu.NONE, PREV_ITEM_ID, Menu.NONE, R.string.prev_scenario)
        }
        if (position < itemCount - 1) {
            menu?.add(Menu.NONE, NEXT_ITEM_ID, Menu.NONE, R.string.next_scenario)
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.prepareOptionsMenu(PREV_ITEM_ID, R.drawable.previous)
        menu?.prepareOptionsMenu(NEXT_ITEM_ID, R.drawable.next)
        return true
    }

    private fun Menu.prepareOptionsMenu(itemId: Int, @DrawableRes icon: Int) {
        findItem(itemId)?.let {
            it.setIcon(icon)
            it.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            PREV_ITEM_ID -> {
                launchScenarioAtPosition(position - 1)
                true
            }
            NEXT_ITEM_ID -> {
                launchScenarioAtPosition(position + 1)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun initLogging() {
        logging.clear()
        logging += "*".repeat(10) + " Ready"
    }

    private fun launchScenarioAtPosition(position: Int) {
        shortMessage(this, R.string.loading_scenario)
        screenRecord.onStop(this)
        lifecycleScope.launch {
            screenRecord.recordState.collect { state ->
                when (state) {
                    ScreenRecord.RecordState.STARTED -> Unit
                    ScreenRecord.RecordState.STOPPED -> {
                        startActivity(
                            launchIntent(this@ScenarioActivity, position),
                            ActivityOptionsCompat.makeCustomAnimation(
                                this@ScenarioActivity,
                                android.R.anim.fade_in,
                                android.R.anim.fade_out
                            ).toBundle()
                        )
                        finish()
                    }
                }
            }
        }
    }

    private fun bindScenario(scenario: Scenario) {
        supportActionBar?.title = scenario.title
        binding.metadataButton.setOnClickListener {
            metadataBottomSheet.showNow(supportFragmentManager, MetadataBottomSheet.TAG)
            (metadataBottomSheet.dialog as BottomSheetDialog).behavior.run {
                state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        binding.singleContainer.isVisible = true
        binding.singleContainer.addView(
            div2ViewCreator.createDiv2View(
                this,
                "regression_test_data/${scenario.file}",
                binding.singleContainer,
                logDelegate,
            )
        )
        initLogging()
    }

    companion object {
        private fun launchIntent(context: Context, position: Int): Intent {
            return Intent(context, ScenarioActivity::class.java).apply {
                putExtra(EXTRA_POSITION, position)
            }
        }

        fun launch(context: Context, position: Int) {
            context.startActivity(launchIntent(context, position))
        }
    }
}
