package com.yandex.div.storage

import android.content.Context
import com.yandex.div.histogram.reporter.HistogramReporterDelegate
import com.yandex.div.storage.database.AndroidDatabaseOpenHelper
import com.yandex.div.storage.histogram.HistogramRecorder
import java.io.Closeable

/**
 * Persistent div storage backed by the database.
 */
class DivDatabaseStorage @JvmOverloads constructor(
    context: Context,
    databaseName: String = "div.db",
) : BaseDivDatabaseStorage(
    openHelperProvider = { name, version, create, upgrade ->
        AndroidDatabaseOpenHelper(
            context, name, version, create, upgrade,
            HistogramRecorder(HistogramReporterDelegate.NoOp, null),
        )
    },
    databaseName = databaseName,
), Closeable
