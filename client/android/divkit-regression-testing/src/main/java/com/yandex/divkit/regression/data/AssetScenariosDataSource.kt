package com.yandex.divkit.regression.data

import android.content.Context
import androidx.annotation.WorkerThread
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetScenariosDataSource @Inject constructor(
    private val context: Context
) : ScenariosDataSource {

    private val scenariosYaml = Yaml(Constructor(Scenario::class.java))

    @WorkerThread
    override fun loadScenarios(): List<Scenario> {
        return scenariosYaml.loadAll(context.assets.open("scenarios.yaml")).map { it as Scenario }
    }
}
