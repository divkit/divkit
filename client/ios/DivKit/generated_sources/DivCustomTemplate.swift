// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivCustomTemplate: TemplateValue {
  public static let type: String = "custom"
  public let parent: String?
  public let accessibility: Field<DivAccessibilityTemplate>?
  public let alignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>?
  public let alignmentVertical: Field<Expression<DivAlignmentVertical>>?
  public let alpha: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let background: Field<[DivBackgroundTemplate]>?
  public let border: Field<DivBorderTemplate>?
  public let columnSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let customProps: Field<[String: Any]>?
  public let customType: Field<String>?
  public let disappearActions: Field<[DivDisappearActionTemplate]>?
  public let extensions: Field<[DivExtensionTemplate]>?
  public let focus: Field<DivFocusTemplate>?
  public let height: Field<DivSizeTemplate>? // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: Field<String>?
  public let items: Field<[DivTemplate]>?
  public let layoutProvider: Field<DivLayoutProviderTemplate>?
  public let margins: Field<DivEdgeInsetsTemplate>?
  public let paddings: Field<DivEdgeInsetsTemplate>?
  public let reuseId: Field<Expression<String>>?
  public let rowSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let selectedActions: Field<[DivActionTemplate]>?
  public let tooltips: Field<[DivTooltipTemplate]>?
  public let transform: Field<DivTransformTemplate>?
  public let transitionChange: Field<DivChangeTransitionTemplate>?
  public let transitionIn: Field<DivAppearanceTransitionTemplate>?
  public let transitionOut: Field<DivAppearanceTransitionTemplate>?
  public let transitionTriggers: Field<[DivTransitionTrigger]>? // at least 1 elements
  public let variableTriggers: Field<[DivTriggerTemplate]>?
  public let variables: Field<[DivVariableTemplate]>?
  public let visibility: Field<Expression<DivVisibility>>? // default value: visible
  public let visibilityAction: Field<DivVisibilityActionTemplate>?
  public let visibilityActions: Field<[DivVisibilityActionTemplate]>?
  public let width: Field<DivSizeTemplate>? // default value: .divMatchParentSize(DivMatchParentSize())

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      accessibility: dictionary.getOptionalField("accessibility", templateToType: templateToType),
      alignmentHorizontal: dictionary.getOptionalExpressionField("alignment_horizontal"),
      alignmentVertical: dictionary.getOptionalExpressionField("alignment_vertical"),
      alpha: dictionary.getOptionalExpressionField("alpha"),
      background: dictionary.getOptionalArray("background", templateToType: templateToType),
      border: dictionary.getOptionalField("border", templateToType: templateToType),
      columnSpan: dictionary.getOptionalExpressionField("column_span"),
      customProps: dictionary.getOptionalField("custom_props"),
      customType: dictionary.getOptionalField("custom_type"),
      disappearActions: dictionary.getOptionalArray("disappear_actions", templateToType: templateToType),
      extensions: dictionary.getOptionalArray("extensions", templateToType: templateToType),
      focus: dictionary.getOptionalField("focus", templateToType: templateToType),
      height: dictionary.getOptionalField("height", templateToType: templateToType),
      id: dictionary.getOptionalField("id"),
      items: dictionary.getOptionalArray("items", templateToType: templateToType),
      layoutProvider: dictionary.getOptionalField("layout_provider", templateToType: templateToType),
      margins: dictionary.getOptionalField("margins", templateToType: templateToType),
      paddings: dictionary.getOptionalField("paddings", templateToType: templateToType),
      reuseId: dictionary.getOptionalExpressionField("reuse_id"),
      rowSpan: dictionary.getOptionalExpressionField("row_span"),
      selectedActions: dictionary.getOptionalArray("selected_actions", templateToType: templateToType),
      tooltips: dictionary.getOptionalArray("tooltips", templateToType: templateToType),
      transform: dictionary.getOptionalField("transform", templateToType: templateToType),
      transitionChange: dictionary.getOptionalField("transition_change", templateToType: templateToType),
      transitionIn: dictionary.getOptionalField("transition_in", templateToType: templateToType),
      transitionOut: dictionary.getOptionalField("transition_out", templateToType: templateToType),
      transitionTriggers: dictionary.getOptionalArray("transition_triggers"),
      variableTriggers: dictionary.getOptionalArray("variable_triggers", templateToType: templateToType),
      variables: dictionary.getOptionalArray("variables", templateToType: templateToType),
      visibility: dictionary.getOptionalExpressionField("visibility"),
      visibilityAction: dictionary.getOptionalField("visibility_action", templateToType: templateToType),
      visibilityActions: dictionary.getOptionalArray("visibility_actions", templateToType: templateToType),
      width: dictionary.getOptionalField("width", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    accessibility: Field<DivAccessibilityTemplate>? = nil,
    alignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>? = nil,
    alignmentVertical: Field<Expression<DivAlignmentVertical>>? = nil,
    alpha: Field<Expression<Double>>? = nil,
    background: Field<[DivBackgroundTemplate]>? = nil,
    border: Field<DivBorderTemplate>? = nil,
    columnSpan: Field<Expression<Int>>? = nil,
    customProps: Field<[String: Any]>? = nil,
    customType: Field<String>? = nil,
    disappearActions: Field<[DivDisappearActionTemplate]>? = nil,
    extensions: Field<[DivExtensionTemplate]>? = nil,
    focus: Field<DivFocusTemplate>? = nil,
    height: Field<DivSizeTemplate>? = nil,
    id: Field<String>? = nil,
    items: Field<[DivTemplate]>? = nil,
    layoutProvider: Field<DivLayoutProviderTemplate>? = nil,
    margins: Field<DivEdgeInsetsTemplate>? = nil,
    paddings: Field<DivEdgeInsetsTemplate>? = nil,
    reuseId: Field<Expression<String>>? = nil,
    rowSpan: Field<Expression<Int>>? = nil,
    selectedActions: Field<[DivActionTemplate]>? = nil,
    tooltips: Field<[DivTooltipTemplate]>? = nil,
    transform: Field<DivTransformTemplate>? = nil,
    transitionChange: Field<DivChangeTransitionTemplate>? = nil,
    transitionIn: Field<DivAppearanceTransitionTemplate>? = nil,
    transitionOut: Field<DivAppearanceTransitionTemplate>? = nil,
    transitionTriggers: Field<[DivTransitionTrigger]>? = nil,
    variableTriggers: Field<[DivTriggerTemplate]>? = nil,
    variables: Field<[DivVariableTemplate]>? = nil,
    visibility: Field<Expression<DivVisibility>>? = nil,
    visibilityAction: Field<DivVisibilityActionTemplate>? = nil,
    visibilityActions: Field<[DivVisibilityActionTemplate]>? = nil,
    width: Field<DivSizeTemplate>? = nil
  ) {
    self.parent = parent
    self.accessibility = accessibility
    self.alignmentHorizontal = alignmentHorizontal
    self.alignmentVertical = alignmentVertical
    self.alpha = alpha
    self.background = background
    self.border = border
    self.columnSpan = columnSpan
    self.customProps = customProps
    self.customType = customType
    self.disappearActions = disappearActions
    self.extensions = extensions
    self.focus = focus
    self.height = height
    self.id = id
    self.items = items
    self.layoutProvider = layoutProvider
    self.margins = margins
    self.paddings = paddings
    self.reuseId = reuseId
    self.rowSpan = rowSpan
    self.selectedActions = selectedActions
    self.tooltips = tooltips
    self.transform = transform
    self.transitionChange = transitionChange
    self.transitionIn = transitionIn
    self.transitionOut = transitionOut
    self.transitionTriggers = transitionTriggers
    self.variableTriggers = variableTriggers
    self.variables = variables
    self.visibility = visibility
    self.visibilityAction = visibilityAction
    self.visibilityActions = visibilityActions
    self.width = width
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivCustomTemplate?) -> DeserializationResult<DivCustom> {
    let accessibilityValue = parent?.accessibility?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let alignmentHorizontalValue = parent?.alignmentHorizontal?.resolveOptionalValue(context: context) ?? .noValue
    let alignmentVerticalValue = parent?.alignmentVertical?.resolveOptionalValue(context: context) ?? .noValue
    let alphaValue = parent?.alpha?.resolveOptionalValue(context: context, validator: ResolvedValue.alphaValidator) ?? .noValue
    let backgroundValue = parent?.background?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let borderValue = parent?.border?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let columnSpanValue = parent?.columnSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.columnSpanValidator) ?? .noValue
    let customPropsValue = parent?.customProps?.resolveOptionalValue(context: context) ?? .noValue
    let customTypeValue = parent?.customType?.resolveValue(context: context) ?? .noValue
    let disappearActionsValue = parent?.disappearActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let extensionsValue = parent?.extensions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let focusValue = parent?.focus?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let heightValue = parent?.height?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let idValue = parent?.id?.resolveOptionalValue(context: context) ?? .noValue
    let itemsValue = parent?.items?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let layoutProviderValue = parent?.layoutProvider?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let marginsValue = parent?.margins?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let paddingsValue = parent?.paddings?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let reuseIdValue = parent?.reuseId?.resolveOptionalValue(context: context) ?? .noValue
    let rowSpanValue = parent?.rowSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.rowSpanValidator) ?? .noValue
    let selectedActionsValue = parent?.selectedActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let tooltipsValue = parent?.tooltips?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let transformValue = parent?.transform?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let transitionChangeValue = parent?.transitionChange?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let transitionInValue = parent?.transitionIn?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let transitionOutValue = parent?.transitionOut?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let transitionTriggersValue = parent?.transitionTriggers?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionTriggersValidator) ?? .noValue
    let variableTriggersValue = parent?.variableTriggers?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let variablesValue = parent?.variables?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let visibilityValue = parent?.visibility?.resolveOptionalValue(context: context) ?? .noValue
    let visibilityActionValue = parent?.visibilityAction?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let visibilityActionsValue = parent?.visibilityActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let widthValue = parent?.width?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    var errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "accessibility", error: $0) },
      alignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_horizontal", error: $0) },
      alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      customPropsValue.errorsOrWarnings?.map { .nestedObjectError(field: "custom_props", error: $0) },
      customTypeValue.errorsOrWarnings?.map { .nestedObjectError(field: "custom_type", error: $0) },
      disappearActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "disappear_actions", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      itemsValue.errorsOrWarnings?.map { .nestedObjectError(field: "items", error: $0) },
      layoutProviderValue.errorsOrWarnings?.map { .nestedObjectError(field: "layout_provider", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      reuseIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "reuse_id", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
      transformValue.errorsOrWarnings?.map { .nestedObjectError(field: "transform", error: $0) },
      transitionChangeValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_change", error: $0) },
      transitionInValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_in", error: $0) },
      transitionOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_out", error: $0) },
      transitionTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_triggers", error: $0) },
      variableTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable_triggers", error: $0) },
      variablesValue.errorsOrWarnings?.map { .nestedObjectError(field: "variables", error: $0) },
      visibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility", error: $0) },
      visibilityActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_action", error: $0) },
      visibilityActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_actions", error: $0) },
      widthValue.errorsOrWarnings?.map { .nestedObjectError(field: "width", error: $0) }
    )
    if case .noValue = customTypeValue {
      errors.append(.requiredFieldIsMissing(field: "custom_type"))
    }
    guard
      let customTypeNonNil = customTypeValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivCustom(
      accessibility: accessibilityValue.value,
      alignmentHorizontal: alignmentHorizontalValue.value,
      alignmentVertical: alignmentVerticalValue.value,
      alpha: alphaValue.value,
      background: backgroundValue.value,
      border: borderValue.value,
      columnSpan: columnSpanValue.value,
      customProps: customPropsValue.value,
      customType: customTypeNonNil,
      disappearActions: disappearActionsValue.value,
      extensions: extensionsValue.value,
      focus: focusValue.value,
      height: heightValue.value,
      id: idValue.value,
      items: itemsValue.value,
      layoutProvider: layoutProviderValue.value,
      margins: marginsValue.value,
      paddings: paddingsValue.value,
      reuseId: reuseIdValue.value,
      rowSpan: rowSpanValue.value,
      selectedActions: selectedActionsValue.value,
      tooltips: tooltipsValue.value,
      transform: transformValue.value,
      transitionChange: transitionChangeValue.value,
      transitionIn: transitionInValue.value,
      transitionOut: transitionOutValue.value,
      transitionTriggers: transitionTriggersValue.value,
      variableTriggers: variableTriggersValue.value,
      variables: variablesValue.value,
      visibility: visibilityValue.value,
      visibilityAction: visibilityActionValue.value,
      visibilityActions: visibilityActionsValue.value,
      width: widthValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivCustomTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivCustom> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var accessibilityValue: DeserializationResult<DivAccessibility> = .noValue
    var alignmentHorizontalValue: DeserializationResult<Expression<DivAlignmentHorizontal>> = parent?.alignmentHorizontal?.value() ?? .noValue
    var alignmentVerticalValue: DeserializationResult<Expression<DivAlignmentVertical>> = parent?.alignmentVertical?.value() ?? .noValue
    var alphaValue: DeserializationResult<Expression<Double>> = parent?.alpha?.value() ?? .noValue
    var backgroundValue: DeserializationResult<[DivBackground]> = .noValue
    var borderValue: DeserializationResult<DivBorder> = .noValue
    var columnSpanValue: DeserializationResult<Expression<Int>> = parent?.columnSpan?.value() ?? .noValue
    var customPropsValue: DeserializationResult<[String: Any]> = parent?.customProps?.value() ?? .noValue
    var customTypeValue: DeserializationResult<String> = parent?.customType?.value() ?? .noValue
    var disappearActionsValue: DeserializationResult<[DivDisappearAction]> = .noValue
    var extensionsValue: DeserializationResult<[DivExtension]> = .noValue
    var focusValue: DeserializationResult<DivFocus> = .noValue
    var heightValue: DeserializationResult<DivSize> = .noValue
    var idValue: DeserializationResult<String> = parent?.id?.value() ?? .noValue
    var itemsValue: DeserializationResult<[Div]> = .noValue
    var layoutProviderValue: DeserializationResult<DivLayoutProvider> = .noValue
    var marginsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var paddingsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var reuseIdValue: DeserializationResult<Expression<String>> = parent?.reuseId?.value() ?? .noValue
    var rowSpanValue: DeserializationResult<Expression<Int>> = parent?.rowSpan?.value() ?? .noValue
    var selectedActionsValue: DeserializationResult<[DivAction]> = .noValue
    var tooltipsValue: DeserializationResult<[DivTooltip]> = .noValue
    var transformValue: DeserializationResult<DivTransform> = .noValue
    var transitionChangeValue: DeserializationResult<DivChangeTransition> = .noValue
    var transitionInValue: DeserializationResult<DivAppearanceTransition> = .noValue
    var transitionOutValue: DeserializationResult<DivAppearanceTransition> = .noValue
    var transitionTriggersValue: DeserializationResult<[DivTransitionTrigger]> = parent?.transitionTriggers?.value(validatedBy: ResolvedValue.transitionTriggersValidator) ?? .noValue
    var variableTriggersValue: DeserializationResult<[DivTrigger]> = .noValue
    var variablesValue: DeserializationResult<[DivVariable]> = .noValue
    var visibilityValue: DeserializationResult<Expression<DivVisibility>> = parent?.visibility?.value() ?? .noValue
    var visibilityActionValue: DeserializationResult<DivVisibilityAction> = .noValue
    var visibilityActionsValue: DeserializationResult<[DivVisibilityAction]> = .noValue
    var widthValue: DeserializationResult<DivSize> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "accessibility":
        accessibilityValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAccessibilityTemplate.self).merged(with: accessibilityValue)
      case "alignment_horizontal":
        alignmentHorizontalValue = deserialize(__dictValue).merged(with: alignmentHorizontalValue)
      case "alignment_vertical":
        alignmentVerticalValue = deserialize(__dictValue).merged(with: alignmentVerticalValue)
      case "alpha":
        alphaValue = deserialize(__dictValue, validator: ResolvedValue.alphaValidator).merged(with: alphaValue)
      case "background":
        backgroundValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBackgroundTemplate.self).merged(with: backgroundValue)
      case "border":
        borderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBorderTemplate.self).merged(with: borderValue)
      case "column_span":
        columnSpanValue = deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator).merged(with: columnSpanValue)
      case "custom_props":
        customPropsValue = deserialize(__dictValue).merged(with: customPropsValue)
      case "custom_type":
        customTypeValue = deserialize(__dictValue).merged(with: customTypeValue)
      case "disappear_actions":
        disappearActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDisappearActionTemplate.self).merged(with: disappearActionsValue)
      case "extensions":
        extensionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivExtensionTemplate.self).merged(with: extensionsValue)
      case "focus":
        focusValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFocusTemplate.self).merged(with: focusValue)
      case "height":
        heightValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self).merged(with: heightValue)
      case "id":
        idValue = deserialize(__dictValue).merged(with: idValue)
      case "items":
        itemsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTemplate.self).merged(with: itemsValue)
      case "layout_provider":
        layoutProviderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivLayoutProviderTemplate.self).merged(with: layoutProviderValue)
      case "margins":
        marginsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self).merged(with: marginsValue)
      case "paddings":
        paddingsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self).merged(with: paddingsValue)
      case "reuse_id":
        reuseIdValue = deserialize(__dictValue).merged(with: reuseIdValue)
      case "row_span":
        rowSpanValue = deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator).merged(with: rowSpanValue)
      case "selected_actions":
        selectedActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: selectedActionsValue)
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
      case "variable_triggers":
        variableTriggersValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTriggerTemplate.self).merged(with: variableTriggersValue)
      case "variables":
        variablesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVariableTemplate.self).merged(with: variablesValue)
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
      case parent?.alignmentHorizontal?.link:
        alignmentHorizontalValue = alignmentHorizontalValue.merged(with: { deserialize(__dictValue) })
      case parent?.alignmentVertical?.link:
        alignmentVerticalValue = alignmentVerticalValue.merged(with: { deserialize(__dictValue) })
      case parent?.alpha?.link:
        alphaValue = alphaValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.alphaValidator) })
      case parent?.background?.link:
        backgroundValue = backgroundValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBackgroundTemplate.self) })
      case parent?.border?.link:
        borderValue = borderValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBorderTemplate.self) })
      case parent?.columnSpan?.link:
        columnSpanValue = columnSpanValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator) })
      case parent?.customProps?.link:
        customPropsValue = customPropsValue.merged(with: { deserialize(__dictValue) })
      case parent?.customType?.link:
        customTypeValue = customTypeValue.merged(with: { deserialize(__dictValue) })
      case parent?.disappearActions?.link:
        disappearActionsValue = disappearActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDisappearActionTemplate.self) })
      case parent?.extensions?.link:
        extensionsValue = extensionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivExtensionTemplate.self) })
      case parent?.focus?.link:
        focusValue = focusValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFocusTemplate.self) })
      case parent?.height?.link:
        heightValue = heightValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self) })
      case parent?.id?.link:
        idValue = idValue.merged(with: { deserialize(__dictValue) })
      case parent?.items?.link:
        itemsValue = itemsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTemplate.self) })
      case parent?.layoutProvider?.link:
        layoutProviderValue = layoutProviderValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivLayoutProviderTemplate.self) })
      case parent?.margins?.link:
        marginsValue = marginsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self) })
      case parent?.paddings?.link:
        paddingsValue = paddingsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self) })
      case parent?.reuseId?.link:
        reuseIdValue = reuseIdValue.merged(with: { deserialize(__dictValue) })
      case parent?.rowSpan?.link:
        rowSpanValue = rowSpanValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator) })
      case parent?.selectedActions?.link:
        selectedActionsValue = selectedActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
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
      case parent?.variableTriggers?.link:
        variableTriggersValue = variableTriggersValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTriggerTemplate.self) })
      case parent?.variables?.link:
        variablesValue = variablesValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVariableTemplate.self) })
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
      backgroundValue = backgroundValue.merged(with: { parent.background?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      borderValue = borderValue.merged(with: { parent.border?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      disappearActionsValue = disappearActionsValue.merged(with: { parent.disappearActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      extensionsValue = extensionsValue.merged(with: { parent.extensions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      focusValue = focusValue.merged(with: { parent.focus?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      heightValue = heightValue.merged(with: { parent.height?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      itemsValue = itemsValue.merged(with: { parent.items?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      layoutProviderValue = layoutProviderValue.merged(with: { parent.layoutProvider?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      marginsValue = marginsValue.merged(with: { parent.margins?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      paddingsValue = paddingsValue.merged(with: { parent.paddings?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      selectedActionsValue = selectedActionsValue.merged(with: { parent.selectedActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      tooltipsValue = tooltipsValue.merged(with: { parent.tooltips?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      transformValue = transformValue.merged(with: { parent.transform?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      transitionChangeValue = transitionChangeValue.merged(with: { parent.transitionChange?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      transitionInValue = transitionInValue.merged(with: { parent.transitionIn?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      transitionOutValue = transitionOutValue.merged(with: { parent.transitionOut?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      variableTriggersValue = variableTriggersValue.merged(with: { parent.variableTriggers?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      variablesValue = variablesValue.merged(with: { parent.variables?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      visibilityActionValue = visibilityActionValue.merged(with: { parent.visibilityAction?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      visibilityActionsValue = visibilityActionsValue.merged(with: { parent.visibilityActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      widthValue = widthValue.merged(with: { parent.width?.resolveOptionalValue(context: context, useOnlyLinks: true) })
    }
    var errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "accessibility", error: $0) },
      alignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_horizontal", error: $0) },
      alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      customPropsValue.errorsOrWarnings?.map { .nestedObjectError(field: "custom_props", error: $0) },
      customTypeValue.errorsOrWarnings?.map { .nestedObjectError(field: "custom_type", error: $0) },
      disappearActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "disappear_actions", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      itemsValue.errorsOrWarnings?.map { .nestedObjectError(field: "items", error: $0) },
      layoutProviderValue.errorsOrWarnings?.map { .nestedObjectError(field: "layout_provider", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      reuseIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "reuse_id", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
      transformValue.errorsOrWarnings?.map { .nestedObjectError(field: "transform", error: $0) },
      transitionChangeValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_change", error: $0) },
      transitionInValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_in", error: $0) },
      transitionOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_out", error: $0) },
      transitionTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_triggers", error: $0) },
      variableTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable_triggers", error: $0) },
      variablesValue.errorsOrWarnings?.map { .nestedObjectError(field: "variables", error: $0) },
      visibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility", error: $0) },
      visibilityActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_action", error: $0) },
      visibilityActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_actions", error: $0) },
      widthValue.errorsOrWarnings?.map { .nestedObjectError(field: "width", error: $0) }
    )
    if case .noValue = customTypeValue {
      errors.append(.requiredFieldIsMissing(field: "custom_type"))
    }
    guard
      let customTypeNonNil = customTypeValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivCustom(
      accessibility: accessibilityValue.value,
      alignmentHorizontal: alignmentHorizontalValue.value,
      alignmentVertical: alignmentVerticalValue.value,
      alpha: alphaValue.value,
      background: backgroundValue.value,
      border: borderValue.value,
      columnSpan: columnSpanValue.value,
      customProps: customPropsValue.value,
      customType: customTypeNonNil,
      disappearActions: disappearActionsValue.value,
      extensions: extensionsValue.value,
      focus: focusValue.value,
      height: heightValue.value,
      id: idValue.value,
      items: itemsValue.value,
      layoutProvider: layoutProviderValue.value,
      margins: marginsValue.value,
      paddings: paddingsValue.value,
      reuseId: reuseIdValue.value,
      rowSpan: rowSpanValue.value,
      selectedActions: selectedActionsValue.value,
      tooltips: tooltipsValue.value,
      transform: transformValue.value,
      transitionChange: transitionChangeValue.value,
      transitionIn: transitionInValue.value,
      transitionOut: transitionOutValue.value,
      transitionTriggers: transitionTriggersValue.value,
      variableTriggers: variableTriggersValue.value,
      variables: variablesValue.value,
      visibility: visibilityValue.value,
      visibilityAction: visibilityActionValue.value,
      visibilityActions: visibilityActionsValue.value,
      width: widthValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivCustomTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivCustomTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivCustomTemplate(
      parent: nil,
      accessibility: accessibility ?? mergedParent.accessibility,
      alignmentHorizontal: alignmentHorizontal ?? mergedParent.alignmentHorizontal,
      alignmentVertical: alignmentVertical ?? mergedParent.alignmentVertical,
      alpha: alpha ?? mergedParent.alpha,
      background: background ?? mergedParent.background,
      border: border ?? mergedParent.border,
      columnSpan: columnSpan ?? mergedParent.columnSpan,
      customProps: customProps ?? mergedParent.customProps,
      customType: customType ?? mergedParent.customType,
      disappearActions: disappearActions ?? mergedParent.disappearActions,
      extensions: extensions ?? mergedParent.extensions,
      focus: focus ?? mergedParent.focus,
      height: height ?? mergedParent.height,
      id: id ?? mergedParent.id,
      items: items ?? mergedParent.items,
      layoutProvider: layoutProvider ?? mergedParent.layoutProvider,
      margins: margins ?? mergedParent.margins,
      paddings: paddings ?? mergedParent.paddings,
      reuseId: reuseId ?? mergedParent.reuseId,
      rowSpan: rowSpan ?? mergedParent.rowSpan,
      selectedActions: selectedActions ?? mergedParent.selectedActions,
      tooltips: tooltips ?? mergedParent.tooltips,
      transform: transform ?? mergedParent.transform,
      transitionChange: transitionChange ?? mergedParent.transitionChange,
      transitionIn: transitionIn ?? mergedParent.transitionIn,
      transitionOut: transitionOut ?? mergedParent.transitionOut,
      transitionTriggers: transitionTriggers ?? mergedParent.transitionTriggers,
      variableTriggers: variableTriggers ?? mergedParent.variableTriggers,
      variables: variables ?? mergedParent.variables,
      visibility: visibility ?? mergedParent.visibility,
      visibilityAction: visibilityAction ?? mergedParent.visibilityAction,
      visibilityActions: visibilityActions ?? mergedParent.visibilityActions,
      width: width ?? mergedParent.width
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivCustomTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivCustomTemplate(
      parent: nil,
      accessibility: merged.accessibility?.tryResolveParent(templates: templates),
      alignmentHorizontal: merged.alignmentHorizontal,
      alignmentVertical: merged.alignmentVertical,
      alpha: merged.alpha,
      background: merged.background?.tryResolveParent(templates: templates),
      border: merged.border?.tryResolveParent(templates: templates),
      columnSpan: merged.columnSpan,
      customProps: merged.customProps,
      customType: merged.customType,
      disappearActions: merged.disappearActions?.tryResolveParent(templates: templates),
      extensions: merged.extensions?.tryResolveParent(templates: templates),
      focus: merged.focus?.tryResolveParent(templates: templates),
      height: merged.height?.tryResolveParent(templates: templates),
      id: merged.id,
      items: merged.items?.tryResolveParent(templates: templates),
      layoutProvider: merged.layoutProvider?.tryResolveParent(templates: templates),
      margins: merged.margins?.tryResolveParent(templates: templates),
      paddings: merged.paddings?.tryResolveParent(templates: templates),
      reuseId: merged.reuseId,
      rowSpan: merged.rowSpan,
      selectedActions: merged.selectedActions?.tryResolveParent(templates: templates),
      tooltips: merged.tooltips?.tryResolveParent(templates: templates),
      transform: merged.transform?.tryResolveParent(templates: templates),
      transitionChange: merged.transitionChange?.tryResolveParent(templates: templates),
      transitionIn: merged.transitionIn?.tryResolveParent(templates: templates),
      transitionOut: merged.transitionOut?.tryResolveParent(templates: templates),
      transitionTriggers: merged.transitionTriggers,
      variableTriggers: merged.variableTriggers?.tryResolveParent(templates: templates),
      variables: merged.variables?.tryResolveParent(templates: templates),
      visibility: merged.visibility,
      visibilityAction: merged.visibilityAction?.tryResolveParent(templates: templates),
      visibilityActions: merged.visibilityActions?.tryResolveParent(templates: templates),
      width: merged.width?.tryResolveParent(templates: templates)
    )
  }
}
