package com.yandex.divkit.regression.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScenariosRepository @Inject constructor(
    private val dataSource: ScenariosDataSource,
    private val ioDispatcher: CoroutineDispatcher
) {

    private var cache: List<Scenario>? = null

    suspend fun loadScenarios(): List<Scenario> = withContext(ioDispatcher) {
        cache ?: loadScenariosInternal()
    }

    suspend fun loadScenarioByPosition(position: Int): Scenario = withContext(ioDispatcher) {
        cache?.get(position) ?: loadScenariosInternal()[position]
    }

    suspend fun itemCount() = withContext(ioDispatcher) {
        cache?.size ?: loadScenariosInternal().size
    }

    suspend fun findScenarioByCaseId(caseId: Int): Scenario? = withContext(ioDispatcher) {
        loadScenariosInternal().firstOrNull { it.case_id == caseId }
    }

    suspend fun findScenarioByTitle(title: String): Scenario? = withContext(ioDispatcher) {
        loadScenariosInternal().firstOrNull {
            it.title.equals(title, ignoreCase = true)
        }
    }

    private fun loadScenariosInternal(): List<Scenario> {
        return dataSource.loadScenarios()
            .filter {
                (it.platforms.isEmpty() || it.platforms.contains(Platforms.android))
                        && !it.automated.contains(Platforms.android)
            }
            .sortedBy { it.title }
            .sortedBy { it.priority }
            .also {
                it.forEachIndexed { index, scenario -> scenario.position = index }
                cache = it
            }
    }
}
