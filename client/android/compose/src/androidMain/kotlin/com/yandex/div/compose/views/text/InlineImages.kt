package com.yandex.div.compose.views.text

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yandex.div.compose.LocalDivContext
import com.yandex.div.compose.collection.observeBuilderDataElements
import com.yandex.div.compose.di.DivLocalComponent
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.images.ImageRequestParams
import com.yandex.div.compose.images.isValidImageUri
import com.yandex.div.compose.images.rememberImageRequest
import com.yandex.div.compose.images.rememberNetworkRestoringImagePainter
import com.yandex.div.compose.utils.toDp
import com.yandex.div.compose.utils.toTextUnit
import com.yandex.div.compose.views.image.toColorFilter
import com.yandex.div2.DivFixedSize
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
    val alignsToLine: Boolean = false,
    val baselineOffset: Dp,
    val descentPlaceholderHeight: TextUnit?,
    val colorFilter: ColorFilter?,
    val accessibilityDescription: String?,
    val accessibilityRole: Role?,
)

/**
 * The inline images placed in [text]: the ones [imageBuilder] builds, or the static [images] otherwise.
 * Both default to the ones of this text; its custom ellipsis passes its own.
 */
@Composable
internal fun DivText.observeInlineImages(
    text: String,
    baseFontSize: Int,
    textMetrics: ObservedTextMetrics,
    textStyle: TextStyle,
    images: List<DivText.Image>? = this.images,
    imageBuilder: DivText.ImageBuilder? = this.imageBuilder,
): List<InlineImageData> {
    val sourceImages = observeSourceImages(images, imageBuilder) ?: return emptyList()
    val positionedImages = sourceImages.mapIndexedNotNull { index, (image, component) ->
        val start = image.start.observedValue(component = component)
        if (start > text.length) {
            return@mapIndexedNotNull null
        }

        val position = when (image.indexingDirection.observedValue(component = component)) {
            DivText.Image.IndexingDirection.NORMAL -> start.toInt()
            DivText.Image.IndexingDirection.REVERSED -> text.length - start.toInt()
        }
        PositionedImage(image, component, index, position)
    }
    if (positionedImages.isEmpty()) {
        return emptyList()
    }
    val sortedImages = remember(positionedImages) {
        positionedImages.sortedWith(compareBy<PositionedImage> { it.position }.thenBy { it.sourceIndex })
    }

    val lineHeight = textMetrics.lineHeight?.toTextUnit(textMetrics.fontSizeUnit)
    val fontSize = lineHeight?.let { baseFontSize.toTextUnit(textMetrics.fontSizeUnit) }
    val lineMetrics = getLineMetrics(
        textStyle = textStyle,
        isRequired = lineHeight == null && sortedImages.any { it.image.baselineOffset != null },
    )
    return sortedImages.map { positionedImage ->
        val image = positionedImage.image
        val component = positionedImage.component
        val baselineOffset = image.baselineOffset?.observedValue(component = component)?.toFloat()
        val alignment = if (baselineOffset == null) {
            image.alignmentVertical.observedValue(component = component)
        } else {
            DivTextAlignmentVertical.BASELINE
        }
        val verticalAlignment = alignment.toPlaceholderVerticalAlign()
        val tintColor = image.tintColor?.observedValue(component = component)
        val width = image.width.observedImageSize(component)
        val height = image.height.observedImageSize(component)
        val url = image.url.observedValue(component = component)
        val accessibilityDescription =
            image.accessibility?.description?.observedValue(component = component)
        val accessibilityRole = image.accessibility?.type.toRole()
        val baselineOffsetTextUnit = baselineOffset?.toTextUnit()
        val isLineHeightConstrained = lineHeight?.let { it < height.textUnit } == true
        val alignsToLine = isLineHeightConstrained && verticalAlignment != PlaceholderVerticalAlign.AboveBaseline
        val placeholderHeight = when {
            baselineOffsetTextUnit != null && lineHeight == null && lineMetrics != null ->
                maxTextUnit(
                    lineMetrics.ascent,
                    addTextUnits(height.textUnit, baselineOffsetTextUnit),
                )
            !isLineHeightConstrained -> height.textUnit
            alignsToLine -> 0.sp
            else -> fontSize ?: height.textUnit
        }
        val placeholderVerticalAlignment = if (baselineOffsetTextUnit != null || isLineHeightConstrained) {
            PlaceholderVerticalAlign.AboveBaseline
        } else {
            verticalAlignment
        }
        val descentPlaceholderHeight = if (baselineOffsetTextUnit != null &&
            lineHeight == null && lineMetrics != null
        ) {
            val requiredDescent = maxTextUnit(
                lineMetrics.descent,
                negateTextUnit(baselineOffsetTextUnit),
            )
            if (requiredDescent.value > lineMetrics.descent.value) {
                addTextUnits(lineMetrics.ascent, requiredDescent)
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
            alignsToLine = alignsToLine,
            baselineOffset = baselineOffset?.dp ?: 0.dp,
            descentPlaceholderHeight = descentPlaceholderHeight,
            colorFilter = tintColor?.let {
                toColorFilter(it, image.tintMode.observedValue(component = component))
            },
            accessibilityDescription = accessibilityDescription,
            accessibilityRole = accessibilityRole,
        )
    }
}

/**
 * The images with the scope each one is resolved in: the ones [imageBuilder] builds, or the static
 * [images] otherwise.
 */
@Composable
private fun observeSourceImages(
    images: List<DivText.Image>?,
    imageBuilder: DivText.ImageBuilder?,
): List<ScopedImage>? {
    val component = LocalComponent.current
    imageBuilder?.let { return it.observeBuiltImages(component) }
    images ?: return null
    return remember(images, component) { images.map { ScopedImage(it, component) } }
}

/**
 * The images this builder builds, each with the scope of its `data` element.
 */
@Composable
private fun DivText.ImageBuilder.observeBuiltImages(parent: DivLocalComponent): List<ScopedImage> {
    return observeBuilderDataElements(
        propertyName = "image_builder",
        elements = data.observedValue(),
        dataElementName = dataElementName,
        parent = parent,
    ) { element ->
        observeSelectedImage(element.component)?.let { ScopedImage(it, element.component) }
    }
}

@Composable
private fun DivText.ImageBuilder.observeSelectedImage(
    component: DivLocalComponent,
): DivText.Image? {
    for (prototype in prototypes) {
        if (prototype.selector.observedValue(component = component)) {
            return prototype.image
        }
    }
    return null
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

private val LocalLineBounds = compositionLocalOf<IntRange?> { null }

internal fun Map<String, InlineTextContent>.alignedToLines(
    text: AnnotatedString,
    lines: List<TextLineBounds>,
): Map<String, InlineTextContent> {
    val aligned = text.getStringAnnotations(0, text.length).mapNotNull { annotation ->
        val content = this[annotation.item] ?: return@mapNotNull null
        val line = lines.find { annotation.start < it.end } ?: return@mapNotNull null
        annotation.item to InlineTextContent(content.placeholder) { alternateText ->
            CompositionLocalProvider(LocalLineBounds provides line.top..line.bottom) {
                content.children(alternateText)
            }
        }
    }
    return if (aligned.isEmpty()) this else this + aligned
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

    val component = LocalDivContext.current.component
    val painter = rememberNetworkRestoringImagePainter(
        model = rememberImageRequest(
            ImageRequestParams(
                data = image.url,
                limitToDisplaySize = true,
            )
        ),
        onState = component.debugConfiguration.imagePainterStateListener,
    )
    val lineBounds = LocalLineBounds.current?.takeIf { image.alignsToLine }
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            modifier = Modifier
                .align(image.contentAlignment)
                .alignedTo(image, lineBounds)
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

private fun Modifier.alignedTo(image: InlineImageData, lineBounds: IntRange?): Modifier {
    if (lineBounds == null) return wrapContentSize(image.contentAlignment, unbounded = true)
    return wrapContentSize(Alignment.TopStart, unbounded = true).offset {
        val size = IntSize(0, image.contentHeight.roundToPx())
        val space = IntSize(0, lineBounds.last - lineBounds.first)
        IntOffset(0, lineBounds.first + image.contentAlignment.align(size, space, LayoutDirection.Ltr).y)
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
private fun DivFixedSize.observedImageSize(component: DivLocalComponent): ObservedImageSize {
    val value = value.observedValue(component = component).toInt()
    val unit = unit.observedValue(component = component)
    return ObservedImageSize(
        textUnit = value.toTextUnit(unit),
        dp = value.toFloat().toDp(unit),
    )
}

@Composable
@ReadOnlyComposable
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

private data class ScopedImage(
    val image: DivText.Image,
    val component: DivLocalComponent,
)

private data class PositionedImage(
    val image: DivText.Image,
    val component: DivLocalComponent,
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
