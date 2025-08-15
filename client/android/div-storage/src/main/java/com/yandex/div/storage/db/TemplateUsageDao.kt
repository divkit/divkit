package com.yandex.div.storage.db

import com.yandex.div.storage.entity.TemplateUsage

internal interface TemplateUsageDao {
    fun insertTemplateUsage(usage: TemplateUsage)
    fun deleteAllTemplateUsages()
    fun deleteTemplateUsages(cardId: String)
}
