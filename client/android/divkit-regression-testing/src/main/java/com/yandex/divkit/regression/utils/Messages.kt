package com.yandex.divkit.regression.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun shortMessage(context: Context, @StringRes message: Int) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
