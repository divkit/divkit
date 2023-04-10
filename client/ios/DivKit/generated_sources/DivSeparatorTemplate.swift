// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivSeparatorTemplate: TemplateValue {
  public final class DelimiterStyleTemplate: TemplateValue {
    public typealias Orientation = DivSeparator.DelimiterStyle.Orientation

    public let color: Field<Expression<Color>>? // default value: #14000000
    public let orientation: Field<Expression<Orientation>>? // default value: horizontal

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      self.init(
        color: try dictionary.getOptionalExpressionField("color", transform: Color.color(withHexString:)),
        orientation: try dictionary.getOptionalExpressionField("orientation")
      )
    }

    init(
      color: Field<Expression<Color>>? = nil,
      orientation: Field<Expression<Orientation>>? = nil
    ) {
      self.color = color
      self.orientation = orientation
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: DelimiterStyleTemplate?) -> DeserializationResult<DivSeparator.DelimiterStyle> {
      let colorValue = parent?.color?.resolveOptionalValue(context: context, transform: Color.color(withHexString:), validator: ResolvedValue.colorValidator) ?? .noValue
      let orientationValue = parent?.orientation?.resolveOptionalValue(context: context, validator: ResolvedValue.orientationValidator) ?? .noValue
      let errors = mergeErrors(
        colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) },
        orientationValue.errorsOrWarnings?.map { .nestedObjectError(field: "orientation", error: $0) }
      )
      let result = DivSeparator.DelimiterStyle(
        color: colorValue.value,
        orientation: orientationValue.value
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: DelimiterStyleTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivSeparator.DelimiterStyle> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var colorValue: DeserializationResult<Expression<Color>> = parent?.color?.value() ?? .noValue
      var orientationValue: DeserializationResult<Expression<DivSeparator.DelimiterStyle.Orientation>> = parent?.orientation?.value() ?? .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "color":
          colorValue = deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.colorValidator).merged(with: colorValue)
        case "orientation":
          orientationValue = deserialize(__dictValue, validator: ResolvedValue.orientationValidator).merged(with: orientationValue)
        case parent?.color?.link:
          colorValue = colorValue.merged(with: deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.colorValidator))
        case parent?.orientation?.link:
          orientationValue = orientationValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.orientationValidator))
        default: break
        }
      }
      let errors = mergeErrors(
        colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) },
        orientationValue.errorsOrWarnings?.map { .nestedObjectError(field: "orientation", error: $0) }
      )
      let result = DivSeparator.DelimiterStyle(
        color: colorValue.value,
        orientation: orientationValue.value
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> DelimiterStyleTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> DelimiterStyleTemplate {
      return try mergedWithParent(templates: templates)
    }
  }

  public static let type: String = "separator"
  public let parent: String? // at least 1 char
  public let accessibility: Field<DivAccessibilityTemplate>?
  public let action: Field<DivActionTemplate>?
  public let actionAnimation: Field<DivAnimationTemplate>? // default value: DivAnimation(duration: .value(100), endValue: .value(0.6), name: .value(.fade), startValue: .value(1))
  public let actions: Field<[DivActionTemplate]>? // at least 1 elements
  public let alignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>?
  public let alignmentVertical: Field<Expression<DivAlignmentVertical>>?
  public let alpha: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let background: Field<[DivBackgroundTemplate]>? // at least 1 elements
  public let border: Field<DivBorderTemplate>?
  public let columnSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let delimiterStyle: Field<DelimiterStyleTemplate>?
  public let doubletapActions: Field<[DivActionTemplate]>? // at least 1 elements
  public let extensions: Field<[DivExtensionTemplate]>? // at least 1 elements
  public let focus: Field<DivFocusTemplate>?
  public let height: Field<DivSizeTemplate>? // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: Field<String>? // at least 1 char
  public let longtapActions: Field<[DivActionTemplate]>? // at least 1 elements
  public let margins: Field<DivEdgeInsetsTemplate>?
  public let paddings: Field<DivEdgeInsetsTemplate>?
  public let rowSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let selectedActions: Field<[DivActionTemplate]>? // at least 1 elements
  public let tooltips: Field<[DivTooltipTemplate]>? // at least 1 elements
  public let transform: Field<DivTransformTemplate>?
  public let transitionChange: Field<DivChangeTransitionTemplate>?
  public let transitionIn: Field<DivAppearanceTransitionTemplate>?
  public let transitionOut: Field<DivAppearanceTransitionTemplate>?
  public let transitionTriggers: Field<[DivTransitionTrigger]>? // at least 1 elements
  public let visibility: Field<Expression<DivVisibility>>? // default value: visible
  public let visibilityAction: Field<DivVisibilityActionTemplate>?
  public let visibilityActions: Field<[DivVisibilityActionTemplate]>? // at least 1 elements
  public let width: Field<DivSizeTemplate>? // default value: .divMatchParentSize(DivMatchParentSize())

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
      accessibility: try dictionary.getOptionalField("accessibility", templateToType: templateToType),
      action: try dictionary.getOptionalField("action", templateToType: templateToType),
      actionAnimation: try dictionary.getOptionalField("action_animation", templateToType: templateToType),
      actions: try dictionary.getOptionalArray("actions", templateToType: templateToType),
      alignmentHorizontal: try dictionary.getOptionalExpressionField("alignment_horizontal"),
      alignmentVertical: try dictionary.getOptionalExpressionField("alignment_vertical"),
      alpha: try dictionary.getOptionalExpressionField("alpha"),
      background: try dictionary.getOptionalArray("background", templateToType: templateToType),
      border: try dictionary.getOptionalField("border", templateToType: templateToType),
      columnSpan: try dictionary.getOptionalExpressionField("column_span"),
      delimiterStyle: try dictionary.getOptionalField("delimiter_style", templateToType: templateToType),
      doubletapActions: try dictionary.getOptionalArray("doubletap_actions", templateToType: templateToType),
      extensions: try dictionary.getOptionalArray("extensions", templateToType: templateToType),
      focus: try dictionary.getOptionalField("focus", templateToType: templateToType),
      height: try dictionary.getOptionalField("height", templateToType: templateToType),
      id: try dictionary.getOptionalField("id"),
      longtapActions: try dictionary.getOptionalArray("longtap_actions", templateToType: templateToType),
      margins: try dictionary.getOptionalField("margins", templateToType: templateToType),
      paddings: try dictionary.getOptionalField("paddings", templateToType: templateToType),
      rowSpan: try dictionary.getOptionalExpressionField("row_span"),
      selectedActions: try dictionary.getOptionalArray("selected_actions", templateToType: templateToType),
      tooltips: try dictionary.getOptionalArray("tooltips", templateToType: templateToType),
      transform: try dictionary.getOptionalField("transform", templateToType: templateToType),
      transitionChange: try dictionary.getOptionalField("transition_change", templateToType: templateToType),
      transitionIn: try dictionary.getOptionalField("transition_in", templateToType: templateToType),
      transitionOut: try dictionary.getOptionalField("transition_out", templateToType: templateToType),
      transitionTriggers: try dictionary.getOptionalArray("transition_triggers"),
      visibility: try dictionary.getOptionalExpressionField("visibility"),
      visibilityAction: try dictionary.getOptionalField("visibility_action", templateToType: templateToType),
      visibilityActions: try dictionary.getOptionalArray("visibility_actions", templateToType: templateToType),
      width: try dictionary.getOptionalField("width", templateToType: templateToType)
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
    background: Field<[DivBackgroundTemplate]>? = nil,
    border: Field<DivBorderTemplate>? = nil,
    columnSpan: Field<Expression<Int>>? = nil,
    delimiterStyle: Field<DelimiterStyleTemplate>? = nil,
    doubletapActions: Field<[DivActionTemplate]>? = nil,
    extensions: Field<[DivExtensionTemplate]>? = nil,
    focus: Field<DivFocusTemplate>? = nil,
    height: Field<DivSizeTemplate>? = nil,
    id: Field<String>? = nil,
    longtapActions: Field<[DivActionTemplate]>? = nil,
    margins: Field<DivEdgeInsetsTemplate>? = nil,
    paddings: Field<DivEdgeInsetsTemplate>? = nil,
    rowSpan: Field<Expression<Int>>? = nil,
    selectedActions: Field<[DivActionTemplate]>? = nil,
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
    self.background = background
    self.border = border
    self.columnSpan = columnSpan
    self.delimiterStyle = delimiterStyle
    self.doubletapActions = doubletapActions
    self.extensions = extensions
    self.focus = focus
    self.height = height
    self.id = id
    self.longtapActions = longtapActions
    self.margins = margins
    self.paddings = paddings
    self.rowSpan = rowSpan
    self.selectedActions = selectedActions
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

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivSeparatorTemplate?) -> DeserializationResult<DivSeparator> {
    let accessibilityValue = parent?.accessibility?.resolveOptionalValue(context: context, validator: ResolvedValue.accessibilityValidator, useOnlyLinks: true) ?? .noValue
    let actionValue = parent?.action?.resolveOptionalValue(context: context, validator: ResolvedValue.actionValidator, useOnlyLinks: true) ?? .noValue
    let actionAnimationValue = parent?.actionAnimation?.resolveOptionalValue(context: context, validator: ResolvedValue.actionAnimationValidator, useOnlyLinks: true) ?? .noValue
    let actionsValue = parent?.actions?.resolveOptionalValue(context: context, validator: ResolvedValue.actionsValidator, useOnlyLinks: true) ?? .noValue
    let alignmentHorizontalValue = parent?.alignmentHorizontal?.resolveOptionalValue(context: context, validator: ResolvedValue.alignmentHorizontalValidator) ?? .noValue
    let alignmentVerticalValue = parent?.alignmentVertical?.resolveOptionalValue(context: context, validator: ResolvedValue.alignmentVerticalValidator) ?? .noValue
    let alphaValue = parent?.alpha?.resolveOptionalValue(context: context, validator: ResolvedValue.alphaValidator) ?? .noValue
    let backgroundValue = parent?.background?.resolveOptionalValue(context: context, validator: ResolvedValue.backgroundValidator, useOnlyLinks: true) ?? .noValue
    let borderValue = parent?.border?.resolveOptionalValue(context: context, validator: ResolvedValue.borderValidator, useOnlyLinks: true) ?? .noValue
    let columnSpanValue = parent?.columnSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.columnSpanValidator) ?? .noValue
    let delimiterStyleValue = parent?.delimiterStyle?.resolveOptionalValue(context: context, validator: ResolvedValue.delimiterStyleValidator, useOnlyLinks: true) ?? .noValue
    let doubletapActionsValue = parent?.doubletapActions?.resolveOptionalValue(context: context, validator: ResolvedValue.doubletapActionsValidator, useOnlyLinks: true) ?? .noValue
    let extensionsValue = parent?.extensions?.resolveOptionalValue(context: context, validator: ResolvedValue.extensionsValidator, useOnlyLinks: true) ?? .noValue
    let focusValue = parent?.focus?.resolveOptionalValue(context: context, validator: ResolvedValue.focusValidator, useOnlyLinks: true) ?? .noValue
    let heightValue = parent?.height?.resolveOptionalValue(context: context, validator: ResolvedValue.heightValidator, useOnlyLinks: true) ?? .noValue
    let idValue = parent?.id?.resolveOptionalValue(context: context, validator: ResolvedValue.idValidator) ?? .noValue
    let longtapActionsValue = parent?.longtapActions?.resolveOptionalValue(context: context, validator: ResolvedValue.longtapActionsValidator, useOnlyLinks: true) ?? .noValue
    let marginsValue = parent?.margins?.resolveOptionalValue(context: context, validator: ResolvedValue.marginsValidator, useOnlyLinks: true) ?? .noValue
    let paddingsValue = parent?.paddings?.resolveOptionalValue(context: context, validator: ResolvedValue.paddingsValidator, useOnlyLinks: true) ?? .noValue
    let rowSpanValue = parent?.rowSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.rowSpanValidator) ?? .noValue
    let selectedActionsValue = parent?.selectedActions?.resolveOptionalValue(context: context, validator: ResolvedValue.selectedActionsValidator, useOnlyLinks: true) ?? .noValue
    let tooltipsValue = parent?.tooltips?.resolveOptionalValue(context: context, validator: ResolvedValue.tooltipsValidator, useOnlyLinks: true) ?? .noValue
    let transformValue = parent?.transform?.resolveOptionalValue(context: context, validator: ResolvedValue.transformValidator, useOnlyLinks: true) ?? .noValue
    let transitionChangeValue = parent?.transitionChange?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionChangeValidator, useOnlyLinks: true) ?? .noValue
    let transitionInValue = parent?.transitionIn?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionInValidator, useOnlyLinks: true) ?? .noValue
    let transitionOutValue = parent?.transitionOut?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionOutValidator, useOnlyLinks: true) ?? .noValue
    let transitionTriggersValue = parent?.transitionTriggers?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionTriggersValidator) ?? .noValue
    let visibilityValue = parent?.visibility?.resolveOptionalValue(context: context, validator: ResolvedValue.visibilityValidator) ?? .noValue
    let visibilityActionValue = parent?.visibilityAction?.resolveOptionalValue(context: context, validator: ResolvedValue.visibilityActionValidator, useOnlyLinks: true) ?? .noValue
    let visibilityActionsValue = parent?.visibilityActions?.resolveOptionalValue(context: context, validator: ResolvedValue.visibilityActionsValidator, useOnlyLinks: true) ?? .noValue
    let widthValue = parent?.width?.resolveOptionalValue(context: context, validator: ResolvedValue.widthValidator, useOnlyLinks: true) ?? .noValue
    let errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "accessibility", error: $0) },
      actionValue.errorsOrWarnings?.map { .nestedObjectError(field: "action", error: $0) },
      actionAnimationValue.errorsOrWarnings?.map { .nestedObjectError(field: "action_animation", error: $0) },
      actionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "actions", error: $0) },
      alignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_horizontal", error: $0) },
      alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      delimiterStyleValue.errorsOrWarnings?.map { .nestedObjectError(field: "delimiter_style", error: $0) },
      doubletapActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "doubletap_actions", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      longtapActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "longtap_actions", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
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
    let result = DivSeparator(
      accessibility: accessibilityValue.value,
      action: actionValue.value,
      actionAnimation: actionAnimationValue.value,
      actions: actionsValue.value,
      alignmentHorizontal: alignmentHorizontalValue.value,
      alignmentVertical: alignmentVerticalValue.value,
      alpha: alphaValue.value,
      background: backgroundValue.value,
      border: borderValue.value,
      columnSpan: columnSpanValue.value,
      delimiterStyle: delimiterStyleValue.value,
      doubletapActions: doubletapActionsValue.value,
      extensions: extensionsValue.value,
      focus: focusValue.value,
      height: heightValue.value,
      id: idValue.value,
      longtapActions: longtapActionsValue.value,
      margins: marginsValue.value,
      paddings: paddingsValue.value,
      rowSpan: rowSpanValue.value,
      selectedActions: selectedActionsValue.value,
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

  public static func resolveValue(context: TemplatesContext, parent: DivSeparatorTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivSeparator> {
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
    var backgroundValue: DeserializationResult<[DivBackground]> = .noValue
    var borderValue: DeserializationResult<DivBorder> = .noValue
    var columnSpanValue: DeserializationResult<Expression<Int>> = parent?.columnSpan?.value() ?? .noValue
    var delimiterStyleValue: DeserializationResult<DivSeparator.DelimiterStyle> = .noValue
    var doubletapActionsValue: DeserializationResult<[DivAction]> = .noValue
    var extensionsValue: DeserializationResult<[DivExtension]> = .noValue
    var focusValue: DeserializationResult<DivFocus> = .noValue
    var heightValue: DeserializationResult<DivSize> = .noValue
    var idValue: DeserializationResult<String> = parent?.id?.value(validatedBy: ResolvedValue.idValidator) ?? .noValue
    var longtapActionsValue: DeserializationResult<[DivAction]> = .noValue
    var marginsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var paddingsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var rowSpanValue: DeserializationResult<Expression<Int>> = parent?.rowSpan?.value() ?? .noValue
    var selectedActionsValue: DeserializationResult<[DivAction]> = .noValue
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
        accessibilityValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.accessibilityValidator, type: DivAccessibilityTemplate.self).merged(with: accessibilityValue)
      case "action":
        actionValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.actionValidator, type: DivActionTemplate.self).merged(with: actionValue)
      case "action_animation":
        actionAnimationValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.actionAnimationValidator, type: DivAnimationTemplate.self).merged(with: actionAnimationValue)
      case "actions":
        actionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.actionsValidator, type: DivActionTemplate.self).merged(with: actionsValue)
      case "alignment_horizontal":
        alignmentHorizontalValue = deserialize(__dictValue, validator: ResolvedValue.alignmentHorizontalValidator).merged(with: alignmentHorizontalValue)
      case "alignment_vertical":
        alignmentVerticalValue = deserialize(__dictValue, validator: ResolvedValue.alignmentVerticalValidator).merged(with: alignmentVerticalValue)
      case "alpha":
        alphaValue = deserialize(__dictValue, validator: ResolvedValue.alphaValidator).merged(with: alphaValue)
      case "background":
        backgroundValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.backgroundValidator, type: DivBackgroundTemplate.self).merged(with: backgroundValue)
      case "border":
        borderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.borderValidator, type: DivBorderTemplate.self).merged(with: borderValue)
      case "column_span":
        columnSpanValue = deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator).merged(with: columnSpanValue)
      case "delimiter_style":
        delimiterStyleValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.delimiterStyleValidator, type: DivSeparatorTemplate.DelimiterStyleTemplate.self).merged(with: delimiterStyleValue)
      case "doubletap_actions":
        doubletapActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.doubletapActionsValidator, type: DivActionTemplate.self).merged(with: doubletapActionsValue)
      case "extensions":
        extensionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.extensionsValidator, type: DivExtensionTemplate.self).merged(with: extensionsValue)
      case "focus":
        focusValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.focusValidator, type: DivFocusTemplate.self).merged(with: focusValue)
      case "height":
        heightValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.heightValidator, type: DivSizeTemplate.self).merged(with: heightValue)
      case "id":
        idValue = deserialize(__dictValue, validator: ResolvedValue.idValidator).merged(with: idValue)
      case "longtap_actions":
        longtapActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.longtapActionsValidator, type: DivActionTemplate.self).merged(with: longtapActionsValue)
      case "margins":
        marginsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.marginsValidator, type: DivEdgeInsetsTemplate.self).merged(with: marginsValue)
      case "paddings":
        paddingsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.paddingsValidator, type: DivEdgeInsetsTemplate.self).merged(with: paddingsValue)
      case "row_span":
        rowSpanValue = deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator).merged(with: rowSpanValue)
      case "selected_actions":
        selectedActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.selectedActionsValidator, type: DivActionTemplate.self).merged(with: selectedActionsValue)
      case "tooltips":
        tooltipsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.tooltipsValidator, type: DivTooltipTemplate.self).merged(with: tooltipsValue)
      case "transform":
        transformValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.transformValidator, type: DivTransformTemplate.self).merged(with: transformValue)
      case "transition_change":
        transitionChangeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.transitionChangeValidator, type: DivChangeTransitionTemplate.self).merged(with: transitionChangeValue)
      case "transition_in":
        transitionInValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.transitionInValidator, type: DivAppearanceTransitionTemplate.self).merged(with: transitionInValue)
      case "transition_out":
        transitionOutValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.transitionOutValidator, type: DivAppearanceTransitionTemplate.self).merged(with: transitionOutValue)
      case "transition_triggers":
        transitionTriggersValue = deserialize(__dictValue, validator: ResolvedValue.transitionTriggersValidator).merged(with: transitionTriggersValue)
      case "visibility":
        visibilityValue = deserialize(__dictValue, validator: ResolvedValue.visibilityValidator).merged(with: visibilityValue)
      case "visibility_action":
        visibilityActionValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.visibilityActionValidator, type: DivVisibilityActionTemplate.self).merged(with: visibilityActionValue)
      case "visibility_actions":
        visibilityActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.visibilityActionsValidator, type: DivVisibilityActionTemplate.self).merged(with: visibilityActionsValue)
      case "width":
        widthValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.widthValidator, type: DivSizeTemplate.self).merged(with: widthValue)
      case parent?.accessibility?.link:
        accessibilityValue = accessibilityValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.accessibilityValidator, type: DivAccessibilityTemplate.self))
      case parent?.action?.link:
        actionValue = actionValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.actionValidator, type: DivActionTemplate.self))
      case parent?.actionAnimation?.link:
        actionAnimationValue = actionAnimationValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.actionAnimationValidator, type: DivAnimationTemplate.self))
      case parent?.actions?.link:
        actionsValue = actionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.actionsValidator, type: DivActionTemplate.self))
      case parent?.alignmentHorizontal?.link:
        alignmentHorizontalValue = alignmentHorizontalValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.alignmentHorizontalValidator))
      case parent?.alignmentVertical?.link:
        alignmentVerticalValue = alignmentVerticalValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.alignmentVerticalValidator))
      case parent?.alpha?.link:
        alphaValue = alphaValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.alphaValidator))
      case parent?.background?.link:
        backgroundValue = backgroundValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.backgroundValidator, type: DivBackgroundTemplate.self))
      case parent?.border?.link:
        borderValue = borderValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.borderValidator, type: DivBorderTemplate.self))
      case parent?.columnSpan?.link:
        columnSpanValue = columnSpanValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator))
      case parent?.delimiterStyle?.link:
        delimiterStyleValue = delimiterStyleValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.delimiterStyleValidator, type: DivSeparatorTemplate.DelimiterStyleTemplate.self))
      case parent?.doubletapActions?.link:
        doubletapActionsValue = doubletapActionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.doubletapActionsValidator, type: DivActionTemplate.self))
      case parent?.extensions?.link:
        extensionsValue = extensionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.extensionsValidator, type: DivExtensionTemplate.self))
      case parent?.focus?.link:
        focusValue = focusValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.focusValidator, type: DivFocusTemplate.self))
      case parent?.height?.link:
        heightValue = heightValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.heightValidator, type: DivSizeTemplate.self))
      case parent?.id?.link:
        idValue = idValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.idValidator))
      case parent?.longtapActions?.link:
        longtapActionsValue = longtapActionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.longtapActionsValidator, type: DivActionTemplate.self))
      case parent?.margins?.link:
        marginsValue = marginsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.marginsValidator, type: DivEdgeInsetsTemplate.self))
      case parent?.paddings?.link:
        paddingsValue = paddingsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.paddingsValidator, type: DivEdgeInsetsTemplate.self))
      case parent?.rowSpan?.link:
        rowSpanValue = rowSpanValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator))
      case parent?.selectedActions?.link:
        selectedActionsValue = selectedActionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.selectedActionsValidator, type: DivActionTemplate.self))
      case parent?.tooltips?.link:
        tooltipsValue = tooltipsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.tooltipsValidator, type: DivTooltipTemplate.self))
      case parent?.transform?.link:
        transformValue = transformValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.transformValidator, type: DivTransformTemplate.self))
      case parent?.transitionChange?.link:
        transitionChangeValue = transitionChangeValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.transitionChangeValidator, type: DivChangeTransitionTemplate.self))
      case parent?.transitionIn?.link:
        transitionInValue = transitionInValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.transitionInValidator, type: DivAppearanceTransitionTemplate.self))
      case parent?.transitionOut?.link:
        transitionOutValue = transitionOutValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.transitionOutValidator, type: DivAppearanceTransitionTemplate.self))
      case parent?.transitionTriggers?.link:
        transitionTriggersValue = transitionTriggersValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.transitionTriggersValidator))
      case parent?.visibility?.link:
        visibilityValue = visibilityValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.visibilityValidator))
      case parent?.visibilityAction?.link:
        visibilityActionValue = visibilityActionValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.visibilityActionValidator, type: DivVisibilityActionTemplate.self))
      case parent?.visibilityActions?.link:
        visibilityActionsValue = visibilityActionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.visibilityActionsValidator, type: DivVisibilityActionTemplate.self))
      case parent?.width?.link:
        widthValue = widthValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.widthValidator, type: DivSizeTemplate.self))
      default: break
      }
    }
    if let parent = parent {
      accessibilityValue = accessibilityValue.merged(with: parent.accessibility?.resolveOptionalValue(context: context, validator: ResolvedValue.accessibilityValidator, useOnlyLinks: true))
      actionValue = actionValue.merged(with: parent.action?.resolveOptionalValue(context: context, validator: ResolvedValue.actionValidator, useOnlyLinks: true))
      actionAnimationValue = actionAnimationValue.merged(with: parent.actionAnimation?.resolveOptionalValue(context: context, validator: ResolvedValue.actionAnimationValidator, useOnlyLinks: true))
      actionsValue = actionsValue.merged(with: parent.actions?.resolveOptionalValue(context: context, validator: ResolvedValue.actionsValidator, useOnlyLinks: true))
      backgroundValue = backgroundValue.merged(with: parent.background?.resolveOptionalValue(context: context, validator: ResolvedValue.backgroundValidator, useOnlyLinks: true))
      borderValue = borderValue.merged(with: parent.border?.resolveOptionalValue(context: context, validator: ResolvedValue.borderValidator, useOnlyLinks: true))
      delimiterStyleValue = delimiterStyleValue.merged(with: parent.delimiterStyle?.resolveOptionalValue(context: context, validator: ResolvedValue.delimiterStyleValidator, useOnlyLinks: true))
      doubletapActionsValue = doubletapActionsValue.merged(with: parent.doubletapActions?.resolveOptionalValue(context: context, validator: ResolvedValue.doubletapActionsValidator, useOnlyLinks: true))
      extensionsValue = extensionsValue.merged(with: parent.extensions?.resolveOptionalValue(context: context, validator: ResolvedValue.extensionsValidator, useOnlyLinks: true))
      focusValue = focusValue.merged(with: parent.focus?.resolveOptionalValue(context: context, validator: ResolvedValue.focusValidator, useOnlyLinks: true))
      heightValue = heightValue.merged(with: parent.height?.resolveOptionalValue(context: context, validator: ResolvedValue.heightValidator, useOnlyLinks: true))
      longtapActionsValue = longtapActionsValue.merged(with: parent.longtapActions?.resolveOptionalValue(context: context, validator: ResolvedValue.longtapActionsValidator, useOnlyLinks: true))
      marginsValue = marginsValue.merged(with: parent.margins?.resolveOptionalValue(context: context, validator: ResolvedValue.marginsValidator, useOnlyLinks: true))
      paddingsValue = paddingsValue.merged(with: parent.paddings?.resolveOptionalValue(context: context, validator: ResolvedValue.paddingsValidator, useOnlyLinks: true))
      selectedActionsValue = selectedActionsValue.merged(with: parent.selectedActions?.resolveOptionalValue(context: context, validator: ResolvedValue.selectedActionsValidator, useOnlyLinks: true))
      tooltipsValue = tooltipsValue.merged(with: parent.tooltips?.resolveOptionalValue(context: context, validator: ResolvedValue.tooltipsValidator, useOnlyLinks: true))
      transformValue = transformValue.merged(with: parent.transform?.resolveOptionalValue(context: context, validator: ResolvedValue.transformValidator, useOnlyLinks: true))
      transitionChangeValue = transitionChangeValue.merged(with: parent.transitionChange?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionChangeValidator, useOnlyLinks: true))
      transitionInValue = transitionInValue.merged(with: parent.transitionIn?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionInValidator, useOnlyLinks: true))
      transitionOutValue = transitionOutValue.merged(with: parent.transitionOut?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionOutValidator, useOnlyLinks: true))
      visibilityActionValue = visibilityActionValue.merged(with: parent.visibilityAction?.resolveOptionalValue(context: context, validator: ResolvedValue.visibilityActionValidator, useOnlyLinks: true))
      visibilityActionsValue = visibilityActionsValue.merged(with: parent.visibilityActions?.resolveOptionalValue(context: context, validator: ResolvedValue.visibilityActionsValidator, useOnlyLinks: true))
      widthValue = widthValue.merged(with: parent.width?.resolveOptionalValue(context: context, validator: ResolvedValue.widthValidator, useOnlyLinks: true))
    }
    let errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "accessibility", error: $0) },
      actionValue.errorsOrWarnings?.map { .nestedObjectError(field: "action", error: $0) },
      actionAnimationValue.errorsOrWarnings?.map { .nestedObjectError(field: "action_animation", error: $0) },
      actionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "actions", error: $0) },
      alignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_horizontal", error: $0) },
      alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      delimiterStyleValue.errorsOrWarnings?.map { .nestedObjectError(field: "delimiter_style", error: $0) },
      doubletapActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "doubletap_actions", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      longtapActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "longtap_actions", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
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
    let result = DivSeparator(
      accessibility: accessibilityValue.value,
      action: actionValue.value,
      actionAnimation: actionAnimationValue.value,
      actions: actionsValue.value,
      alignmentHorizontal: alignmentHorizontalValue.value,
      alignmentVertical: alignmentVerticalValue.value,
      alpha: alphaValue.value,
      background: backgroundValue.value,
      border: borderValue.value,
      columnSpan: columnSpanValue.value,
      delimiterStyle: delimiterStyleValue.value,
      doubletapActions: doubletapActionsValue.value,
      extensions: extensionsValue.value,
      focus: focusValue.value,
      height: heightValue.value,
      id: idValue.value,
      longtapActions: longtapActionsValue.value,
      margins: marginsValue.value,
      paddings: paddingsValue.value,
      rowSpan: rowSpanValue.value,
      selectedActions: selectedActionsValue.value,
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

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivSeparatorTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivSeparatorTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivSeparatorTemplate(
      parent: nil,
      accessibility: accessibility ?? mergedParent.accessibility,
      action: action ?? mergedParent.action,
      actionAnimation: actionAnimation ?? mergedParent.actionAnimation,
      actions: actions ?? mergedParent.actions,
      alignmentHorizontal: alignmentHorizontal ?? mergedParent.alignmentHorizontal,
      alignmentVertical: alignmentVertical ?? mergedParent.alignmentVertical,
      alpha: alpha ?? mergedParent.alpha,
      background: background ?? mergedParent.background,
      border: border ?? mergedParent.border,
      columnSpan: columnSpan ?? mergedParent.columnSpan,
      delimiterStyle: delimiterStyle ?? mergedParent.delimiterStyle,
      doubletapActions: doubletapActions ?? mergedParent.doubletapActions,
      extensions: extensions ?? mergedParent.extensions,
      focus: focus ?? mergedParent.focus,
      height: height ?? mergedParent.height,
      id: id ?? mergedParent.id,
      longtapActions: longtapActions ?? mergedParent.longtapActions,
      margins: margins ?? mergedParent.margins,
      paddings: paddings ?? mergedParent.paddings,
      rowSpan: rowSpan ?? mergedParent.rowSpan,
      selectedActions: selectedActions ?? mergedParent.selectedActions,
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

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivSeparatorTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivSeparatorTemplate(
      parent: nil,
      accessibility: merged.accessibility?.tryResolveParent(templates: templates),
      action: merged.action?.tryResolveParent(templates: templates),
      actionAnimation: merged.actionAnimation?.tryResolveParent(templates: templates),
      actions: merged.actions?.tryResolveParent(templates: templates),
      alignmentHorizontal: merged.alignmentHorizontal,
      alignmentVertical: merged.alignmentVertical,
      alpha: merged.alpha,
      background: merged.background?.tryResolveParent(templates: templates),
      border: merged.border?.tryResolveParent(templates: templates),
      columnSpan: merged.columnSpan,
      delimiterStyle: merged.delimiterStyle?.tryResolveParent(templates: templates),
      doubletapActions: merged.doubletapActions?.tryResolveParent(templates: templates),
      extensions: merged.extensions?.tryResolveParent(templates: templates),
      focus: merged.focus?.tryResolveParent(templates: templates),
      height: merged.height?.tryResolveParent(templates: templates),
      id: merged.id,
      longtapActions: merged.longtapActions?.tryResolveParent(templates: templates),
      margins: merged.margins?.tryResolveParent(templates: templates),
      paddings: merged.paddings?.tryResolveParent(templates: templates),
      rowSpan: merged.rowSpan,
      selectedActions: merged.selectedActions?.tryResolveParent(templates: templates),
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
