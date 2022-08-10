package com.yandex.android.beacon

import com.yandex.div.core.annotations.PublicApi
import java.util.concurrent.Executor

@PublicApi
class SendBeaconConfiguration(
    val executor: Executor,
    val requestExecutor: SendBeaconRequestExecutor,
    val workerScheduler: SendBeaconWorkerScheduler,
    val perWorkerLogger: SendBeaconPerWorkerLogger,
    val databaseName: String
)
