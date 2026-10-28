package com.yandex.div.test.data

import android.graphics.Color
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivAction
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivAnimator
import com.yandex.div2.DivBackground
import com.yandex.div2.DivBorder
import com.yandex.div2.DivCollectionItemBuilder
import com.yandex.div2.DivContainer
import com.yandex.div2.DivDrawable
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivExtension
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivFocus
import com.yandex.div2.DivFunction
import com.yandex.div2.DivLayoutProvider
import com.yandex.div2.DivRoundedRectangleShape
import com.yandex.div2.DivShape
import com.yandex.div2.DivShapeDrawable
import com.yandex.div2.DivSize
import com.yandex.div2.DivVariable
import com.yandex.div2.DivVisibility

fun container(
    accessibility: DivAccessibility? = null,
    action: DivAction? = null,
    alignmentHorizontal: DivAlignmentHorizontal? = null,
    alignmentVertical: DivAlignmentVertical? = null,
    animators: List<DivAnimator>? = null,
    backgrounds: List<DivBackground>? = null,
    border: DivBorder? = null,
    clipToBounds: Expression<Boolean> = constant(true),
    extensions: List<DivExtension>? = null,
    focus: DivFocus? = null,
    functions: List<DivFunction>? = null,
    height: DivSize = wrapContent(),
    id: String? = null,
    itemBuilder: DivCollectionItemBuilder? = null,
    items: List<Div> = emptyList(),
    layoutMode: DivContainer.LayoutMode = DivContainer.LayoutMode.NO_WRAP,
    layoutProvider: DivLayoutProvider? = null,
    longTapActions: List<DivAction>? = null,
    margins: DivEdgeInsets? = null,
    orientation: DivContainer.Orientation = DivContainer.Orientation.VERTICAL,
    paddings: DivEdgeInsets? = null,
    selectedActions: List<DivAction>? = null,
    separator: DivContainer.Separator? = null,
    variables: List<DivVariable>? = null,
    visibility: Expression<DivVisibility> = constant(DivVisibility.VISIBLE),
    width: DivSize = matchParent()
): Div {
    return Div.Container(
        value = DivContainer(
            accessibility = accessibility,
            action = action,
            alignmentHorizontal = alignmentHorizontal?.let(::constant),
            alignmentVertical = alignmentVertical?.let(::constant),
            animators = animators,
            background = backgrounds,
            border = border,
            clipToBounds = clipToBounds,
            extensions = extensions,
            focus = focus,
            functions = functions,
            height = height,
            id = id,
            itemBuilder = itemBuilder,
            items = items,
            layoutMode = constant(layoutMode),
            layoutProvider = layoutProvider,
            longtapActions = longTapActions,
            margins = margins,
            orientation = constant(orientation),
            paddings = paddings,
            selectedActions = selectedActions,
            separator = separator,
            variables = variables,
            visibility = visibility,
            width = width
        )
    )
}

fun containerSeparator(width: Expression<Long>): DivContainer.Separator = DivContainer.Separator(
    style = DivDrawable.Shape(
        DivShapeDrawable(
            shape = DivShape.RoundedRectangle(
                DivRoundedRectangleShape(
                    backgroundColor = constant(Color.BLACK),
                    itemWidth = DivFixedSize(value = width),
                )
            )
        )
    )
)
