package com.yandex.divkit.regression.data

import android.content.Context
import androidx.annotation.WorkerThread
import com.google.gson.Gson
import com.yandex.div.internal.util.forEach
import com.yandex.divkit.regression.utils.AssetReader
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetScenariosDataSource @Inject constructor(
    private val context: Context
) : ScenariosDataSource {

    @WorkerThread
    override fun loadScenarios(): List<Scenario> {
        val scenarios = mutableListOf<Scenario>()
        AssetReader(context).readJson("regression_test_data/index.json")
            .getJSONArray("tests")
            .forEach { _, jsonObject: JSONObject ->
                scenarios.add(Gson().fromJson(jsonObject.toString(), Scenario::class.java))
            }
        return scenarios
    }
}
