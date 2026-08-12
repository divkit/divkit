package com.yandex.div.core.view2.errors

import androidx.annotation.AnyThread
import com.yandex.div.DivDataTag
import com.yandex.div.core.DivErrorsReporter
import com.yandex.div.core.dagger.DivDataScope
import com.yandex.div.core.dagger.Names
import com.yandex.div2.DivData
import javax.inject.Inject
import javax.inject.Named

@DivDataScope
internal class ErrorCollectors @Inject constructor(
    @param:Named(Names.DATA_TAG) private val dataTag: String,
    private val errorsReporter: DivErrorsReporter,
) {

    private val collectors = mutableListOf<ErrorCollector>()

    @AnyThread
    fun getOrCreate(divData: DivData?): ErrorCollector {
        val collector = synchronized(collectors) {
            collectors.find { it.divData === divData }
                ?: ErrorCollector(divData, DivDataTag(dataTag), errorsReporter).also { collectors.add(it) }
        }
        collector.attachParsingErrors()
        return collector
    }

    @AnyThread
    fun getOrNull(divData: DivData?): ErrorCollector? {
        val collector = synchronized(collectors) {
            collectors.find { it.divData === divData }
        }
        collector?.attachParsingErrors()
        return collector
    }

    @AnyThread
    fun reset(): Unit = synchronized(collectors)  {
        collectors.clear()
    }
}
