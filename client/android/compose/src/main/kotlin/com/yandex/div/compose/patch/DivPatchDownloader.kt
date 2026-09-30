package com.yandex.div.compose.patch

import android.net.Uri
import com.yandex.div2.DivPatch

/**
 * Loads and parses patches requested by `download` actions.
 *
 * Configure this in [com.yandex.div.compose.DivConfiguration]. The renderer applies the returned
 * patch and invokes its actions and the download callbacks. Implementations must not block the
 * calling thread and should cancel their request when the calling coroutine is cancelled.
 */
fun interface DivPatchDownloader {
    suspend fun downloadPatch(url: Uri): DivPatch
}
