package com.yandex.divkit.demo.permissions

/**
 * Notifies about permission request results.
 */
fun interface PermissionRequestListener {

    /**
     * Is called when the permission request is completed.
     */
    fun onResult(result: PermissionRequestResult)
}
