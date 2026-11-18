package com.yandex.div.compose.actions

import android.net.Uri
import androidx.core.net.toUri
import com.yandex.div.compose.patch.DivPatchDownloadManager
import com.yandex.div.internal.actions.DivUntypedAction
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionDownload
import com.yandex.div2.DivDownloadCallbacks
import dev.zacsweers.metro.Inject

internal class DownloadActionHandler @Inject constructor(
    private val actionHandler: Lazy<DivActionHandler>,
    private val downloadManager: DivPatchDownloadManager,
) {

    fun handle(
        context: DivActionHandlingContext,
        action: DivActionDownload,
    ) {
        download(
            context = context,
            url = action.url.evaluate(context.expressionResolver),
            onSuccessActions = action.onSuccessActions,
            onFailActions = action.onFailActions,
        )
    }

    fun handle(
        context: DivActionHandlingContext,
        action: DivUntypedAction.Download,
        callbacks: DivDownloadCallbacks?,
    ) {
        download(
            context = context,
            url = action.url.toUri(),
            onSuccessActions = callbacks?.onSuccessActions,
            onFailActions = callbacks?.onFailActions,
        )
    }

    private fun download(
        context: DivActionHandlingContext,
        url: Uri,
        onSuccessActions: List<DivAction>?,
        onFailActions: List<DivAction>?,
    ) {
        downloadManager.download(
            url,
            onSuccess = {
                actionHandler.value.handle(
                    context = context,
                    actions = onSuccessActions.orEmpty(),
                    source = DivActionSource.PATCH,
                )
            },
            onFail = {
                actionHandler.value.handle(
                    context = context,
                    actions = onFailActions.orEmpty(),
                    source = DivActionSource.PATCH,
                )
            }
        )
    }
}
