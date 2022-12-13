package com.yandex.divkit.demo.permissions

import android.app.Activity
import android.content.DialogInterface
import android.util.SparseArray
import androidx.annotation.StringRes
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.util.SafeAlertDialog
import com.yandex.div.core.util.SafeAlertDialogBuilder
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.util.PermissionUtils
import com.yandex.divkit.demo.R

typealias PermissionRequestCallback = (PermissionRequestResult) -> Unit

/**
 * Activity-owned manager that simplifies common operations with permission requests.
 */
@Mockable
abstract class PermissionManager(private val activity: Activity) {

    private val listeners = SparseArray<PermissionRequestCallback>()
    private var alertDialog: SafeAlertDialog? = null

    /**
     * Returns value indicating if the given permission is granted.
     */
    fun hasPermission(permission: Permission) = PermissionUtils.hasPermission(activity, permission.permissionString)

    /**
     * Request required permissions.
     *
     * @param request specially formed request, describing all the desired permissions
     */
    fun requestPermissions(request: PermissionRequest) {
        KAssert.assertNotNull(listeners.get(request.requestCode)) {
            "Callback is not provided for request id: ${request.requestCode}"
        }

        if (request.areAllPermissionsGranted) {
            listeners.get(request.requestCode)?.invoke(FakeRequestResult(request))
                ?: throw IllegalStateException("Callback is not provided for request id: ${request.requestCode}")
            return
        }

        performRequest(request)
    }

    /**
     * Add permission listener.
     */
    fun setListener(requestCode: Int, listener: PermissionRequestCallback) {
        KAssert.assertNull(listeners.get(requestCode)) { "Listener can be set only once" }
        listeners.put(requestCode, listener)
    }

    /**
     * Remove permission listener.
     */
    fun removeListener(requestCode: Int) = listeners.remove(requestCode)

    private fun onCancelPermissionsResult(requestCode: Int, permissionStrings: List<String>) {
        listeners.get(requestCode)?.invoke(CancelRequestResult(permissionStrings))
    }

    private fun performRequest(request: PermissionRequest) {
        val notGrantedPermissions = request.notGrantedPermissions
        val permissionsToRequest = notGrantedPermissions.map { it.permissionString }.toList()

        if (tryShowExplainMessage(
                request.requestCode,
                request.explainMessageResId,
                request.explainMessage,
                permissionsToRequest
            )
        ) {
            return
        }

        showSystemRequestDialog(request.requestCode, permissionsToRequest)
    }

    private fun tryShowExplainMessage(
        requestCode: Int,
        explainMessageResId: Int,
        explainMessage: String?,
        permissionsToRequest: List<String>
    ): Boolean {
        if (explainMessageResId == 0 && explainMessage == null) return false
        showExplainMessage(requestCode, explainMessageResId, explainMessage, permissionsToRequest)
        return true
    }

    private val PermissionRequest.allPermissions get() = requiredPermissions.asSequence() + optionalPermissions.asSequence()
    private val PermissionRequest.notGrantedPermissions get() = allPermissions.filterNot { hasPermission(it) }
    private val PermissionRequest.areAllPermissionsGranted get() = allPermissions.all { hasPermission(it) }

    private fun showSystemRequestDialog(requestCode: Int, permissionsToRequest: List<String>) {
        showSystemRequestDialog(requestCode, permissionsToRequest.toTypedArray())
    }

    protected abstract fun showSystemRequestDialog(requestCode: Int, permissionsToRequest: Array<String>)

    /**
     * Is called when we try to check the permission and discover it is blocked.
     * By default this method shows the message. It tells that permission is blocked and could be enabled in the system settings.
     */
    fun showExplainMessage(
        requestCode: Int,
        @StringRes explainMessageResId: Int,
        explainMessage: String?,
        permissionsToRequest: List<String>
    ) {
        KAssert.assertTrue(explainMessageResId != 0 || explainMessage != null)
        KAssert.assertNull(alertDialog)  // There no sense to show multiple alert dialogs at once.
        val adb = SafeAlertDialogBuilder(activity)
        if (explainMessageResId != 0) {
            adb.setMessage(explainMessageResId)
        } else {
            adb.setMessage(explainMessage)
        }
        alertDialog = adb
            .setPositiveButton(R.string.button_permission_yes) { dialog, _ ->
                onDialogDismissed(dialog)
                showSystemRequestDialog(requestCode, permissionsToRequest)
            }
            .setNegativeButton(R.string.button_permission_no) { dialog, _ ->
                onDialogDismissed(dialog)
                onCancelPermissionsResult(requestCode, permissionsToRequest)
            }
            .setOnCancelListener { dialog ->
                onDialogDismissed(dialog)
                onCancelPermissionsResult(requestCode, permissionsToRequest)
            }.show()
    }

    private fun onDialogDismissed(dialog: DialogInterface) {
        if (alertDialog?.checkEqualReference(dialog) == true) {
            alertDialog = null
        }
    }

    private inner class ActualRequestResult(permissions: Array<out String>, grantResults: IntArray) :
        PermissionRequestResult {

        private val mGrantResults: PermissionUtils.GrantResults =
            PermissionUtils.parseGrantResults(permissions, grantResults)

        override fun isPermissionGranted(permission: Permission) =
            mGrantResults.isPermissionGranted(permission.permissionString) || hasPermission(permission)

        override fun isPermissionBlocked(permission: Permission) =
            mGrantResults.isDeniedWithDontAsk(activity, permission.permissionString)

        override fun areAllPermissionsGranted() = mGrantResults.areAllPermissionsGranted()
        override fun isAnyPermissionBlocked() = mGrantResults.isAnyPermissionDeniedWithDontAsk(activity)
        override fun isPermissionCanceled() = false

        override fun getBlockedPermissions(): Set<Permission> {
            return mGrantResults.allPermissions()
                .asSequence()
                .filter { mGrantResults.isDeniedWithDontAsk(activity, it) }
                .mapNotNull { Permission.find(it) }
                .toSet()
        }
    }

    private inner class FakeRequestResult(private val request: PermissionRequest) :
        PermissionRequestResult {

        override fun isPermissionGranted(permission: Permission) = request.allPermissions.contains(permission)
        override fun isPermissionBlocked(permission: Permission) = false
        override fun areAllPermissionsGranted() = true
        override fun isAnyPermissionBlocked() = false
        override fun isPermissionCanceled() = false
        override fun getBlockedPermissions() = emptySet<Permission>()
    }

    private inner class CancelRequestResult(private val permissions: List<String>) :
        PermissionRequestResult {

        private fun contains(permission: Permission) = permissions.contains(permission.permissionString)
        override fun isPermissionGranted(permission: Permission) = contains(permission) && hasPermission(permission)
        override fun isPermissionCanceled() = true

        override fun isPermissionBlocked(permission: Permission): Boolean {
            return !isPermissionGranted(permission) &&
                    !PermissionUtils.shouldShowRequestPermissionRationale(activity, setOf(permission.permissionString))
        }

        override fun areAllPermissionsGranted() = permissions.all { PermissionUtils.hasPermission(activity, it) }
        override fun isAnyPermissionBlocked() = permissions.any { PermissionUtils.isDeniedWithDontAsk(activity, it) }
        override fun getBlockedPermissions(): Set<Permission> {
            return permissions
                .asSequence()
                .filter { PermissionUtils.isDeniedWithDontAsk(activity, it) }
                .mapNotNull { Permission.find(it) }
                .toSet()
        }
    }
}
