package com.yandex.div.utils

import android.app.Activity
import android.view.View
import android.view.ViewGroup

inline val Activity.contentView: View?
    get() = findViewById<ViewGroup>(android.R.id.content)?.getChildAt(0)