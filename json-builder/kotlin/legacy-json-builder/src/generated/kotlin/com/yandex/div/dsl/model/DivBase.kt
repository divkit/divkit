// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

interface DivBase {
    val accessibility: Property<DivAccessibility>?
    val alignmentHorizontal: Property<DivAlignmentHorizontal>?
    val alignmentVertical: Property<DivAlignmentVertical>?
    val alpha: Property<Double>?
    val background: Property<List<DivBackground>>?
    val border: Property<DivBorder>?
    val columnSpan: Property<Int>?
    val extensions: Property<List<DivExtension>>?
    val focus: Property<DivFocus>?
    val height: Property<DivSize>?
    val id: Property<String>?
    val margins: Property<DivEdgeInsets>?
    val paddings: Property<DivEdgeInsets>?
    val rowSpan: Property<Int>?
    val selectedActions: Property<List<DivAction>>?
    val tooltips: Property<List<DivTooltip>>?
    val transform: Property<DivTransform>?
    val transitionChange: Property<DivChangeTransition>?
    val transitionIn: Property<DivAppearanceTransition>?
    val transitionOut: Property<DivAppearanceTransition>?
    val transitionTriggers: Property<List<DivTransitionTrigger>>?
    val visibility: Property<DivVisibility>?
    val visibilityAction: Property<DivVisibilityAction>?
    val visibilityActions: Property<List<DivVisibilityAction>>?
    val width: Property<DivSize>?
}
