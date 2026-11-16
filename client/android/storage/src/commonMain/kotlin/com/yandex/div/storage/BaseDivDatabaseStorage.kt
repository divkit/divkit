package com.yandex.div.storage

import com.yandex.div.storage.internal.WorkerThread
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.storage.database.DatabaseOpenHelper.Database
import com.yandex.div.storage.database.DatabaseOpenHelperProvider
import com.yandex.div.storage.db.DatabaseOpenHelper
import com.yandex.div.storage.db.TemplateDao
import com.yandex.div.storage.db.TemplateDaoImpl
import com.yandex.div.storage.db.TemplateUsageDao
import com.yandex.div.storage.db.TemplateUsageDaoImpl
import com.yandex.div.storage.entity.Template
import com.yandex.div.storage.entity.TemplateUsage

/**
 * Persistent div storage backed by the database.
 */
@InternalApi
public open class BaseDivDatabaseStorage(
    openHelperProvider: DatabaseOpenHelperProvider,
    databaseName: String = "div.db"
) : DivTemplateStorage, AutoCloseable {

    private val database: Database by lazy {
        DatabaseOpenHelper(openHelperProvider, databaseName).writableDatabase
    }
    private val templateDao: TemplateDao by lazy {
        TemplateDaoImpl(database)
    }
    private val templateUsageDao: TemplateUsageDao by lazy {
        TemplateUsageDaoImpl(database)
    }

    @WorkerThread
    public fun readAllTemplates(): Map<String, ByteArray> {
        return templateDao.getAllTemplates()
            .associate { template -> template.id to template.data }
    }

    @WorkerThread
    public override fun readTemplates(cardId: String): Map<String, ByteArray> {
        return templateDao.getTemplates(cardId)
            .associate { template -> template.id to template.data }
    }

    @WorkerThread
    public override fun readTemplatesByIds(vararg templateId: String): Map<String, ByteArray> {
        return templateDao.getTemplatesByIds(templateId.toList())
            .associate { template -> template.id to template.data }
    }

    @WorkerThread
    public override fun writeTemplates(cardId: String, templates: Map<String, ByteArray>): Unit {
        database.inTransaction {
            templateUsageDao.deleteTemplateUsages(cardId)
            templates.forEach { (templateId, templateData) ->
                templateDao.insertTemplate(Template(templateId, templateData))
                templateUsageDao.insertTemplateUsage(TemplateUsage(cardId, templateId))
            }
            templateDao.deleteUnusedTemplates()
        }
    }

    @WorkerThread
    public override fun deleteTemplates(cardId: String): Unit {
        database.inTransaction {
            templateUsageDao.deleteTemplateUsages(cardId)
            templateDao.deleteUnusedTemplates()
        }
    }

    @WorkerThread
    public override fun clear(): Unit {
        database.inTransaction {
            templateDao.deleteAllTemplates()
            templateUsageDao.deleteAllTemplateUsages()
        }
    }

    override fun close() {
        database.close()
    }

    private inline fun Database.inTransaction(block: () -> Unit) {
        beginTransaction()
        try {
            block()
            setTransactionSuccessful()
        } finally {
            endTransaction()
        }
    }
}
