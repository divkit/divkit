package com.yandex.div.core

import android.app.Application
import com.yandex.div.core.util.Assert

class TestApplication : Application() {

    init {
        Assert.setEnabled(true)
    }
}
