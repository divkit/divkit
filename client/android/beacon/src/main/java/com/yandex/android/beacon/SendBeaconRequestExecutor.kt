package com.yandex.android.beacon

import androidx.annotation.WorkerThread
import java.io.IOException

/**
 * Responsible for send beacon request execution.
 */
interface SendBeaconRequestExecutor {

    /**
     * Executes given send beacon request.
     * @throws [IOException] during network communication or parsing error.
     */
    @WorkerThread
    @Throws(IOException::class)
    fun execute(request: SendBeaconRequest): SendBeaconResponse
}
