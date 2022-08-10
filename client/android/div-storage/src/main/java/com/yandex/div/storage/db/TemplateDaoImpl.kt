package com.yandex.div.storage.db

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.yandex.div.core.util.Assert
import com.yandex.div.storage.db.TemplateQueries.DELETE_ALL_TEMPLATES_QUERY
import com.yandex.div.storage.db.TemplateQueries.DELETE_UNUSED_TEMPLATES_QUERY_TEMPLATE
import com.yandex.div.storage.db.TemplateQueries.GET_ALL_TEMPLATES_QUERY
import com.yandex.div.storage.db.TemplateQueries.GET_TEMPLATES_BY_CARD_ID_QUERY_TEMPLATE
import com.yandex.div.storage.db.TemplateQueries.GET_TEMPLATES_BY_IDS_QUERY_TEMPLATE_WITHOUT_PLACEHOLDER
import com.yandex.div.storage.db.TemplateQueries.INSERT_TEMPLATE_QUERY_TEMPLATE
import com.yandex.div.storage.entity.Template

internal class TemplateDaoImpl(
    private val writableDatabase: SQLiteDatabase
) : TemplateDao {

    init {
        if (writableDatabase.isReadOnly) {
            Assert.fail("${this.javaClass.name} requires writable db!")
        }
    }

    override fun getAllTemplates(): List<Template> {
        val cursor = writableDatabase.rawQuery(GET_ALL_TEMPLATES_QUERY, emptyArray())
        val templates = cursor.retrieveTemplates()
        cursor.close()
        return templates
    }

    override fun getTemplates(cardId: String): List<Template> {
        val cursor = writableDatabase.rawQuery(
            GET_TEMPLATES_BY_CARD_ID_QUERY_TEMPLATE, arrayOf(cardId))
        val templates = cursor.retrieveTemplates()
        cursor.close()
        return templates
    }

    override fun getTemplatesByIds(templateIds: List<String>): List<Template> {
        val queryBuilder = StringBuilder(GET_TEMPLATES_BY_IDS_QUERY_TEMPLATE_WITHOUT_PLACEHOLDER)
            .appendPlaceholders(templateIds.size)
        val cursor = writableDatabase.rawQuery(queryBuilder.toString(), templateIds.toTypedArray())
        val templates = cursor.retrieveTemplates()
        cursor.close()
        return templates
    }

    override fun insertTemplate(template: Template) {
        writableDatabase.execSQL(INSERT_TEMPLATE_QUERY_TEMPLATE, arrayOf(template.id, template.data))
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
