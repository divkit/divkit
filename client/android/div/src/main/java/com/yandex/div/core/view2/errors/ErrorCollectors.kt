package com.yandex.div.core.view2.errors

import com.yandex.div.DivDataTag
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div2.DivData
import javax.inject.Inject

@DivScope
@Mockable
internal class ErrorCollectors @Inject constructor() {
    private val collectors = mutableMapOf<String, ErrorCollector>()

    fun getOrCreate(tag: DivDataTag, divData: DivData?): ErrorCollector = synchronized(collectors) {
        return collectors.getOrPut(tag.id) { ErrorCollector() }.apply {
            attachParsingErrors(divData)
        }
    }

    fun getOrNull(tag: DivDataTag, divData: DivData?): ErrorCollector? = synchronized(collectors) {
        return collectors[tag.id]?.apply {
            attachParsingErrors(divData)
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
