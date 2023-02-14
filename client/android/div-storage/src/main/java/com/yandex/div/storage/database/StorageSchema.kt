@file:JvmName("StorageSchema")

package com.yandex.div.storage.database

internal const val DB_VERSION = 2

internal const val TABLE_CARDS = "cards"
internal const val TABLE_TEMPLATES = "templates"
internal const val TABLE_TEMPLATE_REFERENCES = "template_references"

internal const val COLUMN_LAYOUT_ID = "layout_id"
internal const val COLUMN_CARD_DATA = "card_data"
internal const val COLUMN_CARD_GROUP_ID = "group_id"
internal const val COLUMN_CARD_ID = "card_id"
internal const val COLUMN_CARD_METADATA = "metadata"
internal const val COLUMN_GROUP_ID = "group_id"
internal const val COLUMN_TEMPLATE_ID = "template_id"
internal const val COLUMN_TEMPLATE_HASH = "template_hash"
internal const val COLUMN_TEMPLATE_DATA = "template_data"

internal const val CREATE_TABLE_CARDS = """
    CREATE TABLE IF NOT EXISTS $TABLE_CARDS(
    $COLUMN_LAYOUT_ID TEXT NOT NULL PRIMARY KEY,
    $COLUMN_CARD_DATA BLOB NULLABLE,
    $COLUMN_CARD_METADATA BLOB NULLABLE,
    $COLUMN_CARD_GROUP_ID TEXT NOT NULL)"""

internal const val CREATE_TABLE_TEMPLATES = """
    CREATE TABLE IF NOT EXISTS $TABLE_TEMPLATES(
    $COLUMN_TEMPLATE_HASH TEXT NOT NULL PRIMARY KEY,
    $COLUMN_TEMPLATE_DATA BLOB NULLABLE)"""

internal const val CREATE_TABLE_TEMPLATE_REFERENCES = """
    CREATE TABLE IF NOT EXISTS $TABLE_TEMPLATE_REFERENCES(
    $COLUMN_GROUP_ID TEXT NOT NULL,
    $COLUMN_TEMPLATE_ID TEXT NOT NULL,
    $COLUMN_TEMPLATE_HASH TEXT NOT NULL,
    PRIMARY KEY($COLUMN_GROUP_ID, $COLUMN_TEMPLATE_ID))"""
