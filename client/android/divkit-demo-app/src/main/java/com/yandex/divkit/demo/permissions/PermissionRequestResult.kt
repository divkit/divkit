package com.yandex.divkit.demo.permissions

import com.yandex.divkit.demo.permissions.Permission

/**
 * Represents a result of permission request.
 */
interface PermissionRequestResult {

    /**
     * @return true when given permission was granted by user
     */
    fun isPermissionGranted(permission: Permission): Boolean

    /**
     * @return true when given permission was denied by user with don't ask
     */
    fun isPermissionBlocked(permission: Permission): Boolean

    /**
     * @return true when any permission in the result was denied by user with don't ask
     */
    fun isAnyPermissionBlocked(): Boolean

    /**
     * @return true if user clicks on "later" button
     */
    fun isPermissionCanceled(): Boolean

    /**
     * @return true when all of the permissions in the result are granted by user
     */
    fun areAllPermissionsGranted(): Boolean

    /**
     * @return list of the permissions, that were denied by user with don't ask
     */
    fun getBlockedPermissions(): Set<Permission>
}
