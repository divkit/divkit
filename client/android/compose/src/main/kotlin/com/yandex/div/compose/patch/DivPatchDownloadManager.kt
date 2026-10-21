package com.yandex.div.compose.patch

import android.net.Uri
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.context.DivViewCoroutineScopeProvider
import com.yandex.div.compose.dagger.DivViewScope
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@DivViewScope
internal class DivPatchDownloadManager @Inject constructor(
    private val coroutineScopeProvider: DivViewCoroutineScopeProvider,
    private val downloader: DivPatchDownloader,
    private val patchCoordinator: DivPatchCoordinator,
    private val reporter: DivReporter,
) {
    @Suppress("TooGenericExceptionCaught")
    fun download(url: Uri, onSuccess: () -> Unit, onFail: () -> Unit) {
        coroutineScopeProvider.get()?.launch {
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
        }
    }
}
