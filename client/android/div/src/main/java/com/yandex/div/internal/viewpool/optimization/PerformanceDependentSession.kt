package com.yandex.div.internal.viewpool.optimization

import androidx.annotation.AnyThread
import androidx.collection.ArrayMap
import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.core.view2.DivViewCreator
import kotlinx.serialization.Serializable

@PublicApi
@Serializable
data class PerformanceDependentSession(
    private val viewObtainments: MutableMap<String, MutableList<ViewObtainment>> = prepareArrayMap()
) {
    @Serializable
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

    private companion object {
        fun prepareArrayMap() = ArrayMap<String, MutableList<ViewObtainment>>().apply {
            arrayOf(
                DivViewCreator.TAG_TEXT,
                DivViewCreator.TAG_IMAGE,
                DivViewCreator.TAG_GIF_IMAGE,
                DivViewCreator.TAG_OVERLAP_CONTAINER,
                DivViewCreator.TAG_LINEAR_CONTAINER,
                DivViewCreator.TAG_WRAP_CONTAINER,
                DivViewCreator.TAG_GRID,
                DivViewCreator.TAG_GALLERY,
                DivViewCreator.TAG_PAGER,
                DivViewCreator.TAG_TABS,
                DivViewCreator.TAG_STATE,
                DivViewCreator.TAG_CUSTOM,
                DivViewCreator.TAG_INDICATOR,
                DivViewCreator.TAG_SLIDER,
                DivViewCreator.TAG_INPUT,
                DivViewCreator.TAG_SELECT,
                DivViewCreator.TAG_VIDEO
            ).forEach { this[it] = mutableListOf() }
        }
    }
}
