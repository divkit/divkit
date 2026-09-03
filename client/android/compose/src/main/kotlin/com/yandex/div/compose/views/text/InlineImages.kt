package com.yandex.div.compose.views.text

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.yandex.div.compose.context.divContext
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.images.ImageRequestParams
import com.yandex.div.compose.images.isValidImageUri
import com.yandex.div.compose.images.observeNetworkRestoration
import com.yandex.div.compose.images.rememberImageRequest
import com.yandex.div.compose.utils.toTextUnit
import com.yandex.div.compose.views.image.toColorFilter
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivSizeUnit
import com.yandex.div2.DivText
import com.yandex.div2.DivTextAlignmentVertical

private const val INLINE_IMAGE_ID_PREFIX = "inline-image-"
private const val WORD_JOINER = '\u2060'

@Immutable
internal data class InlineImageData(
    val id: String,
    val position: Int,
    val url: Uri,
    val width: TextUnit,
    val height: TextUnit,
    val contentWidth: Dp,
    val contentHeight: Dp,
    val contentAlignment: Alignment,
    val verticalAlignment: PlaceholderVerticalAlign,
    val baselineOffset: Dp,
    val descentPlaceholderHeight: TextUnit?,
    val colorFilter: ColorFilter?,
    val accessibilityDescription: String?,
    val accessibilityRole: Role?,
)

@Immutable
private data class FontLineMetrics(
    val ascent: TextUnit,
    val descent: TextUnit,
)

@Composable
private fun observeFontLineMetrics(
    textStyle: TextStyle,
    isRequired: Boolean,
): FontLineMetrics? {
    if (!isRequired) {
        return null
    }

    val textMeasurer = rememberTextMeasurer()
    val measuredStyle = remember(textStyle) {
        textStyle.copy(
            lineHeight = TextUnit.Unspecified,
            lineHeightStyle = null,
        )
    }
    val layout = remember(textMeasurer, measuredStyle) {
        textMeasurer.measure(
            text = "M",
            style = measuredStyle,
            maxLines = 1,
        )
    }
    return with(LocalDensity.current) {
        val baseline = layout.firstBaseline
        FontLineMetrics(
            ascent = (baseline - layout.getLineTop(0)).toSp(),
            descent = (layout.getLineBottom(0) - baseline).toSp(),
        )
    }
}

@Composable
internal fun DivText.observeInlineImages(
    text: String,
    baseFontSize: Int,
    textMetrics: ObservedTextMetrics,
    textStyle: TextStyle,
): List<InlineImageData> {
    val images = images ?: return emptyList()
    val positionedImages = images.mapIndexedNotNull { index, image ->
        val start = image.start.observedIntValue()
        if (start > text.length) {
            return@mapIndexedNotNull null
        }

        val position = when (image.indexingDirection.observedValue()) {
            DivText.Image.IndexingDirection.NORMAL -> start
            DivText.Image.IndexingDirection.REVERSED -> text.length - start
        }
        PositionedImage(image, index, position)
    }
    if (positionedImages.isEmpty()) {
        return emptyList()
    }
    val sortedImages = remember(positionedImages) {
        positionedImages.sortedWith(compareBy<PositionedImage> { it.position }.thenBy { it.sourceIndex })
    }

    val lineHeight = textMetrics.lineHeight
        ?.takeIf { it > 0 }
        ?.toTextUnit(textMetrics.fontSizeUnit)
    val fontSize = lineHeight?.let { baseFontSize.toTextUnit(textMetrics.fontSizeUnit) }
    val fontLineMetrics = observeFontLineMetrics(
        textStyle = textStyle,
        isRequired = lineHeight == null && sortedImages.any { it.image.baselineOffset != null },
    )
    return sortedImages.map { positionedImage ->
        val image = positionedImage.image
        val baselineOffset = image.baselineOffset?.observedFloatValue()
        val alignment = if (baselineOffset == null) {
            image.alignmentVertical.observedValue()
        } else {
            DivTextAlignmentVertical.BASELINE
        }
        val verticalAlignment = alignment.toPlaceholderVerticalAlign()
        val tintColor = image.tintColor?.observedValue()
        val width = image.width.observedImageSize()
        val height = image.height.observedImageSize()
        val url = image.url.observedValue()
        val accessibilityDescription = image.accessibility?.description?.observedValue()
        val accessibilityRole = image.accessibility?.type.toRole()
        val baselineOffsetTextUnit = baselineOffset?.toTextUnit()
        val isLineHeightConstrained = lineHeight?.let { it < height.textUnit } == true
        val placeholderHeight = when {
            baselineOffsetTextUnit != null && lineHeight == null && fontLineMetrics != null ->
                maxTextUnit(
                    fontLineMetrics.ascent,
                    addTextUnits(height.textUnit, baselineOffsetTextUnit),
                )
            !isLineHeightConstrained -> height.textUnit
            verticalAlignment == PlaceholderVerticalAlign.AboveBaseline -> fontSize ?: height.textUnit
            else -> lineHeight ?: height.textUnit
        }
        val placeholderVerticalAlignment = when {
            baselineOffsetTextUnit != null -> PlaceholderVerticalAlign.AboveBaseline
            isLineHeightConstrained && verticalAlignment != PlaceholderVerticalAlign.AboveBaseline ->
                PlaceholderVerticalAlign.Top
            else -> verticalAlignment
        }
        val descentPlaceholderHeight = if (baselineOffsetTextUnit != null &&
            lineHeight == null && fontLineMetrics != null
        ) {
            val requiredDescent = maxTextUnit(
                fontLineMetrics.descent,
                negateTextUnit(baselineOffsetTextUnit),
            )
            if (requiredDescent.value > fontLineMetrics.descent.value) {
                addTextUnits(fontLineMetrics.ascent, requiredDescent)
            } else {
                null
            }
        } else {
            null
        }
        InlineImageData(
            id = "$INLINE_IMAGE_ID_PREFIX${positionedImage.sourceIndex}",
            position = positionedImage.position,
            url = url,
            width = width.textUnit,
            height = placeholderHeight,
            contentWidth = width.dp,
            contentHeight = height.dp,
            contentAlignment = alignment.toContentAlignment(),
            verticalAlignment = placeholderVerticalAlignment,
            baselineOffset = baselineOffset?.dp ?: 0.dp,
            descentPlaceholderHeight = descentPlaceholderHeight,
            colorFilter = tintColor?.let { toColorFilter(it, image.tintMode.observedValue()) },
            accessibilityDescription = accessibilityDescription,
            accessibilityRole = accessibilityRole,
        )
    }
}

@Composable
internal fun rememberInlineContent(images: List<InlineImageData>): Map<String, InlineTextContent> {
    return remember(images) {
        buildMap {
            images.forEach { image ->
                put(
                    image.id,
                    InlineTextContent(
                        placeholder = Placeholder(
                            width = image.width,
                            height = image.height,
                            placeholderVerticalAlign = image.verticalAlignment,
                        ),
                        children = { InlineImage(image) },
                    )
                )
                image.descentPlaceholderHeight?.let { height ->
                    put(
                        image.descentPlaceholderId,
                        InlineTextContent(
                            placeholder = Placeholder(
                                width = 0.sp,
                                height = height,
                                placeholderVerticalAlign = PlaceholderVerticalAlign.Top,
                            ),
                            children = {},
                        )
                    )
                }
            }
        }
    }
}

internal fun AnnotatedString.Builder.appendTextWithInlineImages(
    text: String,
    images: List<InlineImageData>,
): OriginalTextOffsets {
    val beforeInlineContent = IntArray(text.length + 1)
    val afterInlineContent = IntArray(text.length + 1)
    var imageIndex = 0

    for (textIndex in 0..text.length) {
        beforeInlineContent[textIndex] = length
        while (imageIndex < images.size && images[imageIndex].position == textIndex) {
            val previousPosition = images.getOrNull(imageIndex - 1)?.position
            if (previousPosition?.plus(1) != textIndex &&
                textIndex > 0 && !text[textIndex - 1].isWhitespace()
            ) {
                append(WORD_JOINER)
            }
            appendInlineContent(images[imageIndex].id, WORD_JOINER.toString())
            if (images[imageIndex].descentPlaceholderHeight != null) {
                appendInlineContent(images[imageIndex].descentPlaceholderId, WORD_JOINER.toString())
            }
            imageIndex++
        }
        afterInlineContent[textIndex] = length
        if (textIndex < text.length) {
            append(text[textIndex])
        }
    }

    return OriginalTextOffsets(beforeInlineContent, afterInlineContent)
}

internal class OriginalTextOffsets(
    private val beforeInlineContent: IntArray,
    private val afterInlineContent: IntArray,
) {
    fun rangeStart(offset: Int): Int = afterInlineContent[offset]

    fun rangeEnd(offset: Int): Int = beforeInlineContent[offset]
}

@Composable
private fun InlineImage(image: InlineImageData) {
    if (!image.url.isValidImageUri()) {
        Box(modifier = Modifier.fillMaxSize().inlineImageAccessibility(image))
        return
    }

    val component = divContext.component
    val painter = rememberAsyncImagePainter(
        model = rememberImageRequest(
            ImageRequestParams(
                data = image.url,
                limitToDisplaySize = true,
            )
        ),
        imageLoader = component.imageLoader,
        onState = component.debugConfiguration.imagePainterStateListener,
    )
    painter.observeNetworkRestoration()
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            modifier = Modifier
                .align(image.contentAlignment)
                .requiredSize(image.contentWidth, image.contentHeight)
                .offset(y = -image.baselineOffset)
                .inlineImageAccessibility(image),
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            colorFilter = image.colorFilter,
        )
    }
}

private fun Modifier.inlineImageAccessibility(image: InlineImageData): Modifier {
    val description = image.accessibilityDescription ?: return this
    return semantics(mergeDescendants = true) {
        contentDescription = description
        image.accessibilityRole?.let { role = it }
    }
}

@Composable
private fun DivFixedSize.observedImageSize(): ObservedImageSize {
    val value = value.observedIntValue()
    val unit = unit.observedValue()
    return ObservedImageSize(
        textUnit = value.toTextUnit(unit),
        dp = value.toDp(unit),
    )
}

@Composable
private fun Int.toDp(unit: DivSizeUnit): Dp {
    return when (unit) {
        DivSizeUnit.DP -> dp
        DivSizeUnit.SP -> with(LocalDensity.current) { sp.toDp() }
        DivSizeUnit.PX -> with(LocalDensity.current) { toDp() }
    }
}

@Composable
private fun Float.toTextUnit(): TextUnit {
    return with(LocalDensity.current) { dp.toSp() }
}

private fun addTextUnits(first: TextUnit, second: TextUnit): TextUnit {
    return (first.value + second.value).sp
}

private fun maxTextUnit(first: TextUnit, second: TextUnit): TextUnit {
    return maxOf(first.value, second.value).sp
}

private fun negateTextUnit(value: TextUnit): TextUnit {
    return (-value.value).sp
}

private fun DivTextAlignmentVertical.toContentAlignment(): Alignment {
    return when (this) {
        DivTextAlignmentVertical.TOP -> Alignment.TopStart
        DivTextAlignmentVertical.CENTER -> Alignment.CenterStart
        DivTextAlignmentVertical.BASELINE,
        DivTextAlignmentVertical.BOTTOM -> Alignment.BottomStart
    }
}

@Immutable
private data class ObservedImageSize(
    val textUnit: TextUnit,
    val dp: Dp,
)

private data class PositionedImage(
    val image: DivText.Image,
    val sourceIndex: Int,
    val position: Int,
)

private val InlineImageData.descentPlaceholderId: String
    get() = "$id-descent"

private fun DivTextAlignmentVertical.toPlaceholderVerticalAlign(): PlaceholderVerticalAlign {
    return when (this) {
        DivTextAlignmentVertical.TOP -> PlaceholderVerticalAlign.Top
        DivTextAlignmentVertical.CENTER -> PlaceholderVerticalAlign.Center
        DivTextAlignmentVertical.BASELINE -> PlaceholderVerticalAlign.AboveBaseline
        DivTextAlignmentVertical.BOTTOM -> PlaceholderVerticalAlign.Bottom
    }
}

private fun DivText.Image.Accessibility.Type?.toRole(): Role? {
    return when (this) {
        DivText.Image.Accessibility.Type.BUTTON -> Role.Button
        DivText.Image.Accessibility.Type.IMAGE,
        DivText.Image.Accessibility.Type.AUTO -> Role.Image
        DivText.Image.Accessibility.Type.NONE,
        DivText.Image.Accessibility.Type.TEXT,
        null -> null
    }
}
