package com.yandex.div

import android.content.Context

class AssetEnumerator(private val context: Context) {

    fun enumerate(path: String, predicate: (String) -> Boolean = { true }): List<String> {
        val (directories, files) = ls(path).map { "$path/$it" }.partition { it.isDirectory() }
        return arrayListOf<String>().apply {
            addAll(files.filter(predicate))
            addAll(directories.flatMap { enumerate(it, predicate) })
        }
    }

    private fun ls(path: String) = context.assets.list(path)!!

    private fun String.isDirectory() = ls(this).isNotEmpty()
}
