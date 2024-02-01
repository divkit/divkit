// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivContainerTemplate: TemplateValue {
  public final class SeparatorTemplate: TemplateValue {
    public let margins: Field<DivEdgeInsetsTemplate>?
    public let showAtEnd: Field<Expression<Bool>>? // default value: false
    public let showAtStart: Field<Expression<Bool>>? // default value: false
    public let showBetween: Field<Expression<Bool>>? // default value: true
    public let style: Field<DivDrawableTemplate>?

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      self.init(
        margins: dictionary.getOptionalField("margins", templateToType: templateToType),
        showAtEnd: dictionary.getOptionalExpressionField("show_at_end"),
        showAtStart: dictionary.getOptionalExpressionField("show_at_start"),
        showBetween: dictionary.getOptionalExpressionField("show_between"),
        style: dictionary.getOptionalField("style", templateToType: templateToType)
      )
    }

    init(
      margins: Field<DivEdgeInsetsTemplate>? = nil,
      showAtEnd: Field<Expression<Bool>>? = nil,
      showAtStart: Field<Expression<Bool>>? = nil,
      showBetween: Field<Expression<Bool>>? = nil,
      style: Field<DivDrawableTemplate>? = nil
    ) {
      self.margins = margins
      self.showAtEnd = showAtEnd
      self.showAtStart = showAtStart
      self.showBetween = showBetween
      self.style = style
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: SeparatorTemplate?) -> DeserializationResult<DivContainer.Separator> {
      let marginsValue = parent?.margins?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
      let showAtEndValue = parent?.showAtEnd?.resolveOptionalValue(context: context) ?? .noValue
      let showAtStartValue = parent?.showAtStart?.resolveOptionalValue(context: context) ?? .noValue
      let showBetweenValue = parent?.showBetween?.resolveOptionalValue(context: context) ?? .noValue
      let styleValue = parent?.style?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue
      var errors = mergeErrors(
        marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
        showAtEndValue.errorsOrWarnings?.map { .nestedObjectError(field: "show_at_end", error: $0) },
        showAtStartValue.errorsOrWarnings?.map { .nestedObjectError(field: "show_at_start", error: $0) },
        showBetweenValue.errorsOrWarnings?.map { .nestedObjectError(field: "show_between", error: $0) },
        styleValue.errorsOrWarnings?.map { .nestedObjectError(field: "style", error: $0) }
      )
      if case .noValue = styleValue {
        errors.append(.requiredFieldIsMissing(field: "style"))
      }
      guard
        let styleNonNil = styleValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivContainer.Separator(
        margins: marginsValue.value,
        showAtEnd: showAtEndValue.value,
        showAtStart: showAtStartValue.value,
        showBetween: showBetweenValue.value,
        style: styleNonNil
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: SeparatorTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivContainer.Separator> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var marginsValue: DeserializationResult<DivEdgeInsets> = .noValue
      var showAtEndValue: DeserializationResult<Expression<Bool>> = parent?.showAtEnd?.value() ?? .noValue
      var showAtStartValue: DeserializationResult<Expression<Bool>> = parent?.showAtStart?.value() ?? .noValue
      var showBetweenValue: DeserializationResult<Expression<Bool>> = parent?.showBetween?.value() ?? .noValue
      var styleValue: DeserializationResult<DivDrawable> = .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "margins":
          marginsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self).merged(with: marginsValue)
        case "show_at_end":
          showAtEndValue = deserialize(__dictValue).merged(with: showAtEndValue)
        case "show_at_start":
          showAtStartValue = deserialize(__dictValue).merged(with: showAtStartValue)
        case "show_between":
          showBetweenValue = deserialize(__dictValue).merged(with: showBetweenValue)
        case "style":
          styleValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDrawableTemplate.self).merged(with: styleValue)
        case parent?.margins?.link:
          marginsValue = marginsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self) })
        case parent?.showAtEnd?.link:
          showAtEndValue = showAtEndValue.merged(with: { deserialize(__dictValue) })
        case parent?.showAtStart?.link:
          showAtStartValue = showAtStartValue.merged(with: { deserialize(__dictValue) })
        case parent?.showBetween?.link:
          showBetweenValue = showBetweenValue.merged(with: { deserialize(__dictValue) })
        case parent?.style?.link:
          styleValue = styleValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDrawableTemplate.self) })
        default: break
        }
      }
      if let parent = parent {
        marginsValue = marginsValue.merged(with: { parent.margins?.resolveOptionalValue(context: context, useOnlyLinks: true) })
        styleValue = styleValue.merged(with: { parent.style?.resolveValue(context: context, useOnlyLinks: true) })
      }
      var errors = mergeErrors(
        marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
        showAtEndValue.errorsOrWarnings?.map { .nestedObjectError(field: "show_at_end", error: $0) },
        showAtStartValue.errorsOrWarnings?.map { .nestedObjectError(field: "show_at_start", error: $0) },
        showBetweenValue.errorsOrWarnings?.map { .nestedObjectError(field: "show_between", error: $0) },
        styleValue.errorsOrWarnings?.map { .nestedObjectError(field: "style", error: $0) }
      )
      if case .noValue = styleValue {
        errors.append(.requiredFieldIsMissing(field: "style"))
      }
      guard
        let styleNonNil = styleValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivContainer.Separator(
        margins: marginsValue.value,
        showAtEnd: showAtEndValue.value,
        showAtStart: showAtStartValue.value,
        showBetween: showBetweenValue.value,
        style: styleNonNil
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> SeparatorTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> SeparatorTemplate {
      let merged = try mergedWithParent(templates: templates)

      return SeparatorTemplate(
        margins: merged.margins?.tryResolveParent(templates: templates),
        showAtEnd: merged.showAtEnd,
        showAtStart: merged.showAtStart,
        showBetween: merged.showBetween,
        style: try merged.style?.resolveParent(templates: templates)
      )
    }
  }

  public typealias LayoutMode = DivContainer.LayoutMode

  public typealias Orientation = DivContainer.Orientation

  public static let type: String = "container"
  public let parent: String?
  public let accessibility: Field<DivAccessibilityTemplate>?
  public let action: Field<DivActionTemplate>?
  public let actionAnimation: Field<DivAnimationTemplate>? // default value: DivAnimation(duration: .value(100), endValue: .value(0.6), name: .value(.fade), startValue: .value(1))
  public let actions: Field<[DivActionTemplate]>?
  public let alignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>?
  public let alignmentVertical: Field<Expression<DivAlignmentVertical>>?
  public let alpha: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let aspect: Field<DivAspectTemplate>?
  public let background: Field<[DivBackgroundTemplate]>?
  public let border: Field<DivBorderTemplate>?
  public let clipToBounds: Field<Expression<Bool>>? // default value: true
  public let columnSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let contentAlignmentHorizontal: Field<Expression<DivContentAlignmentHorizontal>>? // default value: start
  public let contentAlignmentVertical: Field<Expression<DivContentAlignmentVertical>>? // default value: top
  public let disappearActions: Field<[DivDisappearActionTemplate]>?
  public let doubletapActions: Field<[DivActionTemplate]>?
  public let extensions: Field<[DivExtensionTemplate]>?
  public let focus: Field<DivFocusTemplate>?
  public let height: Field<DivSizeTemplate>? // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: Field<String>?
  public let itemBuilder: Field<DivCollectionItemBuilderTemplate>?
  public let items: Field<[DivTemplate]>?
  public let layoutMode: Field<Expression<LayoutMode>>? // default value: no_wrap
  public let lineSeparator: Field<SeparatorTemplate>?
  public let longtapActions: Field<[DivActionTemplate]>?
  public let margins: Field<DivEdgeInsetsTemplate>?
  public let orientation: Field<Expression<Orientation>>? // default value: vertical
  public let paddings: Field<DivEdgeInsetsTemplate>?
  public let rowSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let selectedActions: Field<[DivActionTemplate]>?
  public let separator: Field<SeparatorTemplate>?
  public let tooltips: Field<[DivTooltipTemplate]>?
  public let transform: Field<DivTransformTemplate>?
  public let transitionChange: Field<DivChangeTransitionTemplate>?
  public let transitionIn: Field<DivAppearanceTransitionTemplate>?
  public let transitionOut: Field<DivAppearanceTransitionTemplate>?
  public let transitionTriggers: Field<[DivTransitionTrigger]>? // at least 1 elements
  public let visibility: Field<Expression<DivVisibility>>? // default value: visible
  public let visibilityAction: Field<DivVisibilityActionTemplate>?
  public let visibilityActions: Field<[DivVisibilityActionTemplate]>?
  public let width: Field<DivSizeTemplate>? // default value: .divMatchParentSize(DivMatchParentSize())

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      accessibility: dictionary.getOptionalField("accessibility", templateToType: templateToType),
      action: dictionary.getOptionalField("action", templateToType: templateToType),
      actionAnimation: dictionary.getOptionalField("action_animation", templateToType: templateToType),
      actions: dictionary.getOptionalArray("actions", templateToType: templateToType),
      alignmentHorizontal: dictionary.getOptionalExpressionField("alignment_horizontal"),
      alignmentVertical: dictionary.getOptionalExpressionField("alignment_vertical"),
      alpha: dictionary.getOptionalExpressionField("alpha"),
      aspect: dictionary.getOptionalField("aspect", templateToType: templateToType),
      background: dictionary.getOptionalArray("background", templateToType: templateToType),
      border: dictionary.getOptionalField("border", templateToType: templateToType),
      clipToBounds: dictionary.getOptionalExpressionField("clip_to_bounds"),
      columnSpan: dictionary.getOptionalExpressionField("column_span"),
      contentAlignmentHorizontal: dictionary.getOptionalExpressionField("content_alignment_horizontal"),
      contentAlignmentVertical: dictionary.getOptionalExpressionField("content_alignment_vertical"),
      disappearActions: dictionary.getOptionalArray("disappear_actions", templateToType: templateToType),
      doubletapActions: dictionary.getOptionalArray("doubletap_actions", templateToType: templateToType),
      extensions: dictionary.getOptionalArray("extensions", templateToType: templateToType),
      focus: dictionary.getOptionalField("focus", templateToType: templateToType),
      height: dictionary.getOptionalField("height", templateToType: templateToType),
      id: dictionary.getOptionalField("id"),
      itemBuilder: dictionary.getOptionalField("item_builder", templateToType: templateToType),
      items: dictionary.getOptionalArray("items", templateToType: templateToType),
      layoutMode: dictionary.getOptionalExpressionField("layout_mode"),
      lineSeparator: dictionary.getOptionalField("line_separator", templateToType: templateToType),
      longtapActions: dictionary.getOptionalArray("longtap_actions", templateToType: templateToType),
      margins: dictionary.getOptionalField("margins", templateToType: templateToType),
      orientation: dictionary.getOptionalExpressionField("orientation"),
      paddings: dictionary.getOptionalField("paddings", templateToType: templateToType),
      rowSpan: dictionary.getOptionalExpressionField("row_span"),
      selectedActions: dictionary.getOptionalArray("selected_actions", templateToType: templateToType),
      separator: dictionary.getOptionalField("separator", templateToType: templateToType),
      tooltips: dictionary.getOptionalArray("tooltips", templateToType: templateToType),
      transform: dictionary.getOptionalField("transform", templateToType: templateToType),
      transitionChange: dictionary.getOptionalField("transition_change", templateToType: templateToType),
      transitionIn: dictionary.getOptionalField("transition_in", templateToType: templateToType),
      transitionOut: dictionary.getOptionalField("transition_out", templateToType: templateToType),
      transitionTriggers: dictionary.getOptionalArray("transition_triggers"),
      visibility: dictionary.getOptionalExpressionField("visibility"),
      visibilityAction: dictionary.getOptionalField("visibility_action", templateToType: templateToType),
      visibilityActions: dictionary.getOptionalArray("visibility_actions", templateToType: templateToType),
      width: dictionary.getOptionalField("width", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    accessibility: Field<DivAccessibilityTemplate>? = nil,
    action: Field<DivActionTemplate>? = nil,
    actionAnimation: Field<DivAnimationTemplate>? = nil,
    actions: Field<[DivActionTemplate]>? = nil,
    alignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>? = nil,
    alignmentVertical: Field<Expression<DivAlignmentVertical>>? = nil,
    alpha: Field<Expression<Double>>? = nil,
    aspect: Field<DivAspectTemplate>? = nil,
    background: Field<[DivBackgroundTemplate]>? = nil,
    border: Field<DivBorderTemplate>? = nil,
    clipToBounds: Field<Expression<Bool>>? = nil,
    columnSpan: Field<Expression<Int>>? = nil,
    contentAlignmentHorizontal: Field<Expression<DivContentAlignmentHorizontal>>? = nil,
    contentAlignmentVertical: Field<Expression<DivContentAlignmentVertical>>? = nil,
    disappearActions: Field<[DivDisappearActionTemplate]>? = nil,
    doubletapActions: Field<[DivActionTemplate]>? = nil,
    extensions: Field<[DivExtensionTemplate]>? = nil,
    focus: Field<DivFocusTemplate>? = nil,
    height: Field<DivSizeTemplate>? = nil,
    id: Field<String>? = nil,
    itemBuilder: Field<DivCollectionItemBuilderTemplate>? = nil,
    items: Field<[DivTemplate]>? = nil,
    layoutMode: Field<Expression<LayoutMode>>? = nil,
    lineSeparator: Field<SeparatorTemplate>? = nil,
    longtapActions: Field<[DivActionTemplate]>? = nil,
    margins: Field<DivEdgeInsetsTemplate>? = nil,
    orientation: Field<Expression<Orientation>>? = nil,
    paddings: Field<DivEdgeInsetsTemplate>? = nil,
    rowSpan: Field<Expression<Int>>? = nil,
    selectedActions: Field<[DivActionTemplate]>? = nil,
    separator: Field<SeparatorTemplate>? = nil,
    tooltips: Field<[DivTooltipTemplate]>? = nil,
    transform: Field<DivTransformTemplate>? = nil,
    transitionChange: Field<DivChangeTransitionTemplate>? = nil,
    transitionIn: Field<DivAppearanceTransitionTemplate>? = nil,
    transitionOut: Field<DivAppearanceTransitionTemplate>? = nil,
    transitionTriggers: Field<[DivTransitionTrigger]>? = nil,
    visibility: Field<Expression<DivVisibility>>? = nil,
    visibilityAction: Field<DivVisibilityActionTemplate>? = nil,
    visibilityActions: Field<[DivVisibilityActionTemplate]>? = nil,
    width: Field<DivSizeTemplate>? = nil
  ) {
    self.parent = parent
    self.accessibility = accessibility
    self.action = action
    self.actionAnimation = actionAnimation
    self.actions = actions
    self.alignmentHorizontal = alignmentHorizontal
    self.alignmentVertical = alignmentVertical
    self.alpha = alpha
    self.aspect = aspect
    self.background = background
    self.border = border
    self.clipToBounds = clipToBounds
    self.columnSpan = columnSpan
    self.contentAlignmentHorizontal = contentAlignmentHorizontal
    self.contentAlignmentVertical = contentAlignmentVertical
    self.disappearActions = disappearActions
    self.doubletapActions = doubletapActions
    self.extensions = extensions
    self.focus = focus
    self.height = height
    self.id = id
    self.itemBuilder = itemBuilder
    self.items = items
    self.layoutMode = layoutMode
    self.lineSeparator = lineSeparator
    self.longtapActions = longtapActions
    self.margins = margins
    self.orientation = orientation
    self.paddings = paddings
    self.rowSpan = rowSpan
    self.selectedActions = selectedActions
    self.separator = separator
    self.tooltips = tooltips
    self.transform = transform
    self.transitionChange = transitionChange
    self.transitionIn = transitionIn
    self.transitionOut = transitionOut
    self.transitionTriggers = transitionTriggers
    self.visibility = visibility
    self.visibilityAction = visibilityAction
    self.visibilityActions = visibilityActions
    self.width = width
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivContainerTemplate?) -> DeserializationResult<DivContainer> {
    let accessibilityValue = parent?.accessibility?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let actionValue = parent?.action?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let actionAnimationValue = parent?.actionAnimation?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let actionsValue = parent?.actions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let alignmentHorizontalValue = parent?.alignmentHorizontal?.resolveOptionalValue(context: context) ?? .noValue
    let alignmentVerticalValue = parent?.alignmentVertical?.resolveOptionalValue(context: context) ?? .noValue
    let alphaValue = parent?.alpha?.resolveOptionalValue(context: context, validator: ResolvedValue.alphaValidator) ?? .noValue
    let aspectValue = parent?.aspect?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let backgroundValue = parent?.background?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let borderValue = parent?.border?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let clipToBoundsValue = parent?.clipToBounds?.resolveOptionalValue(context: context) ?? .noValue
    let columnSpanValue = parent?.columnSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.columnSpanValidator) ?? .noValue
    let contentAlignmentHorizontalValue = parent?.contentAlignmentHorizontal?.resolveOptionalValue(context: context) ?? .noValue
    let contentAlignmentVerticalValue = parent?.contentAlignmentVertical?.resolveOptionalValue(context: context) ?? .noValue
    let disappearActionsValue = parent?.disappearActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let doubletapActionsValue = parent?.doubletapActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let extensionsValue = parent?.extensions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let focusValue = parent?.focus?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let heightValue = parent?.height?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let idValue = parent?.id?.resolveOptionalValue(context: context) ?? .noValue
    let itemBuilderValue = parent?.itemBuilder?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let itemsValue = parent?.items?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let layoutModeValue = parent?.layoutMode?.resolveOptionalValue(context: context) ?? .noValue
    let lineSeparatorValue = parent?.lineSeparator?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let longtapActionsValue = parent?.longtapActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let marginsValue = parent?.margins?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let orientationValue = parent?.orientation?.resolveOptionalValue(context: context) ?? .noValue
    let paddingsValue = parent?.paddings?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let rowSpanValue = parent?.rowSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.rowSpanValidator) ?? .noValue
    let selectedActionsValue = parent?.selectedActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let separatorValue = parent?.separator?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let tooltipsValue = parent?.tooltips?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let transformValue = parent?.transform?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let transitionChangeValue = parent?.transitionChange?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let transitionInValue = parent?.transitionIn?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let transitionOutValue = parent?.transitionOut?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let transitionTriggersValue = parent?.transitionTriggers?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionTriggersValidator) ?? .noValue
    let visibilityValue = parent?.visibility?.resolveOptionalValue(context: context) ?? .noValue
    let visibilityActionValue = parent?.visibilityAction?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let visibilityActionsValue = parent?.visibilityActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let widthValue = parent?.width?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "accessibility", error: $0) },
      actionValue.errorsOrWarnings?.map { .nestedObjectError(field: "action", error: $0) },
      actionAnimationValue.errorsOrWarnings?.map { .nestedObjectError(field: "action_animation", error: $0) },
      actionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "actions", error: $0) },
      alignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_horizontal", error: $0) },
      alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      aspectValue.errorsOrWarnings?.map { .nestedObjectError(field: "aspect", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      clipToBoundsValue.errorsOrWarnings?.map { .nestedObjectError(field: "clip_to_bounds", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      contentAlignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "content_alignment_horizontal", error: $0) },
      contentAlignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "content_alignment_vertical", error: $0) },
      disappearActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "disappear_actions", error: $0) },
      doubletapActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "doubletap_actions", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      itemBuilderValue.errorsOrWarnings?.map { .nestedObjectError(field: "item_builder", error: $0) },
      itemsValue.errorsOrWarnings?.map { .nestedObjectError(field: "items", error: $0) },
      layoutModeValue.errorsOrWarnings?.map { .nestedObjectError(field: "layout_mode", error: $0) },
      lineSeparatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "line_separator", error: $0) },
      longtapActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "longtap_actions", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      orientationValue.errorsOrWarnings?.map { .nestedObjectError(field: "orientation", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
      separatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "separator", error: $0) },
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
      transformValue.errorsOrWarnings?.map { .nestedObjectError(field: "transform", error: $0) },
      transitionChangeValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_change", error: $0) },
      transitionInValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_in", error: $0) },
      transitionOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_out", error: $0) },
      transitionTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_triggers", error: $0) },
      visibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility", error: $0) },
      visibilityActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_action", error: $0) },
      visibilityActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_actions", error: $0) },
      widthValue.errorsOrWarnings?.map { .nestedObjectError(field: "width", error: $0) }
    )
    let result = DivContainer(
      accessibility: accessibilityValue.value,
      action: actionValue.value,
      actionAnimation: actionAnimationValue.value,
      actions: actionsValue.value,
      alignmentHorizontal: alignmentHorizontalValue.value,
      alignmentVertical: alignmentVerticalValue.value,
      alpha: alphaValue.value,
      aspect: aspectValue.value,
      background: backgroundValue.value,
      border: borderValue.value,
      clipToBounds: clipToBoundsValue.value,
      columnSpan: columnSpanValue.value,
      contentAlignmentHorizontal: contentAlignmentHorizontalValue.value,
      contentAlignmentVertical: contentAlignmentVerticalValue.value,
      disappearActions: disappearActionsValue.value,
      doubletapActions: doubletapActionsValue.value,
      extensions: extensionsValue.value,
      focus: focusValue.value,
      height: heightValue.value,
      id: idValue.value,
      itemBuilder: itemBuilderValue.value,
      items: itemsValue.value,
      layoutMode: layoutModeValue.value,
      lineSeparator: lineSeparatorValue.value,
      longtapActions: longtapActionsValue.value,
      margins: marginsValue.value,
      orientation: orientationValue.value,
      paddings: paddingsValue.value,
      rowSpan: rowSpanValue.value,
      selectedActions: selectedActionsValue.value,
      separator: separatorValue.value,
      tooltips: tooltipsValue.value,
      transform: transformValue.value,
      transitionChange: transitionChangeValue.value,
      transitionIn: transitionInValue.value,
      transitionOut: transitionOutValue.value,
      transitionTriggers: transitionTriggersValue.value,
      visibility: visibilityValue.value,
      visibilityAction: visibilityActionValue.value,
      visibilityActions: visibilityActionsValue.value,
      width: widthValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivContainerTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivContainer> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var accessibilityValue: DeserializationResult<DivAccessibility> = .noValue
    var actionValue: DeserializationResult<DivAction> = .noValue
    var actionAnimationValue: DeserializationResult<DivAnimation> = .noValue
    var actionsValue: DeserializationResult<[DivAction]> = .noValue
    var alignmentHorizontalValue: DeserializationResult<Expression<DivAlignmentHorizontal>> = parent?.alignmentHorizontal?.value() ?? .noValue
    var alignmentVerticalValue: DeserializationResult<Expression<DivAlignmentVertical>> = parent?.alignmentVertical?.value() ?? .noValue
    var alphaValue: DeserializationResult<Expression<Double>> = parent?.alpha?.value() ?? .noValue
    var aspectValue: DeserializationResult<DivAspect> = .noValue
    var backgroundValue: DeserializationResult<[DivBackground]> = .noValue
    var borderValue: DeserializationResult<DivBorder> = .noValue
    var clipToBoundsValue: DeserializationResult<Expression<Bool>> = parent?.clipToBounds?.value() ?? .noValue
    var columnSpanValue: DeserializationResult<Expression<Int>> = parent?.columnSpan?.value() ?? .noValue
    var contentAlignmentHorizontalValue: DeserializationResult<Expression<DivContentAlignmentHorizontal>> = parent?.contentAlignmentHorizontal?.value() ?? .noValue
    var contentAlignmentVerticalValue: DeserializationResult<Expression<DivContentAlignmentVertical>> = parent?.contentAlignmentVertical?.value() ?? .noValue
    var disappearActionsValue: DeserializationResult<[DivDisappearAction]> = .noValue
    var doubletapActionsValue: DeserializationResult<[DivAction]> = .noValue
    var extensionsValue: DeserializationResult<[DivExtension]> = .noValue
    var focusValue: DeserializationResult<DivFocus> = .noValue
    var heightValue: DeserializationResult<DivSize> = .noValue
    var idValue: DeserializationResult<String> = parent?.id?.value() ?? .noValue
    var itemBuilderValue: DeserializationResult<DivCollectionItemBuilder> = .noValue
    var itemsValue: DeserializationResult<[Div]> = .noValue
    var layoutModeValue: DeserializationResult<Expression<DivContainer.LayoutMode>> = parent?.layoutMode?.value() ?? .noValue
    var lineSeparatorValue: DeserializationResult<DivContainer.Separator> = .noValue
    var longtapActionsValue: DeserializationResult<[DivAction]> = .noValue
    var marginsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var orientationValue: DeserializationResult<Expression<DivContainer.Orientation>> = parent?.orientation?.value() ?? .noValue
    var paddingsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var rowSpanValue: DeserializationResult<Expression<Int>> = parent?.rowSpan?.value() ?? .noValue
    var selectedActionsValue: DeserializationResult<[DivAction]> = .noValue
    var separatorValue: DeserializationResult<DivContainer.Separator> = .noValue
    var tooltipsValue: DeserializationResult<[DivTooltip]> = .noValue
    var transformValue: DeserializationResult<DivTransform> = .noValue
    var transitionChangeValue: DeserializationResult<DivChangeTransition> = .noValue
    var transitionInValue: DeserializationResult<DivAppearanceTransition> = .noValue
    var transitionOutValue: DeserializationResult<DivAppearanceTransition> = .noValue
    var transitionTriggersValue: DeserializationResult<[DivTransitionTrigger]> = parent?.transitionTriggers?.value(validatedBy: ResolvedValue.transitionTriggersValidator) ?? .noValue
    var visibilityValue: DeserializationResult<Expression<DivVisibility>> = parent?.visibility?.value() ?? .noValue
    var visibilityActionValue: DeserializationResult<DivVisibilityAction> = .noValue
    var visibilityActionsValue: DeserializationResult<[DivVisibilityAction]> = .noValue
    var widthValue: DeserializationResult<DivSize> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "accessibility":
        accessibilityValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAccessibilityTemplate.self).merged(with: accessibilityValue)
      case "action":
        actionValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: actionValue)
      case "action_animation":
        actionAnimationValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAnimationTemplate.self).merged(with: actionAnimationValue)
      case "actions":
        actionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: actionsValue)
      case "alignment_horizontal":
        alignmentHorizontalValue = deserialize(__dictValue).merged(with: alignmentHorizontalValue)
      case "alignment_vertical":
        alignmentVerticalValue = deserialize(__dictValue).merged(with: alignmentVerticalValue)
      case "alpha":
        alphaValue = deserialize(__dictValue, validator: ResolvedValue.alphaValidator).merged(with: alphaValue)
      case "aspect":
        aspectValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAspectTemplate.self).merged(with: aspectValue)
      case "background":
        backgroundValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBackgroundTemplate.self).merged(with: backgroundValue)
      case "border":
        borderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBorderTemplate.self).merged(with: borderValue)
      case "clip_to_bounds":
        clipToBoundsValue = deserialize(__dictValue).merged(with: clipToBoundsValue)
      case "column_span":
        columnSpanValue = deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator).merged(with: columnSpanValue)
      case "content_alignment_horizontal":
        contentAlignmentHorizontalValue = deserialize(__dictValue).merged(with: contentAlignmentHorizontalValue)
      case "content_alignment_vertical":
        contentAlignmentVerticalValue = deserialize(__dictValue).merged(with: contentAlignmentVerticalValue)
      case "disappear_actions":
        disappearActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDisappearActionTemplate.self).merged(with: disappearActionsValue)
      case "doubletap_actions":
        doubletapActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: doubletapActionsValue)
      case "extensions":
        extensionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivExtensionTemplate.self).merged(with: extensionsValue)
      case "focus":
        focusValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFocusTemplate.self).merged(with: focusValue)
      case "height":
        heightValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self).merged(with: heightValue)
      case "id":
        idValue = deserialize(__dictValue).merged(with: idValue)
      case "item_builder":
        itemBuilderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivCollectionItemBuilderTemplate.self).merged(with: itemBuilderValue)
      case "items":
        itemsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTemplate.self).merged(with: itemsValue)
      case "layout_mode":
        layoutModeValue = deserialize(__dictValue).merged(with: layoutModeValue)
      case "line_separator":
        lineSeparatorValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivContainerTemplate.SeparatorTemplate.self).merged(with: lineSeparatorValue)
      case "longtap_actions":
        longtapActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: longtapActionsValue)
      case "margins":
        marginsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self).merged(with: marginsValue)
      case "orientation":
        orientationValue = deserialize(__dictValue).merged(with: orientationValue)
      case "paddings":
        paddingsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self).merged(with: paddingsValue)
      case "row_span":
        rowSpanValue = deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator).merged(with: rowSpanValue)
      case "selected_actions":
        selectedActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: selectedActionsValue)
      case "separator":
        separatorValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivContainerTemplate.SeparatorTemplate.self).merged(with: separatorValue)
      case "tooltips":
        tooltipsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTooltipTemplate.self).merged(with: tooltipsValue)
      case "transform":
        transformValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTransformTemplate.self).merged(with: transformValue)
      case "transition_change":
        transitionChangeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivChangeTransitionTemplate.self).merged(with: transitionChangeValue)
      case "transition_in":
        transitionInValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self).merged(with: transitionInValue)
      case "transition_out":
        transitionOutValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self).merged(with: transitionOutValue)
      case "transition_triggers":
        transitionTriggersValue = deserialize(__dictValue, validator: ResolvedValue.transitionTriggersValidator).merged(with: transitionTriggersValue)
      case "visibility":
        visibilityValue = deserialize(__dictValue).merged(with: visibilityValue)
      case "visibility_action":
        visibilityActionValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self).merged(with: visibilityActionValue)
      case "visibility_actions":
        visibilityActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self).merged(with: visibilityActionsValue)
      case "width":
        widthValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self).merged(with: widthValue)
      case parent?.accessibility?.link:
        accessibilityValue = accessibilityValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAccessibilityTemplate.self) })
      case parent?.action?.link:
        actionValue = actionValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
      case parent?.actionAnimation?.link:
        actionAnimationValue = actionAnimationValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAnimationTemplate.self) })
      case parent?.actions?.link:
        actionsValue = actionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
      case parent?.alignmentHorizontal?.link:
        alignmentHorizontalValue = alignmentHorizontalValue.merged(with: { deserialize(__dictValue) })
      case parent?.alignmentVertical?.link:
        alignmentVerticalValue = alignmentVerticalValue.merged(with: { deserialize(__dictValue) })
      case parent?.alpha?.link:
        alphaValue = alphaValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.alphaValidator) })
      case parent?.aspect?.link:
        aspectValue = aspectValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAspectTemplate.self) })
      case parent?.background?.link:
        backgroundValue = backgroundValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBackgroundTemplate.self) })
      case parent?.border?.link:
        borderValue = borderValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBorderTemplate.self) })
      case parent?.clipToBounds?.link:
        clipToBoundsValue = clipToBoundsValue.merged(with: { deserialize(__dictValue) })
      case parent?.columnSpan?.link:
        columnSpanValue = columnSpanValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator) })
      case parent?.contentAlignmentHorizontal?.link:
        contentAlignmentHorizontalValue = contentAlignmentHorizontalValue.merged(with: { deserialize(__dictValue) })
      case parent?.contentAlignmentVertical?.link:
        contentAlignmentVerticalValue = contentAlignmentVerticalValue.merged(with: { deserialize(__dictValue) })
      case parent?.disappearActions?.link:
        disappearActionsValue = disappearActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDisappearActionTemplate.self) })
      case parent?.doubletapActions?.link:
        doubletapActionsValue = doubletapActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
      case parent?.extensions?.link:
        extensionsValue = extensionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivExtensionTemplate.self) })
      case parent?.focus?.link:
        focusValue = focusValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFocusTemplate.self) })
      case parent?.height?.link:
        heightValue = heightValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self) })
      case parent?.id?.link:
        idValue = idValue.merged(with: { deserialize(__dictValue) })
      case parent?.itemBuilder?.link:
        itemBuilderValue = itemBuilderValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivCollectionItemBuilderTemplate.self) })
      case parent?.items?.link:
        itemsValue = itemsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTemplate.self) })
      case parent?.layoutMode?.link:
        layoutModeValue = layoutModeValue.merged(with: { deserialize(__dictValue) })
      case parent?.lineSeparator?.link:
        lineSeparatorValue = lineSeparatorValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivContainerTemplate.SeparatorTemplate.self) })
      case parent?.longtapActions?.link:
        longtapActionsValue = longtapActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
      case parent?.margins?.link:
        marginsValue = marginsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self) })
      case parent?.orientation?.link:
        orientationValue = orientationValue.merged(with: { deserialize(__dictValue) })
      case parent?.paddings?.link:
        paddingsValue = paddingsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self) })
      case parent?.rowSpan?.link:
        rowSpanValue = rowSpanValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator) })
      case parent?.selectedActions?.link:
        selectedActionsValue = selectedActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
      case parent?.separator?.link:
        separatorValue = separatorValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivContainerTemplate.SeparatorTemplate.self) })
      case parent?.tooltips?.link:
        tooltipsValue = tooltipsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTooltipTemplate.self) })
      case parent?.transform?.link:
        transformValue = transformValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTransformTemplate.self) })
      case parent?.transitionChange?.link:
        transitionChangeValue = transitionChangeValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivChangeTransitionTemplate.self) })
      case parent?.transitionIn?.link:
        transitionInValue = transitionInValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self) })
      case parent?.transitionOut?.link:
        transitionOutValue = transitionOutValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self) })
      case parent?.transitionTriggers?.link:
        transitionTriggersValue = transitionTriggersValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.transitionTriggersValidator) })
      case parent?.visibility?.link:
        visibilityValue = visibilityValue.merged(with: { deserialize(__dictValue) })
      case parent?.visibilityAction?.link:
        visibilityActionValue = visibilityActionValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self) })
      case parent?.visibilityActions?.link:
        visibilityActionsValue = visibilityActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self) })
      case parent?.width?.link:
        widthValue = widthValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self) })
      default: break
      }
    }
    if let parent = parent {
      accessibilityValue = accessibilityValue.merged(with: { parent.accessibility?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      actionValue = actionValue.merged(with: { parent.action?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      actionAnimationValue = actionAnimationValue.merged(with: { parent.actionAnimation?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      actionsValue = actionsValue.merged(with: { parent.actions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      aspectValue = aspectValue.merged(with: { parent.aspect?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      backgroundValue = backgroundValue.merged(with: { parent.background?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      borderValue = borderValue.merged(with: { parent.border?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      disappearActionsValue = disappearActionsValue.merged(with: { parent.disappearActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      doubletapActionsValue = doubletapActionsValue.merged(with: { parent.doubletapActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      extensionsValue = extensionsValue.merged(with: { parent.extensions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      focusValue = focusValue.merged(with: { parent.focus?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      heightValue = heightValue.merged(with: { parent.height?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      itemBuilderValue = itemBuilderValue.merged(with: { parent.itemBuilder?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      itemsValue = itemsValue.merged(with: { parent.items?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      lineSeparatorValue = lineSeparatorValue.merged(with: { parent.lineSeparator?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      longtapActionsValue = longtapActionsValue.merged(with: { parent.longtapActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      marginsValue = marginsValue.merged(with: { parent.margins?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      paddingsValue = paddingsValue.merged(with: { parent.paddings?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      selectedActionsValue = selectedActionsValue.merged(with: { parent.selectedActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      separatorValue = separatorValue.merged(with: { parent.separator?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      tooltipsValue = tooltipsValue.merged(with: { parent.tooltips?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      transformValue = transformValue.merged(with: { parent.transform?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      transitionChangeValue = transitionChangeValue.merged(with: { parent.transitionChange?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      transitionInValue = transitionInValue.merged(with: { parent.transitionIn?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      transitionOutValue = transitionOutValue.merged(with: { parent.transitionOut?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      visibilityActionValue = visibilityActionValue.merged(with: { parent.visibilityAction?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      visibilityActionsValue = visibilityActionsValue.merged(with: { parent.visibilityActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      widthValue = widthValue.merged(with: { parent.width?.resolveOptionalValue(context: context, useOnlyLinks: true) })
    }
    let errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "accessibility", error: $0) },
      actionValue.errorsOrWarnings?.map { .nestedObjectError(field: "action", error: $0) },
      actionAnimationValue.errorsOrWarnings?.map { .nestedObjectError(field: "action_animation", error: $0) },
      actionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "actions", error: $0) },
      alignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_horizontal", error: $0) },
      alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      aspectValue.errorsOrWarnings?.map { .nestedObjectError(field: "aspect", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      clipToBoundsValue.errorsOrWarnings?.map { .nestedObjectError(field: "clip_to_bounds", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      contentAlignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "content_alignment_horizontal", error: $0) },
      contentAlignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "content_alignment_vertical", error: $0) },
      disappearActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "disappear_actions", error: $0) },
      doubletapActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "doubletap_actions", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      itemBuilderValue.errorsOrWarnings?.map { .nestedObjectError(field: "item_builder", error: $0) },
      itemsValue.errorsOrWarnings?.map { .nestedObjectError(field: "items", error: $0) },
      layoutModeValue.errorsOrWarnings?.map { .nestedObjectError(field: "layout_mode", error: $0) },
      lineSeparatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "line_separator", error: $0) },
      longtapActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "longtap_actions", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      orientationValue.errorsOrWarnings?.map { .nestedObjectError(field: "orientation", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
      separatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "separator", error: $0) },
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
      transformValue.errorsOrWarnings?.map { .nestedObjectError(field: "transform", error: $0) },
      transitionChangeValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_change", error: $0) },
      transitionInValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_in", error: $0) },
      transitionOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_out", error: $0) },
      transitionTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_triggers", error: $0) },
      visibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility", error: $0) },
      visibilityActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_action", error: $0) },
      visibilityActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_actions", error: $0) },
      widthValue.errorsOrWarnings?.map { .nestedObjectError(field: "width", error: $0) }
    )
    let result = DivContainer(
      accessibility: accessibilityValue.value,
      action: actionValue.value,
      actionAnimation: actionAnimationValue.value,
      actions: actionsValue.value,
      alignmentHorizontal: alignmentHorizontalValue.value,
      alignmentVertical: alignmentVerticalValue.value,
      alpha: alphaValue.value,
      aspect: aspectValue.value,
      background: backgroundValue.value,
      border: borderValue.value,
      clipToBounds: clipToBoundsValue.value,
      columnSpan: columnSpanValue.value,
      contentAlignmentHorizontal: contentAlignmentHorizontalValue.value,
      contentAlignmentVertical: contentAlignmentVerticalValue.value,
      disappearActions: disappearActionsValue.value,
      doubletapActions: doubletapActionsValue.value,
      extensions: extensionsValue.value,
      focus: focusValue.value,
      height: heightValue.value,
      id: idValue.value,
      itemBuilder: itemBuilderValue.value,
      items: itemsValue.value,
      layoutMode: layoutModeValue.value,
      lineSeparator: lineSeparatorValue.value,
      longtapActions: longtapActionsValue.value,
      margins: marginsValue.value,
      orientation: orientationValue.value,
      paddings: paddingsValue.value,
      rowSpan: rowSpanValue.value,
      selectedActions: selectedActionsValue.value,
      separator: separatorValue.value,
      tooltips: tooltipsValue.value,
      transform: transformValue.value,
      transitionChange: transitionChangeValue.value,
      transitionIn: transitionInValue.value,
      transitionOut: transitionOutValue.value,
      transitionTriggers: transitionTriggersValue.value,
      visibility: visibilityValue.value,
      visibilityAction: visibilityActionValue.value,
      visibilityActions: visibilityActionsValue.value,
      width: widthValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivContainerTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivContainerTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivContainerTemplate(
      parent: nil,
      accessibility: accessibility ?? mergedParent.accessibility,
      action: action ?? mergedParent.action,
      actionAnimation: actionAnimation ?? mergedParent.actionAnimation,
      actions: actions ?? mergedParent.actions,
      alignmentHorizontal: alignmentHorizontal ?? mergedParent.alignmentHorizontal,
      alignmentVertical: alignmentVertical ?? mergedParent.alignmentVertical,
      alpha: alpha ?? mergedParent.alpha,
      aspect: aspect ?? mergedParent.aspect,
      background: background ?? mergedParent.background,
      border: border ?? mergedParent.border,
      clipToBounds: clipToBounds ?? mergedParent.clipToBounds,
      columnSpan: columnSpan ?? mergedParent.columnSpan,
      contentAlignmentHorizontal: contentAlignmentHorizontal ?? mergedParent.contentAlignmentHorizontal,
      contentAlignmentVertical: contentAlignmentVertical ?? mergedParent.contentAlignmentVertical,
      disappearActions: disappearActions ?? mergedParent.disappearActions,
      doubletapActions: doubletapActions ?? mergedParent.doubletapActions,
      extensions: extensions ?? mergedParent.extensions,
      focus: focus ?? mergedParent.focus,
      height: height ?? mergedParent.height,
      id: id ?? mergedParent.id,
      itemBuilder: itemBuilder ?? mergedParent.itemBuilder,
      items: items ?? mergedParent.items,
      layoutMode: layoutMode ?? mergedParent.layoutMode,
      lineSeparator: lineSeparator ?? mergedParent.lineSeparator,
      longtapActions: longtapActions ?? mergedParent.longtapActions,
      margins: margins ?? mergedParent.margins,
      orientation: orientation ?? mergedParent.orientation,
      paddings: paddings ?? mergedParent.paddings,
      rowSpan: rowSpan ?? mergedParent.rowSpan,
      selectedActions: selectedActions ?? mergedParent.selectedActions,
      separator: separator ?? mergedParent.separator,
      tooltips: tooltips ?? mergedParent.tooltips,
      transform: transform ?? mergedParent.transform,
      transitionChange: transitionChange ?? mergedParent.transitionChange,
      transitionIn: transitionIn ?? mergedParent.transitionIn,
      transitionOut: transitionOut ?? mergedParent.transitionOut,
      transitionTriggers: transitionTriggers ?? mergedParent.transitionTriggers,
      visibility: visibility ?? mergedParent.visibility,
      visibilityAction: visibilityAction ?? mergedParent.visibilityAction,
      visibilityActions: visibilityActions ?? mergedParent.visibilityActions,
      width: width ?? mergedParent.width
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivContainerTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivContainerTemplate(
      parent: nil,
      accessibility: merged.accessibility?.tryResolveParent(templates: templates),
      action: merged.action?.tryResolveParent(templates: templates),
      actionAnimation: merged.actionAnimation?.tryResolveParent(templates: templates),
      actions: merged.actions?.tryResolveParent(templates: templates),
      alignmentHorizontal: merged.alignmentHorizontal,
      alignmentVertical: merged.alignmentVertical,
      alpha: merged.alpha,
      aspect: merged.aspect?.tryResolveParent(templates: templates),
      background: merged.background?.tryResolveParent(templates: templates),
      border: merged.border?.tryResolveParent(templates: templates),
      clipToBounds: merged.clipToBounds,
      columnSpan: merged.columnSpan,
      contentAlignmentHorizontal: merged.contentAlignmentHorizontal,
      contentAlignmentVertical: merged.contentAlignmentVertical,
      disappearActions: merged.disappearActions?.tryResolveParent(templates: templates),
      doubletapActions: merged.doubletapActions?.tryResolveParent(templates: templates),
      extensions: merged.extensions?.tryResolveParent(templates: templates),
      focus: merged.focus?.tryResolveParent(templates: templates),
      height: merged.height?.tryResolveParent(templates: templates),
      id: merged.id,
      itemBuilder: merged.itemBuilder?.tryResolveParent(templates: templates),
      items: merged.items?.tryResolveParent(templates: templates),
      layoutMode: merged.layoutMode,
      lineSeparator: merged.lineSeparator?.tryResolveParent(templates: templates),
      longtapActions: merged.longtapActions?.tryResolveParent(templates: templates),
      margins: merged.margins?.tryResolveParent(templates: templates),
      orientation: merged.orientation,
      paddings: merged.paddings?.tryResolveParent(templates: templates),
      rowSpan: merged.rowSpan,
      selectedActions: merged.selectedActions?.tryResolveParent(templates: templates),
      separator: merged.separator?.tryResolveParent(templates: templates),
      tooltips: merged.tooltips?.tryResolveParent(templates: templates),
      transform: merged.transform?.tryResolveParent(templates: templates),
      transitionChange: merged.transitionChange?.tryResolveParent(templates: templates),
      transitionIn: merged.transitionIn?.tryResolveParent(templates: templates),
      transitionOut: merged.transitionOut?.tryResolveParent(templates: templates),
      transitionTriggers: merged.transitionTriggers,
      visibility: merged.visibility,
      visibilityAction: merged.visibilityAction?.tryResolveParent(templates: templates),
      visibilityActions: merged.visibilityActions?.tryResolveParent(templates: templates),
      width: merged.width?.tryResolveParent(templates: templates)
    )
  }
}
