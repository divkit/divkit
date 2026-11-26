package com.yandex.div.storage.database

import android.content.Context
import com.yandex.div.storage.database.DatabaseOpenHelper.CreateCallback
import com.yandex.div.storage.database.DatabaseOpenHelper.UpgradeCallback
import com.yandex.div.storage.histogram.HistogramRecorder

internal class AndroidDatabaseOpenHelper(
    context: Context,
    name: String,
    version: Int,
    ccb: CreateCallback,
    ucb: UpgradeCallback,
    histogramRecorder: HistogramRecorder,
) : DatabaseOpenHelper by createAndroidDatabaseOpenHelper(
    context, name, version, ccb, ucb, histogramRecorder::reportDatabaseOpenTime,
)
