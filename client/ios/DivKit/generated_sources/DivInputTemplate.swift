// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivInputTemplate: TemplateValue, Sendable {
  public final class NativeInterfaceTemplate: TemplateValue, Sendable {
    public let color: Field<Expression<Color>>?

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      self.init(
        color: dictionary.getOptionalExpressionField("color", transform: Color.color(withHexString:))
      )
    }

    init(
      color: Field<Expression<Color>>? = nil
    ) {
      self.color = color
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: NativeInterfaceTemplate?) -> DeserializationResult<DivInput.NativeInterface> {
      let colorValue = { parent?.color?.resolveValue(context: context, transform: Color.color(withHexString:)) ?? .noValue }()
      var errors = mergeErrors(
        colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) }
      )
      if case .noValue = colorValue {
        errors.append(.requiredFieldIsMissing(field: "color"))
      }
      guard
        let colorNonNil = colorValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivInput.NativeInterface(
        color: { colorNonNil }()
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: NativeInterfaceTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivInput.NativeInterface> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var colorValue: DeserializationResult<Expression<Color>> = { parent?.color?.value() ?? .noValue }()
      _ = {
        // Each field is parsed in its own lambda to keep the stack size managable
        // Otherwise the compiler will allocate stack for each intermediate variable
        // upfront even when we don't actually visit a relevant branch
        for (key, __dictValue) in context.templateData {
          _ = {
            if key == "color" {
             colorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: colorValue)
            }
          }()
          _ = {
           if key == parent?.color?.link {
             colorValue = colorValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
            }
          }()
        }
      }()
      var errors = mergeErrors(
        colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) }
      )
      if case .noValue = colorValue {
        errors.append(.requiredFieldIsMissing(field: "color"))
      }
      guard
        let colorNonNil = colorValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivInput.NativeInterface(
        color: { colorNonNil }()
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> NativeInterfaceTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> NativeInterfaceTemplate {
      return try mergedWithParent(templates: templates)
    }
  }

  public typealias Autocapitalization = DivInput.Autocapitalization

  public typealias EnterKeyType = DivInput.EnterKeyType

  public typealias KeyboardType = DivInput.KeyboardType

  public static let type: String = "input"
  public let parent: String?
  public let accessibility: Field<DivAccessibilityTemplate>?
  public let alignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>?
  public let alignmentVertical: Field<Expression<DivAlignmentVertical>>?
  public let alpha: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let animators: Field<[DivAnimatorTemplate]>?
  public let autocapitalization: Field<Expression<Autocapitalization>>? // default value: auto
  public let background: Field<[DivBackgroundTemplate]>?
  public let border: Field<DivBorderTemplate>?
  public let captureFocusOnAction: Field<Expression<Bool>>? // default value: true
  public let columnSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let disappearActions: Field<[DivDisappearActionTemplate]>?
  public let enterKeyActions: Field<[DivActionTemplate]>?
  public let enterKeyType: Field<Expression<EnterKeyType>>? // default value: default
  public let extensions: Field<[DivExtensionTemplate]>?
  public let filters: Field<[DivInputFilterTemplate]>?
  public let focus: Field<DivFocusTemplate>?
  public let fontFamily: Field<Expression<String>>?
  public let fontSize: Field<Expression<Int>>? // constraint: number >= 0; default value: 12
  public let fontSizeUnit: Field<Expression<DivSizeUnit>>? // default value: sp
  public let fontWeight: Field<Expression<DivFontWeight>>? // default value: regular
  public let fontWeightValue: Field<Expression<Int>>? // constraint: number > 0
  public let functions: Field<[DivFunctionTemplate]>?
  public let height: Field<DivSizeTemplate>? // default value: .divWrapContentSize(DivWrapContentSize())
  public let highlightColor: Field<Expression<Color>>?
  public let hintColor: Field<Expression<Color>>? // default value: #73000000
  public let hintText: Field<Expression<String>>?
  public let id: Field<String>?
  public let isEnabled: Field<Expression<Bool>>? // default value: true
  public let keyboardType: Field<Expression<KeyboardType>>? // default value: multi_line_text
  public let layoutProvider: Field<DivLayoutProviderTemplate>?
  public let letterSpacing: Field<Expression<Double>>? // default value: 0
  public let lineHeight: Field<Expression<Int>>? // constraint: number >= 0
  public let margins: Field<DivEdgeInsetsTemplate>?
  public let mask: Field<DivInputMaskTemplate>?
  public let maxLength: Field<Expression<Int>>? // constraint: number > 0
  public let maxVisibleLines: Field<Expression<Int>>? // constraint: number > 0
  public let nativeInterface: Field<NativeInterfaceTemplate>?
  public let paddings: Field<DivEdgeInsetsTemplate>?
  public let reuseId: Field<Expression<String>>?
  public let rowSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let selectAllOnFocus: Field<Expression<Bool>>? // default value: false
  public let selectedActions: Field<[DivActionTemplate]>?
  public let textAlignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>? // default value: start
  public let textAlignmentVertical: Field<Expression<DivAlignmentVertical>>? // default value: center
  public let textColor: Field<Expression<Color>>? // default value: #FF000000
  public let textVariable: Field<String>?
  public let tooltips: Field<[DivTooltipTemplate]>?
  public let transform: Field<DivTransformTemplate>?
  public let transitionChange: Field<DivChangeTransitionTemplate>?
  public let transitionIn: Field<DivAppearanceTransitionTemplate>?
  public let transitionOut: Field<DivAppearanceTransitionTemplate>?
  public let transitionTriggers: Field<[DivTransitionTrigger]>? // at least 1 elements
  public let validators: Field<[DivInputValidatorTemplate]>?
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
      autocapitalization: dictionary.getOptionalExpressionField("autocapitalization"),
      background: dictionary.getOptionalArray("background", templateToType: templateToType),
      border: dictionary.getOptionalField("border", templateToType: templateToType),
      captureFocusOnAction: dictionary.getOptionalExpressionField("capture_focus_on_action"),
      columnSpan: dictionary.getOptionalExpressionField("column_span"),
      disappearActions: dictionary.getOptionalArray("disappear_actions", templateToType: templateToType),
      enterKeyActions: dictionary.getOptionalArray("enter_key_actions", templateToType: templateToType),
      enterKeyType: dictionary.getOptionalExpressionField("enter_key_type"),
      extensions: dictionary.getOptionalArray("extensions", templateToType: templateToType),
      filters: dictionary.getOptionalArray("filters", templateToType: templateToType),
      focus: dictionary.getOptionalField("focus", templateToType: templateToType),
      fontFamily: dictionary.getOptionalExpressionField("font_family"),
      fontSize: dictionary.getOptionalExpressionField("font_size"),
      fontSizeUnit: dictionary.getOptionalExpressionField("font_size_unit"),
      fontWeight: dictionary.getOptionalExpressionField("font_weight"),
      fontWeightValue: dictionary.getOptionalExpressionField("font_weight_value"),
      functions: dictionary.getOptionalArray("functions", templateToType: templateToType),
      height: dictionary.getOptionalField("height", templateToType: templateToType),
      highlightColor: dictionary.getOptionalExpressionField("highlight_color", transform: Color.color(withHexString:)),
      hintColor: dictionary.getOptionalExpressionField("hint_color", transform: Color.color(withHexString:)),
      hintText: dictionary.getOptionalExpressionField("hint_text"),
      id: dictionary.getOptionalField("id"),
      isEnabled: dictionary.getOptionalExpressionField("is_enabled"),
      keyboardType: dictionary.getOptionalExpressionField("keyboard_type"),
      layoutProvider: dictionary.getOptionalField("layout_provider", templateToType: templateToType),
      letterSpacing: dictionary.getOptionalExpressionField("letter_spacing"),
      lineHeight: dictionary.getOptionalExpressionField("line_height"),
      margins: dictionary.getOptionalField("margins", templateToType: templateToType),
      mask: dictionary.getOptionalField("mask", templateToType: templateToType),
      maxLength: dictionary.getOptionalExpressionField("max_length"),
      maxVisibleLines: dictionary.getOptionalExpressionField("max_visible_lines"),
      nativeInterface: dictionary.getOptionalField("native_interface", templateToType: templateToType),
      paddings: dictionary.getOptionalField("paddings", templateToType: templateToType),
      reuseId: dictionary.getOptionalExpressionField("reuse_id"),
      rowSpan: dictionary.getOptionalExpressionField("row_span"),
      selectAllOnFocus: dictionary.getOptionalExpressionField("select_all_on_focus"),
      selectedActions: dictionary.getOptionalArray("selected_actions", templateToType: templateToType),
      textAlignmentHorizontal: dictionary.getOptionalExpressionField("text_alignment_horizontal"),
      textAlignmentVertical: dictionary.getOptionalExpressionField("text_alignment_vertical"),
      textColor: dictionary.getOptionalExpressionField("text_color", transform: Color.color(withHexString:)),
      textVariable: dictionary.getOptionalField("text_variable"),
      tooltips: dictionary.getOptionalArray("tooltips", templateToType: templateToType),
      transform: dictionary.getOptionalField("transform", templateToType: templateToType),
      transitionChange: dictionary.getOptionalField("transition_change", templateToType: templateToType),
      transitionIn: dictionary.getOptionalField("transition_in", templateToType: templateToType),
      transitionOut: dictionary.getOptionalField("transition_out", templateToType: templateToType),
      transitionTriggers: dictionary.getOptionalArray("transition_triggers"),
      validators: dictionary.getOptionalArray("validators", templateToType: templateToType),
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
    autocapitalization: Field<Expression<Autocapitalization>>? = nil,
    background: Field<[DivBackgroundTemplate]>? = nil,
    border: Field<DivBorderTemplate>? = nil,
    captureFocusOnAction: Field<Expression<Bool>>? = nil,
    columnSpan: Field<Expression<Int>>? = nil,
    disappearActions: Field<[DivDisappearActionTemplate]>? = nil,
    enterKeyActions: Field<[DivActionTemplate]>? = nil,
    enterKeyType: Field<Expression<EnterKeyType>>? = nil,
    extensions: Field<[DivExtensionTemplate]>? = nil,
    filters: Field<[DivInputFilterTemplate]>? = nil,
    focus: Field<DivFocusTemplate>? = nil,
    fontFamily: Field<Expression<String>>? = nil,
    fontSize: Field<Expression<Int>>? = nil,
    fontSizeUnit: Field<Expression<DivSizeUnit>>? = nil,
    fontWeight: Field<Expression<DivFontWeight>>? = nil,
    fontWeightValue: Field<Expression<Int>>? = nil,
    functions: Field<[DivFunctionTemplate]>? = nil,
    height: Field<DivSizeTemplate>? = nil,
    highlightColor: Field<Expression<Color>>? = nil,
    hintColor: Field<Expression<Color>>? = nil,
    hintText: Field<Expression<String>>? = nil,
    id: Field<String>? = nil,
    isEnabled: Field<Expression<Bool>>? = nil,
    keyboardType: Field<Expression<KeyboardType>>? = nil,
    layoutProvider: Field<DivLayoutProviderTemplate>? = nil,
    letterSpacing: Field<Expression<Double>>? = nil,
    lineHeight: Field<Expression<Int>>? = nil,
    margins: Field<DivEdgeInsetsTemplate>? = nil,
    mask: Field<DivInputMaskTemplate>? = nil,
    maxLength: Field<Expression<Int>>? = nil,
    maxVisibleLines: Field<Expression<Int>>? = nil,
    nativeInterface: Field<NativeInterfaceTemplate>? = nil,
    paddings: Field<DivEdgeInsetsTemplate>? = nil,
    reuseId: Field<Expression<String>>? = nil,
    rowSpan: Field<Expression<Int>>? = nil,
    selectAllOnFocus: Field<Expression<Bool>>? = nil,
    selectedActions: Field<[DivActionTemplate]>? = nil,
    textAlignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>? = nil,
    textAlignmentVertical: Field<Expression<DivAlignmentVertical>>? = nil,
    textColor: Field<Expression<Color>>? = nil,
    textVariable: Field<String>? = nil,
    tooltips: Field<[DivTooltipTemplate]>? = nil,
    transform: Field<DivTransformTemplate>? = nil,
    transitionChange: Field<DivChangeTransitionTemplate>? = nil,
    transitionIn: Field<DivAppearanceTransitionTemplate>? = nil,
    transitionOut: Field<DivAppearanceTransitionTemplate>? = nil,
    transitionTriggers: Field<[DivTransitionTrigger]>? = nil,
    validators: Field<[DivInputValidatorTemplate]>? = nil,
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
    self.autocapitalization = autocapitalization
    self.background = background
    self.border = border
    self.captureFocusOnAction = captureFocusOnAction
    self.columnSpan = columnSpan
    self.disappearActions = disappearActions
    self.enterKeyActions = enterKeyActions
    self.enterKeyType = enterKeyType
    self.extensions = extensions
    self.filters = filters
    self.focus = focus
    self.fontFamily = fontFamily
    self.fontSize = fontSize
    self.fontSizeUnit = fontSizeUnit
    self.fontWeight = fontWeight
    self.fontWeightValue = fontWeightValue
    self.functions = functions
    self.height = height
    self.highlightColor = highlightColor
    self.hintColor = hintColor
    self.hintText = hintText
    self.id = id
    self.isEnabled = isEnabled
    self.keyboardType = keyboardType
    self.layoutProvider = layoutProvider
    self.letterSpacing = letterSpacing
    self.lineHeight = lineHeight
    self.margins = margins
    self.mask = mask
    self.maxLength = maxLength
    self.maxVisibleLines = maxVisibleLines
    self.nativeInterface = nativeInterface
    self.paddings = paddings
    self.reuseId = reuseId
    self.rowSpan = rowSpan
    self.selectAllOnFocus = selectAllOnFocus
    self.selectedActions = selectedActions
    self.textAlignmentHorizontal = textAlignmentHorizontal
    self.textAlignmentVertical = textAlignmentVertical
    self.textColor = textColor
    self.textVariable = textVariable
    self.tooltips = tooltips
    self.transform = transform
    self.transitionChange = transitionChange
    self.transitionIn = transitionIn
    self.transitionOut = transitionOut
    self.transitionTriggers = transitionTriggers
    self.validators = validators
    self.variableTriggers = variableTriggers
    self.variables = variables
    self.visibility = visibility
    self.visibilityAction = visibilityAction
    self.visibilityActions = visibilityActions
    self.width = width
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivInputTemplate?) -> DeserializationResult<DivInput> {
    let accessibilityValue = { parent?.accessibility?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let alignmentHorizontalValue = { parent?.alignmentHorizontal?.resolveOptionalValue(context: context) ?? .noValue }()
    let alignmentVerticalValue = { parent?.alignmentVertical?.resolveOptionalValue(context: context) ?? .noValue }()
    let alphaValue = { parent?.alpha?.resolveOptionalValue(context: context, validator: ResolvedValue.alphaValidator) ?? .noValue }()
    let animatorsValue = { parent?.animators?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let autocapitalizationValue = { parent?.autocapitalization?.resolveOptionalValue(context: context) ?? .noValue }()
    let backgroundValue = { parent?.background?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let borderValue = { parent?.border?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let captureFocusOnActionValue = { parent?.captureFocusOnAction?.resolveOptionalValue(context: context) ?? .noValue }()
    let columnSpanValue = { parent?.columnSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.columnSpanValidator) ?? .noValue }()
    let disappearActionsValue = { parent?.disappearActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let enterKeyActionsValue = { parent?.enterKeyActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let enterKeyTypeValue = { parent?.enterKeyType?.resolveOptionalValue(context: context) ?? .noValue }()
    let extensionsValue = { parent?.extensions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let filtersValue = { parent?.filters?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let focusValue = { parent?.focus?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let fontFamilyValue = { parent?.fontFamily?.resolveOptionalValue(context: context) ?? .noValue }()
    let fontSizeValue = { parent?.fontSize?.resolveOptionalValue(context: context, validator: ResolvedValue.fontSizeValidator) ?? .noValue }()
    let fontSizeUnitValue = { parent?.fontSizeUnit?.resolveOptionalValue(context: context) ?? .noValue }()
    let fontWeightValue = { parent?.fontWeight?.resolveOptionalValue(context: context) ?? .noValue }()
    let fontWeightValueValue = { parent?.fontWeightValue?.resolveOptionalValue(context: context, validator: ResolvedValue.fontWeightValueValidator) ?? .noValue }()
    let functionsValue = { parent?.functions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let heightValue = { parent?.height?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let highlightColorValue = { parent?.highlightColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:)) ?? .noValue }()
    let hintColorValue = { parent?.hintColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:)) ?? .noValue }()
    let hintTextValue = { parent?.hintText?.resolveOptionalValue(context: context) ?? .noValue }()
    let idValue = { parent?.id?.resolveOptionalValue(context: context) ?? .noValue }()
    let isEnabledValue = { parent?.isEnabled?.resolveOptionalValue(context: context) ?? .noValue }()
    let keyboardTypeValue = { parent?.keyboardType?.resolveOptionalValue(context: context) ?? .noValue }()
    let layoutProviderValue = { parent?.layoutProvider?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let letterSpacingValue = { parent?.letterSpacing?.resolveOptionalValue(context: context) ?? .noValue }()
    let lineHeightValue = { parent?.lineHeight?.resolveOptionalValue(context: context, validator: ResolvedValue.lineHeightValidator) ?? .noValue }()
    let marginsValue = { parent?.margins?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let maskValue = { parent?.mask?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let maxLengthValue = { parent?.maxLength?.resolveOptionalValue(context: context, validator: ResolvedValue.maxLengthValidator) ?? .noValue }()
    let maxVisibleLinesValue = { parent?.maxVisibleLines?.resolveOptionalValue(context: context, validator: ResolvedValue.maxVisibleLinesValidator) ?? .noValue }()
    let nativeInterfaceValue = { parent?.nativeInterface?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let paddingsValue = { parent?.paddings?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let reuseIdValue = { parent?.reuseId?.resolveOptionalValue(context: context) ?? .noValue }()
    let rowSpanValue = { parent?.rowSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.rowSpanValidator) ?? .noValue }()
    let selectAllOnFocusValue = { parent?.selectAllOnFocus?.resolveOptionalValue(context: context) ?? .noValue }()
    let selectedActionsValue = { parent?.selectedActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let textAlignmentHorizontalValue = { parent?.textAlignmentHorizontal?.resolveOptionalValue(context: context) ?? .noValue }()
    let textAlignmentVerticalValue = { parent?.textAlignmentVertical?.resolveOptionalValue(context: context) ?? .noValue }()
    let textColorValue = { parent?.textColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:)) ?? .noValue }()
    let textVariableValue = { parent?.textVariable?.resolveValue(context: context) ?? .noValue }()
    let tooltipsValue = { parent?.tooltips?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let transformValue = { parent?.transform?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let transitionChangeValue = { parent?.transitionChange?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let transitionInValue = { parent?.transitionIn?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let transitionOutValue = { parent?.transitionOut?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let transitionTriggersValue = { parent?.transitionTriggers?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionTriggersValidator) ?? .noValue }()
    let validatorsValue = { parent?.validators?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
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
      autocapitalizationValue.errorsOrWarnings?.map { .nestedObjectError(field: "autocapitalization", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      captureFocusOnActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "capture_focus_on_action", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      disappearActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "disappear_actions", error: $0) },
      enterKeyActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "enter_key_actions", error: $0) },
      enterKeyTypeValue.errorsOrWarnings?.map { .nestedObjectError(field: "enter_key_type", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      filtersValue.errorsOrWarnings?.map { .nestedObjectError(field: "filters", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      fontFamilyValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_family", error: $0) },
      fontSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size", error: $0) },
      fontSizeUnitValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size_unit", error: $0) },
      fontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight", error: $0) },
      fontWeightValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight_value", error: $0) },
      functionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "functions", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      highlightColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "highlight_color", error: $0) },
      hintColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "hint_color", error: $0) },
      hintTextValue.errorsOrWarnings?.map { .nestedObjectError(field: "hint_text", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      isEnabledValue.errorsOrWarnings?.map { .nestedObjectError(field: "is_enabled", error: $0) },
      keyboardTypeValue.errorsOrWarnings?.map { .nestedObjectError(field: "keyboard_type", error: $0) },
      layoutProviderValue.errorsOrWarnings?.map { .nestedObjectError(field: "layout_provider", error: $0) },
      letterSpacingValue.errorsOrWarnings?.map { .nestedObjectError(field: "letter_spacing", error: $0) },
      lineHeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "line_height", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      maskValue.errorsOrWarnings?.map { .nestedObjectError(field: "mask", error: $0) },
      maxLengthValue.errorsOrWarnings?.map { .nestedObjectError(field: "max_length", error: $0) },
      maxVisibleLinesValue.errorsOrWarnings?.map { .nestedObjectError(field: "max_visible_lines", error: $0) },
      nativeInterfaceValue.errorsOrWarnings?.map { .nestedObjectError(field: "native_interface", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      reuseIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "reuse_id", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      selectAllOnFocusValue.errorsOrWarnings?.map { .nestedObjectError(field: "select_all_on_focus", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
      textAlignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_alignment_horizontal", error: $0) },
      textAlignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_alignment_vertical", error: $0) },
      textColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_color", error: $0) },
      textVariableValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_variable", error: $0) },
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
      transformValue.errorsOrWarnings?.map { .nestedObjectError(field: "transform", error: $0) },
      transitionChangeValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_change", error: $0) },
      transitionInValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_in", error: $0) },
      transitionOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_out", error: $0) },
      transitionTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_triggers", error: $0) },
      validatorsValue.errorsOrWarnings?.map { .nestedObjectError(field: "validators", error: $0) },
      variableTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable_triggers", error: $0) },
      variablesValue.errorsOrWarnings?.map { .nestedObjectError(field: "variables", error: $0) },
      visibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility", error: $0) },
      visibilityActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_action", error: $0) },
      visibilityActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_actions", error: $0) },
      widthValue.errorsOrWarnings?.map { .nestedObjectError(field: "width", error: $0) }
    )
    if case .noValue = textVariableValue {
      errors.append(.requiredFieldIsMissing(field: "text_variable"))
    }
    guard
      let textVariableNonNil = textVariableValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivInput(
      accessibility: { accessibilityValue.value }(),
      alignmentHorizontal: { alignmentHorizontalValue.value }(),
      alignmentVertical: { alignmentVerticalValue.value }(),
      alpha: { alphaValue.value }(),
      animators: { animatorsValue.value }(),
      autocapitalization: { autocapitalizationValue.value }(),
      background: { backgroundValue.value }(),
      border: { borderValue.value }(),
      captureFocusOnAction: { captureFocusOnActionValue.value }(),
      columnSpan: { columnSpanValue.value }(),
      disappearActions: { disappearActionsValue.value }(),
      enterKeyActions: { enterKeyActionsValue.value }(),
      enterKeyType: { enterKeyTypeValue.value }(),
      extensions: { extensionsValue.value }(),
      filters: { filtersValue.value }(),
      focus: { focusValue.value }(),
      fontFamily: { fontFamilyValue.value }(),
      fontSize: { fontSizeValue.value }(),
      fontSizeUnit: { fontSizeUnitValue.value }(),
      fontWeight: { fontWeightValue.value }(),
      fontWeightValue: { fontWeightValueValue.value }(),
      functions: { functionsValue.value }(),
      height: { heightValue.value }(),
      highlightColor: { highlightColorValue.value }(),
      hintColor: { hintColorValue.value }(),
      hintText: { hintTextValue.value }(),
      id: { idValue.value }(),
      isEnabled: { isEnabledValue.value }(),
      keyboardType: { keyboardTypeValue.value }(),
      layoutProvider: { layoutProviderValue.value }(),
      letterSpacing: { letterSpacingValue.value }(),
      lineHeight: { lineHeightValue.value }(),
      margins: { marginsValue.value }(),
      mask: { maskValue.value }(),
      maxLength: { maxLengthValue.value }(),
      maxVisibleLines: { maxVisibleLinesValue.value }(),
      nativeInterface: { nativeInterfaceValue.value }(),
      paddings: { paddingsValue.value }(),
      reuseId: { reuseIdValue.value }(),
      rowSpan: { rowSpanValue.value }(),
      selectAllOnFocus: { selectAllOnFocusValue.value }(),
      selectedActions: { selectedActionsValue.value }(),
      textAlignmentHorizontal: { textAlignmentHorizontalValue.value }(),
      textAlignmentVertical: { textAlignmentVerticalValue.value }(),
      textColor: { textColorValue.value }(),
      textVariable: { textVariableNonNil }(),
      tooltips: { tooltipsValue.value }(),
      transform: { transformValue.value }(),
      transitionChange: { transitionChangeValue.value }(),
      transitionIn: { transitionInValue.value }(),
      transitionOut: { transitionOutValue.value }(),
      transitionTriggers: { transitionTriggersValue.value }(),
      validators: { validatorsValue.value }(),
      variableTriggers: { variableTriggersValue.value }(),
      variables: { variablesValue.value }(),
      visibility: { visibilityValue.value }(),
      visibilityAction: { visibilityActionValue.value }(),
      visibilityActions: { visibilityActionsValue.value }(),
      width: { widthValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivInputTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivInput> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var accessibilityValue: DeserializationResult<DivAccessibility> = .noValue
    var alignmentHorizontalValue: DeserializationResult<Expression<DivAlignmentHorizontal>> = { parent?.alignmentHorizontal?.value() ?? .noValue }()
    var alignmentVerticalValue: DeserializationResult<Expression<DivAlignmentVertical>> = { parent?.alignmentVertical?.value() ?? .noValue }()
    var alphaValue: DeserializationResult<Expression<Double>> = { parent?.alpha?.value() ?? .noValue }()
    var animatorsValue: DeserializationResult<[DivAnimator]> = .noValue
    var autocapitalizationValue: DeserializationResult<Expression<DivInput.Autocapitalization>> = { parent?.autocapitalization?.value() ?? .noValue }()
    var backgroundValue: DeserializationResult<[DivBackground]> = .noValue
    var borderValue: DeserializationResult<DivBorder> = .noValue
    var captureFocusOnActionValue: DeserializationResult<Expression<Bool>> = { parent?.captureFocusOnAction?.value() ?? .noValue }()
    var columnSpanValue: DeserializationResult<Expression<Int>> = { parent?.columnSpan?.value() ?? .noValue }()
    var disappearActionsValue: DeserializationResult<[DivDisappearAction]> = .noValue
    var enterKeyActionsValue: DeserializationResult<[DivAction]> = .noValue
    var enterKeyTypeValue: DeserializationResult<Expression<DivInput.EnterKeyType>> = { parent?.enterKeyType?.value() ?? .noValue }()
    var extensionsValue: DeserializationResult<[DivExtension]> = .noValue
    var filtersValue: DeserializationResult<[DivInputFilter]> = .noValue
    var focusValue: DeserializationResult<DivFocus> = .noValue
    var fontFamilyValue: DeserializationResult<Expression<String>> = { parent?.fontFamily?.value() ?? .noValue }()
    var fontSizeValue: DeserializationResult<Expression<Int>> = { parent?.fontSize?.value() ?? .noValue }()
    var fontSizeUnitValue: DeserializationResult<Expression<DivSizeUnit>> = { parent?.fontSizeUnit?.value() ?? .noValue }()
    var fontWeightValue: DeserializationResult<Expression<DivFontWeight>> = { parent?.fontWeight?.value() ?? .noValue }()
    var fontWeightValueValue: DeserializationResult<Expression<Int>> = { parent?.fontWeightValue?.value() ?? .noValue }()
    var functionsValue: DeserializationResult<[DivFunction]> = .noValue
    var heightValue: DeserializationResult<DivSize> = .noValue
    var highlightColorValue: DeserializationResult<Expression<Color>> = { parent?.highlightColor?.value() ?? .noValue }()
    var hintColorValue: DeserializationResult<Expression<Color>> = { parent?.hintColor?.value() ?? .noValue }()
    var hintTextValue: DeserializationResult<Expression<String>> = { parent?.hintText?.value() ?? .noValue }()
    var idValue: DeserializationResult<String> = { parent?.id?.value() ?? .noValue }()
    var isEnabledValue: DeserializationResult<Expression<Bool>> = { parent?.isEnabled?.value() ?? .noValue }()
    var keyboardTypeValue: DeserializationResult<Expression<DivInput.KeyboardType>> = { parent?.keyboardType?.value() ?? .noValue }()
    var layoutProviderValue: DeserializationResult<DivLayoutProvider> = .noValue
    var letterSpacingValue: DeserializationResult<Expression<Double>> = { parent?.letterSpacing?.value() ?? .noValue }()
    var lineHeightValue: DeserializationResult<Expression<Int>> = { parent?.lineHeight?.value() ?? .noValue }()
    var marginsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var maskValue: DeserializationResult<DivInputMask> = .noValue
    var maxLengthValue: DeserializationResult<Expression<Int>> = { parent?.maxLength?.value() ?? .noValue }()
    var maxVisibleLinesValue: DeserializationResult<Expression<Int>> = { parent?.maxVisibleLines?.value() ?? .noValue }()
    var nativeInterfaceValue: DeserializationResult<DivInput.NativeInterface> = .noValue
    var paddingsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var reuseIdValue: DeserializationResult<Expression<String>> = { parent?.reuseId?.value() ?? .noValue }()
    var rowSpanValue: DeserializationResult<Expression<Int>> = { parent?.rowSpan?.value() ?? .noValue }()
    var selectAllOnFocusValue: DeserializationResult<Expression<Bool>> = { parent?.selectAllOnFocus?.value() ?? .noValue }()
    var selectedActionsValue: DeserializationResult<[DivAction]> = .noValue
    var textAlignmentHorizontalValue: DeserializationResult<Expression<DivAlignmentHorizontal>> = { parent?.textAlignmentHorizontal?.value() ?? .noValue }()
    var textAlignmentVerticalValue: DeserializationResult<Expression<DivAlignmentVertical>> = { parent?.textAlignmentVertical?.value() ?? .noValue }()
    var textColorValue: DeserializationResult<Expression<Color>> = { parent?.textColor?.value() ?? .noValue }()
    var textVariableValue: DeserializationResult<String> = { parent?.textVariable?.value() ?? .noValue }()
    var tooltipsValue: DeserializationResult<[DivTooltip]> = .noValue
    var transformValue: DeserializationResult<DivTransform> = .noValue
    var transitionChangeValue: DeserializationResult<DivChangeTransition> = .noValue
    var transitionInValue: DeserializationResult<DivAppearanceTransition> = .noValue
    var transitionOutValue: DeserializationResult<DivAppearanceTransition> = .noValue
    var transitionTriggersValue: DeserializationResult<[DivTransitionTrigger]> = { parent?.transitionTriggers?.value(validatedBy: ResolvedValue.transitionTriggersValidator) ?? .noValue }()
    var validatorsValue: DeserializationResult<[DivInputValidator]> = .noValue
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
          if key == "autocapitalization" {
           autocapitalizationValue = deserialize(__dictValue).merged(with: autocapitalizationValue)
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
          if key == "capture_focus_on_action" {
           captureFocusOnActionValue = deserialize(__dictValue).merged(with: captureFocusOnActionValue)
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
          if key == "enter_key_actions" {
           enterKeyActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: enterKeyActionsValue)
          }
        }()
        _ = {
          if key == "enter_key_type" {
           enterKeyTypeValue = deserialize(__dictValue).merged(with: enterKeyTypeValue)
          }
        }()
        _ = {
          if key == "extensions" {
           extensionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivExtensionTemplate.self).merged(with: extensionsValue)
          }
        }()
        _ = {
          if key == "filters" {
           filtersValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivInputFilterTemplate.self).merged(with: filtersValue)
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
          if key == "highlight_color" {
           highlightColorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: highlightColorValue)
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
          if key == "is_enabled" {
           isEnabledValue = deserialize(__dictValue).merged(with: isEnabledValue)
          }
        }()
        _ = {
          if key == "keyboard_type" {
           keyboardTypeValue = deserialize(__dictValue).merged(with: keyboardTypeValue)
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
          if key == "mask" {
           maskValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivInputMaskTemplate.self).merged(with: maskValue)
          }
        }()
        _ = {
          if key == "max_length" {
           maxLengthValue = deserialize(__dictValue, validator: ResolvedValue.maxLengthValidator).merged(with: maxLengthValue)
          }
        }()
        _ = {
          if key == "max_visible_lines" {
           maxVisibleLinesValue = deserialize(__dictValue, validator: ResolvedValue.maxVisibleLinesValidator).merged(with: maxVisibleLinesValue)
          }
        }()
        _ = {
          if key == "native_interface" {
           nativeInterfaceValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivInputTemplate.NativeInterfaceTemplate.self).merged(with: nativeInterfaceValue)
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
          if key == "select_all_on_focus" {
           selectAllOnFocusValue = deserialize(__dictValue).merged(with: selectAllOnFocusValue)
          }
        }()
        _ = {
          if key == "selected_actions" {
           selectedActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: selectedActionsValue)
          }
        }()
        _ = {
          if key == "text_alignment_horizontal" {
           textAlignmentHorizontalValue = deserialize(__dictValue).merged(with: textAlignmentHorizontalValue)
          }
        }()
        _ = {
          if key == "text_alignment_vertical" {
           textAlignmentVerticalValue = deserialize(__dictValue).merged(with: textAlignmentVerticalValue)
          }
        }()
        _ = {
          if key == "text_color" {
           textColorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: textColorValue)
          }
        }()
        _ = {
          if key == "text_variable" {
           textVariableValue = deserialize(__dictValue).merged(with: textVariableValue)
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
          if key == "validators" {
           validatorsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivInputValidatorTemplate.self).merged(with: validatorsValue)
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
         if key == parent?.accessibility?.link {
           accessibilityValue = accessibilityValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAccessibilityTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.alignmentHorizontal?.link {
           alignmentHorizontalValue = alignmentHorizontalValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.alignmentVertical?.link {
           alignmentVerticalValue = alignmentVerticalValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.alpha?.link {
           alphaValue = alphaValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.alphaValidator) })
          }
        }()
        _ = {
         if key == parent?.animators?.link {
           animatorsValue = animatorsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAnimatorTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.autocapitalization?.link {
           autocapitalizationValue = autocapitalizationValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.background?.link {
           backgroundValue = backgroundValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBackgroundTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.border?.link {
           borderValue = borderValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBorderTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.captureFocusOnAction?.link {
           captureFocusOnActionValue = captureFocusOnActionValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.columnSpan?.link {
           columnSpanValue = columnSpanValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator) })
          }
        }()
        _ = {
         if key == parent?.disappearActions?.link {
           disappearActionsValue = disappearActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDisappearActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.enterKeyActions?.link {
           enterKeyActionsValue = enterKeyActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.enterKeyType?.link {
           enterKeyTypeValue = enterKeyTypeValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.extensions?.link {
           extensionsValue = extensionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivExtensionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.filters?.link {
           filtersValue = filtersValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivInputFilterTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.focus?.link {
           focusValue = focusValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFocusTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.fontFamily?.link {
           fontFamilyValue = fontFamilyValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.fontSize?.link {
           fontSizeValue = fontSizeValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.fontSizeValidator) })
          }
        }()
        _ = {
         if key == parent?.fontSizeUnit?.link {
           fontSizeUnitValue = fontSizeUnitValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.fontWeight?.link {
           fontWeightValue = fontWeightValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.fontWeightValue?.link {
           fontWeightValueValue = fontWeightValueValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.fontWeightValueValidator) })
          }
        }()
        _ = {
         if key == parent?.functions?.link {
           functionsValue = functionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFunctionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.height?.link {
           heightValue = heightValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.highlightColor?.link {
           highlightColorValue = highlightColorValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
          }
        }()
        _ = {
         if key == parent?.hintColor?.link {
           hintColorValue = hintColorValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
          }
        }()
        _ = {
         if key == parent?.hintText?.link {
           hintTextValue = hintTextValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.id?.link {
           idValue = idValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.isEnabled?.link {
           isEnabledValue = isEnabledValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.keyboardType?.link {
           keyboardTypeValue = keyboardTypeValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.layoutProvider?.link {
           layoutProviderValue = layoutProviderValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivLayoutProviderTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.letterSpacing?.link {
           letterSpacingValue = letterSpacingValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.lineHeight?.link {
           lineHeightValue = lineHeightValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.lineHeightValidator) })
          }
        }()
        _ = {
         if key == parent?.margins?.link {
           marginsValue = marginsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.mask?.link {
           maskValue = maskValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivInputMaskTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.maxLength?.link {
           maxLengthValue = maxLengthValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.maxLengthValidator) })
          }
        }()
        _ = {
         if key == parent?.maxVisibleLines?.link {
           maxVisibleLinesValue = maxVisibleLinesValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.maxVisibleLinesValidator) })
          }
        }()
        _ = {
         if key == parent?.nativeInterface?.link {
           nativeInterfaceValue = nativeInterfaceValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivInputTemplate.NativeInterfaceTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.paddings?.link {
           paddingsValue = paddingsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.reuseId?.link {
           reuseIdValue = reuseIdValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.rowSpan?.link {
           rowSpanValue = rowSpanValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator) })
          }
        }()
        _ = {
         if key == parent?.selectAllOnFocus?.link {
           selectAllOnFocusValue = selectAllOnFocusValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.selectedActions?.link {
           selectedActionsValue = selectedActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.textAlignmentHorizontal?.link {
           textAlignmentHorizontalValue = textAlignmentHorizontalValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.textAlignmentVertical?.link {
           textAlignmentVerticalValue = textAlignmentVerticalValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.textColor?.link {
           textColorValue = textColorValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
          }
        }()
        _ = {
         if key == parent?.textVariable?.link {
           textVariableValue = textVariableValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.tooltips?.link {
           tooltipsValue = tooltipsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTooltipTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.transform?.link {
           transformValue = transformValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTransformTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.transitionChange?.link {
           transitionChangeValue = transitionChangeValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivChangeTransitionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.transitionIn?.link {
           transitionInValue = transitionInValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.transitionOut?.link {
           transitionOutValue = transitionOutValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.transitionTriggers?.link {
           transitionTriggersValue = transitionTriggersValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.transitionTriggersValidator) })
          }
        }()
        _ = {
         if key == parent?.validators?.link {
           validatorsValue = validatorsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivInputValidatorTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.variableTriggers?.link {
           variableTriggersValue = variableTriggersValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTriggerTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.variables?.link {
           variablesValue = variablesValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVariableTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.visibility?.link {
           visibilityValue = visibilityValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.visibilityAction?.link {
           visibilityActionValue = visibilityActionValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.visibilityActions?.link {
           visibilityActionsValue = visibilityActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.width?.link {
           widthValue = widthValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self) })
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
      _ = { enterKeyActionsValue = enterKeyActionsValue.merged(with: { parent.enterKeyActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { extensionsValue = extensionsValue.merged(with: { parent.extensions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { filtersValue = filtersValue.merged(with: { parent.filters?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { focusValue = focusValue.merged(with: { parent.focus?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { functionsValue = functionsValue.merged(with: { parent.functions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { heightValue = heightValue.merged(with: { parent.height?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { layoutProviderValue = layoutProviderValue.merged(with: { parent.layoutProvider?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { marginsValue = marginsValue.merged(with: { parent.margins?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { maskValue = maskValue.merged(with: { parent.mask?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { nativeInterfaceValue = nativeInterfaceValue.merged(with: { parent.nativeInterface?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { paddingsValue = paddingsValue.merged(with: { parent.paddings?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { selectedActionsValue = selectedActionsValue.merged(with: { parent.selectedActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { tooltipsValue = tooltipsValue.merged(with: { parent.tooltips?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { transformValue = transformValue.merged(with: { parent.transform?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { transitionChangeValue = transitionChangeValue.merged(with: { parent.transitionChange?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { transitionInValue = transitionInValue.merged(with: { parent.transitionIn?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { transitionOutValue = transitionOutValue.merged(with: { parent.transitionOut?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { validatorsValue = validatorsValue.merged(with: { parent.validators?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
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
      autocapitalizationValue.errorsOrWarnings?.map { .nestedObjectError(field: "autocapitalization", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      captureFocusOnActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "capture_focus_on_action", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      disappearActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "disappear_actions", error: $0) },
      enterKeyActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "enter_key_actions", error: $0) },
      enterKeyTypeValue.errorsOrWarnings?.map { .nestedObjectError(field: "enter_key_type", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      filtersValue.errorsOrWarnings?.map { .nestedObjectError(field: "filters", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      fontFamilyValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_family", error: $0) },
      fontSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size", error: $0) },
      fontSizeUnitValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size_unit", error: $0) },
      fontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight", error: $0) },
      fontWeightValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight_value", error: $0) },
      functionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "functions", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      highlightColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "highlight_color", error: $0) },
      hintColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "hint_color", error: $0) },
      hintTextValue.errorsOrWarnings?.map { .nestedObjectError(field: "hint_text", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      isEnabledValue.errorsOrWarnings?.map { .nestedObjectError(field: "is_enabled", error: $0) },
      keyboardTypeValue.errorsOrWarnings?.map { .nestedObjectError(field: "keyboard_type", error: $0) },
      layoutProviderValue.errorsOrWarnings?.map { .nestedObjectError(field: "layout_provider", error: $0) },
      letterSpacingValue.errorsOrWarnings?.map { .nestedObjectError(field: "letter_spacing", error: $0) },
      lineHeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "line_height", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      maskValue.errorsOrWarnings?.map { .nestedObjectError(field: "mask", error: $0) },
      maxLengthValue.errorsOrWarnings?.map { .nestedObjectError(field: "max_length", error: $0) },
      maxVisibleLinesValue.errorsOrWarnings?.map { .nestedObjectError(field: "max_visible_lines", error: $0) },
      nativeInterfaceValue.errorsOrWarnings?.map { .nestedObjectError(field: "native_interface", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      reuseIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "reuse_id", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      selectAllOnFocusValue.errorsOrWarnings?.map { .nestedObjectError(field: "select_all_on_focus", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
      textAlignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_alignment_horizontal", error: $0) },
      textAlignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_alignment_vertical", error: $0) },
      textColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_color", error: $0) },
      textVariableValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_variable", error: $0) },
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
      transformValue.errorsOrWarnings?.map { .nestedObjectError(field: "transform", error: $0) },
      transitionChangeValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_change", error: $0) },
      transitionInValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_in", error: $0) },
      transitionOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_out", error: $0) },
      transitionTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_triggers", error: $0) },
      validatorsValue.errorsOrWarnings?.map { .nestedObjectError(field: "validators", error: $0) },
      variableTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable_triggers", error: $0) },
      variablesValue.errorsOrWarnings?.map { .nestedObjectError(field: "variables", error: $0) },
      visibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility", error: $0) },
      visibilityActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_action", error: $0) },
      visibilityActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_actions", error: $0) },
      widthValue.errorsOrWarnings?.map { .nestedObjectError(field: "width", error: $0) }
    )
    if case .noValue = textVariableValue {
      errors.append(.requiredFieldIsMissing(field: "text_variable"))
    }
    guard
      let textVariableNonNil = textVariableValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivInput(
      accessibility: { accessibilityValue.value }(),
      alignmentHorizontal: { alignmentHorizontalValue.value }(),
      alignmentVertical: { alignmentVerticalValue.value }(),
      alpha: { alphaValue.value }(),
      animators: { animatorsValue.value }(),
      autocapitalization: { autocapitalizationValue.value }(),
      background: { backgroundValue.value }(),
      border: { borderValue.value }(),
      captureFocusOnAction: { captureFocusOnActionValue.value }(),
      columnSpan: { columnSpanValue.value }(),
      disappearActions: { disappearActionsValue.value }(),
      enterKeyActions: { enterKeyActionsValue.value }(),
      enterKeyType: { enterKeyTypeValue.value }(),
      extensions: { extensionsValue.value }(),
      filters: { filtersValue.value }(),
      focus: { focusValue.value }(),
      fontFamily: { fontFamilyValue.value }(),
      fontSize: { fontSizeValue.value }(),
      fontSizeUnit: { fontSizeUnitValue.value }(),
      fontWeight: { fontWeightValue.value }(),
      fontWeightValue: { fontWeightValueValue.value }(),
      functions: { functionsValue.value }(),
      height: { heightValue.value }(),
      highlightColor: { highlightColorValue.value }(),
      hintColor: { hintColorValue.value }(),
      hintText: { hintTextValue.value }(),
      id: { idValue.value }(),
      isEnabled: { isEnabledValue.value }(),
      keyboardType: { keyboardTypeValue.value }(),
      layoutProvider: { layoutProviderValue.value }(),
      letterSpacing: { letterSpacingValue.value }(),
      lineHeight: { lineHeightValue.value }(),
      margins: { marginsValue.value }(),
      mask: { maskValue.value }(),
      maxLength: { maxLengthValue.value }(),
      maxVisibleLines: { maxVisibleLinesValue.value }(),
      nativeInterface: { nativeInterfaceValue.value }(),
      paddings: { paddingsValue.value }(),
      reuseId: { reuseIdValue.value }(),
      rowSpan: { rowSpanValue.value }(),
      selectAllOnFocus: { selectAllOnFocusValue.value }(),
      selectedActions: { selectedActionsValue.value }(),
      textAlignmentHorizontal: { textAlignmentHorizontalValue.value }(),
      textAlignmentVertical: { textAlignmentVerticalValue.value }(),
      textColor: { textColorValue.value }(),
      textVariable: { textVariableNonNil }(),
      tooltips: { tooltipsValue.value }(),
      transform: { transformValue.value }(),
      transitionChange: { transitionChangeValue.value }(),
      transitionIn: { transitionInValue.value }(),
      transitionOut: { transitionOutValue.value }(),
      transitionTriggers: { transitionTriggersValue.value }(),
      validators: { validatorsValue.value }(),
      variableTriggers: { variableTriggersValue.value }(),
      variables: { variablesValue.value }(),
      visibility: { visibilityValue.value }(),
      visibilityAction: { visibilityActionValue.value }(),
      visibilityActions: { visibilityActionsValue.value }(),
      width: { widthValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivInputTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivInputTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivInputTemplate(
      parent: nil,
      accessibility: accessibility ?? mergedParent.accessibility,
      alignmentHorizontal: alignmentHorizontal ?? mergedParent.alignmentHorizontal,
      alignmentVertical: alignmentVertical ?? mergedParent.alignmentVertical,
      alpha: alpha ?? mergedParent.alpha,
      animators: animators ?? mergedParent.animators,
      autocapitalization: autocapitalization ?? mergedParent.autocapitalization,
      background: background ?? mergedParent.background,
      border: border ?? mergedParent.border,
      captureFocusOnAction: captureFocusOnAction ?? mergedParent.captureFocusOnAction,
      columnSpan: columnSpan ?? mergedParent.columnSpan,
      disappearActions: disappearActions ?? mergedParent.disappearActions,
      enterKeyActions: enterKeyActions ?? mergedParent.enterKeyActions,
      enterKeyType: enterKeyType ?? mergedParent.enterKeyType,
      extensions: extensions ?? mergedParent.extensions,
      filters: filters ?? mergedParent.filters,
      focus: focus ?? mergedParent.focus,
      fontFamily: fontFamily ?? mergedParent.fontFamily,
      fontSize: fontSize ?? mergedParent.fontSize,
      fontSizeUnit: fontSizeUnit ?? mergedParent.fontSizeUnit,
      fontWeight: fontWeight ?? mergedParent.fontWeight,
      fontWeightValue: fontWeightValue ?? mergedParent.fontWeightValue,
      functions: functions ?? mergedParent.functions,
      height: height ?? mergedParent.height,
      highlightColor: highlightColor ?? mergedParent.highlightColor,
      hintColor: hintColor ?? mergedParent.hintColor,
      hintText: hintText ?? mergedParent.hintText,
      id: id ?? mergedParent.id,
      isEnabled: isEnabled ?? mergedParent.isEnabled,
      keyboardType: keyboardType ?? mergedParent.keyboardType,
      layoutProvider: layoutProvider ?? mergedParent.layoutProvider,
      letterSpacing: letterSpacing ?? mergedParent.letterSpacing,
      lineHeight: lineHeight ?? mergedParent.lineHeight,
      margins: margins ?? mergedParent.margins,
      mask: mask ?? mergedParent.mask,
      maxLength: maxLength ?? mergedParent.maxLength,
      maxVisibleLines: maxVisibleLines ?? mergedParent.maxVisibleLines,
      nativeInterface: nativeInterface ?? mergedParent.nativeInterface,
      paddings: paddings ?? mergedParent.paddings,
      reuseId: reuseId ?? mergedParent.reuseId,
      rowSpan: rowSpan ?? mergedParent.rowSpan,
      selectAllOnFocus: selectAllOnFocus ?? mergedParent.selectAllOnFocus,
      selectedActions: selectedActions ?? mergedParent.selectedActions,
      textAlignmentHorizontal: textAlignmentHorizontal ?? mergedParent.textAlignmentHorizontal,
      textAlignmentVertical: textAlignmentVertical ?? mergedParent.textAlignmentVertical,
      textColor: textColor ?? mergedParent.textColor,
      textVariable: textVariable ?? mergedParent.textVariable,
      tooltips: tooltips ?? mergedParent.tooltips,
      transform: transform ?? mergedParent.transform,
      transitionChange: transitionChange ?? mergedParent.transitionChange,
      transitionIn: transitionIn ?? mergedParent.transitionIn,
      transitionOut: transitionOut ?? mergedParent.transitionOut,
      transitionTriggers: transitionTriggers ?? mergedParent.transitionTriggers,
      validators: validators ?? mergedParent.validators,
      variableTriggers: variableTriggers ?? mergedParent.variableTriggers,
      variables: variables ?? mergedParent.variables,
      visibility: visibility ?? mergedParent.visibility,
      visibilityAction: visibilityAction ?? mergedParent.visibilityAction,
      visibilityActions: visibilityActions ?? mergedParent.visibilityActions,
      width: width ?? mergedParent.width
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivInputTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivInputTemplate(
      parent: nil,
      accessibility: merged.accessibility?.tryResolveParent(templates: templates),
      alignmentHorizontal: merged.alignmentHorizontal,
      alignmentVertical: merged.alignmentVertical,
      alpha: merged.alpha,
      animators: merged.animators?.tryResolveParent(templates: templates),
      autocapitalization: merged.autocapitalization,
      background: merged.background?.tryResolveParent(templates: templates),
      border: merged.border?.tryResolveParent(templates: templates),
      captureFocusOnAction: merged.captureFocusOnAction,
      columnSpan: merged.columnSpan,
      disappearActions: merged.disappearActions?.tryResolveParent(templates: templates),
      enterKeyActions: merged.enterKeyActions?.tryResolveParent(templates: templates),
      enterKeyType: merged.enterKeyType,
      extensions: merged.extensions?.tryResolveParent(templates: templates),
      filters: merged.filters?.tryResolveParent(templates: templates),
      focus: merged.focus?.tryResolveParent(templates: templates),
      fontFamily: merged.fontFamily,
      fontSize: merged.fontSize,
      fontSizeUnit: merged.fontSizeUnit,
      fontWeight: merged.fontWeight,
      fontWeightValue: merged.fontWeightValue,
      functions: merged.functions?.tryResolveParent(templates: templates),
      height: merged.height?.tryResolveParent(templates: templates),
      highlightColor: merged.highlightColor,
      hintColor: merged.hintColor,
      hintText: merged.hintText,
      id: merged.id,
      isEnabled: merged.isEnabled,
      keyboardType: merged.keyboardType,
      layoutProvider: merged.layoutProvider?.tryResolveParent(templates: templates),
      letterSpacing: merged.letterSpacing,
      lineHeight: merged.lineHeight,
      margins: merged.margins?.tryResolveParent(templates: templates),
      mask: merged.mask?.tryResolveParent(templates: templates),
      maxLength: merged.maxLength,
      maxVisibleLines: merged.maxVisibleLines,
      nativeInterface: merged.nativeInterface?.tryResolveParent(templates: templates),
      paddings: merged.paddings?.tryResolveParent(templates: templates),
      reuseId: merged.reuseId,
      rowSpan: merged.rowSpan,
      selectAllOnFocus: merged.selectAllOnFocus,
      selectedActions: merged.selectedActions?.tryResolveParent(templates: templates),
      textAlignmentHorizontal: merged.textAlignmentHorizontal,
      textAlignmentVertical: merged.textAlignmentVertical,
      textColor: merged.textColor,
      textVariable: merged.textVariable,
      tooltips: merged.tooltips?.tryResolveParent(templates: templates),
      transform: merged.transform?.tryResolveParent(templates: templates),
      transitionChange: merged.transitionChange?.tryResolveParent(templates: templates),
      transitionIn: merged.transitionIn?.tryResolveParent(templates: templates),
      transitionOut: merged.transitionOut?.tryResolveParent(templates: templates),
      transitionTriggers: merged.transitionTriggers,
      validators: merged.validators?.tryResolveParent(templates: templates),
      variableTriggers: merged.variableTriggers?.tryResolveParent(templates: templates),
      variables: merged.variables?.tryResolveParent(templates: templates),
      visibility: merged.visibility,
      visibilityAction: merged.visibilityAction?.tryResolveParent(templates: templates),
      visibilityActions: merged.visibilityActions?.tryResolveParent(templates: templates),
      width: merged.width?.tryResolveParent(templates: templates)
    )
  }
}
