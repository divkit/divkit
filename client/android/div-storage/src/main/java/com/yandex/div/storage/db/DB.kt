package com.yandex.div.storage.db

import com.yandex.div.storage.db.TemplateEntity.TEMPLATES_TABLE
import com.yandex.div.storage.db.TemplateEntity.TEMPLATE_DATA
import com.yandex.div.storage.db.TemplateEntity.TEMPLATE_ID
import com.yandex.div.storage.db.TemplateEntity.TEMPLATE_USAGES_CARD_ID
import com.yandex.div.storage.db.TemplateEntity.TEMPLATE_USAGES_TABLE

internal object TemplateEntity {
    const val TEMPLATES_TABLE = "templates"
    const val TEMPLATE_ID = "template_id"
    const val TEMPLATE_DATA = "template_data"

    const val TEMPLATE_USAGES_TABLE = "template_usages"
    const val TEMPLATE_USAGES_CARD_ID = "card_id"
}

/**
 * If you change something here please up db version.
 */
internal object TemplateSchema {

    const val CREATE_TEMPLATES_TABLE_QUERY =
        "CREATE TABLE IF NOT EXISTS `$TEMPLATES_TABLE` (`$TEMPLATE_ID` TEXT NOT NULL, `$TEMPLATE_DATA` BLOB NOT NULL, PRIMARY KEY(`$TEMPLATE_ID`))"
    const val CREATE_TEMPLATE_USAGES_TABLE_QUERY =
        "CREATE TABLE IF NOT EXISTS `$TEMPLATE_USAGES_TABLE` (`$TEMPLATE_USAGES_CARD_ID` TEXT NOT NULL, `$TEMPLATE_ID` TEXT NOT NULL, PRIMARY KEY(`$TEMPLATE_USAGES_CARD_ID`, `$TEMPLATE_ID`))"
}

/**
 * You can change queries in following objects without migration as they do not change schema.
 */
internal object TemplateQueries {
    const val INSERT_TEMPLATE_QUERY_TEMPLATE =
        "INSERT OR IGNORE INTO `$TEMPLATES_TABLE` (`$TEMPLATE_ID`,`$TEMPLATE_DATA`) VALUES (?,?)"
    const val DELETE_UNUSED_TEMPLATES_QUERY_TEMPLATE =
        "DELETE FROM $TEMPLATES_TABLE WHERE $TEMPLATE_ID NOT IN (SELECT DISTINCT $TEMPLATE_ID FROM $TEMPLATE_USAGES_TABLE)"
    const val DELETE_ALL_TEMPLATES_QUERY = "DELETE FROM $TEMPLATES_TABLE"
    const val GET_ALL_TEMPLATES_QUERY = "SELECT * FROM $TEMPLATES_TABLE"
    const val GET_TEMPLATES_BY_CARD_ID_QUERY_TEMPLATE =
        "SELECT $TEMPLATES_TABLE.$TEMPLATE_ID, $TEMPLATES_TABLE.$TEMPLATE_DATA FROM $TEMPLATES_TABLE INNER JOIN $TEMPLATE_USAGES_TABLE ON $TEMPLATES_TABLE.$TEMPLATE_ID = $TEMPLATE_USAGES_TABLE.$TEMPLATE_ID WHERE $TEMPLATE_USAGES_TABLE.$TEMPLATE_USAGES_CARD_ID = ?"

    /**
     * Use this template with [appendPlaceholders] func below!
     */
    const val GET_TEMPLATES_BY_IDS_QUERY_TEMPLATE_WITHOUT_PLACEHOLDER =
        "SELECT $TEMPLATE_ID, $TEMPLATE_DATA FROM $TEMPLATES_TABLE WHERE $TEMPLATE_ID IN "
}

internal object TemplateUsageQueries {

    const val INSERT_TEMPLATE_USAGE_QUERY_TEMPLATE = "INSERT OR IGNORE INTO `$TEMPLATE_USAGES_TABLE` (`$TEMPLATE_USAGES_CARD_ID`,`$TEMPLATE_ID`) VALUES (?,?)"
    const val DELETE_ALL_TEMPLATE_USAGES_QUERY = "DELETE FROM $TEMPLATE_USAGES_TABLE"
    const val DELETE_TEMPLATE_USAGE_BY_CARD_ID_QUERY_TEMPLATE = "DELETE FROM $TEMPLATE_USAGES_TABLE WHERE $TEMPLATE_USAGES_CARD_ID = ?"
}

/**
 * Adds bind variable placeholders (?) to the given string. Each placeholder is separated
 * by a comma.
 *
 * @param count Number of placeholders
 */
internal fun StringBuilder.appendPlaceholders(count: Int): StringBuilder {
    append("(")
    for (i in 0 until count) {
        append("?")
        if (i < count - 1) {
            append(",")
        }
    }
    append(")")
    return this
}
