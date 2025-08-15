package com.yandex.div.internal.viewpool.optimization

import androidx.annotation.AnyThread
import androidx.collection.ArrayMap
import com.yandex.div.core.view2.DivViewCreator
import kotlinx.serialization.Serializable

sealed class PerformanceDependentSession {
    sealed class ViewObtainmentStatistics {
        abstract val maxSuccessiveBlocked: Int
        abstract val minUnused: Int?

        override fun toString(): String =
            "ViewObtainmentStatistics(maxSuccessiveBlocked=$maxSuccessiveBlocked, minUnused=$minUnused)"
    }

    abstract val viewObtainmentStatistics: Map<String, ViewObtainmentStatistics>

    @AnyThread
    internal abstract fun viewObtained(
        viewType: String,
        obtainmentDuration: Long,
        availableViews: Int,
        isObtainedWithBlock: Boolean
    )

    @AnyThread
    internal abstract fun clear()

    class Lightweight : PerformanceDependentSession() {
        override val viewObtainmentStatistics: Map<String, ViewObtainmentStatistics>
            get() = mutableViewObtainmentStatistics

        private val mutableViewObtainmentStatistics =
            ArrayMap<String, MutableViewObtainmentStatistics>().prepare { MutableViewObtainmentStatistics() }

        override fun viewObtained(
            viewType: String,
            obtainmentDuration: Long,
            availableViews: Int,
            isObtainedWithBlock: Boolean
        ) {
            mutableViewObtainmentStatistics[viewType]?.report(availableViews, isObtainedWithBlock)
        }

        override fun clear() = mutableViewObtainmentStatistics.values.forEach { it.clear() }
    }

    class Detailed : PerformanceDependentSession() {
        @Serializable
        data class ViewObtainment(
            val obtainmentTime: Long,
            val obtainmentDuration: Long,
            val availableViews: Int,
            val isObtainedWithBlock: Boolean
        )

        override val viewObtainmentStatistics: Map<String, ViewObtainmentStatistics>
            get() = viewObtainments.mapValues { (_, value) ->
                MutableViewObtainmentStatistics().apply {
                    value.forEach { report(it.availableViews, it.isObtainedWithBlock) }
                }
            }

        private val viewObtainments =
            ArrayMap<String, MutableList<ViewObtainment>>().prepare { mutableListOf() }

        fun getViewObtainments(): Map<String, List<ViewObtainment>> = viewObtainments.mapValues { (_, value) ->
            value.toList()
        }

        override fun viewObtained(
            viewType: String,
            obtainmentDuration: Long,
            availableViews: Int,
            isObtainedWithBlock: Boolean
        ) {
            val obtainmentList = viewObtainments[viewType] ?: return

            val obtainment = ViewObtainment(
                obtainmentTime = System.currentTimeMillis(),
                obtainmentDuration = obtainmentDuration,
                availableViews = availableViews,
                isObtainedWithBlock = isObtainedWithBlock
            )

            synchronized(obtainmentList) { obtainmentList.add(obtainment) }
        }

        override fun clear() = viewObtainments.values.forEach { synchronized(it) { it.clear() } }
    }

    private companion object {
        inline fun <T : Any> ArrayMap<String, T>.prepare(defaultValue: () -> T): ArrayMap<String, T> =
            DivViewCreator.TAGS.associateWithTo(this) { defaultValue() }
    }
}

private class MutableViewObtainmentStatistics : PerformanceDependentSession.ViewObtainmentStatistics() {
    override var maxSuccessiveBlocked: Int = 0
    private var currentSuccessiveBlocked: Int = 0

    override var minUnused: Int? = null

    fun report(availableViews: Int, isObtainedWithBlock: Boolean) {
        synchronized(this) {
            if (isObtainedWithBlock) {
                ++currentSuccessiveBlocked
                if (currentSuccessiveBlocked > maxSuccessiveBlocked) {
                    maxSuccessiveBlocked = currentSuccessiveBlocked
                }
            } else {
                currentSuccessiveBlocked = 0

                if (minUnused == null || availableViews < minUnused!!) {
                    minUnused = availableViews
                }
            }
        }
    }

    fun clear() {
        synchronized(this) {
            maxSuccessiveBlocked = 0
            currentSuccessiveBlocked = 0
            minUnused = 0
        }
    }
}
