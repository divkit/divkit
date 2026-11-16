package com.yandex.div.storage.db

import com.yandex.div.storage.database.DatabaseOpenHelper.Database
import com.yandex.div.storage.db.TemplateUsageQueries.DELETE_ALL_TEMPLATE_USAGES_QUERY
import com.yandex.div.storage.db.TemplateUsageQueries.DELETE_TEMPLATE_USAGE_BY_CARD_ID_QUERY_TEMPLATE
import com.yandex.div.storage.db.TemplateUsageQueries.INSERT_TEMPLATE_USAGE_QUERY_TEMPLATE
import com.yandex.div.storage.entity.TemplateUsage

internal class TemplateUsageDaoImpl(
    private val writableDatabase: Database
) : TemplateUsageDao {

    override fun insertTemplateUsage(usage: TemplateUsage) {
        writableDatabase.compileStatement(INSERT_TEMPLATE_USAGE_QUERY_TEMPLATE).use {
            it.bindString(1, usage.cardId)
            it.bindString(2, usage.templateId)
            it.execute()
        }
    }

    override fun deleteAllTemplateUsages() {
        writableDatabase.execSQL(DELETE_ALL_TEMPLATE_USAGES_QUERY)
    }

    override fun deleteTemplateUsages(cardId: String) {
        writableDatabase.compileStatement(DELETE_TEMPLATE_USAGE_BY_CARD_ID_QUERY_TEMPLATE).use {
            it.bindString(1, cardId)
            it.execute()
        }
    }
}
