package com.yandex.divkit.demo.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Encapsulates permissions requesting logic
 */
class DivkitDemoPermissionHelper(private val activity: Activity) {

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 101
        private const val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 102

        const val ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
        const val ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        private const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE

        fun requestReadExternalStoragePermission(activity: Activity, @StringRes rationale: Int) {
            if (ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return
            }

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, READ_EXTERNAL_STORAGE)) {
                Toast.makeText(activity, rationale, Toast.LENGTH_LONG).show()
            } else {
                ActivityCompat.requestPermissions(activity, arrayOf(READ_EXTERNAL_STORAGE), READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE)
            }
        }
    }

    private var locationPermissionGrantedCallback: Runnable? = null

    fun requestLocationPermission(callback: Runnable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            locationPermissionGrantedCallback = callback
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (isGranted(ACCESS_COARSE_LOCATION, permissions, grantResults) || isGranted(ACCESS_FINE_LOCATION, permissions, grantResults)) {
                    locationPermissionGrantedCallback?.run()
                    locationPermissionGrantedCallback = null
                }
            }
        }
    }

    private fun isGranted(permission: String, permissions: Array<out String>, grantResults: IntArray) =
            permissions.contains(permission) && grantResults[permissions.indexOf(permission)] == PackageManager.PERMISSION_GRANTED
}
