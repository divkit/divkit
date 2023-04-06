// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivInput: DivBase {
  @frozen
  public enum KeyboardType: String, CaseIterable {
    case singleLineText = "single_line_text"
    case multiLineText = "multi_line_text"
    case phone = "phone"
    case number = "number"
    case email = "email"
    case uri = "uri"
  }

  public final class NativeInterface {
    public let color: Expression<Color>

    public func resolveColor(_ resolver: ExpressionResolver) -> Color? {
      resolver.resolveStringBasedValue(expression: color, initializer: Color.color(withHexString:))
    }

    init(
      color: Expression<Color>
    ) {
      self.color = color
    }
  }

  public static let type: String = "input"
  public let accessibility: DivAccessibility
  public let alignmentHorizontal: Expression<DivAlignmentHorizontal>?
  public let alignmentVertical: Expression<DivAlignmentVertical>?
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let background: [DivBackground]? // at least 1 elements
  public let border: DivBorder
  public let columnSpan: Expression<Int>? // constraint: number >= 0
  public let extensions: [DivExtension]? // at least 1 elements
  public let focus: DivFocus?
  public let fontFamily: Expression<DivFontFamily> // default value: text
  public let fontSize: Expression<Int> // constraint: number >= 0; default value: 12
  public let fontSizeUnit: Expression<DivSizeUnit> // default value: sp
  public let fontWeight: Expression<DivFontWeight> // default value: regular
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let highlightColor: Expression<Color>?
  public let hintColor: Expression<Color> // default value: #73000000
  public let hintText: Expression<String>? // at least 1 char
  public let id: String? // at least 1 char
  public let keyboardType: Expression<KeyboardType> // default value: multi_line_text
  public let letterSpacing: Expression<Double> // default value: 0
  public let lineHeight: Expression<Int>? // constraint: number >= 0
  public let margins: DivEdgeInsets
  public let mask: DivInputMask?
  public let maxVisibleLines: Expression<Int>? // constraint: number > 0
  public let nativeInterface: NativeInterface?
  public let paddings: DivEdgeInsets
  public let rawTextVariable: String? // at least 1 char
  public let rowSpan: Expression<Int>? // constraint: number >= 0
  public let selectAllOnFocus: Expression<Bool> // default value: false
  public let selectedActions: [DivAction]? // at least 1 elements
  public let textColor: Expression<Color> // default value: #FF000000
  public let textVariable: String // at least 1 char
  public let tooltips: [DivTooltip]? // at least 1 elements
  public let transform: DivTransform
  public let transitionChange: DivChangeTransition?
  public let transitionIn: DivAppearanceTransition?
  public let transitionOut: DivAppearanceTransition?
  public let transitionTriggers: [DivTransitionTrigger]? // at least 1 elements
  public let visibility: Expression<DivVisibility> // default value: visible
  public let visibilityAction: DivVisibilityAction?
  public let visibilityActions: [DivVisibilityAction]? // at least 1 elements
  public let width: DivSize // default value: .divMatchParentSize(DivMatchParentSize())

  public func resolveAlignmentHorizontal(_ resolver: ExpressionResolver) -> DivAlignmentHorizontal? {
    resolver.resolveStringBasedValue(expression: alignmentHorizontal, initializer: DivAlignmentHorizontal.init(rawValue:))
  }

  public func resolveAlignmentVertical(_ resolver: ExpressionResolver) -> DivAlignmentVertical? {
    resolver.resolveStringBasedValue(expression: alignmentVertical, initializer: DivAlignmentVertical.init(rawValue:))
  }

  public func resolveAlpha(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumericValue(expression: alpha) ?? 1.0
  }

  public func resolveColumnSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: columnSpan)
  }

  public func resolveFontFamily(_ resolver: ExpressionResolver) -> DivFontFamily {
    resolver.resolveStringBasedValue(expression: fontFamily, initializer: DivFontFamily.init(rawValue:)) ?? DivFontFamily.text
  }

  public func resolveFontSize(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: fontSize) ?? 12
  }

  public func resolveFontSizeUnit(_ resolver: ExpressionResolver) -> DivSizeUnit {
    resolver.resolveStringBasedValue(expression: fontSizeUnit, initializer: DivSizeUnit.init(rawValue:)) ?? DivSizeUnit.sp
  }

  public func resolveFontWeight(_ resolver: ExpressionResolver) -> DivFontWeight {
    resolver.resolveStringBasedValue(expression: fontWeight, initializer: DivFontWeight.init(rawValue:)) ?? DivFontWeight.regular
  }

  public func resolveHighlightColor(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveStringBasedValue(expression: highlightColor, initializer: Color.color(withHexString:))
  }

  public func resolveHintColor(_ resolver: ExpressionResolver) -> Color {
    resolver.resolveStringBasedValue(expression: hintColor, initializer: Color.color(withHexString:)) ?? Color.colorWithARGBHexCode(0x73000000)
  }

  public func resolveHintText(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveStringBasedValue(expression: hintText, initializer: { $0 })
  }

  public func resolveKeyboardType(_ resolver: ExpressionResolver) -> KeyboardType {
    resolver.resolveStringBasedValue(expression: keyboardType, initializer: KeyboardType.init(rawValue:)) ?? KeyboardType.multiLineText
  }

  public func resolveLetterSpacing(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumericValue(expression: letterSpacing) ?? 0
  }

  public func resolveLineHeight(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: lineHeight)
  }

  public func resolveMaxVisibleLines(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: maxVisibleLines)
  }

  public func resolveRowSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: rowSpan)
  }

  public func resolveSelectAllOnFocus(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumericValue(expression: selectAllOnFocus) ?? false
  }

  public func resolveTextColor(_ resolver: ExpressionResolver) -> Color {
    resolver.resolveStringBasedValue(expression: textColor, initializer: Color.color(withHexString:)) ?? Color.colorWithARGBHexCode(0xFF000000)
  }

  public func resolveVisibility(_ resolver: ExpressionResolver) -> DivVisibility {
    resolver.resolveStringBasedValue(expression: visibility, initializer: DivVisibility.init(rawValue:)) ?? DivVisibility.visible
  }

  static let accessibilityValidator: AnyValueValidator<DivAccessibility> =
    makeNoOpValueValidator()

  static let alignmentHorizontalValidator: AnyValueValidator<DivAlignmentHorizontal> =
    makeNoOpValueValidator()

  static let alignmentVerticalValidator: AnyValueValidator<DivAlignmentVertical> =
    makeNoOpValueValidator()

  static let alphaValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 >= 0.0 && $0 <= 1.0 })

  static let backgroundValidator: AnyArrayValueValidator<DivBackground> =
    makeArrayValidator(minItems: 1)

  static let borderValidator: AnyValueValidator<DivBorder> =
    makeNoOpValueValidator()

  static let columnSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let extensionsValidator: AnyArrayValueValidator<DivExtension> =
    makeArrayValidator(minItems: 1)

  static let focusValidator: AnyValueValidator<DivFocus> =
    makeNoOpValueValidator()

  static let fontFamilyValidator: AnyValueValidator<DivFontFamily> =
    makeNoOpValueValidator()

  static let fontSizeValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let fontSizeUnitValidator: AnyValueValidator<DivSizeUnit> =
    makeNoOpValueValidator()

  static let fontWeightValidator: AnyValueValidator<DivFontWeight> =
    makeNoOpValueValidator()

  static let heightValidator: AnyValueValidator<DivSize> =
    makeNoOpValueValidator()

  static let highlightColorValidator: AnyValueValidator<Color> =
    makeNoOpValueValidator()

  static let hintColorValidator: AnyValueValidator<Color> =
    makeNoOpValueValidator()

  static let hintTextValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let idValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let keyboardTypeValidator: AnyValueValidator<DivInput.KeyboardType> =
    makeNoOpValueValidator()

  static let lineHeightValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let marginsValidator: AnyValueValidator<DivEdgeInsets> =
    makeNoOpValueValidator()

  static let maskValidator: AnyValueValidator<DivInputMask> =
    makeNoOpValueValidator()

  static let maxVisibleLinesValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 > 0 })

  static let nativeInterfaceValidator: AnyValueValidator<DivInput.NativeInterface> =
    makeNoOpValueValidator()

  static let paddingsValidator: AnyValueValidator<DivEdgeInsets> =
    makeNoOpValueValidator()

  static let rawTextVariableValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let rowSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let selectAllOnFocusValidator: AnyValueValidator<Bool> =
    makeNoOpValueValidator()

  static let selectedActionsValidator: AnyArrayValueValidator<DivAction> =
    makeArrayValidator(minItems: 1)

  static let textColorValidator: AnyValueValidator<Color> =
    makeNoOpValueValidator()

  static let textVariableValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let tooltipsValidator: AnyArrayValueValidator<DivTooltip> =
    makeArrayValidator(minItems: 1)

  static let transformValidator: AnyValueValidator<DivTransform> =
    makeNoOpValueValidator()

  static let transitionChangeValidator: AnyValueValidator<DivChangeTransition> =
    makeNoOpValueValidator()

  static let transitionInValidator: AnyValueValidator<DivAppearanceTransition> =
    makeNoOpValueValidator()

  static let transitionOutValidator: AnyValueValidator<DivAppearanceTransition> =
    makeNoOpValueValidator()

  static let transitionTriggersValidator: AnyArrayValueValidator<DivTransitionTrigger> =
    makeArrayValidator(minItems: 1)

  static let visibilityValidator: AnyValueValidator<DivVisibility> =
    makeNoOpValueValidator()

  static let visibilityActionValidator: AnyValueValidator<DivVisibilityAction> =
    makeNoOpValueValidator()

  static let visibilityActionsValidator: AnyArrayValueValidator<DivVisibilityAction> =
    makeArrayValidator(minItems: 1)

  static let widthValidator: AnyValueValidator<DivSize> =
    makeNoOpValueValidator()

  init(
    accessibility: DivAccessibility? = nil,
    alignmentHorizontal: Expression<DivAlignmentHorizontal>? = nil,
    alignmentVertical: Expression<DivAlignmentVertical>? = nil,
    alpha: Expression<Double>? = nil,
    background: [DivBackground]? = nil,
    border: DivBorder? = nil,
    columnSpan: Expression<Int>? = nil,
    extensions: [DivExtension]? = nil,
    focus: DivFocus? = nil,
    fontFamily: Expression<DivFontFamily>? = nil,
    fontSize: Expression<Int>? = nil,
    fontSizeUnit: Expression<DivSizeUnit>? = nil,
    fontWeight: Expression<DivFontWeight>? = nil,
    height: DivSize? = nil,
    highlightColor: Expression<Color>? = nil,
    hintColor: Expression<Color>? = nil,
    hintText: Expression<String>? = nil,
    id: String? = nil,
    keyboardType: Expression<KeyboardType>? = nil,
    letterSpacing: Expression<Double>? = nil,
    lineHeight: Expression<Int>? = nil,
    margins: DivEdgeInsets? = nil,
    mask: DivInputMask? = nil,
    maxVisibleLines: Expression<Int>? = nil,
    nativeInterface: NativeInterface? = nil,
    paddings: DivEdgeInsets? = nil,
    rawTextVariable: String? = nil,
    rowSpan: Expression<Int>? = nil,
    selectAllOnFocus: Expression<Bool>? = nil,
    selectedActions: [DivAction]? = nil,
    textColor: Expression<Color>? = nil,
    textVariable: String,
    tooltips: [DivTooltip]? = nil,
    transform: DivTransform? = nil,
    transitionChange: DivChangeTransition? = nil,
    transitionIn: DivAppearanceTransition? = nil,
    transitionOut: DivAppearanceTransition? = nil,
    transitionTriggers: [DivTransitionTrigger]? = nil,
    visibility: Expression<DivVisibility>? = nil,
    visibilityAction: DivVisibilityAction? = nil,
    visibilityActions: [DivVisibilityAction]? = nil,
    width: DivSize? = nil
  ) {
    self.accessibility = accessibility ?? DivAccessibility()
    self.alignmentHorizontal = alignmentHorizontal
    self.alignmentVertical = alignmentVertical
    self.alpha = alpha ?? .value(1.0)
    self.background = background
    self.border = border ?? DivBorder()
    self.columnSpan = columnSpan
    self.extensions = extensions
    self.focus = focus
    self.fontFamily = fontFamily ?? .value(.text)
    self.fontSize = fontSize ?? .value(12)
    self.fontSizeUnit = fontSizeUnit ?? .value(.sp)
    self.fontWeight = fontWeight ?? .value(.regular)
    self.height = height ?? .divWrapContentSize(DivWrapContentSize())
    self.highlightColor = highlightColor
    self.hintColor = hintColor ?? .value(Color.colorWithARGBHexCode(0x73000000))
    self.hintText = hintText
    self.id = id
    self.keyboardType = keyboardType ?? .value(.multiLineText)
    self.letterSpacing = letterSpacing ?? .value(0)
    self.lineHeight = lineHeight
    self.margins = margins ?? DivEdgeInsets()
    self.mask = mask
    self.maxVisibleLines = maxVisibleLines
    self.nativeInterface = nativeInterface
    self.paddings = paddings ?? DivEdgeInsets()
    self.rawTextVariable = rawTextVariable
    self.rowSpan = rowSpan
    self.selectAllOnFocus = selectAllOnFocus ?? .value(false)
    self.selectedActions = selectedActions
    self.textColor = textColor ?? .value(Color.colorWithARGBHexCode(0xFF000000))
    self.textVariable = textVariable
    self.tooltips = tooltips
    self.transform = transform ?? DivTransform()
    self.transitionChange = transitionChange
    self.transitionIn = transitionIn
    self.transitionOut = transitionOut
    self.transitionTriggers = transitionTriggers
    self.visibility = visibility ?? .value(.visible)
    self.visibilityAction = visibilityAction
    self.visibilityActions = visibilityActions
    self.width = width ?? .divMatchParentSize(DivMatchParentSize())
  }
}

#if DEBUG
extension DivInput: Equatable {
  public static func ==(lhs: DivInput, rhs: DivInput) -> Bool {
    guard
      lhs.accessibility == rhs.accessibility,
      lhs.alignmentHorizontal == rhs.alignmentHorizontal,
      lhs.alignmentVertical == rhs.alignmentVertical
    else {
      return false
    }
    guard
      lhs.alpha == rhs.alpha,
      lhs.background == rhs.background,
      lhs.border == rhs.border
    else {
      return false
    }
    guard
      lhs.columnSpan == rhs.columnSpan,
      lhs.extensions == rhs.extensions,
      lhs.focus == rhs.focus
    else {
      return false
    }
    guard
      lhs.fontFamily == rhs.fontFamily,
      lhs.fontSize == rhs.fontSize,
      lhs.fontSizeUnit == rhs.fontSizeUnit
    else {
      return false
    }
    guard
      lhs.fontWeight == rhs.fontWeight,
      lhs.height == rhs.height,
      lhs.highlightColor == rhs.highlightColor
    else {
      return false
    }
    guard
      lhs.hintColor == rhs.hintColor,
      lhs.hintText == rhs.hintText,
      lhs.id == rhs.id
    else {
      return false
    }
    guard
      lhs.keyboardType == rhs.keyboardType,
      lhs.letterSpacing == rhs.letterSpacing,
      lhs.lineHeight == rhs.lineHeight
    else {
      return false
    }
    guard
      lhs.margins == rhs.margins,
      lhs.mask == rhs.mask,
      lhs.maxVisibleLines == rhs.maxVisibleLines
    else {
      return false
    }
    guard
      lhs.nativeInterface == rhs.nativeInterface,
      lhs.paddings == rhs.paddings,
      lhs.rawTextVariable == rhs.rawTextVariable
    else {
      return false
    }
    guard
      lhs.rowSpan == rhs.rowSpan,
      lhs.selectAllOnFocus == rhs.selectAllOnFocus,
      lhs.selectedActions == rhs.selectedActions
    else {
      return false
    }
    guard
      lhs.textColor == rhs.textColor,
      lhs.textVariable == rhs.textVariable,
      lhs.tooltips == rhs.tooltips
    else {
      return false
    }
    guard
      lhs.transform == rhs.transform,
      lhs.transitionChange == rhs.transitionChange,
      lhs.transitionIn == rhs.transitionIn
    else {
      return false
    }
    guard
      lhs.transitionOut == rhs.transitionOut,
      lhs.transitionTriggers == rhs.transitionTriggers,
      lhs.visibility == rhs.visibility
    else {
      return false
    }
    guard
      lhs.visibilityAction == rhs.visibilityAction,
      lhs.visibilityActions == rhs.visibilityActions,
      lhs.width == rhs.width
    else {
      return false
    }
    return true
  }
}
#endif

extension DivInput: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["accessibility"] = accessibility.toDictionary()
    result["alignment_horizontal"] = alignmentHorizontal?.toValidSerializationValue()
    result["alignment_vertical"] = alignmentVertical?.toValidSerializationValue()
    result["alpha"] = alpha.toValidSerializationValue()
    result["background"] = background?.map { $0.toDictionary() }
    result["border"] = border.toDictionary()
    result["column_span"] = columnSpan?.toValidSerializationValue()
    result["extensions"] = extensions?.map { $0.toDictionary() }
    result["focus"] = focus?.toDictionary()
    result["font_family"] = fontFamily.toValidSerializationValue()
    result["font_size"] = fontSize.toValidSerializationValue()
    result["font_size_unit"] = fontSizeUnit.toValidSerializationValue()
    result["font_weight"] = fontWeight.toValidSerializationValue()
    result["height"] = height.toDictionary()
    result["highlight_color"] = highlightColor?.toValidSerializationValue()
    result["hint_color"] = hintColor.toValidSerializationValue()
    result["hint_text"] = hintText?.toValidSerializationValue()
    result["id"] = id
    result["keyboard_type"] = keyboardType.toValidSerializationValue()
    result["letter_spacing"] = letterSpacing.toValidSerializationValue()
    result["line_height"] = lineHeight?.toValidSerializationValue()
    result["margins"] = margins.toDictionary()
    result["mask"] = mask?.toDictionary()
    result["max_visible_lines"] = maxVisibleLines?.toValidSerializationValue()
    result["native_interface"] = nativeInterface?.toDictionary()
    result["paddings"] = paddings.toDictionary()
    result["raw_text_variable"] = rawTextVariable
    result["row_span"] = rowSpan?.toValidSerializationValue()
    result["select_all_on_focus"] = selectAllOnFocus.toValidSerializationValue()
    result["selected_actions"] = selectedActions?.map { $0.toDictionary() }
    result["text_color"] = textColor.toValidSerializationValue()
    result["text_variable"] = textVariable
    result["tooltips"] = tooltips?.map { $0.toDictionary() }
    result["transform"] = transform.toDictionary()
    result["transition_change"] = transitionChange?.toDictionary()
    result["transition_in"] = transitionIn?.toDictionary()
    result["transition_out"] = transitionOut?.toDictionary()
    result["transition_triggers"] = transitionTriggers?.map { $0.rawValue }
    result["visibility"] = visibility.toValidSerializationValue()
    result["visibility_action"] = visibilityAction?.toDictionary()
    result["visibility_actions"] = visibilityActions?.map { $0.toDictionary() }
    result["width"] = width.toDictionary()
    return result
  }
}

#if DEBUG
extension DivInput.NativeInterface: Equatable {
  public static func ==(lhs: DivInput.NativeInterface, rhs: DivInput.NativeInterface) -> Bool {
    guard
      lhs.color == rhs.color
    else {
      return false
    }
    return true
  }
}
#endif

extension DivInput.NativeInterface: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["color"] = color.toValidSerializationValue()
    return result
  }
}
