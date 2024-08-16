package com.yandex.div

import android.app.Application
import com.yandex.div.internal.Assert

class TestApplication : Application() {

    init {
        Assert.setEnabled(true)
    }
}
