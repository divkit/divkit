package com.yandex.div

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert

class AssetEnumerator {
    private val context: Context = ApplicationProvider.getApplicationContext()

    fun enumerate(path: String): List<String> {
        val (directories, files) = ls(path)
            .map { "$path/$it" }
            .partition { it.isDirectory() }
        val allFiles = mutableListOf<String>().apply {
            addAll(files)
            addAll(directories.flatMap { enumerate(it) })
        }
        if (allFiles.isEmpty()) {
            Assert.fail("No files at: $path")
        }
        return allFiles
    }

    private fun ls(path: String) = context.assets.list(path)!!

    private fun String.isDirectory() = ls(this).isNotEmpty()
}
