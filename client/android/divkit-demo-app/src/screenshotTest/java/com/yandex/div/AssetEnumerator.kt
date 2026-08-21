package com.yandex.div

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert

class AssetEnumerator {
    private val context: Context = ApplicationProvider.getApplicationContext()

    fun enumerate(path: String): List<String> {
        return enumerateAll(path)
    }

    fun requireSelectedCase(cases: List<String>): List<String> {
        val selectedCase = selectedCase ?: return cases
        val selected = cases.filter { it == selectedCase }
        require(selected.isNotEmpty()) {
            "Selected test case is not supported by this suite: $selectedCase"
        }
        return selected
    }

    private fun enumerateAll(path: String): List<String> {
        val (directories, files) = ls(path)
            .map { "$path/$it" }
            .partition { it.isDirectory() }
        val allFiles = mutableListOf<String>().apply {
            addAll(files)
            addAll(directories.flatMap { enumerateAll(it) })
        }
        if (allFiles.isEmpty()) {
            Assert.fail("No files at: $path")
        }
        return allFiles
    }

    private fun ls(path: String) = context.assets.list(path)!!

    private fun String.isDirectory() = ls(this).isNotEmpty()

    private val selectedCase: String?
        get() = InstrumentationRegistry.getArguments()
            .getString(TEST_FILTER_ARGUMENT)
            ?.takeIf { it.isNotBlank() }

    private companion object {
        const val TEST_FILTER_ARGUMENT = "divkitTestFilter"
    }
}
