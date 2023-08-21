package com.yandex.divkit.regression

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy
import com.yandex.div.internal.Assert
import com.yandex.divkit.regression.databinding.RegressionActivityBinding
import com.yandex.divkit.regression.di.provideDiv2ViewCreator
import kotlinx.coroutines.flow.FlowCollector

private const val TAG_FILTER_MENU_ID = 1

class RegressionActivity : AppCompatActivity() {

    private lateinit var binding: RegressionActivityBinding
    private val scenarioListAdapter by lazy(LazyThreadSafetyMode.NONE) {
        ScenarioListAdapter(this, provideDiv2ViewCreator())
    }

    private val regressionViewModel: RegressionViewModel by viewModels {
        RegressionViewModel.Factory(this)
    }

    private var tagFilter: Map<String, Boolean> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegressionActivityBinding.inflate(layoutInflater)

        setSupportActionBar(binding.regressionToolbar)
        binding.toolbarLayout.title = getString(R.string.regression_label)
        val config: Configuration = resources.configuration
        val icon = if (config.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            R.drawable.ic_back_rtl
        } else {
            R.drawable.ic_back
        }
        binding.regressionToolbar.setNavigationIcon(icon)
        binding.toolbarLayout.setCollapsedTitleTextColor(Color.BLACK)
        binding.regressionToolbar.setNavigationOnClickListener { onBackPressed() }

        with(binding.scenarioList) {
            addItemDecoration(DividerItemDecoration(this@RegressionActivity, 0))
            layoutManager = LinearLayoutManager(this@RegressionActivity)
            adapter = scenarioListAdapter.also {
                it.stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
        }

        setContentView(binding.root)
        lifecycleScope.launchWhenCreated {
            regressionViewModel.uiState.collect(object : FlowCollector<RegressionUiState> {
                override suspend fun emit(uiState: RegressionUiState) {
                    when (uiState) {
                        RegressionUiState.Loading -> Unit
                        is RegressionUiState.Data -> {
                            tagFilter = uiState.tagFilter
                            scenarioListAdapter.submitList(uiState.scenarios) {
                                binding.scenarioList.scrollToPosition(0)
                            }
                        }
                    }
                }
            })
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (menu == null) return super.onPrepareOptionsMenu(menu)
        val tagFilterSubMenu = menu.findItem(TAG_FILTER_MENU_ID).subMenu
        Assert.assertNotNull(tagFilterSubMenu)
        tagFilterSubMenu?.clear()

        tagFilter.entries.forEachIndexed { index, (tag, checked) ->
            tagFilterSubMenu?.add(TAG_FILTER_MENU_ID, index, Menu.NONE, tag)?.apply {
                isCheckable = true
                isChecked = checked
            }
        }
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.groupId == TAG_FILTER_MENU_ID) {
            val selected = tagFilter[item.title]!!
            regressionViewModel.updateTagFilter(item.title.toString(), !selected)
            return false
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.addSubMenu(Menu.NONE, TAG_FILTER_MENU_ID, Menu.NONE, "Filter by tag")
        return true
    }
}
