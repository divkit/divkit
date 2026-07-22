package com.yandex.divkit.demo.utils

internal class DownloadList<T> {

    private val activeDownloads = HashSet<T>()

    val size get() = activeDownloads.size

    fun add(download: T) {
        synchronized(this) {
            activeDownloads.add(download)
        }
    }

    fun remove(download: T) {
        synchronized(this) {
            activeDownloads.remove(download)
        }
    }

    fun clean() {
        synchronized(this) {
            activeDownloads.clear()
        }
    }
}
