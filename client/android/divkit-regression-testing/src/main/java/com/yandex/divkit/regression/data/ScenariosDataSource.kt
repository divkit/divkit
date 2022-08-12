package com.yandex.divkit.regression.data

import androidx.annotation.WorkerThread

interface ScenariosDataSource {

    @WorkerThread
    fun loadScenarios(): List<Scenario>
}
