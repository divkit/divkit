package com.yandex.div.storage.db

import com.yandex.div.storage.database.DatabaseOpenHelper.Cursor
import com.yandex.div.storage.database.DatabaseOpenHelper.Database
import com.yandex.div.storage.db.TemplateQueries.DELETE_ALL_TEMPLATES_QUERY
import com.yandex.div.storage.db.TemplateQueries.DELETE_UNUSED_TEMPLATES_QUERY_TEMPLATE
import com.yandex.div.storage.db.TemplateQueries.GET_ALL_TEMPLATES_QUERY
import com.yandex.div.storage.db.TemplateQueries.GET_TEMPLATES_BY_CARD_ID_QUERY_TEMPLATE
import com.yandex.div.storage.db.TemplateQueries.GET_TEMPLATES_BY_IDS_QUERY_TEMPLATE_WITHOUT_PLACEHOLDER
import com.yandex.div.storage.db.TemplateQueries.INSERT_TEMPLATE_QUERY_TEMPLATE
import com.yandex.div.storage.entity.Template
import com.yandex.div.storage.util.getColumnIndexOrThrow

internal class TemplateDaoImpl(
    private val writableDatabase: Database
) : TemplateDao {

    override fun getAllTemplates(): List<Template> {
        return writableDatabase.rawQuery(GET_ALL_TEMPLATES_QUERY, emptyArray()).use {
            it.retrieveTemplates()
        }
    }

    override fun getTemplates(cardId: String): List<Template> {
        return writableDatabase.rawQuery(
            GET_TEMPLATES_BY_CARD_ID_QUERY_TEMPLATE, arrayOf(cardId)
        ).use { it.retrieveTemplates() }
    }

    override fun getTemplatesByIds(templateIds: List<String>): List<Template> {
        val queryBuilder = StringBuilder(GET_TEMPLATES_BY_IDS_QUERY_TEMPLATE_WITHOUT_PLACEHOLDER)
            .appendPlaceholders(templateIds.size)
        return writableDatabase.rawQuery(queryBuilder.toString(), templateIds.toTypedArray()).use {
            it.retrieveTemplates()
        }
    }

    override fun insertTemplate(template: Template) {
        writableDatabase.compileStatement(INSERT_TEMPLATE_QUERY_TEMPLATE).use {
            it.bindString(1, template.id)
            it.bindBlob(2, template.data)
            it.execute()
        }
    }

    override fun deleteAllTemplates() {
        writableDatabase.execSQL(DELETE_ALL_TEMPLATES_QUERY)
    }

    override fun deleteUnusedTemplates() {
        writableDatabase.execSQL(DELETE_UNUSED_TEMPLATES_QUERY_TEMPLATE)
    }

    private fun Cursor.getId() = getString(getColumnIndexOrThrow(TemplateEntity.TEMPLATE_ID))
    private fun Cursor.getData() = getBlob(getColumnIndexOrThrow(TemplateEntity.TEMPLATE_DATA))
    private fun Cursor.retrieveTemplates(): List<Template> {
        val templates = mutableListOf<Template>()
        while (moveToNext()) {
            val id = getId()
            val data = getData()
            templates.add(Template(id, data))
        }
        return templates
    }
}
