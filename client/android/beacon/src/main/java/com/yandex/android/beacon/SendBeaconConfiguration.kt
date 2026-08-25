package com.yandex.android.beacon

import com.yandex.div.core.network.DivNetworkClient
import java.util.concurrent.Executor

class SendBeaconConfiguration
    @Deprecated("Use constructor with DivNetworkClient")
    constructor(
        val executor: Executor,
        val requestExecutor: SendBeaconRequestExecutor,
        val workerScheduler: SendBeaconWorkerScheduler,
        val perWorkerLogger: SendBeaconPerWorkerLogger,
        val databaseName: String,
    ) {
        constructor(
            executor: Executor,
            networkClient: DivNetworkClient,
            workerScheduler: SendBeaconWorkerScheduler,
            perWorkerLogger: SendBeaconPerWorkerLogger,
            databaseName: String,
        ) : this(
            executor,
            DivNetworkSendBeaconRequestExecutor(networkClient),
            workerScheduler,
            perWorkerLogger,
            databaseName,
        )
    }
