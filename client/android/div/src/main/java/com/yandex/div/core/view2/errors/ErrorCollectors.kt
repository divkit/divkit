package com.yandex.div.core.view2.errors

import androidx.annotation.AnyThread
import com.yandex.div.DivDataTag
import com.yandex.div.core.DivErrorsReporter
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div2.DivData
import javax.inject.Inject

@DivScope
@Mockable
internal class ErrorCollectors @Inject constructor(
    private val errorsReporter: DivErrorsReporter,
) {
    private val collectors = mutableMapOf<String, MutableList<ErrorCollector>>()

    @AnyThread
    fun getOrCreate(tag: DivDataTag, divData: DivData?): ErrorCollector {
        val collector = synchronized(collectors) {
            val bucket = collectors.getOrPut(tag.id) { mutableListOf() }
            bucket.find { it.divData === divData }
                ?: ErrorCollector(divData, tag, errorsReporter).also { bucket.add(it) }
        }
        collector.attachParsingErrors()
        return collector
    }

    @AnyThread
    fun getOrNull(tag: DivDataTag, divData: DivData?): ErrorCollector? {
        val collector = synchronized(collectors) {
            collectors[tag.id]?.find { it.divData === divData }
        }
        collector?.attachParsingErrors()
        return collector
    }

    @AnyThread
    fun reset(tags: List<DivDataTag>): Unit = synchronized(collectors)  {
        if (tags.isEmpty()) {
            collectors.clear()
        } else {
            tags.forEach { tag ->
                collectors.remove(tag.id)
            }
        }
    }
}
