package com.yandex.test.screenshot

import android.annotation.SuppressLint
import androidx.test.services.storage.TestStorage
import java.io.OutputStream

@JvmInline
@SuppressLint("UnsafeOptInUsageError")
value class TestFile(val path: String) {
    fun open(): OutputStream = testStorage.openOutputFile(path)

    private companion object {
        val testStorage: TestStorage = TestStorage()
    }
}
