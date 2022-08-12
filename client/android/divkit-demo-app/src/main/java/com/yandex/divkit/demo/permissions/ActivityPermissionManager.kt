package com.yandex.divkit.demo.permissions

import android.app.Activity
import com.yandex.div.core.utils.PermissionUtils

/**
 * This implementation of [PermissionManager] requests permissions within a [Activity] so that [Activity.onRequestPermissionsResult]
 * will be called. Your [Activity] is responsible for callin' [.onPermissionsResult] on this event.
 */
class ActivityPermissionManager(private val activity: Activity) : PermissionManager(activity) {

    override fun showSystemRequestDialog(requestCode: Int, permissionsToRequest: Array<String>) =
        PermissionUtils.requestPermissions(activity, requestCode, permissionsToRequest)
}
