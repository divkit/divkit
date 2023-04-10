// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivInputTemplate: TemplateValue {
  public final class NativeInterfaceTemplate: TemplateValue {
    public let color: Field<Expression<Color>>?

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      do {
        self.init(
          color: try dictionary.getOptionalExpressionField("color", transform: Color.color(withHexString:))
        )
      } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
        throw DeserializationError.invalidFieldRepresentation(field: "native_interface_template." + field, representation: representation)
      }
    }

    init(
      color: Field<Expression<Color>>? = nil
    ) {
      self.color = color
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: NativeInterfaceTemplate?) -> DeserializationResult<DivInput.NativeInterface> {
      let colorValue = parent?.color?.resolveValue(context: context, transform: Color.color(withHexString:)) ?? .noValue
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
        color: colorNonNil
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: NativeInterfaceTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivInput.NativeInterface> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var colorValue: DeserializationResult<Expression<Color>> = parent?.color?.value() ?? .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "color":
          colorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: colorValue)
        case parent?.color?.link:
          colorValue = colorValue.merged(with: deserialize(__dictValue, transform: Color.color(withHexString:)))
        default: break
        }
      }
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
        color: colorNonNil
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

  public typealias KeyboardType = DivInput.KeyboardType

  public static let type: String = "input"
  public let parent: String? // at least 1 char
  public let accessibility: Field<DivAccessibilityTemplate>?
  public let alignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>?
  public let alignmentVertical: Field<Expression<DivAlignmentVertical>>?
  public let alpha: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let background: Field<[DivBackgroundTemplate]>? // at least 1 elements
  public let border: Field<DivBorderTemplate>?
  public let columnSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let extensions: Field<[DivExtensionTemplate]>? // at least 1 elements
  public let focus: Field<DivFocusTemplate>?
  public let fontFamily: Field<Expression<DivFontFamily>>? // default value: text
  public let fontSize: Field<Expression<Int>>? // constraint: number >= 0; default value: 12
  public let fontSizeUnit: Field<Expression<DivSizeUnit>>? // default value: sp
  public let fontWeight: Field<Expression<DivFontWeight>>? // default value: regular
  public let height: Field<DivSizeTemplate>? // default value: .divWrapContentSize(DivWrapContentSize())
  public let highlightColor: Field<Expression<Color>>?
  public let hintColor: Field<Expression<Color>>? // default value: #73000000
  public let hintText: Field<Expression<String>>? // at least 1 char
  public let id: Field<String>? // at least 1 char
  public let keyboardType: Field<Expression<KeyboardType>>? // default value: multi_line_text
  public let letterSpacing: Field<Expression<Double>>? // default value: 0
  public let lineHeight: Field<Expression<Int>>? // constraint: number >= 0
  public let margins: Field<DivEdgeInsetsTemplate>?
  public let mask: Field<DivInputMaskTemplate>?
  public let maxVisibleLines: Field<Expression<Int>>? // constraint: number > 0
  public let nativeInterface: Field<NativeInterfaceTemplate>?
  public let paddings: Field<DivEdgeInsetsTemplate>?
  public let rawTextVariable: Field<String>? // at least 1 char
  public let rowSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let selectAllOnFocus: Field<Expression<Bool>>? // default value: false
  public let selectedActions: Field<[DivActionTemplate]>? // at least 1 elements
  public let textColor: Field<Expression<Color>>? // default value: #FF000000
  public let textVariable: Field<String>? // at least 1 char
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
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        accessibility: try dictionary.getOptionalField("accessibility", templateToType: templateToType),
        alignmentHorizontal: try dictionary.getOptionalExpressionField("alignment_horizontal"),
        alignmentVertical: try dictionary.getOptionalExpressionField("alignment_vertical"),
        alpha: try dictionary.getOptionalExpressionField("alpha"),
        background: try dictionary.getOptionalArray("background", templateToType: templateToType),
        border: try dictionary.getOptionalField("border", templateToType: templateToType),
        columnSpan: try dictionary.getOptionalExpressionField("column_span"),
        extensions: try dictionary.getOptionalArray("extensions", templateToType: templateToType),
        focus: try dictionary.getOptionalField("focus", templateToType: templateToType),
        fontFamily: try dictionary.getOptionalExpressionField("font_family"),
        fontSize: try dictionary.getOptionalExpressionField("font_size"),
        fontSizeUnit: try dictionary.getOptionalExpressionField("font_size_unit"),
        fontWeight: try dictionary.getOptionalExpressionField("font_weight"),
        height: try dictionary.getOptionalField("height", templateToType: templateToType),
        highlightColor: try dictionary.getOptionalExpressionField("highlight_color", transform: Color.color(withHexString:)),
        hintColor: try dictionary.getOptionalExpressionField("hint_color", transform: Color.color(withHexString:)),
        hintText: try dictionary.getOptionalExpressionField("hint_text"),
        id: try dictionary.getOptionalField("id"),
        keyboardType: try dictionary.getOptionalExpressionField("keyboard_type"),
        letterSpacing: try dictionary.getOptionalExpressionField("letter_spacing"),
        lineHeight: try dictionary.getOptionalExpressionField("line_height"),
        margins: try dictionary.getOptionalField("margins", templateToType: templateToType),
        mask: try dictionary.getOptionalField("mask", templateToType: templateToType),
        maxVisibleLines: try dictionary.getOptionalExpressionField("max_visible_lines"),
        nativeInterface: try dictionary.getOptionalField("native_interface", templateToType: templateToType),
        paddings: try dictionary.getOptionalField("paddings", templateToType: templateToType),
        rawTextVariable: try dictionary.getOptionalField("raw_text_variable"),
        rowSpan: try dictionary.getOptionalExpressionField("row_span"),
        selectAllOnFocus: try dictionary.getOptionalExpressionField("select_all_on_focus"),
        selectedActions: try dictionary.getOptionalArray("selected_actions", templateToType: templateToType),
        textColor: try dictionary.getOptionalExpressionField("text_color", transform: Color.color(withHexString:)),
        textVariable: try dictionary.getOptionalField("text_variable"),
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
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-input_template." + field, representation: representation)
    }
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
    extensions: Field<[DivExtensionTemplate]>? = nil,
    focus: Field<DivFocusTemplate>? = nil,
    fontFamily: Field<Expression<DivFontFamily>>? = nil,
    fontSize: Field<Expression<Int>>? = nil,
    fontSizeUnit: Field<Expression<DivSizeUnit>>? = nil,
    fontWeight: Field<Expression<DivFontWeight>>? = nil,
    height: Field<DivSizeTemplate>? = nil,
    highlightColor: Field<Expression<Color>>? = nil,
    hintColor: Field<Expression<Color>>? = nil,
    hintText: Field<Expression<String>>? = nil,
    id: Field<String>? = nil,
    keyboardType: Field<Expression<KeyboardType>>? = nil,
    letterSpacing: Field<Expression<Double>>? = nil,
    lineHeight: Field<Expression<Int>>? = nil,
    margins: Field<DivEdgeInsetsTemplate>? = nil,
    mask: Field<DivInputMaskTemplate>? = nil,
    maxVisibleLines: Field<Expression<Int>>? = nil,
    nativeInterface: Field<NativeInterfaceTemplate>? = nil,
    paddings: Field<DivEdgeInsetsTemplate>? = nil,
    rawTextVariable: Field<String>? = nil,
    rowSpan: Field<Expression<Int>>? = nil,
    selectAllOnFocus: Field<Expression<Bool>>? = nil,
    selectedActions: Field<[DivActionTemplate]>? = nil,
    textColor: Field<Expression<Color>>? = nil,
    textVariable: Field<String>? = nil,
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
    self.alignmentHorizontal = alignmentHorizontal
    self.alignmentVertical = alignmentVertical
    self.alpha = alpha
    self.background = background
    self.border = border
    self.columnSpan = columnSpan
    self.extensions = extensions
    self.focus = focus
    self.fontFamily = fontFamily
    self.fontSize = fontSize
    self.fontSizeUnit = fontSizeUnit
    self.fontWeight = fontWeight
    self.height = height
    self.highlightColor = highlightColor
    self.hintColor = hintColor
    self.hintText = hintText
    self.id = id
    self.keyboardType = keyboardType
    self.letterSpacing = letterSpacing
    self.lineHeight = lineHeight
    self.margins = margins
    self.mask = mask
    self.maxVisibleLines = maxVisibleLines
    self.nativeInterface = nativeInterface
    self.paddings = paddings
    self.rawTextVariable = rawTextVariable
    self.rowSpan = rowSpan
    self.selectAllOnFocus = selectAllOnFocus
    self.selectedActions = selectedActions
    self.textColor = textColor
    self.textVariable = textVariable
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

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivInputTemplate?) -> DeserializationResult<DivInput> {
    let accessibilityValue = parent?.accessibility?.resolveOptionalValue(context: context, validator: ResolvedValue.accessibilityValidator, useOnlyLinks: true) ?? .noValue
    let alignmentHorizontalValue = parent?.alignmentHorizontal?.resolveOptionalValue(context: context, validator: ResolvedValue.alignmentHorizontalValidator) ?? .noValue
    let alignmentVerticalValue = parent?.alignmentVertical?.resolveOptionalValue(context: context, validator: ResolvedValue.alignmentVerticalValidator) ?? .noValue
    let alphaValue = parent?.alpha?.resolveOptionalValue(context: context, validator: ResolvedValue.alphaValidator) ?? .noValue
    let backgroundValue = parent?.background?.resolveOptionalValue(context: context, validator: ResolvedValue.backgroundValidator, useOnlyLinks: true) ?? .noValue
    let borderValue = parent?.border?.resolveOptionalValue(context: context, validator: ResolvedValue.borderValidator, useOnlyLinks: true) ?? .noValue
    let columnSpanValue = parent?.columnSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.columnSpanValidator) ?? .noValue
    let extensionsValue = parent?.extensions?.resolveOptionalValue(context: context, validator: ResolvedValue.extensionsValidator, useOnlyLinks: true) ?? .noValue
    let focusValue = parent?.focus?.resolveOptionalValue(context: context, validator: ResolvedValue.focusValidator, useOnlyLinks: true) ?? .noValue
    let fontFamilyValue = parent?.fontFamily?.resolveOptionalValue(context: context, validator: ResolvedValue.fontFamilyValidator) ?? .noValue
    let fontSizeValue = parent?.fontSize?.resolveOptionalValue(context: context, validator: ResolvedValue.fontSizeValidator) ?? .noValue
    let fontSizeUnitValue = parent?.fontSizeUnit?.resolveOptionalValue(context: context, validator: ResolvedValue.fontSizeUnitValidator) ?? .noValue
    let fontWeightValue = parent?.fontWeight?.resolveOptionalValue(context: context, validator: ResolvedValue.fontWeightValidator) ?? .noValue
    let heightValue = parent?.height?.resolveOptionalValue(context: context, validator: ResolvedValue.heightValidator, useOnlyLinks: true) ?? .noValue
    let highlightColorValue = parent?.highlightColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:), validator: ResolvedValue.highlightColorValidator) ?? .noValue
    let hintColorValue = parent?.hintColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:), validator: ResolvedValue.hintColorValidator) ?? .noValue
    let hintTextValue = parent?.hintText?.resolveOptionalValue(context: context, validator: ResolvedValue.hintTextValidator) ?? .noValue
    let idValue = parent?.id?.resolveOptionalValue(context: context, validator: ResolvedValue.idValidator) ?? .noValue
    let keyboardTypeValue = parent?.keyboardType?.resolveOptionalValue(context: context, validator: ResolvedValue.keyboardTypeValidator) ?? .noValue
    let letterSpacingValue = parent?.letterSpacing?.resolveOptionalValue(context: context) ?? .noValue
    let lineHeightValue = parent?.lineHeight?.resolveOptionalValue(context: context, validator: ResolvedValue.lineHeightValidator) ?? .noValue
    let marginsValue = parent?.margins?.resolveOptionalValue(context: context, validator: ResolvedValue.marginsValidator, useOnlyLinks: true) ?? .noValue
    let maskValue = parent?.mask?.resolveOptionalValue(context: context, validator: ResolvedValue.maskValidator, useOnlyLinks: true) ?? .noValue
    let maxVisibleLinesValue = parent?.maxVisibleLines?.resolveOptionalValue(context: context, validator: ResolvedValue.maxVisibleLinesValidator) ?? .noValue
    let nativeInterfaceValue = parent?.nativeInterface?.resolveOptionalValue(context: context, validator: ResolvedValue.nativeInterfaceValidator, useOnlyLinks: true) ?? .noValue
    let paddingsValue = parent?.paddings?.resolveOptionalValue(context: context, validator: ResolvedValue.paddingsValidator, useOnlyLinks: true) ?? .noValue
    let rawTextVariableValue = parent?.rawTextVariable?.resolveOptionalValue(context: context, validator: ResolvedValue.rawTextVariableValidator) ?? .noValue
    let rowSpanValue = parent?.rowSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.rowSpanValidator) ?? .noValue
    let selectAllOnFocusValue = parent?.selectAllOnFocus?.resolveOptionalValue(context: context, validator: ResolvedValue.selectAllOnFocusValidator) ?? .noValue
    let selectedActionsValue = parent?.selectedActions?.resolveOptionalValue(context: context, validator: ResolvedValue.selectedActionsValidator, useOnlyLinks: true) ?? .noValue
    let textColorValue = parent?.textColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:), validator: ResolvedValue.textColorValidator) ?? .noValue
    let textVariableValue = parent?.textVariable?.resolveValue(context: context, validator: ResolvedValue.textVariableValidator) ?? .noValue
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
    var errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "accessibility", error: $0) },
      alignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_horizontal", error: $0) },
      alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      fontFamilyValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_family", error: $0) },
      fontSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size", error: $0) },
      fontSizeUnitValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size_unit", error: $0) },
      fontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      highlightColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "highlight_color", error: $0) },
      hintColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "hint_color", error: $0) },
      hintTextValue.errorsOrWarnings?.map { .nestedObjectError(field: "hint_text", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      keyboardTypeValue.errorsOrWarnings?.map { .nestedObjectError(field: "keyboard_type", error: $0) },
      letterSpacingValue.errorsOrWarnings?.map { .nestedObjectError(field: "letter_spacing", error: $0) },
      lineHeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "line_height", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      maskValue.errorsOrWarnings?.map { .nestedObjectError(field: "mask", error: $0) },
      maxVisibleLinesValue.errorsOrWarnings?.map { .nestedObjectError(field: "max_visible_lines", error: $0) },
      nativeInterfaceValue.errorsOrWarnings?.map { .nestedObjectError(field: "native_interface", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      rawTextVariableValue.errorsOrWarnings?.map { .nestedObjectError(field: "raw_text_variable", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      selectAllOnFocusValue.errorsOrWarnings?.map { .nestedObjectError(field: "select_all_on_focus", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
      textColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_color", error: $0) },
      textVariableValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_variable", error: $0) },
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
    if case .noValue = textVariableValue {
      errors.append(.requiredFieldIsMissing(field: "text_variable"))
    }
    guard
      let textVariableNonNil = textVariableValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivInput(
      accessibility: accessibilityValue.value,
      alignmentHorizontal: alignmentHorizontalValue.value,
      alignmentVertical: alignmentVerticalValue.value,
      alpha: alphaValue.value,
      background: backgroundValue.value,
      border: borderValue.value,
      columnSpan: columnSpanValue.value,
      extensions: extensionsValue.value,
      focus: focusValue.value,
      fontFamily: fontFamilyValue.value,
      fontSize: fontSizeValue.value,
      fontSizeUnit: fontSizeUnitValue.value,
      fontWeight: fontWeightValue.value,
      height: heightValue.value,
      highlightColor: highlightColorValue.value,
      hintColor: hintColorValue.value,
      hintText: hintTextValue.value,
      id: idValue.value,
      keyboardType: keyboardTypeValue.value,
      letterSpacing: letterSpacingValue.value,
      lineHeight: lineHeightValue.value,
      margins: marginsValue.value,
      mask: maskValue.value,
      maxVisibleLines: maxVisibleLinesValue.value,
      nativeInterface: nativeInterfaceValue.value,
      paddings: paddingsValue.value,
      rawTextVariable: rawTextVariableValue.value,
      rowSpan: rowSpanValue.value,
      selectAllOnFocus: selectAllOnFocusValue.value,
      selectedActions: selectedActionsValue.value,
      textColor: textColorValue.value,
      textVariable: textVariableNonNil,
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

  public static func resolveValue(context: TemplatesContext, parent: DivInputTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivInput> {
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
    var extensionsValue: DeserializationResult<[DivExtension]> = .noValue
    var focusValue: DeserializationResult<DivFocus> = .noValue
    var fontFamilyValue: DeserializationResult<Expression<DivFontFamily>> = parent?.fontFamily?.value() ?? .noValue
    var fontSizeValue: DeserializationResult<Expression<Int>> = parent?.fontSize?.value() ?? .noValue
    var fontSizeUnitValue: DeserializationResult<Expression<DivSizeUnit>> = parent?.fontSizeUnit?.value() ?? .noValue
    var fontWeightValue: DeserializationResult<Expression<DivFontWeight>> = parent?.fontWeight?.value() ?? .noValue
    var heightValue: DeserializationResult<DivSize> = .noValue
    var highlightColorValue: DeserializationResult<Expression<Color>> = parent?.highlightColor?.value() ?? .noValue
    var hintColorValue: DeserializationResult<Expression<Color>> = parent?.hintColor?.value() ?? .noValue
    var hintTextValue: DeserializationResult<Expression<String>> = parent?.hintText?.value() ?? .noValue
    var idValue: DeserializationResult<String> = parent?.id?.value(validatedBy: ResolvedValue.idValidator) ?? .noValue
    var keyboardTypeValue: DeserializationResult<Expression<DivInput.KeyboardType>> = parent?.keyboardType?.value() ?? .noValue
    var letterSpacingValue: DeserializationResult<Expression<Double>> = parent?.letterSpacing?.value() ?? .noValue
    var lineHeightValue: DeserializationResult<Expression<Int>> = parent?.lineHeight?.value() ?? .noValue
    var marginsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var maskValue: DeserializationResult<DivInputMask> = .noValue
    var maxVisibleLinesValue: DeserializationResult<Expression<Int>> = parent?.maxVisibleLines?.value() ?? .noValue
    var nativeInterfaceValue: DeserializationResult<DivInput.NativeInterface> = .noValue
    var paddingsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var rawTextVariableValue: DeserializationResult<String> = parent?.rawTextVariable?.value(validatedBy: ResolvedValue.rawTextVariableValidator) ?? .noValue
    var rowSpanValue: DeserializationResult<Expression<Int>> = parent?.rowSpan?.value() ?? .noValue
    var selectAllOnFocusValue: DeserializationResult<Expression<Bool>> = parent?.selectAllOnFocus?.value() ?? .noValue
    var selectedActionsValue: DeserializationResult<[DivAction]> = .noValue
    var textColorValue: DeserializationResult<Expression<Color>> = parent?.textColor?.value() ?? .noValue
    var textVariableValue: DeserializationResult<String> = parent?.textVariable?.value(validatedBy: ResolvedValue.textVariableValidator) ?? .noValue
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
      case "extensions":
        extensionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.extensionsValidator, type: DivExtensionTemplate.self).merged(with: extensionsValue)
      case "focus":
        focusValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.focusValidator, type: DivFocusTemplate.self).merged(with: focusValue)
      case "font_family":
        fontFamilyValue = deserialize(__dictValue, validator: ResolvedValue.fontFamilyValidator).merged(with: fontFamilyValue)
      case "font_size":
        fontSizeValue = deserialize(__dictValue, validator: ResolvedValue.fontSizeValidator).merged(with: fontSizeValue)
      case "font_size_unit":
        fontSizeUnitValue = deserialize(__dictValue, validator: ResolvedValue.fontSizeUnitValidator).merged(with: fontSizeUnitValue)
      case "font_weight":
        fontWeightValue = deserialize(__dictValue, validator: ResolvedValue.fontWeightValidator).merged(with: fontWeightValue)
      case "height":
        heightValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.heightValidator, type: DivSizeTemplate.self).merged(with: heightValue)
      case "highlight_color":
        highlightColorValue = deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.highlightColorValidator).merged(with: highlightColorValue)
      case "hint_color":
        hintColorValue = deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.hintColorValidator).merged(with: hintColorValue)
      case "hint_text":
        hintTextValue = deserialize(__dictValue, validator: ResolvedValue.hintTextValidator).merged(with: hintTextValue)
      case "id":
        idValue = deserialize(__dictValue, validator: ResolvedValue.idValidator).merged(with: idValue)
      case "keyboard_type":
        keyboardTypeValue = deserialize(__dictValue, validator: ResolvedValue.keyboardTypeValidator).merged(with: keyboardTypeValue)
      case "letter_spacing":
        letterSpacingValue = deserialize(__dictValue).merged(with: letterSpacingValue)
      case "line_height":
        lineHeightValue = deserialize(__dictValue, validator: ResolvedValue.lineHeightValidator).merged(with: lineHeightValue)
      case "margins":
        marginsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.marginsValidator, type: DivEdgeInsetsTemplate.self).merged(with: marginsValue)
      case "mask":
        maskValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.maskValidator, type: DivInputMaskTemplate.self).merged(with: maskValue)
      case "max_visible_lines":
        maxVisibleLinesValue = deserialize(__dictValue, validator: ResolvedValue.maxVisibleLinesValidator).merged(with: maxVisibleLinesValue)
      case "native_interface":
        nativeInterfaceValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.nativeInterfaceValidator, type: DivInputTemplate.NativeInterfaceTemplate.self).merged(with: nativeInterfaceValue)
      case "paddings":
        paddingsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.paddingsValidator, type: DivEdgeInsetsTemplate.self).merged(with: paddingsValue)
      case "raw_text_variable":
        rawTextVariableValue = deserialize(__dictValue, validator: ResolvedValue.rawTextVariableValidator).merged(with: rawTextVariableValue)
      case "row_span":
        rowSpanValue = deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator).merged(with: rowSpanValue)
      case "select_all_on_focus":
        selectAllOnFocusValue = deserialize(__dictValue, validator: ResolvedValue.selectAllOnFocusValidator).merged(with: selectAllOnFocusValue)
      case "selected_actions":
        selectedActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.selectedActionsValidator, type: DivActionTemplate.self).merged(with: selectedActionsValue)
      case "text_color":
        textColorValue = deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.textColorValidator).merged(with: textColorValue)
      case "text_variable":
        textVariableValue = deserialize(__dictValue, validator: ResolvedValue.textVariableValidator).merged(with: textVariableValue)
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
      case parent?.extensions?.link:
        extensionsValue = extensionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.extensionsValidator, type: DivExtensionTemplate.self))
      case parent?.focus?.link:
        focusValue = focusValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.focusValidator, type: DivFocusTemplate.self))
      case parent?.fontFamily?.link:
        fontFamilyValue = fontFamilyValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.fontFamilyValidator))
      case parent?.fontSize?.link:
        fontSizeValue = fontSizeValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.fontSizeValidator))
      case parent?.fontSizeUnit?.link:
        fontSizeUnitValue = fontSizeUnitValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.fontSizeUnitValidator))
      case parent?.fontWeight?.link:
        fontWeightValue = fontWeightValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.fontWeightValidator))
      case parent?.height?.link:
        heightValue = heightValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.heightValidator, type: DivSizeTemplate.self))
      case parent?.highlightColor?.link:
        highlightColorValue = highlightColorValue.merged(with: deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.highlightColorValidator))
      case parent?.hintColor?.link:
        hintColorValue = hintColorValue.merged(with: deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.hintColorValidator))
      case parent?.hintText?.link:
        hintTextValue = hintTextValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.hintTextValidator))
      case parent?.id?.link:
        idValue = idValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.idValidator))
      case parent?.keyboardType?.link:
        keyboardTypeValue = keyboardTypeValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.keyboardTypeValidator))
      case parent?.letterSpacing?.link:
        letterSpacingValue = letterSpacingValue.merged(with: deserialize(__dictValue))
      case parent?.lineHeight?.link:
        lineHeightValue = lineHeightValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.lineHeightValidator))
      case parent?.margins?.link:
        marginsValue = marginsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.marginsValidator, type: DivEdgeInsetsTemplate.self))
      case parent?.mask?.link:
        maskValue = maskValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.maskValidator, type: DivInputMaskTemplate.self))
      case parent?.maxVisibleLines?.link:
        maxVisibleLinesValue = maxVisibleLinesValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.maxVisibleLinesValidator))
      case parent?.nativeInterface?.link:
        nativeInterfaceValue = nativeInterfaceValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.nativeInterfaceValidator, type: DivInputTemplate.NativeInterfaceTemplate.self))
      case parent?.paddings?.link:
        paddingsValue = paddingsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.paddingsValidator, type: DivEdgeInsetsTemplate.self))
      case parent?.rawTextVariable?.link:
        rawTextVariableValue = rawTextVariableValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.rawTextVariableValidator))
      case parent?.rowSpan?.link:
        rowSpanValue = rowSpanValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator))
      case parent?.selectAllOnFocus?.link:
        selectAllOnFocusValue = selectAllOnFocusValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.selectAllOnFocusValidator))
      case parent?.selectedActions?.link:
        selectedActionsValue = selectedActionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.selectedActionsValidator, type: DivActionTemplate.self))
      case parent?.textColor?.link:
        textColorValue = textColorValue.merged(with: deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.textColorValidator))
      case parent?.textVariable?.link:
        textVariableValue = textVariableValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.textVariableValidator))
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
      backgroundValue = backgroundValue.merged(with: parent.background?.resolveOptionalValue(context: context, validator: ResolvedValue.backgroundValidator, useOnlyLinks: true))
      borderValue = borderValue.merged(with: parent.border?.resolveOptionalValue(context: context, validator: ResolvedValue.borderValidator, useOnlyLinks: true))
      extensionsValue = extensionsValue.merged(with: parent.extensions?.resolveOptionalValue(context: context, validator: ResolvedValue.extensionsValidator, useOnlyLinks: true))
      focusValue = focusValue.merged(with: parent.focus?.resolveOptionalValue(context: context, validator: ResolvedValue.focusValidator, useOnlyLinks: true))
      heightValue = heightValue.merged(with: parent.height?.resolveOptionalValue(context: context, validator: ResolvedValue.heightValidator, useOnlyLinks: true))
      marginsValue = marginsValue.merged(with: parent.margins?.resolveOptionalValue(context: context, validator: ResolvedValue.marginsValidator, useOnlyLinks: true))
      maskValue = maskValue.merged(with: parent.mask?.resolveOptionalValue(context: context, validator: ResolvedValue.maskValidator, useOnlyLinks: true))
      nativeInterfaceValue = nativeInterfaceValue.merged(with: parent.nativeInterface?.resolveOptionalValue(context: context, validator: ResolvedValue.nativeInterfaceValidator, useOnlyLinks: true))
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
    var errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "accessibility", error: $0) },
      alignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_horizontal", error: $0) },
      alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      fontFamilyValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_family", error: $0) },
      fontSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size", error: $0) },
      fontSizeUnitValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size_unit", error: $0) },
      fontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      highlightColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "highlight_color", error: $0) },
      hintColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "hint_color", error: $0) },
      hintTextValue.errorsOrWarnings?.map { .nestedObjectError(field: "hint_text", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      keyboardTypeValue.errorsOrWarnings?.map { .nestedObjectError(field: "keyboard_type", error: $0) },
      letterSpacingValue.errorsOrWarnings?.map { .nestedObjectError(field: "letter_spacing", error: $0) },
      lineHeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "line_height", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      maskValue.errorsOrWarnings?.map { .nestedObjectError(field: "mask", error: $0) },
      maxVisibleLinesValue.errorsOrWarnings?.map { .nestedObjectError(field: "max_visible_lines", error: $0) },
      nativeInterfaceValue.errorsOrWarnings?.map { .nestedObjectError(field: "native_interface", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      rawTextVariableValue.errorsOrWarnings?.map { .nestedObjectError(field: "raw_text_variable", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      selectAllOnFocusValue.errorsOrWarnings?.map { .nestedObjectError(field: "select_all_on_focus", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
      textColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_color", error: $0) },
      textVariableValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_variable", error: $0) },
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
    if case .noValue = textVariableValue {
      errors.append(.requiredFieldIsMissing(field: "text_variable"))
    }
    guard
      let textVariableNonNil = textVariableValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivInput(
      accessibility: accessibilityValue.value,
      alignmentHorizontal: alignmentHorizontalValue.value,
      alignmentVertical: alignmentVerticalValue.value,
      alpha: alphaValue.value,
      background: backgroundValue.value,
      border: borderValue.value,
      columnSpan: columnSpanValue.value,
      extensions: extensionsValue.value,
      focus: focusValue.value,
      fontFamily: fontFamilyValue.value,
      fontSize: fontSizeValue.value,
      fontSizeUnit: fontSizeUnitValue.value,
      fontWeight: fontWeightValue.value,
      height: heightValue.value,
      highlightColor: highlightColorValue.value,
      hintColor: hintColorValue.value,
      hintText: hintTextValue.value,
      id: idValue.value,
      keyboardType: keyboardTypeValue.value,
      letterSpacing: letterSpacingValue.value,
      lineHeight: lineHeightValue.value,
      margins: marginsValue.value,
      mask: maskValue.value,
      maxVisibleLines: maxVisibleLinesValue.value,
      nativeInterface: nativeInterfaceValue.value,
      paddings: paddingsValue.value,
      rawTextVariable: rawTextVariableValue.value,
      rowSpan: rowSpanValue.value,
      selectAllOnFocus: selectAllOnFocusValue.value,
      selectedActions: selectedActionsValue.value,
      textColor: textColorValue.value,
      textVariable: textVariableNonNil,
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
      background: background ?? mergedParent.background,
      border: border ?? mergedParent.border,
      columnSpan: columnSpan ?? mergedParent.columnSpan,
      extensions: extensions ?? mergedParent.extensions,
      focus: focus ?? mergedParent.focus,
      fontFamily: fontFamily ?? mergedParent.fontFamily,
      fontSize: fontSize ?? mergedParent.fontSize,
      fontSizeUnit: fontSizeUnit ?? mergedParent.fontSizeUnit,
      fontWeight: fontWeight ?? mergedParent.fontWeight,
      height: height ?? mergedParent.height,
      highlightColor: highlightColor ?? mergedParent.highlightColor,
      hintColor: hintColor ?? mergedParent.hintColor,
      hintText: hintText ?? mergedParent.hintText,
      id: id ?? mergedParent.id,
      keyboardType: keyboardType ?? mergedParent.keyboardType,
      letterSpacing: letterSpacing ?? mergedParent.letterSpacing,
      lineHeight: lineHeight ?? mergedParent.lineHeight,
      margins: margins ?? mergedParent.margins,
      mask: mask ?? mergedParent.mask,
      maxVisibleLines: maxVisibleLines ?? mergedParent.maxVisibleLines,
      nativeInterface: nativeInterface ?? mergedParent.nativeInterface,
      paddings: paddings ?? mergedParent.paddings,
      rawTextVariable: rawTextVariable ?? mergedParent.rawTextVariable,
      rowSpan: rowSpan ?? mergedParent.rowSpan,
      selectAllOnFocus: selectAllOnFocus ?? mergedParent.selectAllOnFocus,
      selectedActions: selectedActions ?? mergedParent.selectedActions,
      textColor: textColor ?? mergedParent.textColor,
      textVariable: textVariable ?? mergedParent.textVariable,
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

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivInputTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivInputTemplate(
      parent: nil,
      accessibility: merged.accessibility?.tryResolveParent(templates: templates),
      alignmentHorizontal: merged.alignmentHorizontal,
      alignmentVertical: merged.alignmentVertical,
      alpha: merged.alpha,
      background: merged.background?.tryResolveParent(templates: templates),
      border: merged.border?.tryResolveParent(templates: templates),
      columnSpan: merged.columnSpan,
      extensions: merged.extensions?.tryResolveParent(templates: templates),
      focus: merged.focus?.tryResolveParent(templates: templates),
      fontFamily: merged.fontFamily,
      fontSize: merged.fontSize,
      fontSizeUnit: merged.fontSizeUnit,
      fontWeight: merged.fontWeight,
      height: merged.height?.tryResolveParent(templates: templates),
      highlightColor: merged.highlightColor,
      hintColor: merged.hintColor,
      hintText: merged.hintText,
      id: merged.id,
      keyboardType: merged.keyboardType,
      letterSpacing: merged.letterSpacing,
      lineHeight: merged.lineHeight,
      margins: merged.margins?.tryResolveParent(templates: templates),
      mask: merged.mask?.tryResolveParent(templates: templates),
      maxVisibleLines: merged.maxVisibleLines,
      nativeInterface: merged.nativeInterface?.tryResolveParent(templates: templates),
      paddings: merged.paddings?.tryResolveParent(templates: templates),
      rawTextVariable: merged.rawTextVariable,
      rowSpan: merged.rowSpan,
      selectAllOnFocus: merged.selectAllOnFocus,
      selectedActions: merged.selectedActions?.tryResolveParent(templates: templates),
      textColor: merged.textColor,
      textVariable: merged.textVariable,
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
