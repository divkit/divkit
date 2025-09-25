package com.yandex.div.core.view2.errors

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

    fun getOrCreate(tag: DivDataTag, divData: DivData?): ErrorCollector = synchronized(collectors) {
        val bucket = collectors.getOrPut(tag.id) { mutableListOf() }
        return bucket.find { it.divData === divData }?.let { existingCollector ->
            existingCollector.attachParsingErrors()
            existingCollector
        } ?: run {
            ErrorCollector(divData, tag, errorsReporter).also { newCollector ->
                bucket.add(newCollector)
                newCollector.attachParsingErrors()
            }
        }
    }

    fun getOrNull(tag: DivDataTag, divData: DivData?): ErrorCollector? = synchronized(collectors) {
        return collectors[tag.id]?.find { it.divData === divData }?.apply {
            attachParsingErrors()
        }
    }

    fun reset(tags: List<DivDataTag>) {
        if (tags.isEmpty()) {
            collectors.clear()
        } else {
            tags.forEach { tag ->
                collectors.remove(tag.id)
            }
        }
    }
}
