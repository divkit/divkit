package com.yandex.div.compose.patch

import com.yandex.div.compose.DivView
import com.yandex.div.compose.context.DivViewContextFactory
import com.yandex.div.compose.dagger.DivContextScope
import com.yandex.div.compose.dagger.Names
import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div2.DivData
import com.yandex.div2.DivPatch
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

/** Applies patches to layouts rendered by [DivView]. */
@ExperimentalApi
@DivContextScope
class DivPatcher @Inject internal constructor(
    @Named(Names.MAIN_COROUTINE_SCOPE)
    private val mainCoroutineScope: CoroutineScope,
    private val viewContextFactory: DivViewContextFactory,
) {
    /**
     * Applies [patch] to the layout associated with [data], preserving unchanged elements' state.
     *
     * Keep passing the original [data] to [DivView]. Subsequent patches update the current layout.
     * Returns whether the patch was applied. Success actions run after the updated layout is
     * composed and laid out; failure actions run before returning. If the card is not composed,
     * success actions wait until it is shown.
     * A transactional patch with missing targets leaves the layout unchanged.
     *
     * Can be called from any coroutine; layout and action updates run on the main thread.
     *
     *    val applied = divContext.patcher.applyPatch(data, patch)
     */
    suspend fun applyPatch(data: DivData, patch: DivPatch): Boolean {
        return withContext(mainCoroutineScope.coroutineContext.minusKey(Job)) {
            val viewContext = viewContextFactory.getOrCreate(data)
            viewContext.patchCoordinator.applyPatch(patch, viewContext.rootLocalComponent)
        }
    }
}
