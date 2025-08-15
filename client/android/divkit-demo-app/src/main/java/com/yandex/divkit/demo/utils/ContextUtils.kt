package com.yandex.divkit.demo.utils

import android.content.Context
import android.net.ConnectivityManager
import android.view.WindowManager

val Context.connectivityManager: ConnectivityManager
    get() = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

val Context.windowManager: WindowManager
    get() = getSystemService(Context.WINDOW_SERVICE) as WindowManager