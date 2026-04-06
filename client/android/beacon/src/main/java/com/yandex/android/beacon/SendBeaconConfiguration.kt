package com.yandex.android.beacon

import java.util.concurrent.Executor

class SendBeaconConfiguration(
    val executor: Executor,
    val requestExecutor: SendBeaconRequestExecutor,
    val workerScheduler: SendBeaconWorkerScheduler,
    val perWorkerLogger: SendBeaconPerWorkerLogger,
    val databaseName: String
)
