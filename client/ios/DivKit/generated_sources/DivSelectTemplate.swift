// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivSelectTemplate: TemplateValue, @unchecked Sendable {
  public final class OptionTemplate: TemplateValue, Sendable {
    public let text: Field<Expression<String>>?
    public let value: Field<Expression<String>>?

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      self.init(
        text: dictionary.getOptionalExpressionField("text"),
        value: dictionary.getOptionalExpressionField("value")
      )
    }

    init(
      text: Field<Expression<String>>? = nil,
      value: Field<Expression<String>>? = nil
    ) {
      self.text = text
      self.value = value
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: OptionTemplate?) -> DeserializationResult<DivSelect.Option> {
      let textValue = { parent?.text?.resolveOptionalValue(context: context) ?? .noValue }()
      let valueValue = { parent?.value?.resolveValue(context: context) ?? .noValue }()
      var errors = mergeErrors(
        textValue.errorsOrWarnings?.map { .nestedObjectError(field: "text", error: $0) },
        valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) }
      )
      if case .noValue = valueValue {
        errors.append(.requiredFieldIsMissing(field: "value"))
      }
      guard
        let valueNonNil = valueValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivSelect.Option(
        text: { textValue.value }(),
        value: { valueNonNil }()
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: OptionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivSelect.Option> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var textValue: DeserializationResult<Expression<String>> = { parent?.text?.value() ?? .noValue }()
      var valueValue: DeserializationResult<Expression<String>> = { parent?.value?.value() ?? .noValue }()
      _ = {
        // Each field is parsed in its own lambda to keep the stack size managable
        // Otherwise the compiler will allocate stack for each intermediate variable
        // upfront even when we don't actually visit a relevant branch
        for (key, __dictValue) in context.templateData {
          _ = {
            if key == "text" {
             textValue = deserialize(__dictValue).merged(with: textValue)
            }
          }()
          _ = {
            if key == "value" {
             valueValue = deserialize(__dictValue).merged(with: valueValue)
            }
          }()
          _ = {
           if key == parent?.text?.link, context.templateData["text"] == nil {
             textValue = deserialize(__dictValue).orFallback(textValue)
            }
          }()
          _ = {
           if key == parent?.value?.link, context.templateData["value"] == nil {
             valueValue = deserialize(__dictValue).orFallback(valueValue)
            }
          }()
        }
      }()
      var errors = mergeErrors(
        textValue.errorsOrWarnings?.map { .nestedObjectError(field: "text", error: $0) },
        valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) }
      )
      if case .noValue = valueValue {
        errors.append(.requiredFieldIsMissing(field: "value"))
      }
      guard
        let valueNonNil = valueValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivSelect.Option(
        text: { textValue.value }(),
        value: { valueNonNil }()
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> OptionTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> OptionTemplate {
      return try mergedWithParent(templates: templates)
    }
  }

  public static let type: String = "select"
  public let parent: String?
  public let accessibility: Field<DivAccessibilityTemplate>?
  public let alignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>?
  public let alignmentVertical: Field<Expression<DivAlignmentVertical>>?
  public let alpha: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let animators: Field<[DivAnimatorTemplate]>?
  public let background: Field<[DivBackgroundTemplate]>?
  public let border: Field<DivBorderTemplate>?
  public let columnSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let disappearActions: Field<[DivDisappearActionTemplate]>?
  public let extensions: Field<[DivExtensionTemplate]>?
  public let focus: Field<DivFocusTemplate>?
  public let fontFamily: Field<Expression<String>>?
  public let fontSize: Field<Expression<Int>>? // constraint: number >= 0; default value: 12
  public let fontSizeUnit: Field<Expression<DivSizeUnit>>? // default value: sp
  public let fontVariationSettings: Field<Expression<[String: Any]>>?
  public let fontWeight: Field<Expression<DivFontWeight>>?
  public let fontWeightValue: Field<Expression<Int>>? // constraint: number > 0
  public let functions: Field<[DivFunctionTemplate]>?
  public let height: Field<DivSizeTemplate>? // default value: .divWrapContentSize(DivWrapContentSize())
  public let hintColor: Field<Expression<Color>>? // default value: #73000000
  public let hintText: Field<Expression<String>>?
  public let id: Field<String>?
  public let layoutProvider: Field<DivLayoutProviderTemplate>?
  public let letterSpacing: Field<Expression<Double>>? // default value: 0
  public let lineHeight: Field<Expression<Int>>? // constraint: number >= 0
  public let margins: Field<DivEdgeInsetsTemplate>?
  public let options: Field<[OptionTemplate]>? // at least 1 elements
  public let paddings: Field<DivEdgeInsetsTemplate>?
  public let reuseId: Field<Expression<String>>?
  public let rowSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let selectedActions: Field<[DivActionTemplate]>?
  public let textColor: Field<Expression<Color>>? // default value: #FF000000
  public let tooltips: Field<[DivTooltipTemplate]>?
  public let transform: Field<DivTransformTemplate>?
  public let transformations: Field<[DivTransformationTemplate]>?
  public let transitionChange: Field<DivChangeTransitionTemplate>?
  public let transitionIn: Field<DivAppearanceTransitionTemplate>?
  public let transitionOut: Field<DivAppearanceTransitionTemplate>?
  public let transitionTriggers: Field<[DivTransitionTrigger]>? // at least 1 elements
  public let valueVariable: Field<String>?
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
      animators: dictionary.getOptionalArray("animators", templateToType: templateToType),
      background: dictionary.getOptionalArray("background", templateToType: templateToType),
      border: dictionary.getOptionalField("border", templateToType: templateToType),
      columnSpan: dictionary.getOptionalExpressionField("column_span"),
      disappearActions: dictionary.getOptionalArray("disappear_actions", templateToType: templateToType),
      extensions: dictionary.getOptionalArray("extensions", templateToType: templateToType),
      focus: dictionary.getOptionalField("focus", templateToType: templateToType),
      fontFamily: dictionary.getOptionalExpressionField("font_family"),
      fontSize: dictionary.getOptionalExpressionField("font_size"),
      fontSizeUnit: dictionary.getOptionalExpressionField("font_size_unit"),
      fontVariationSettings: dictionary.getOptionalExpressionField("font_variation_settings"),
      fontWeight: dictionary.getOptionalExpressionField("font_weight"),
      fontWeightValue: dictionary.getOptionalExpressionField("font_weight_value"),
      functions: dictionary.getOptionalArray("functions", templateToType: templateToType),
      height: dictionary.getOptionalField("height", templateToType: templateToType),
      hintColor: dictionary.getOptionalExpressionField("hint_color", transform: Color.color(withHexString:)),
      hintText: dictionary.getOptionalExpressionField("hint_text"),
      id: dictionary.getOptionalField("id"),
      layoutProvider: dictionary.getOptionalField("layout_provider", templateToType: templateToType),
      letterSpacing: dictionary.getOptionalExpressionField("letter_spacing"),
      lineHeight: dictionary.getOptionalExpressionField("line_height"),
      margins: dictionary.getOptionalField("margins", templateToType: templateToType),
      options: dictionary.getOptionalArray("options", templateToType: templateToType),
      paddings: dictionary.getOptionalField("paddings", templateToType: templateToType),
      reuseId: dictionary.getOptionalExpressionField("reuse_id"),
      rowSpan: dictionary.getOptionalExpressionField("row_span"),
      selectedActions: dictionary.getOptionalArray("selected_actions", templateToType: templateToType),
      textColor: dictionary.getOptionalExpressionField("text_color", transform: Color.color(withHexString:)),
      tooltips: dictionary.getOptionalArray("tooltips", templateToType: templateToType),
      transform: dictionary.getOptionalField("transform", templateToType: templateToType),
      transformations: dictionary.getOptionalArray("transformations", templateToType: templateToType),
      transitionChange: dictionary.getOptionalField("transition_change", templateToType: templateToType),
      transitionIn: dictionary.getOptionalField("transition_in", templateToType: templateToType),
      transitionOut: dictionary.getOptionalField("transition_out", templateToType: templateToType),
      transitionTriggers: dictionary.getOptionalArray("transition_triggers"),
      valueVariable: dictionary.getOptionalField("value_variable"),
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
    animators: Field<[DivAnimatorTemplate]>? = nil,
    background: Field<[DivBackgroundTemplate]>? = nil,
    border: Field<DivBorderTemplate>? = nil,
    columnSpan: Field<Expression<Int>>? = nil,
    disappearActions: Field<[DivDisappearActionTemplate]>? = nil,
    extensions: Field<[DivExtensionTemplate]>? = nil,
    focus: Field<DivFocusTemplate>? = nil,
    fontFamily: Field<Expression<String>>? = nil,
    fontSize: Field<Expression<Int>>? = nil,
    fontSizeUnit: Field<Expression<DivSizeUnit>>? = nil,
    fontVariationSettings: Field<Expression<[String: Any]>>? = nil,
    fontWeight: Field<Expression<DivFontWeight>>? = nil,
    fontWeightValue: Field<Expression<Int>>? = nil,
    functions: Field<[DivFunctionTemplate]>? = nil,
    height: Field<DivSizeTemplate>? = nil,
    hintColor: Field<Expression<Color>>? = nil,
    hintText: Field<Expression<String>>? = nil,
    id: Field<String>? = nil,
    layoutProvider: Field<DivLayoutProviderTemplate>? = nil,
    letterSpacing: Field<Expression<Double>>? = nil,
    lineHeight: Field<Expression<Int>>? = nil,
    margins: Field<DivEdgeInsetsTemplate>? = nil,
    options: Field<[OptionTemplate]>? = nil,
    paddings: Field<DivEdgeInsetsTemplate>? = nil,
    reuseId: Field<Expression<String>>? = nil,
    rowSpan: Field<Expression<Int>>? = nil,
    selectedActions: Field<[DivActionTemplate]>? = nil,
    textColor: Field<Expression<Color>>? = nil,
    tooltips: Field<[DivTooltipTemplate]>? = nil,
    transform: Field<DivTransformTemplate>? = nil,
    transformations: Field<[DivTransformationTemplate]>? = nil,
    transitionChange: Field<DivChangeTransitionTemplate>? = nil,
    transitionIn: Field<DivAppearanceTransitionTemplate>? = nil,
    transitionOut: Field<DivAppearanceTransitionTemplate>? = nil,
    transitionTriggers: Field<[DivTransitionTrigger]>? = nil,
    valueVariable: Field<String>? = nil,
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
    self.animators = animators
    self.background = background
    self.border = border
    self.columnSpan = columnSpan
    self.disappearActions = disappearActions
    self.extensions = extensions
    self.focus = focus
    self.fontFamily = fontFamily
    self.fontSize = fontSize
    self.fontSizeUnit = fontSizeUnit
    self.fontVariationSettings = fontVariationSettings
    self.fontWeight = fontWeight
    self.fontWeightValue = fontWeightValue
    self.functions = functions
    self.height = height
    self.hintColor = hintColor
    self.hintText = hintText
    self.id = id
    self.layoutProvider = layoutProvider
    self.letterSpacing = letterSpacing
    self.lineHeight = lineHeight
    self.margins = margins
    self.options = options
    self.paddings = paddings
    self.reuseId = reuseId
    self.rowSpan = rowSpan
    self.selectedActions = selectedActions
    self.textColor = textColor
    self.tooltips = tooltips
    self.transform = transform
    self.transformations = transformations
    self.transitionChange = transitionChange
    self.transitionIn = transitionIn
    self.transitionOut = transitionOut
    self.transitionTriggers = transitionTriggers
    self.valueVariable = valueVariable
    self.variableTriggers = variableTriggers
    self.variables = variables
    self.visibility = visibility
    self.visibilityAction = visibilityAction
    self.visibilityActions = visibilityActions
    self.width = width
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivSelectTemplate?) -> DeserializationResult<DivSelect> {
    let accessibilityValue = { parent?.accessibility?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let alignmentHorizontalValue = { parent?.alignmentHorizontal?.resolveOptionalValue(context: context) ?? .noValue }()
    let alignmentVerticalValue = { parent?.alignmentVertical?.resolveOptionalValue(context: context) ?? .noValue }()
    let alphaValue = { parent?.alpha?.resolveOptionalValue(context: context, validator: ResolvedValue.alphaValidator) ?? .noValue }()
    let animatorsValue = { parent?.animators?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let backgroundValue = { parent?.background?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let borderValue = { parent?.border?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let columnSpanValue = { parent?.columnSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.columnSpanValidator) ?? .noValue }()
    let disappearActionsValue = { parent?.disappearActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let extensionsValue = { parent?.extensions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let focusValue = { parent?.focus?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let fontFamilyValue = { parent?.fontFamily?.resolveOptionalValue(context: context) ?? .noValue }()
    let fontSizeValue = { parent?.fontSize?.resolveOptionalValue(context: context, validator: ResolvedValue.fontSizeValidator) ?? .noValue }()
    let fontSizeUnitValue = { parent?.fontSizeUnit?.resolveOptionalValue(context: context) ?? .noValue }()
    let fontVariationSettingsValue = { parent?.fontVariationSettings?.resolveOptionalValue(context: context) ?? .noValue }()
    let fontWeightValue = { parent?.fontWeight?.resolveOptionalValue(context: context) ?? .noValue }()
    let fontWeightValueValue = { parent?.fontWeightValue?.resolveOptionalValue(context: context, validator: ResolvedValue.fontWeightValueValidator) ?? .noValue }()
    let functionsValue = { parent?.functions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let heightValue = { parent?.height?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let hintColorValue = { parent?.hintColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:)) ?? .noValue }()
    let hintTextValue = { parent?.hintText?.resolveOptionalValue(context: context) ?? .noValue }()
    let idValue = { parent?.id?.resolveOptionalValue(context: context) ?? .noValue }()
    let layoutProviderValue = { parent?.layoutProvider?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let letterSpacingValue = { parent?.letterSpacing?.resolveOptionalValue(context: context) ?? .noValue }()
    let lineHeightValue = { parent?.lineHeight?.resolveOptionalValue(context: context, validator: ResolvedValue.lineHeightValidator) ?? .noValue }()
    let marginsValue = { parent?.margins?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let optionsValue = { parent?.options?.resolveValue(context: context, validator: ResolvedValue.optionsValidator, useOnlyLinks: true) ?? .noValue }()
    let paddingsValue = { parent?.paddings?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let reuseIdValue = { parent?.reuseId?.resolveOptionalValue(context: context) ?? .noValue }()
    let rowSpanValue = { parent?.rowSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.rowSpanValidator) ?? .noValue }()
    let selectedActionsValue = { parent?.selectedActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let textColorValue = { parent?.textColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:)) ?? .noValue }()
    let tooltipsValue = { parent?.tooltips?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let transformValue = { parent?.transform?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let transformationsValue = { parent?.transformations?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let transitionChangeValue = { parent?.transitionChange?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let transitionInValue = { parent?.transitionIn?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let transitionOutValue = { parent?.transitionOut?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let transitionTriggersValue = { parent?.transitionTriggers?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionTriggersValidator) ?? .noValue }()
    let valueVariableValue = { parent?.valueVariable?.resolveValue(context: context) ?? .noValue }()
    let variableTriggersValue = { parent?.variableTriggers?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let variablesValue = { parent?.variables?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let visibilityValue = { parent?.visibility?.resolveOptionalValue(context: context) ?? .noValue }()
    let visibilityActionValue = { parent?.visibilityAction?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let visibilityActionsValue = { parent?.visibilityActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let widthValue = { parent?.width?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    var errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "accessibility", error: $0) },
      alignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_horizontal", error: $0) },
      alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      animatorsValue.errorsOrWarnings?.map { .nestedObjectError(field: "animators", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      disappearActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "disappear_actions", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      fontFamilyValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_family", error: $0) },
      fontSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size", error: $0) },
      fontSizeUnitValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size_unit", error: $0) },
      fontVariationSettingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_variation_settings", error: $0) },
      fontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight", error: $0) },
      fontWeightValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight_value", error: $0) },
      functionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "functions", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      hintColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "hint_color", error: $0) },
      hintTextValue.errorsOrWarnings?.map { .nestedObjectError(field: "hint_text", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      layoutProviderValue.errorsOrWarnings?.map { .nestedObjectError(field: "layout_provider", error: $0) },
      letterSpacingValue.errorsOrWarnings?.map { .nestedObjectError(field: "letter_spacing", error: $0) },
      lineHeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "line_height", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      optionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "options", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      reuseIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "reuse_id", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
      textColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_color", error: $0) },
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
      transformValue.errorsOrWarnings?.map { .nestedObjectError(field: "transform", error: $0) },
      transformationsValue.errorsOrWarnings?.map { .nestedObjectError(field: "transformations", error: $0) },
      transitionChangeValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_change", error: $0) },
      transitionInValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_in", error: $0) },
      transitionOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_out", error: $0) },
      transitionTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_triggers", error: $0) },
      valueVariableValue.errorsOrWarnings?.map { .nestedObjectError(field: "value_variable", error: $0) },
      variableTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable_triggers", error: $0) },
      variablesValue.errorsOrWarnings?.map { .nestedObjectError(field: "variables", error: $0) },
      visibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility", error: $0) },
      visibilityActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_action", error: $0) },
      visibilityActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_actions", error: $0) },
      widthValue.errorsOrWarnings?.map { .nestedObjectError(field: "width", error: $0) }
    )
    if case .noValue = optionsValue {
      errors.append(.requiredFieldIsMissing(field: "options"))
    }
    if case .noValue = valueVariableValue {
      errors.append(.requiredFieldIsMissing(field: "value_variable"))
    }
    guard
      let optionsNonNil = optionsValue.value,
      let valueVariableNonNil = valueVariableValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivSelect(
      accessibility: { accessibilityValue.value }(),
      alignmentHorizontal: { alignmentHorizontalValue.value }(),
      alignmentVertical: { alignmentVerticalValue.value }(),
      alpha: { alphaValue.value }(),
      animators: { animatorsValue.value }(),
      background: { backgroundValue.value }(),
      border: { borderValue.value }(),
      columnSpan: { columnSpanValue.value }(),
      disappearActions: { disappearActionsValue.value }(),
      extensions: { extensionsValue.value }(),
      focus: { focusValue.value }(),
      fontFamily: { fontFamilyValue.value }(),
      fontSize: { fontSizeValue.value }(),
      fontSizeUnit: { fontSizeUnitValue.value }(),
      fontVariationSettings: { fontVariationSettingsValue.value }(),
      fontWeight: { fontWeightValue.value }(),
      fontWeightValue: { fontWeightValueValue.value }(),
      functions: { functionsValue.value }(),
      height: { heightValue.value }(),
      hintColor: { hintColorValue.value }(),
      hintText: { hintTextValue.value }(),
      id: { idValue.value }(),
      layoutProvider: { layoutProviderValue.value }(),
      letterSpacing: { letterSpacingValue.value }(),
      lineHeight: { lineHeightValue.value }(),
      margins: { marginsValue.value }(),
      options: { optionsNonNil }(),
      paddings: { paddingsValue.value }(),
      reuseId: { reuseIdValue.value }(),
      rowSpan: { rowSpanValue.value }(),
      selectedActions: { selectedActionsValue.value }(),
      textColor: { textColorValue.value }(),
      tooltips: { tooltipsValue.value }(),
      transform: { transformValue.value }(),
      transformations: { transformationsValue.value }(),
      transitionChange: { transitionChangeValue.value }(),
      transitionIn: { transitionInValue.value }(),
      transitionOut: { transitionOutValue.value }(),
      transitionTriggers: { transitionTriggersValue.value }(),
      valueVariable: { valueVariableNonNil }(),
      variableTriggers: { variableTriggersValue.value }(),
      variables: { variablesValue.value }(),
      visibility: { visibilityValue.value }(),
      visibilityAction: { visibilityActionValue.value }(),
      visibilityActions: { visibilityActionsValue.value }(),
      width: { widthValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivSelectTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivSelect> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var accessibilityValue: DeserializationResult<DivAccessibility> = .noValue
    var alignmentHorizontalValue: DeserializationResult<Expression<DivAlignmentHorizontal>> = { parent?.alignmentHorizontal?.value() ?? .noValue }()
    var alignmentVerticalValue: DeserializationResult<Expression<DivAlignmentVertical>> = { parent?.alignmentVertical?.value() ?? .noValue }()
    var alphaValue: DeserializationResult<Expression<Double>> = { parent?.alpha?.value() ?? .noValue }()
    var animatorsValue: DeserializationResult<[DivAnimator]> = .noValue
    var backgroundValue: DeserializationResult<[DivBackground]> = .noValue
    var borderValue: DeserializationResult<DivBorder> = .noValue
    var columnSpanValue: DeserializationResult<Expression<Int>> = { parent?.columnSpan?.value() ?? .noValue }()
    var disappearActionsValue: DeserializationResult<[DivDisappearAction]> = .noValue
    var extensionsValue: DeserializationResult<[DivExtension]> = .noValue
    var focusValue: DeserializationResult<DivFocus> = .noValue
    var fontFamilyValue: DeserializationResult<Expression<String>> = { parent?.fontFamily?.value() ?? .noValue }()
    var fontSizeValue: DeserializationResult<Expression<Int>> = { parent?.fontSize?.value() ?? .noValue }()
    var fontSizeUnitValue: DeserializationResult<Expression<DivSizeUnit>> = { parent?.fontSizeUnit?.value() ?? .noValue }()
    var fontVariationSettingsValue: DeserializationResult<Expression<[String: Any]>> = { parent?.fontVariationSettings?.value() ?? .noValue }()
    var fontWeightValue: DeserializationResult<Expression<DivFontWeight>> = { parent?.fontWeight?.value() ?? .noValue }()
    var fontWeightValueValue: DeserializationResult<Expression<Int>> = { parent?.fontWeightValue?.value() ?? .noValue }()
    var functionsValue: DeserializationResult<[DivFunction]> = .noValue
    var heightValue: DeserializationResult<DivSize> = .noValue
    var hintColorValue: DeserializationResult<Expression<Color>> = { parent?.hintColor?.value() ?? .noValue }()
    var hintTextValue: DeserializationResult<Expression<String>> = { parent?.hintText?.value() ?? .noValue }()
    var idValue: DeserializationResult<String> = { parent?.id?.value() ?? .noValue }()
    var layoutProviderValue: DeserializationResult<DivLayoutProvider> = .noValue
    var letterSpacingValue: DeserializationResult<Expression<Double>> = { parent?.letterSpacing?.value() ?? .noValue }()
    var lineHeightValue: DeserializationResult<Expression<Int>> = { parent?.lineHeight?.value() ?? .noValue }()
    var marginsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var optionsValue: DeserializationResult<[DivSelect.Option]> = .noValue
    var paddingsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var reuseIdValue: DeserializationResult<Expression<String>> = { parent?.reuseId?.value() ?? .noValue }()
    var rowSpanValue: DeserializationResult<Expression<Int>> = { parent?.rowSpan?.value() ?? .noValue }()
    var selectedActionsValue: DeserializationResult<[DivAction]> = .noValue
    var textColorValue: DeserializationResult<Expression<Color>> = { parent?.textColor?.value() ?? .noValue }()
    var tooltipsValue: DeserializationResult<[DivTooltip]> = .noValue
    var transformValue: DeserializationResult<DivTransform> = .noValue
    var transformationsValue: DeserializationResult<[DivTransformation]> = .noValue
    var transitionChangeValue: DeserializationResult<DivChangeTransition> = .noValue
    var transitionInValue: DeserializationResult<DivAppearanceTransition> = .noValue
    var transitionOutValue: DeserializationResult<DivAppearanceTransition> = .noValue
    var transitionTriggersValue: DeserializationResult<[DivTransitionTrigger]> = { parent?.transitionTriggers?.value(validatedBy: ResolvedValue.transitionTriggersValidator) ?? .noValue }()
    var valueVariableValue: DeserializationResult<String> = { parent?.valueVariable?.value() ?? .noValue }()
    var variableTriggersValue: DeserializationResult<[DivTrigger]> = .noValue
    var variablesValue: DeserializationResult<[DivVariable]> = .noValue
    var visibilityValue: DeserializationResult<Expression<DivVisibility>> = { parent?.visibility?.value() ?? .noValue }()
    var visibilityActionValue: DeserializationResult<DivVisibilityAction> = .noValue
    var visibilityActionsValue: DeserializationResult<[DivVisibilityAction]> = .noValue
    var widthValue: DeserializationResult<DivSize> = .noValue
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "accessibility" {
           accessibilityValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAccessibilityTemplate.self).merged(with: accessibilityValue)
          }
        }()
        _ = {
          if key == "alignment_horizontal" {
           alignmentHorizontalValue = deserialize(__dictValue).merged(with: alignmentHorizontalValue)
          }
        }()
        _ = {
          if key == "alignment_vertical" {
           alignmentVerticalValue = deserialize(__dictValue).merged(with: alignmentVerticalValue)
          }
        }()
        _ = {
          if key == "alpha" {
           alphaValue = deserialize(__dictValue, validator: ResolvedValue.alphaValidator).merged(with: alphaValue)
          }
        }()
        _ = {
          if key == "animators" {
           animatorsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAnimatorTemplate.self).merged(with: animatorsValue)
          }
        }()
        _ = {
          if key == "background" {
           backgroundValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBackgroundTemplate.self).merged(with: backgroundValue)
          }
        }()
        _ = {
          if key == "border" {
           borderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBorderTemplate.self).merged(with: borderValue)
          }
        }()
        _ = {
          if key == "column_span" {
           columnSpanValue = deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator).merged(with: columnSpanValue)
          }
        }()
        _ = {
          if key == "disappear_actions" {
           disappearActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDisappearActionTemplate.self).merged(with: disappearActionsValue)
          }
        }()
        _ = {
          if key == "extensions" {
           extensionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivExtensionTemplate.self).merged(with: extensionsValue)
          }
        }()
        _ = {
          if key == "focus" {
           focusValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFocusTemplate.self).merged(with: focusValue)
          }
        }()
        _ = {
          if key == "font_family" {
           fontFamilyValue = deserialize(__dictValue).merged(with: fontFamilyValue)
          }
        }()
        _ = {
          if key == "font_size" {
           fontSizeValue = deserialize(__dictValue, validator: ResolvedValue.fontSizeValidator).merged(with: fontSizeValue)
          }
        }()
        _ = {
          if key == "font_size_unit" {
           fontSizeUnitValue = deserialize(__dictValue).merged(with: fontSizeUnitValue)
          }
        }()
        _ = {
          if key == "font_variation_settings" {
           fontVariationSettingsValue = deserialize(__dictValue).merged(with: fontVariationSettingsValue)
          }
        }()
        _ = {
          if key == "font_weight" {
           fontWeightValue = deserialize(__dictValue).merged(with: fontWeightValue)
          }
        }()
        _ = {
          if key == "font_weight_value" {
           fontWeightValueValue = deserialize(__dictValue, validator: ResolvedValue.fontWeightValueValidator).merged(with: fontWeightValueValue)
          }
        }()
        _ = {
          if key == "functions" {
           functionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFunctionTemplate.self).merged(with: functionsValue)
          }
        }()
        _ = {
          if key == "height" {
           heightValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self).merged(with: heightValue)
          }
        }()
        _ = {
          if key == "hint_color" {
           hintColorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: hintColorValue)
          }
        }()
        _ = {
          if key == "hint_text" {
           hintTextValue = deserialize(__dictValue).merged(with: hintTextValue)
          }
        }()
        _ = {
          if key == "id" {
           idValue = deserialize(__dictValue).merged(with: idValue)
          }
        }()
        _ = {
          if key == "layout_provider" {
           layoutProviderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivLayoutProviderTemplate.self).merged(with: layoutProviderValue)
          }
        }()
        _ = {
          if key == "letter_spacing" {
           letterSpacingValue = deserialize(__dictValue).merged(with: letterSpacingValue)
          }
        }()
        _ = {
          if key == "line_height" {
           lineHeightValue = deserialize(__dictValue, validator: ResolvedValue.lineHeightValidator).merged(with: lineHeightValue)
          }
        }()
        _ = {
          if key == "margins" {
           marginsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self).merged(with: marginsValue)
          }
        }()
        _ = {
          if key == "options" {
           optionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.optionsValidator, type: DivSelectTemplate.OptionTemplate.self).merged(with: optionsValue)
          }
        }()
        _ = {
          if key == "paddings" {
           paddingsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self).merged(with: paddingsValue)
          }
        }()
        _ = {
          if key == "reuse_id" {
           reuseIdValue = deserialize(__dictValue).merged(with: reuseIdValue)
          }
        }()
        _ = {
          if key == "row_span" {
           rowSpanValue = deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator).merged(with: rowSpanValue)
          }
        }()
        _ = {
          if key == "selected_actions" {
           selectedActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: selectedActionsValue)
          }
        }()
        _ = {
          if key == "text_color" {
           textColorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: textColorValue)
          }
        }()
        _ = {
          if key == "tooltips" {
           tooltipsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTooltipTemplate.self).merged(with: tooltipsValue)
          }
        }()
        _ = {
          if key == "transform" {
           transformValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTransformTemplate.self).merged(with: transformValue)
          }
        }()
        _ = {
          if key == "transformations" {
           transformationsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTransformationTemplate.self).merged(with: transformationsValue)
          }
        }()
        _ = {
          if key == "transition_change" {
           transitionChangeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivChangeTransitionTemplate.self).merged(with: transitionChangeValue)
          }
        }()
        _ = {
          if key == "transition_in" {
           transitionInValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self).merged(with: transitionInValue)
          }
        }()
        _ = {
          if key == "transition_out" {
           transitionOutValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self).merged(with: transitionOutValue)
          }
        }()
        _ = {
          if key == "transition_triggers" {
           transitionTriggersValue = deserialize(__dictValue, validator: ResolvedValue.transitionTriggersValidator).merged(with: transitionTriggersValue)
          }
        }()
        _ = {
          if key == "value_variable" {
           valueVariableValue = deserialize(__dictValue).merged(with: valueVariableValue)
          }
        }()
        _ = {
          if key == "variable_triggers" {
           variableTriggersValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTriggerTemplate.self).merged(with: variableTriggersValue)
          }
        }()
        _ = {
          if key == "variables" {
           variablesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVariableTemplate.self).merged(with: variablesValue)
          }
        }()
        _ = {
          if key == "visibility" {
           visibilityValue = deserialize(__dictValue).merged(with: visibilityValue)
          }
        }()
        _ = {
          if key == "visibility_action" {
           visibilityActionValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self).merged(with: visibilityActionValue)
          }
        }()
        _ = {
          if key == "visibility_actions" {
           visibilityActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self).merged(with: visibilityActionsValue)
          }
        }()
        _ = {
          if key == "width" {
           widthValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self).merged(with: widthValue)
          }
        }()
        _ = {
         if key == parent?.accessibility?.link, context.templateData["accessibility"] == nil {
           accessibilityValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAccessibilityTemplate.self).orFallback(accessibilityValue)
          }
        }()
        _ = {
         if key == parent?.alignmentHorizontal?.link, context.templateData["alignment_horizontal"] == nil {
           alignmentHorizontalValue = deserialize(__dictValue).orFallback(alignmentHorizontalValue)
          }
        }()
        _ = {
         if key == parent?.alignmentVertical?.link, context.templateData["alignment_vertical"] == nil {
           alignmentVerticalValue = deserialize(__dictValue).orFallback(alignmentVerticalValue)
          }
        }()
        _ = {
         if key == parent?.alpha?.link, context.templateData["alpha"] == nil {
           alphaValue = deserialize(__dictValue, validator: ResolvedValue.alphaValidator).orFallback(alphaValue)
          }
        }()
        _ = {
         if key == parent?.animators?.link, context.templateData["animators"] == nil {
           animatorsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAnimatorTemplate.self).orFallback(animatorsValue)
          }
        }()
        _ = {
         if key == parent?.background?.link, context.templateData["background"] == nil {
           backgroundValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBackgroundTemplate.self).orFallback(backgroundValue)
          }
        }()
        _ = {
         if key == parent?.border?.link, context.templateData["border"] == nil {
           borderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBorderTemplate.self).orFallback(borderValue)
          }
        }()
        _ = {
         if key == parent?.columnSpan?.link, context.templateData["column_span"] == nil {
           columnSpanValue = deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator).orFallback(columnSpanValue)
          }
        }()
        _ = {
         if key == parent?.disappearActions?.link, context.templateData["disappear_actions"] == nil {
           disappearActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDisappearActionTemplate.self).orFallback(disappearActionsValue)
          }
        }()
        _ = {
         if key == parent?.extensions?.link, context.templateData["extensions"] == nil {
           extensionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivExtensionTemplate.self).orFallback(extensionsValue)
          }
        }()
        _ = {
         if key == parent?.focus?.link, context.templateData["focus"] == nil {
           focusValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFocusTemplate.self).orFallback(focusValue)
          }
        }()
        _ = {
         if key == parent?.fontFamily?.link, context.templateData["font_family"] == nil {
           fontFamilyValue = deserialize(__dictValue).orFallback(fontFamilyValue)
          }
        }()
        _ = {
         if key == parent?.fontSize?.link, context.templateData["font_size"] == nil {
           fontSizeValue = deserialize(__dictValue, validator: ResolvedValue.fontSizeValidator).orFallback(fontSizeValue)
          }
        }()
        _ = {
         if key == parent?.fontSizeUnit?.link, context.templateData["font_size_unit"] == nil {
           fontSizeUnitValue = deserialize(__dictValue).orFallback(fontSizeUnitValue)
          }
        }()
        _ = {
         if key == parent?.fontVariationSettings?.link, context.templateData["font_variation_settings"] == nil {
           fontVariationSettingsValue = deserialize(__dictValue).orFallback(fontVariationSettingsValue)
          }
        }()
        _ = {
         if key == parent?.fontWeight?.link, context.templateData["font_weight"] == nil {
           fontWeightValue = deserialize(__dictValue).orFallback(fontWeightValue)
          }
        }()
        _ = {
         if key == parent?.fontWeightValue?.link, context.templateData["font_weight_value"] == nil {
           fontWeightValueValue = deserialize(__dictValue, validator: ResolvedValue.fontWeightValueValidator).orFallback(fontWeightValueValue)
          }
        }()
        _ = {
         if key == parent?.functions?.link, context.templateData["functions"] == nil {
           functionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFunctionTemplate.self).orFallback(functionsValue)
          }
        }()
        _ = {
         if key == parent?.height?.link, context.templateData["height"] == nil {
           heightValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self).orFallback(heightValue)
          }
        }()
        _ = {
         if key == parent?.hintColor?.link, context.templateData["hint_color"] == nil {
           hintColorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).orFallback(hintColorValue)
          }
        }()
        _ = {
         if key == parent?.hintText?.link, context.templateData["hint_text"] == nil {
           hintTextValue = deserialize(__dictValue).orFallback(hintTextValue)
          }
        }()
        _ = {
         if key == parent?.id?.link, context.templateData["id"] == nil {
           idValue = deserialize(__dictValue).orFallback(idValue)
          }
        }()
        _ = {
         if key == parent?.layoutProvider?.link, context.templateData["layout_provider"] == nil {
           layoutProviderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivLayoutProviderTemplate.self).orFallback(layoutProviderValue)
          }
        }()
        _ = {
         if key == parent?.letterSpacing?.link, context.templateData["letter_spacing"] == nil {
           letterSpacingValue = deserialize(__dictValue).orFallback(letterSpacingValue)
          }
        }()
        _ = {
         if key == parent?.lineHeight?.link, context.templateData["line_height"] == nil {
           lineHeightValue = deserialize(__dictValue, validator: ResolvedValue.lineHeightValidator).orFallback(lineHeightValue)
          }
        }()
        _ = {
         if key == parent?.margins?.link, context.templateData["margins"] == nil {
           marginsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self).orFallback(marginsValue)
          }
        }()
        _ = {
         if key == parent?.options?.link, context.templateData["options"] == nil {
           optionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.optionsValidator, type: DivSelectTemplate.OptionTemplate.self).orFallback(optionsValue)
          }
        }()
        _ = {
         if key == parent?.paddings?.link, context.templateData["paddings"] == nil {
           paddingsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self).orFallback(paddingsValue)
          }
        }()
        _ = {
         if key == parent?.reuseId?.link, context.templateData["reuse_id"] == nil {
           reuseIdValue = deserialize(__dictValue).orFallback(reuseIdValue)
          }
        }()
        _ = {
         if key == parent?.rowSpan?.link, context.templateData["row_span"] == nil {
           rowSpanValue = deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator).orFallback(rowSpanValue)
          }
        }()
        _ = {
         if key == parent?.selectedActions?.link, context.templateData["selected_actions"] == nil {
           selectedActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).orFallback(selectedActionsValue)
          }
        }()
        _ = {
         if key == parent?.textColor?.link, context.templateData["text_color"] == nil {
           textColorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).orFallback(textColorValue)
          }
        }()
        _ = {
         if key == parent?.tooltips?.link, context.templateData["tooltips"] == nil {
           tooltipsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTooltipTemplate.self).orFallback(tooltipsValue)
          }
        }()
        _ = {
         if key == parent?.transform?.link, context.templateData["transform"] == nil {
           transformValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTransformTemplate.self).orFallback(transformValue)
          }
        }()
        _ = {
         if key == parent?.transformations?.link, context.templateData["transformations"] == nil {
           transformationsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTransformationTemplate.self).orFallback(transformationsValue)
          }
        }()
        _ = {
         if key == parent?.transitionChange?.link, context.templateData["transition_change"] == nil {
           transitionChangeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivChangeTransitionTemplate.self).orFallback(transitionChangeValue)
          }
        }()
        _ = {
         if key == parent?.transitionIn?.link, context.templateData["transition_in"] == nil {
           transitionInValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self).orFallback(transitionInValue)
          }
        }()
        _ = {
         if key == parent?.transitionOut?.link, context.templateData["transition_out"] == nil {
           transitionOutValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self).orFallback(transitionOutValue)
          }
        }()
        _ = {
         if key == parent?.transitionTriggers?.link, context.templateData["transition_triggers"] == nil {
           transitionTriggersValue = deserialize(__dictValue, validator: ResolvedValue.transitionTriggersValidator).orFallback(transitionTriggersValue)
          }
        }()
        _ = {
         if key == parent?.valueVariable?.link, context.templateData["value_variable"] == nil {
           valueVariableValue = deserialize(__dictValue).orFallback(valueVariableValue)
          }
        }()
        _ = {
         if key == parent?.variableTriggers?.link, context.templateData["variable_triggers"] == nil {
           variableTriggersValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTriggerTemplate.self).orFallback(variableTriggersValue)
          }
        }()
        _ = {
         if key == parent?.variables?.link, context.templateData["variables"] == nil {
           variablesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVariableTemplate.self).orFallback(variablesValue)
          }
        }()
        _ = {
         if key == parent?.visibility?.link, context.templateData["visibility"] == nil {
           visibilityValue = deserialize(__dictValue).orFallback(visibilityValue)
          }
        }()
        _ = {
         if key == parent?.visibilityAction?.link, context.templateData["visibility_action"] == nil {
           visibilityActionValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self).orFallback(visibilityActionValue)
          }
        }()
        _ = {
         if key == parent?.visibilityActions?.link, context.templateData["visibility_actions"] == nil {
           visibilityActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self).orFallback(visibilityActionsValue)
          }
        }()
        _ = {
         if key == parent?.width?.link, context.templateData["width"] == nil {
           widthValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self).orFallback(widthValue)
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { accessibilityValue = accessibilityValue.merged(with: { parent.accessibility?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { animatorsValue = animatorsValue.merged(with: { parent.animators?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { backgroundValue = backgroundValue.merged(with: { parent.background?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { borderValue = borderValue.merged(with: { parent.border?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { disappearActionsValue = disappearActionsValue.merged(with: { parent.disappearActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { extensionsValue = extensionsValue.merged(with: { parent.extensions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { focusValue = focusValue.merged(with: { parent.focus?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { functionsValue = functionsValue.merged(with: { parent.functions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { heightValue = heightValue.merged(with: { parent.height?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { layoutProviderValue = layoutProviderValue.merged(with: { parent.layoutProvider?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { marginsValue = marginsValue.merged(with: { parent.margins?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { optionsValue = optionsValue.merged(with: { parent.options?.resolveValue(context: context, validator: ResolvedValue.optionsValidator, useOnlyLinks: true) }) }()
      _ = { paddingsValue = paddingsValue.merged(with: { parent.paddings?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { selectedActionsValue = selectedActionsValue.merged(with: { parent.selectedActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { tooltipsValue = tooltipsValue.merged(with: { parent.tooltips?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { transformValue = transformValue.merged(with: { parent.transform?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { transformationsValue = transformationsValue.merged(with: { parent.transformations?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { transitionChangeValue = transitionChangeValue.merged(with: { parent.transitionChange?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { transitionInValue = transitionInValue.merged(with: { parent.transitionIn?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { transitionOutValue = transitionOutValue.merged(with: { parent.transitionOut?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { variableTriggersValue = variableTriggersValue.merged(with: { parent.variableTriggers?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { variablesValue = variablesValue.merged(with: { parent.variables?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { visibilityActionValue = visibilityActionValue.merged(with: { parent.visibilityAction?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { visibilityActionsValue = visibilityActionsValue.merged(with: { parent.visibilityActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { widthValue = widthValue.merged(with: { parent.width?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
    }
    var errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "accessibility", error: $0) },
      alignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_horizontal", error: $0) },
      alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      animatorsValue.errorsOrWarnings?.map { .nestedObjectError(field: "animators", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      disappearActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "disappear_actions", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      fontFamilyValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_family", error: $0) },
      fontSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size", error: $0) },
      fontSizeUnitValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size_unit", error: $0) },
      fontVariationSettingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_variation_settings", error: $0) },
      fontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight", error: $0) },
      fontWeightValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight_value", error: $0) },
      functionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "functions", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      hintColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "hint_color", error: $0) },
      hintTextValue.errorsOrWarnings?.map { .nestedObjectError(field: "hint_text", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      layoutProviderValue.errorsOrWarnings?.map { .nestedObjectError(field: "layout_provider", error: $0) },
      letterSpacingValue.errorsOrWarnings?.map { .nestedObjectError(field: "letter_spacing", error: $0) },
      lineHeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "line_height", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      optionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "options", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      reuseIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "reuse_id", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
      textColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_color", error: $0) },
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
      transformValue.errorsOrWarnings?.map { .nestedObjectError(field: "transform", error: $0) },
      transformationsValue.errorsOrWarnings?.map { .nestedObjectError(field: "transformations", error: $0) },
      transitionChangeValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_change", error: $0) },
      transitionInValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_in", error: $0) },
      transitionOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_out", error: $0) },
      transitionTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_triggers", error: $0) },
      valueVariableValue.errorsOrWarnings?.map { .nestedObjectError(field: "value_variable", error: $0) },
      variableTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable_triggers", error: $0) },
      variablesValue.errorsOrWarnings?.map { .nestedObjectError(field: "variables", error: $0) },
      visibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility", error: $0) },
      visibilityActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_action", error: $0) },
      visibilityActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_actions", error: $0) },
      widthValue.errorsOrWarnings?.map { .nestedObjectError(field: "width", error: $0) }
    )
    if case .noValue = optionsValue {
      errors.append(.requiredFieldIsMissing(field: "options"))
    }
    if case .noValue = valueVariableValue {
      errors.append(.requiredFieldIsMissing(field: "value_variable"))
    }
    guard
      let optionsNonNil = optionsValue.value,
      let valueVariableNonNil = valueVariableValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivSelect(
      accessibility: { accessibilityValue.value }(),
      alignmentHorizontal: { alignmentHorizontalValue.value }(),
      alignmentVertical: { alignmentVerticalValue.value }(),
      alpha: { alphaValue.value }(),
      animators: { animatorsValue.value }(),
      background: { backgroundValue.value }(),
      border: { borderValue.value }(),
      columnSpan: { columnSpanValue.value }(),
      disappearActions: { disappearActionsValue.value }(),
      extensions: { extensionsValue.value }(),
      focus: { focusValue.value }(),
      fontFamily: { fontFamilyValue.value }(),
      fontSize: { fontSizeValue.value }(),
      fontSizeUnit: { fontSizeUnitValue.value }(),
      fontVariationSettings: { fontVariationSettingsValue.value }(),
      fontWeight: { fontWeightValue.value }(),
      fontWeightValue: { fontWeightValueValue.value }(),
      functions: { functionsValue.value }(),
      height: { heightValue.value }(),
      hintColor: { hintColorValue.value }(),
      hintText: { hintTextValue.value }(),
      id: { idValue.value }(),
      layoutProvider: { layoutProviderValue.value }(),
      letterSpacing: { letterSpacingValue.value }(),
      lineHeight: { lineHeightValue.value }(),
      margins: { marginsValue.value }(),
      options: { optionsNonNil }(),
      paddings: { paddingsValue.value }(),
      reuseId: { reuseIdValue.value }(),
      rowSpan: { rowSpanValue.value }(),
      selectedActions: { selectedActionsValue.value }(),
      textColor: { textColorValue.value }(),
      tooltips: { tooltipsValue.value }(),
      transform: { transformValue.value }(),
      transformations: { transformationsValue.value }(),
      transitionChange: { transitionChangeValue.value }(),
      transitionIn: { transitionInValue.value }(),
      transitionOut: { transitionOutValue.value }(),
      transitionTriggers: { transitionTriggersValue.value }(),
      valueVariable: { valueVariableNonNil }(),
      variableTriggers: { variableTriggersValue.value }(),
      variables: { variablesValue.value }(),
      visibility: { visibilityValue.value }(),
      visibilityAction: { visibilityActionValue.value }(),
      visibilityActions: { visibilityActionsValue.value }(),
      width: { widthValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivSelectTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivSelectTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivSelectTemplate(
      parent: nil,
      accessibility: accessibility ?? mergedParent.accessibility,
      alignmentHorizontal: alignmentHorizontal ?? mergedParent.alignmentHorizontal,
      alignmentVertical: alignmentVertical ?? mergedParent.alignmentVertical,
      alpha: alpha ?? mergedParent.alpha,
      animators: animators ?? mergedParent.animators,
      background: background ?? mergedParent.background,
      border: border ?? mergedParent.border,
      columnSpan: columnSpan ?? mergedParent.columnSpan,
      disappearActions: disappearActions ?? mergedParent.disappearActions,
      extensions: extensions ?? mergedParent.extensions,
      focus: focus ?? mergedParent.focus,
      fontFamily: fontFamily ?? mergedParent.fontFamily,
      fontSize: fontSize ?? mergedParent.fontSize,
      fontSizeUnit: fontSizeUnit ?? mergedParent.fontSizeUnit,
      fontVariationSettings: fontVariationSettings ?? mergedParent.fontVariationSettings,
      fontWeight: fontWeight ?? mergedParent.fontWeight,
      fontWeightValue: fontWeightValue ?? mergedParent.fontWeightValue,
      functions: functions ?? mergedParent.functions,
      height: height ?? mergedParent.height,
      hintColor: hintColor ?? mergedParent.hintColor,
      hintText: hintText ?? mergedParent.hintText,
      id: id ?? mergedParent.id,
      layoutProvider: layoutProvider ?? mergedParent.layoutProvider,
      letterSpacing: letterSpacing ?? mergedParent.letterSpacing,
      lineHeight: lineHeight ?? mergedParent.lineHeight,
      margins: margins ?? mergedParent.margins,
      options: options ?? mergedParent.options,
      paddings: paddings ?? mergedParent.paddings,
      reuseId: reuseId ?? mergedParent.reuseId,
      rowSpan: rowSpan ?? mergedParent.rowSpan,
      selectedActions: selectedActions ?? mergedParent.selectedActions,
      textColor: textColor ?? mergedParent.textColor,
      tooltips: tooltips ?? mergedParent.tooltips,
      transform: transform ?? mergedParent.transform,
      transformations: transformations ?? mergedParent.transformations,
      transitionChange: transitionChange ?? mergedParent.transitionChange,
      transitionIn: transitionIn ?? mergedParent.transitionIn,
      transitionOut: transitionOut ?? mergedParent.transitionOut,
      transitionTriggers: transitionTriggers ?? mergedParent.transitionTriggers,
      valueVariable: valueVariable ?? mergedParent.valueVariable,
      variableTriggers: variableTriggers ?? mergedParent.variableTriggers,
      variables: variables ?? mergedParent.variables,
      visibility: visibility ?? mergedParent.visibility,
      visibilityAction: visibilityAction ?? mergedParent.visibilityAction,
      visibilityActions: visibilityActions ?? mergedParent.visibilityActions,
      width: width ?? mergedParent.width
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivSelectTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivSelectTemplate(
      parent: nil,
      accessibility: merged.accessibility?.tryResolveParent(templates: templates),
      alignmentHorizontal: merged.alignmentHorizontal,
      alignmentVertical: merged.alignmentVertical,
      alpha: merged.alpha,
      animators: merged.animators?.tryResolveParent(templates: templates),
      background: merged.background?.tryResolveParent(templates: templates),
      border: merged.border?.tryResolveParent(templates: templates),
      columnSpan: merged.columnSpan,
      disappearActions: merged.disappearActions?.tryResolveParent(templates: templates),
      extensions: merged.extensions?.tryResolveParent(templates: templates),
      focus: merged.focus?.tryResolveParent(templates: templates),
      fontFamily: merged.fontFamily,
      fontSize: merged.fontSize,
      fontSizeUnit: merged.fontSizeUnit,
      fontVariationSettings: merged.fontVariationSettings,
      fontWeight: merged.fontWeight,
      fontWeightValue: merged.fontWeightValue,
      functions: merged.functions?.tryResolveParent(templates: templates),
      height: merged.height?.tryResolveParent(templates: templates),
      hintColor: merged.hintColor,
      hintText: merged.hintText,
      id: merged.id,
      layoutProvider: merged.layoutProvider?.tryResolveParent(templates: templates),
      letterSpacing: merged.letterSpacing,
      lineHeight: merged.lineHeight,
      margins: merged.margins?.tryResolveParent(templates: templates),
      options: try merged.options?.resolveParent(templates: templates),
      paddings: merged.paddings?.tryResolveParent(templates: templates),
      reuseId: merged.reuseId,
      rowSpan: merged.rowSpan,
      selectedActions: merged.selectedActions?.tryResolveParent(templates: templates),
      textColor: merged.textColor,
      tooltips: merged.tooltips?.tryResolveParent(templates: templates),
      transform: merged.transform?.tryResolveParent(templates: templates),
      transformations: merged.transformations?.tryResolveParent(templates: templates),
      transitionChange: merged.transitionChange?.tryResolveParent(templates: templates),
      transitionIn: merged.transitionIn?.tryResolveParent(templates: templates),
      transitionOut: merged.transitionOut?.tryResolveParent(templates: templates),
      transitionTriggers: merged.transitionTriggers,
      valueVariable: merged.valueVariable,
      variableTriggers: merged.variableTriggers?.tryResolveParent(templates: templates),
      variables: merged.variables?.tryResolveParent(templates: templates),
      visibility: merged.visibility,
      visibilityAction: merged.visibilityAction?.tryResolveParent(templates: templates),
      visibilityActions: merged.visibilityActions?.tryResolveParent(templates: templates),
      width: merged.width?.tryResolveParent(templates: templates)
    )
  }
}
