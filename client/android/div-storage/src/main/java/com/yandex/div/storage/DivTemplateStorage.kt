package com.yandex.div.storage

import androidx.annotation.WorkerThread
import com.yandex.div.core.annotations.PublicApi

@PublicApi
interface DivTemplateStorage {

    @WorkerThread
    fun readTemplates(cardId: String): Map<String, ByteArray>

    @WorkerThread
    fun readTemplatesByIds(vararg templateId: String): Map<String, ByteArray>

    @WorkerThread
    fun writeTemplates(cardId: String, templates: Map<String, ByteArray>)

    @WorkerThread
    fun deleteTemplates(cardId: String)

    @WorkerThread
    fun clear()
}
