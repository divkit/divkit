package com.yandex.divkit.regression.data

class FakeScenariosDataSource : ScenariosDataSource {

    private val scenarios = mutableListOf<Scenario>()

    override fun loadScenarios(): List<Scenario> = scenarios

    fun addScenario(scenario: Scenario) {
        scenarios += scenario
    }
}
