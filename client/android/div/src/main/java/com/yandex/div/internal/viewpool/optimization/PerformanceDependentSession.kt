package com.yandex.div.internal.viewpool.optimization

import androidx.annotation.AnyThread
import androidx.collection.ArrayMap
import com.yandex.div.core.annotations.PublicApi

@PublicApi
data class PerformanceDependentSession(
    private val viewObtainments: MutableMap<String, MutableList<ViewObtainment>> = ArrayMap()
) {
    data class ViewObtainment(
        val obtainmentTime: Long,
        val obtainmentDuration: Long,
        val availableViews: Int,
        val isObtainedWithBlock: Boolean
    )

    @AnyThread
    fun getViewObtainments(): Map<String, List<ViewObtainment>> = synchronized(viewObtainments) {
        viewObtainments.toMap()
    }

    @AnyThread
    internal fun viewObtained(
        viewType: String,
        obtainmentDuration: Long,
        availableViews: Int,
        isObtainedWithBlock: Boolean
    ): Unit = synchronized(viewObtainments) {
        viewObtainments.getOrPut(viewType) { mutableListOf() }.add(
            ViewObtainment(
                System.currentTimeMillis(),
                obtainmentDuration,
                availableViews,
                isObtainedWithBlock
            )
        )
    }

    @AnyThread
    internal fun clear(): Unit = synchronized(viewObtainments) {
        viewObtainments.clear()
    }
}
