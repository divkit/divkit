package com.yandex.div.storage

import com.yandex.div.storage.internal.WorkerThread

public interface DivTemplateStorage {

    @WorkerThread
    public fun readTemplates(cardId: String): Map<String, ByteArray>

    @WorkerThread
    public fun readTemplatesByIds(vararg templateId: String): Map<String, ByteArray>

    @WorkerThread
    public fun writeTemplates(cardId: String, templates: Map<String, ByteArray>)

    @WorkerThread
    public fun deleteTemplates(cardId: String)

    @WorkerThread
    public fun clear()
}
