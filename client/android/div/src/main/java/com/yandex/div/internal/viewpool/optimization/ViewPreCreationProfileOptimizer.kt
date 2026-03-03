package com.yandex.div.internal.viewpool.optimization

import androidx.annotation.FloatRange
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.internal.KLog
import com.yandex.div.internal.Log
import com.yandex.div.internal.viewpool.PreCreationModel
import com.yandex.div.internal.viewpool.ViewPreCreationProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sign

private const val DEFAULT_CONVERGENCE_RATE = 0.6
private const val TAG = "ViewPreCreationProfileOptimizer"

object ViewPreCreationProfileOptimizer {
    @JvmStatic
    suspend fun optimize(
        profile: ViewPreCreationProfile,
        session: PerformanceDependentSession,
        @FloatRange(from = 0.0, to = 1.0, fromInclusive = false)
        convergenceRate: Double = DEFAULT_CONVERGENCE_RATE
    ): ViewPreCreationProfile = withContext(Dispatchers.Default) {
        val newProfile = profile.optimize(session.viewObtainmentStatistics, convergenceRate)

        if (Log.isEnabled()) {
            session.log(oldProfile = profile, newProfile = newProfile)
        }

        newProfile
    }

    private fun ViewPreCreationProfile.optimize(
        statistics: Map<String, PerformanceDependentSession.ViewObtainmentStatistics>,
        rate: Double
    ) = with(statistics) {
        ViewPreCreationProfile(
            id = id,
            text = text.optimizeForItem(get(DivViewCreator.TAG_TEXT), rate),
            image = image.optimizeForItem(get(DivViewCreator.TAG_IMAGE), rate),
            gifImage = gifImage.optimizeForItem(get(DivViewCreator.TAG_GIF_IMAGE), rate),
            overlapContainer = overlapContainer.optimizeForItem(get(DivViewCreator.TAG_OVERLAP_CONTAINER), rate),
            linearContainer = linearContainer.optimizeForItem(get(DivViewCreator.TAG_LINEAR_CONTAINER), rate),
            wrapContainer = wrapContainer.optimizeForItem(get(DivViewCreator.TAG_WRAP_CONTAINER), rate),
            grid = grid.optimizeForItem(get(DivViewCreator.TAG_GRID), rate),
            gallery = gallery.optimizeForItem(get(DivViewCreator.TAG_GALLERY), rate),
            pager = pager.optimizeForItem(get(DivViewCreator.TAG_PAGER), rate),
            tab = tab.optimizeForItem(get(DivViewCreator.TAG_TABS), rate),
            state = state.optimizeForItem(get(DivViewCreator.TAG_STATE), rate),
            custom = custom.optimizeForItem(get(DivViewCreator.TAG_CUSTOM), rate),
            indicator = indicator.optimizeForItem(get(DivViewCreator.TAG_INDICATOR), rate),
            slider = slider.optimizeForItem(get(DivViewCreator.TAG_SLIDER), rate),
            input = input.optimizeForItem(get(DivViewCreator.TAG_INPUT), rate),
            select = select.optimizeForItem(get(DivViewCreator.TAG_SELECT), rate),
            video = video.optimizeForItem(get(DivViewCreator.TAG_VIDEO), rate)
        )
    }

    private fun PreCreationModel.optimizeForItem(
        statistics: PerformanceDependentSession.ViewObtainmentStatistics?,
        convergenceRate: Double
    ): PreCreationModel {
        statistics ?: return this

        val delta = statistics.findOptimalDelta(capacity).run {
            sign * absoluteValue.toDouble().pow(convergenceRate)
        }

        return copy(capacity = (capacity + delta).roundToInt().coerceIn(min, max))
    }

    private fun PerformanceDependentSession.ViewObtainmentStatistics.findOptimalDelta(capacity: Int): Int =
        maxSuccessiveBlocked.takeUnless { it == 0 } ?: minUnused?.let { -it } ?: -capacity

    private fun PerformanceDependentSession.log(
        oldProfile: ViewPreCreationProfile,
        newProfile: ViewPreCreationProfile
    ) {
        when (this) {
            is PerformanceDependentSession.Lightweight -> {
                viewObtainmentStatistics.forEach { (key, value) ->
                    KLog.d(TAG) { "$key: $value" }
                }
            }

            is PerformanceDependentSession.Detailed -> {
                getViewObtainments().forEach { (key, value) ->
                    KLog.d(TAG) { key }
                    KLog.d(TAG) {
                        value.joinToString(separator = " ", prefix = "Obtained with block: ") {
                            if (it.isObtainedWithBlock) {
                                "1"
                            } else {
                                "0"
                            }
                        }
                    }
                    KLog.d(TAG) {
                        value.joinToString(separator = " ", prefix = "Available views left: ") {
                            it.availableViews.toString()
                        }
                    }
                }
            }
        }

        KLog.d(TAG) { oldProfile.toString() }
        KLog.d(TAG) { newProfile.toString() }
        KLog.d(TAG) { "Is profile changed: ${oldProfile != newProfile}" }
    }
}
