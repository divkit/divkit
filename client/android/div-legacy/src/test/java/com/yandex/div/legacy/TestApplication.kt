package com.yandex.div.legacy

import android.app.Application
import com.yandex.alicekit.core.utils.Assert

class TestApplication : Application() {

    init {
        Assert.setEnabled(true)
    }
}
