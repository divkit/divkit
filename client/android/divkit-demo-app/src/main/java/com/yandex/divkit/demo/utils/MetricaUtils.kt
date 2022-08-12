package com.yandex.divkit.demo.utils

import android.content.Context
import com.yandex.metrica.IReporterInternal
import com.yandex.metrica.YandexMetricaInternal

object MetricaUtils {

    /**
     * AliceKit in AppMetrica: https://appmetrica.yandex.ru/application/edit?appId=1630031
     */
    private const val API_KEY = "e48dd638-f5ba-4cb8-b272-53b6d275062f"

    /**
     * Gets AppMetrica reporter for AliceKit components.
     */
    @JvmStatic
    fun getReporter(context: Context): IReporterInternal {
        return YandexMetricaInternal.getReporter(context.applicationContext, API_KEY)
    }
}
