package com.yandex.div.compose.patch

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div.compose.dagger.Names
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@DivViewScope
internal class DivPatchDownloadManager @Inject constructor(
    private val downloader: DivPatchDownloader,
    @Named(Names.MAIN_COROUTINE_SCOPE)
    private val mainCoroutineScope: CoroutineScope,
    private val patchCoordinator: DivPatchCoordinator,
    private val reporter: DivReporter,
) {
    private val jobs = mutableSetOf<Job>()

    @Suppress("TooGenericExceptionCaught")
    fun download(url: Uri, onSuccess: () -> Unit, onFail: () -> Unit) {
        val job = mainCoroutineScope.launch(start = CoroutineStart.LAZY) {
            try {
                val patch = try {
                    downloader.downloadPatch(url)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    reporter.reportError(e)
                    onFail()
                    return@launch
                }
                patchCoordinator.applyPatch(patch, onSuccess)
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
}

@Composable
internal fun DivPatchDownloadManager.observe() {
    DisposableEffect(this) {
        onDispose { cancel() }
    }
}
