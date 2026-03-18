package com.yandex.div

import android.content.Context

class AssetEnumerator(private val context: Context) {

    fun enumerate(
        path: String,
        predicate: (String) -> Boolean = { true }
    ): List<String> {
        return enumerate(path = path, predicate = predicate, recursive = true)
    }

    fun enumerate(
        path: String,
        predicate: (String) -> Boolean = { true },
        recursive: Boolean = true
    ): List<String> {
        val (directories, files) = ls(path).map { "$path/$it" }.partition { it.isDirectory() }
        return arrayListOf<String>().apply {
            addAll(files.filter(predicate))
            if (recursive) {
                addAll(directories.flatMap { enumerate(it, predicate, recursive = true) })
            }
        }
    }

    private fun ls(path: String) = context.assets.list(path)!!

    private fun String.isDirectory() = ls(this).isNotEmpty()
}
