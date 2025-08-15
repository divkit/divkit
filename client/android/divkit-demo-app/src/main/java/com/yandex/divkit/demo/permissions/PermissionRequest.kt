package com.yandex.divkit.demo.permissions

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes

/**
 * Unified representation of permission
 *
 * Should be passed to PermissionManager, when need to request permissions.
 */
data class PermissionRequest(
    val requestCode: Int,
    val requiredPermissions: List<Permission>,
    val optionalPermissions: List<Permission> = emptyList(),
    @StringRes val explainMessageResId: Int = 0,
    val explainMessage: String? = null,
)

// For java, which lacks named arguments and fast list builders
class PermissionRequestBuilder {
    private var requestCode = -1
    private val requiredPermissions = mutableListOf<Permission>()
    private val optionalPermissions = mutableListOf<Permission>()
    @StringRes
    private var explainMessageResId = 0
    private var explainMessage: String? = null

    fun requestCode(requestCode: Int): PermissionRequestBuilder {
        this.requestCode = requestCode
        return this
    }

    fun requiredPermission(permission: Permission): PermissionRequestBuilder {
        requiredPermissions.add(permission)
        return this
    }

    fun optionalPermission(permission: Permission): PermissionRequestBuilder {
        optionalPermissions.add(permission)
        return this
    }

    fun explainMessageResId(@StringRes explainMessageResId: Int): PermissionRequestBuilder {
        this.explainMessageResId = explainMessageResId
        return this
    }

    fun explainMessage(explainMessage: String): PermissionRequestBuilder {
        this.explainMessage = explainMessage
        return this
    }

    fun build() =
        PermissionRequest(
            requestCode = requestCode.takeUnless { it == -1 }
                ?: throw IllegalArgumentException("requestCode is required"),
            requiredPermissions = requiredPermissions.toList(),
            optionalPermissions = optionalPermissions.toList(),
            explainMessageResId = explainMessageResId,
            explainMessage = explainMessage
        )
}

enum class Permission(val permissionString: String) {
    ACCESS_COARSE_LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION),
    ACCESS_FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION),
    // Required by SearchApp for permission URI handling.
    @Suppress("unused")
    @RequiresApi(Build.VERSION_CODES.Q)
    ACCESS_BACKGROUND_LOCATION(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
    RECORD_AUDIO(Manifest.permission.RECORD_AUDIO),
    READ_CONTACTS(Manifest.permission.READ_CONTACTS),
    CALL_PHONE(Manifest.permission.CALL_PHONE),
    READ_EXTERNAL_STORAGE(Manifest.permission.READ_EXTERNAL_STORAGE),
    WRITE_EXTERNAL_STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE),
    CAMERA(Manifest.permission.CAMERA),
    READ_PHONE_STATE(Manifest.permission.READ_PHONE_STATE),
    ;

    companion object {
        fun find(permissionString: String) = values().find { it.permissionString == permissionString }
    }
}
