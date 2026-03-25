package com.yandex.divkit.demo.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Encapsulates permissions requesting logic
 */
object DivkitDemoPermissionHelper {

    private const val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 102
    private const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE

    fun requestReadExternalStoragePermission(activity: Activity, @StringRes rationale: Int) {
        if (ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, READ_EXTERNAL_STORAGE)) {
            Toast.makeText(activity, rationale, Toast.LENGTH_LONG).show()
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
            )
        }
    }
}
