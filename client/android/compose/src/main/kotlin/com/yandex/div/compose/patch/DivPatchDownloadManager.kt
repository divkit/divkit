package com.yandex.div.compose.patch

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div2.DivPatch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@DivViewScope
internal class DivPatchDownloadManager @Inject constructor(
    private val downloader: DivPatchDownloader,
    private val coroutineScope: CoroutineScope,
    private val reporter: DivReporter
) {
    private lateinit var applyPatch: (DivPatch, () -> Unit) -> Boolean
    private val jobs = mutableSetOf<Job>()
    private var attachedViews = 0

    fun init(applyPatch: (DivPatch, () -> Unit) -> Boolean) {
        this.applyPatch = applyPatch
    }

    @Suppress("TooGenericExceptionCaught")
    fun download(url: Uri, onSuccess: () -> Unit, onFail: () -> Unit) {
        val job = coroutineScope.launch(start = CoroutineStart.LAZY) {
            try {
                val patch = try {
                    downloader.downloadPatch(url)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    currentCoroutineContext().ensureActive()
                    reporter.reportError(e)
                    onFail()
                    return@launch
                }
                currentCoroutineContext().ensureActive()
                applyPatch(patch, onSuccess)
            } finally {
                jobs.remove(currentCoroutineContext()[Job])
            }
        }
        jobs.add(job)
        job.start()
    }

    fun cancel() {
        val pendingJobs = jobs.toList()
        jobs.clear()
        pendingJobs.forEach { it.cancel() }
    }

    fun attach() {
        attachedViews++
    }

    fun detach() {
        if (--attachedViews == 0) cancel()
    }
}

@Composable
internal fun DivPatchDownloadManager.observe() {
    DisposableEffect(this) {
        attach()
        onDispose { detach() }
    }
}
