package com.yandex.div.legacy

import android.net.Uri
import androidx.annotation.ColorInt
import com.yandex.div.DivAlignment
import com.yandex.div.DivAlignmentVertical
import com.yandex.div.DivImageElement
import com.yandex.div.DivPosition
import com.yandex.div.DivSize
import com.yandex.div.DivTableBlock
import com.yandex.div.DivTextStyle
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.util.ReflectionHelpers

@DslMarker
@Target(AnnotationTarget.TYPE)
annotation class DivTableDslMarker

typealias ColumnList = MutableList<DivTableBlock.Column>
typealias RowList = MutableList<DivTableBlock.Row>
typealias CellList = MutableList<DivTableBlock.RowElement.Cell>

inline fun divTable(init: @DivTableDslMarker DivTableBlock.() -> Unit): DivTableBlock {
    val tableBlock = mock<DivTableBlock>()
    tableBlock.init()
    return tableBlock
}

inline fun DivTableBlock.columns(init: @DivTableDslMarker ColumnList.() -> Unit) {
    val columns = arrayListOf<DivTableBlock.Column>()
    columns.init()
    ReflectionHelpers.setField(this, "columns", columns)
}


fun ColumnList.column(
    weight: Int = 0,
    @DivSize leftPadding: String = DivSize.XS,
    @DivSize rightPadding: String = DivSize.XS
) {
    val column = mock<DivTableBlock.Column>()
    ReflectionHelpers.setField(column, "weight", weight)
    ReflectionHelpers.setField(column, "leftPadding", leftPadding)
    ReflectionHelpers.setField(column, "rightPadding", rightPadding)
    add(column)
}

inline fun DivTableBlock.rows(init: @DivTableDslMarker RowList.() -> Unit) {
    val rows = arrayListOf<DivTableBlock.Row>()
    rows.init()
    ReflectionHelpers.setField(this, "rows", rows)
}

fun RowList.separator(@ColorInt color: Int = 0x14000000) {
    val separator = mock<DivTableBlock.SeparatorElement>()
    ReflectionHelpers.setField(separator, "color", color)

    val row = mock<DivTableBlock.Row>()
    ReflectionHelpers.setField(row, "type", DivTableBlock.Row.Type.SEPARATOR_ELEMENT)
    ReflectionHelpers.setField(row, "value", separator)
    whenever(row.asSeparatorElement()).thenReturn(separator)
    add(row)
}

inline fun RowList.row(
    @DivSize topPadding: String = DivSize.ZERO,
    @DivSize bottomPadding: String = DivSize.XXS,
    init: @DivTableDslMarker CellList.() -> Unit
) {
    val cells = arrayListOf<DivTableBlock.RowElement.Cell>()
    cells.init()

    val rowElement = mock<DivTableBlock.RowElement>()
    ReflectionHelpers.setField(rowElement, "topPadding", topPadding)
    ReflectionHelpers.setField(rowElement, "bottomPadding", bottomPadding)
    ReflectionHelpers.setField(rowElement, "cells", cells)

    val row = mock<DivTableBlock.Row>()
    ReflectionHelpers.setField(row, "type", DivTableBlock.Row.Type.ROW_ELEMENT)
    ReflectionHelpers.setField(row, "value", rowElement)
    whenever(row.asRowElement()).thenReturn(rowElement)
    add(row)
}

fun CellList.text(
    text: String,
    @DivTextStyle textStyle: String = DivTextStyle.TEXT_M,
    @DivAlignment horizontalAlignment: String = DivAlignment.LEFT,
    @DivAlignmentVertical verticalAlignment: String = DivAlignmentVertical.CENTER
) {
    cell(
        text = text,
        textStyle = textStyle,
        imageUrl = null,
        imageRatio = 1.0f,
        imageSize = DivSize.M,
        imagePosition = DivPosition.LEFT,
        horizontalAlignment = horizontalAlignment,
        verticalAlignment = verticalAlignment
    )
}

fun CellList.image(
    imageUrl: String,
    imageRatio: Float = 1.0f,
    @DivSize imageSize: String = DivSize.M,
    @DivAlignment horizontalAlignment: String = DivAlignment.LEFT,
    @DivAlignmentVertical verticalAlignment: String = DivAlignmentVertical.CENTER
) {
    cell(
        text = null,
        textStyle = DivTextStyle.TEXT_M,
        imageUrl = imageUrl,
        imageRatio = imageRatio,
        imageSize = imageSize,
        imagePosition = DivPosition.LEFT,
        horizontalAlignment = horizontalAlignment,
        verticalAlignment = verticalAlignment
    )
}

fun CellList.imageAndText(
    text: String,
    @DivTextStyle textStyle: String = DivTextStyle.TEXT_M,
    imageUrl: String,
    imageRatio: Float = 1.0f,
    @DivSize imageSize: String = DivSize.M,
    @DivPosition imagePosition: String = DivPosition.LEFT,
    @DivAlignment horizontalAlignment: String = DivAlignment.LEFT,
    @DivAlignmentVertical verticalAlignment: String = DivAlignmentVertical.CENTER
) {
    cell(
        text = text,
        textStyle = textStyle,
        imageUrl = imageUrl,
        imageRatio = imageRatio,
        imageSize = imageSize,
        imagePosition = imagePosition,
        horizontalAlignment = horizontalAlignment,
        verticalAlignment = verticalAlignment
    )
}

private fun CellList.cell(
    text: String?,
    @DivTextStyle textStyle: String,
    imageUrl: String?,
    imageRatio: Float,
    @DivSize imageSize: String,
    @DivPosition imagePosition: String,
    @DivAlignment horizontalAlignment: String,
    @DivAlignmentVertical verticalAlignment: String
) {
    val image: DivImageElement? = imageUrl?.let { url ->
        mock<DivImageElement>().also { image ->
            ReflectionHelpers.setField(image, "imageUrl", Uri.parse(url))
            ReflectionHelpers.setField(image, "ratio", imageRatio)
        }
    }

    val cell = mock<DivTableBlock.RowElement.Cell>()
    ReflectionHelpers.setField(cell, "text", text)
    ReflectionHelpers.setField(cell, "textStyle", textStyle)
    ReflectionHelpers.setField(cell, "image", image)
    ReflectionHelpers.setField(cell, "imageSize", imageSize)
    ReflectionHelpers.setField(cell, "imagePosition", imagePosition)
    ReflectionHelpers.setField(cell, "horizontalAlignment", horizontalAlignment)
    ReflectionHelpers.setField(cell, "verticalAlignment", verticalAlignment)
    add(cell)
}
