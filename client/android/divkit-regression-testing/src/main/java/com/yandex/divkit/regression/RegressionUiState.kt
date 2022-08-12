package com.yandex.divkit.regression

import com.yandex.divkit.regression.data.Scenario

internal sealed class RegressionUiState {
    object Loading : RegressionUiState()
    data class Data(
        val scenarios: List<Scenario>,
        val tagFilter: Map<String, Boolean>
    ) : RegressionUiState()
}