package com.yandex.div.internal.viewpool.optimization

import android.content.Context
import com.yandex.div.internal.KLog
import com.yandex.div.internal.viewpool.ConstrainedPreCreationProfile

internal typealias PerformanceDependentSessionList = MutableList<PerformanceDependentSession>

internal class PerformanceDependentSessionRepository(
    context: Context,
    profile: ConstrainedPreCreationProfile
) {
    fun get(): PerformanceDependentSessionList = runCatching {
        mutableListOf<PerformanceDependentSession>()
    }
        .onFailure { KLog.e(TAG, it) }
        .getOrElse { mutableListOf() }


    fun save(session: PerformanceDependentSession): Boolean = runCatching {
        false
    }
        .onFailure { KLog.e(TAG, it) }
        .isSuccess

    private companion object {
        const val TAG = "PerformanceDependentSessionRepository"
    }
}