package com.yandex.div.internal.viewpool.optimization

import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.internal.KLog
import com.yandex.div.internal.viewpool.PreCreationModel
import com.yandex.div.internal.viewpool.ViewPreCreationProfile
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

private typealias ViewObtainments = List<PerformanceDependentSession.ViewObtainment>

internal class ViewPreCreationProfileOptimizer(
    private val sessionRepository: PerformanceDependentSessionRepository,
    private val profileRepository: OptimizedViewPreCreationProfileRepository,
    private val constraints: ViewPreCreationProfile,
    private val executorService: ExecutorService
) {
    private var future: Future<*>? = null

    fun optimize() {
        future?.cancel(true)

        future = executorService.submit {
            val data = sessionRepository.get()
                .flatMap { it.getViewObtainments().entries }
                .groupBy(keySelector = { it.key }, valueTransform = { it.value })

            val currentProfile = profileRepository.get()

            val newProfile = runCatching { data.createNewProfile(currentProfile) }
                .onFailure { KLog.e(TAG, it) }
                .getOrNull()

            KLog.d(TAG) { currentProfile.toString() }
            KLog.d(TAG) { newProfile.toString() }
            KLog.d(TAG) { (currentProfile == newProfile).toString() }

            newProfile?.let {
                profileRepository.save(it)
            }
        }
    }

    private fun Map<String, List<ViewObtainments>>.createNewProfile(old: ViewPreCreationProfile) =
        constraints.let {
            ViewPreCreationProfile(
                text = get(DivViewCreator.TAG_TEXT).optimizeForItem(old.text, it.text),
                image = get(DivViewCreator.TAG_IMAGE).optimizeForItem(old.image, it.image),
                gifImage = get(DivViewCreator.TAG_GIF_IMAGE).optimizeForItem(old.gifImage, it.gifImage),
                overlapContainer = get(DivViewCreator.TAG_OVERLAP_CONTAINER).optimizeForItem(old.overlapContainer, it.overlapContainer),
                linearContainer = get(DivViewCreator.TAG_LINEAR_CONTAINER).optimizeForItem(old.linearContainer, it.linearContainer),
                wrapContainer = get(DivViewCreator.TAG_WRAP_CONTAINER).optimizeForItem(old.wrapContainer, it.wrapContainer),
                grid = get(DivViewCreator.TAG_GRID).optimizeForItem(old.grid, it.grid),
                gallery = get(DivViewCreator.TAG_GALLERY).optimizeForItem(old.gallery, it.gallery),
                pager = get(DivViewCreator.TAG_PAGER).optimizeForItem(old.pager, it.pager),
                tab = get(DivViewCreator.TAG_TABS).optimizeForItem(old.tab, it.tab),
                state = get(DivViewCreator.TAG_STATE).optimizeForItem(old.state, it.state),
                custom = get(DivViewCreator.TAG_CUSTOM).optimizeForItem(old.custom, it.custom),
                indicator = get(DivViewCreator.TAG_INDICATOR).optimizeForItem(old.indicator, it.indicator),
                slider = get(DivViewCreator.TAG_SLIDER).optimizeForItem(old.slider, it.slider),
                input = get(DivViewCreator.TAG_INPUT).optimizeForItem(old.input, it.input),
                select = get(DivViewCreator.TAG_SELECT).optimizeForItem(old.select, it.select),
                video = get(DivViewCreator.TAG_VIDEO).optimizeForItem(old.video, it.video)
            )
        }

    private fun List<ViewObtainments>?.optimizeForItem(
        old: PreCreationModel,
        constraints: PreCreationModel
    ) = constraints.copy(capacity = run {
        if (this == null || size < 3) {
            return@run old.capacity
        }

        val recent = takeLast(3).filterNot { it.isEmpty() }

        val starvationTimes = recent.count { obtainments ->
            val obtainedWithBlock = obtainments.count { it.isObtainedWithBlock }

            STARVATION_THRESHOLD * obtainedWithBlock >= obtainments.size
        }

        val usedFully = recent.count { obtainments ->
            val usedFully = obtainments.count { it.availableViews == 0 }

            REDUNDANCY_THRESHOLD * usedFully >= obtainments.size
        }

        old.capacity + when (starvationTimes) {
            0 -> when (usedFully) {
                0, 1 -> -1
                else -> 0
            }

            3 -> +1
            else -> 0
        }
    }.coerceIn(constraints.min, constraints.max))

    private companion object {
        const val STARVATION_THRESHOLD = 10
        const val REDUNDANCY_THRESHOLD = 5

        const val TAG = "ViewPreCreationProfileOptimizer"
    }
}
