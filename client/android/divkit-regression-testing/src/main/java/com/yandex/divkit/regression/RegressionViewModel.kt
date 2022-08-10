package com.yandex.divkit.regression

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yandex.divkit.regression.data.ScenariosRepository
import com.yandex.divkit.regression.di.provideScenariosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class RegressionViewModel(private val repository: ScenariosRepository) : ViewModel() {

    private val mutableUiState = MutableStateFlow<RegressionUiState>(RegressionUiState.Loading)

    val uiState: StateFlow<RegressionUiState>
        get() = mutableUiState

    private val tagFilter: MutableMap<String, Boolean> = mutableMapOf()

    init {
        viewModelScope.launch {
            val scenarios = repository.loadScenarios()
            val availableTags = scenarios.flatMap { scenario -> scenario.tags }.distinct().sorted()
            tagFilter.putAll(availableTags.map { it to false })

            mutableUiState.value = RegressionUiState.Data(scenarios = scenarios, tagFilter)
        }
    }

    fun updateTagFilter(tag: String, isSelected: Boolean) {
        tagFilter[tag] = isSelected
        viewModelScope.launch {
            val selectedTags = tagFilter.filter { it.value }.map { it.key }

            val scenarios = repository.loadScenarios().filter { scenario ->
                scenario.tags.containsAll(selectedTags)
            }

            mutableUiState.value = RegressionUiState.Data(scenarios = scenarios, tagFilter)
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(RegressionViewModel::class.java)) {
                RegressionViewModel(context.provideScenariosRepository()) as T
            } else {
                error("Unsupported modelClass $modelClass")
            }
        }
    }
}
