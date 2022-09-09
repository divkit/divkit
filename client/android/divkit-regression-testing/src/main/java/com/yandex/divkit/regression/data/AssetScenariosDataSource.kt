package com.yandex.divkit.regression.data

import android.content.Context
import androidx.annotation.WorkerThread
import com.google.gson.Gson
import com.yandex.div.core.utils.IOUtils
import com.yandex.div.json.forEach
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetScenariosDataSource @Inject constructor(
    private val context: Context
) : ScenariosDataSource {

    @WorkerThread
    override fun loadScenarios(): List<Scenario> {
        val testJson = JSONObject(IOUtils.toString(context.assets.open("regression_test_data/index.json")))
        val scenarios = ArrayList<Scenario>()
        testJson.getJSONArray("tests").forEach { i, jsonObject: JSONObject ->
            scenarios.add(Gson().fromJson(jsonObject.toString(), Scenario::class.java))
        }
        return scenarios
    }
}
