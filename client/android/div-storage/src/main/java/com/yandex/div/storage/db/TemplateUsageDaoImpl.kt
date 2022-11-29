package com.yandex.div.storage.db

import android.database.sqlite.SQLiteDatabase
import com.yandex.div.internal.Assert
import com.yandex.div.storage.db.TemplateUsageQueries.DELETE_ALL_TEMPLATE_USAGES_QUERY
import com.yandex.div.storage.db.TemplateUsageQueries.DELETE_TEMPLATE_USAGE_BY_CARD_ID_QUERY_TEMPLATE
import com.yandex.div.storage.db.TemplateUsageQueries.INSERT_TEMPLATE_USAGE_QUERY_TEMPLATE
import com.yandex.div.storage.entity.TemplateUsage

internal class TemplateUsageDaoImpl(
    private val writableDatabase: SQLiteDatabase
) : TemplateUsageDao {

    init {
        if (writableDatabase.isReadOnly) {
            Assert.fail("${this.javaClass.name} requires writable db!")
        }
    }

    override fun insertTemplateUsage(usage: TemplateUsage) {
        writableDatabase.execSQL(
            INSERT_TEMPLATE_USAGE_QUERY_TEMPLATE,
            arrayOf(usage.cardId, usage.templateId)
        )
    }

    override fun deleteAllTemplateUsages() {
        writableDatabase.execSQL(DELETE_ALL_TEMPLATE_USAGES_QUERY)
    }

    override fun deleteTemplateUsages(cardId: String) {
        writableDatabase.execSQL(DELETE_TEMPLATE_USAGE_BY_CARD_ID_QUERY_TEMPLATE, arrayOf(cardId))
    }
}
