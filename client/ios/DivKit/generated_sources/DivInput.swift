// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivInput: DivBase {
  @frozen
  public enum KeyboardType: String, CaseIterable {
    case singleLineText = "single_line_text"
    case multiLineText = "multi_line_text"
    case phone = "phone"
    case number = "number"
    case email = "email"
    case uri = "uri"
    case password = "password"
  }

  public final class NativeInterface {
    public let color: Expression<Color>

    public func resolveColor(_ resolver: ExpressionResolver) -> Color? {
      resolver.resolveColor(color)
    }

    init(
      color: Expression<Color>
    ) {
      self.color = color
    }
  }

  public static let type: String = "input"
  public let accessibility: DivAccessibility?
  public let alignmentHorizontal: Expression<DivAlignmentHorizontal>?
  public let alignmentVertical: Expression<DivAlignmentVertical>?
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let background: [DivBackground]?
  public let border: DivBorder?
  public let columnSpan: Expression<Int>? // constraint: number >= 0
  public let disappearActions: [DivDisappearAction]?
  public let extensions: [DivExtension]?
  public let focus: DivFocus?
  public let fontFamily: Expression<String>?
  public let fontSize: Expression<Int> // constraint: number >= 0; default value: 12
  public let fontSizeUnit: Expression<DivSizeUnit> // default value: sp
  public let fontWeight: Expression<DivFontWeight> // default value: regular
  public let fontWeightValue: Expression<Int>? // constraint: number > 0
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let highlightColor: Expression<Color>?
  public let hintColor: Expression<Color> // default value: #73000000
  public let hintText: Expression<String>?
  public let id: String?
  public let isEnabled: Expression<Bool> // default value: true
  public let keyboardType: Expression<KeyboardType> // default value: multi_line_text
  public let layoutProvider: DivLayoutProvider?
  public let letterSpacing: Expression<Double> // default value: 0
  public let lineHeight: Expression<Int>? // constraint: number >= 0
  public let margins: DivEdgeInsets?
  public let mask: DivInputMask?
  public let maxLength: Expression<Int>? // constraint: number > 0
  public let maxVisibleLines: Expression<Int>? // constraint: number > 0
  public let nativeInterface: NativeInterface?
  public let paddings: DivEdgeInsets?
  public let reuseId: Expression<String>?
  public let rowSpan: Expression<Int>? // constraint: number >= 0
  public let selectAllOnFocus: Expression<Bool> // default value: false
  public let selectedActions: [DivAction]?
  public let textAlignmentHorizontal: Expression<DivAlignmentHorizontal> // default value: start
  public let textAlignmentVertical: Expression<DivAlignmentVertical> // default value: center
  public let textColor: Expression<Color> // default value: #FF000000
  public let textVariable: String
  public let tooltips: [DivTooltip]?
  public let transform: DivTransform?
  public let transitionChange: DivChangeTransition?
  public let transitionIn: DivAppearanceTransition?
  public let transitionOut: DivAppearanceTransition?
  public let transitionTriggers: [DivTransitionTrigger]? // at least 1 elements
  public let validators: [DivInputValidator]?
  public let variables: [DivVariable]?
  public let visibility: Expression<DivVisibility> // default value: visible
  public let visibilityAction: DivVisibilityAction?
  public let visibilityActions: [DivVisibilityAction]?
  public let width: DivSize // default value: .divMatchParentSize(DivMatchParentSize())

  public func resolveAlignmentHorizontal(_ resolver: ExpressionResolver) -> DivAlignmentHorizontal? {
    resolver.resolveEnum(alignmentHorizontal)
  }

  public func resolveAlignmentVertical(_ resolver: ExpressionResolver) -> DivAlignmentVertical? {
    resolver.resolveEnum(alignmentVertical)
  }

  public func resolveAlpha(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumeric(alpha) ?? 1.0
  }

  public func resolveColumnSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(columnSpan)
  }

  public func resolveFontFamily(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(fontFamily)
  }

  public func resolveFontSize(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(fontSize) ?? 12
  }

  public func resolveFontSizeUnit(_ resolver: ExpressionResolver) -> DivSizeUnit {
    resolver.resolveEnum(fontSizeUnit) ?? DivSizeUnit.sp
  }

  public func resolveFontWeight(_ resolver: ExpressionResolver) -> DivFontWeight {
    resolver.resolveEnum(fontWeight) ?? DivFontWeight.regular
  }

  public func resolveFontWeightValue(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(fontWeightValue)
  }

  public func resolveHighlightColor(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveColor(highlightColor)
  }

  public func resolveHintColor(_ resolver: ExpressionResolver) -> Color {
    resolver.resolveColor(hintColor) ?? Color.colorWithARGBHexCode(0x73000000)
  }

  public func resolveHintText(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(hintText)
  }

  public func resolveIsEnabled(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(isEnabled) ?? true
  }

  public func resolveKeyboardType(_ resolver: ExpressionResolver) -> KeyboardType {
    resolver.resolveEnum(keyboardType) ?? KeyboardType.multiLineText
  }

  public func resolveLetterSpacing(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumeric(letterSpacing) ?? 0
  }

  public func resolveLineHeight(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(lineHeight)
  }

  public func resolveMaxLength(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(maxLength)
  }

  public func resolveMaxVisibleLines(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(maxVisibleLines)
  }

  public func resolveReuseId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(reuseId)
  }

  public func resolveRowSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(rowSpan)
  }

  public func resolveSelectAllOnFocus(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(selectAllOnFocus) ?? false
  }

  public func resolveTextAlignmentHorizontal(_ resolver: ExpressionResolver) -> DivAlignmentHorizontal {
    resolver.resolveEnum(textAlignmentHorizontal) ?? DivAlignmentHorizontal.start
  }

  public func resolveTextAlignmentVertical(_ resolver: ExpressionResolver) -> DivAlignmentVertical {
    resolver.resolveEnum(textAlignmentVertical) ?? DivAlignmentVertical.center
  }

  public func resolveTextColor(_ resolver: ExpressionResolver) -> Color {
    resolver.resolveColor(textColor) ?? Color.colorWithARGBHexCode(0xFF000000)
  }

  public func resolveVisibility(_ resolver: ExpressionResolver) -> DivVisibility {
    resolver.resolveEnum(visibility) ?? DivVisibility.visible
  }

  static let alphaValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 >= 0.0 && $0 <= 1.0 })

  static let columnSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let fontSizeValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let fontWeightValueValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 > 0 })

  static let lineHeightValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let maxLengthValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 > 0 })

  static let maxVisibleLinesValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 > 0 })

  static let rowSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let transitionTriggersValidator: AnyArrayValueValidator<DivTransitionTrigger> =
    makeArrayValidator(minItems: 1)

  init(
    accessibility: DivAccessibility? = nil,
    alignmentHorizontal: Expression<DivAlignmentHorizontal>? = nil,
    alignmentVertical: Expression<DivAlignmentVertical>? = nil,
    alpha: Expression<Double>? = nil,
    background: [DivBackground]? = nil,
    border: DivBorder? = nil,
    columnSpan: Expression<Int>? = nil,
    disappearActions: [DivDisappearAction]? = nil,
    extensions: [DivExtension]? = nil,
    focus: DivFocus? = nil,
    fontFamily: Expression<String>? = nil,
    fontSize: Expression<Int>? = nil,
    fontSizeUnit: Expression<DivSizeUnit>? = nil,
    fontWeight: Expression<DivFontWeight>? = nil,
    fontWeightValue: Expression<Int>? = nil,
    height: DivSize? = nil,
    highlightColor: Expression<Color>? = nil,
    hintColor: Expression<Color>? = nil,
    hintText: Expression<String>? = nil,
    id: String? = nil,
    isEnabled: Expression<Bool>? = nil,
    keyboardType: Expression<KeyboardType>? = nil,
    layoutProvider: DivLayoutProvider? = nil,
    letterSpacing: Expression<Double>? = nil,
    lineHeight: Expression<Int>? = nil,
    margins: DivEdgeInsets? = nil,
    mask: DivInputMask? = nil,
    maxLength: Expression<Int>? = nil,
    maxVisibleLines: Expression<Int>? = nil,
    nativeInterface: NativeInterface? = nil,
    paddings: DivEdgeInsets? = nil,
    reuseId: Expression<String>? = nil,
    rowSpan: Expression<Int>? = nil,
    selectAllOnFocus: Expression<Bool>? = nil,
    selectedActions: [DivAction]? = nil,
    textAlignmentHorizontal: Expression<DivAlignmentHorizontal>? = nil,
    textAlignmentVertical: Expression<DivAlignmentVertical>? = nil,
    textColor: Expression<Color>? = nil,
    textVariable: String,
    tooltips: [DivTooltip]? = nil,
    transform: DivTransform? = nil,
    transitionChange: DivChangeTransition? = nil,
    transitionIn: DivAppearanceTransition? = nil,
    transitionOut: DivAppearanceTransition? = nil,
    transitionTriggers: [DivTransitionTrigger]? = nil,
    validators: [DivInputValidator]? = nil,
    variables: [DivVariable]? = nil,
    visibility: Expression<DivVisibility>? = nil,
    visibilityAction: DivVisibilityAction? = nil,
    visibilityActions: [DivVisibilityAction]? = nil,
    width: DivSize? = nil
  ) {
    self.accessibility = accessibility
    self.alignmentHorizontal = alignmentHorizontal
    self.alignmentVertical = alignmentVertical
    self.alpha = alpha ?? .value(1.0)
    self.background = background
    self.border = border
    self.columnSpan = columnSpan
    self.disappearActions = disappearActions
    self.extensions = extensions
    self.focus = focus
    self.fontFamily = fontFamily
    self.fontSize = fontSize ?? .value(12)
    self.fontSizeUnit = fontSizeUnit ?? .value(.sp)
    self.fontWeight = fontWeight ?? .value(.regular)
    self.fontWeightValue = fontWeightValue
    self.height = height ?? .divWrapContentSize(DivWrapContentSize())
    self.highlightColor = highlightColor
    self.hintColor = hintColor ?? .value(Color.colorWithARGBHexCode(0x73000000))
    self.hintText = hintText
    self.id = id
    self.isEnabled = isEnabled ?? .value(true)
    self.keyboardType = keyboardType ?? .value(.multiLineText)
    self.layoutProvider = layoutProvider
    self.letterSpacing = letterSpacing ?? .value(0)
    self.lineHeight = lineHeight
    self.margins = margins
    self.mask = mask
    self.maxLength = maxLength
    self.maxVisibleLines = maxVisibleLines
    self.nativeInterface = nativeInterface
    self.paddings = paddings
    self.reuseId = reuseId
    self.rowSpan = rowSpan
    self.selectAllOnFocus = selectAllOnFocus ?? .value(false)
    self.selectedActions = selectedActions
    self.textAlignmentHorizontal = textAlignmentHorizontal ?? .value(.start)
    self.textAlignmentVertical = textAlignmentVertical ?? .value(.center)
    self.textColor = textColor ?? .value(Color.colorWithARGBHexCode(0xFF000000))
    self.textVariable = textVariable
    self.tooltips = tooltips
    self.transform = transform
    self.transitionChange = transitionChange
    self.transitionIn = transitionIn
    self.transitionOut = transitionOut
    self.transitionTriggers = transitionTriggers
    self.validators = validators
    self.variables = variables
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
      lhs.disappearActions == rhs.disappearActions,
      lhs.extensions == rhs.extensions
    else {
      return false
    }
    guard
      lhs.focus == rhs.focus,
      lhs.fontFamily == rhs.fontFamily,
      lhs.fontSize == rhs.fontSize
    else {
      return false
    }
    guard
      lhs.fontSizeUnit == rhs.fontSizeUnit,
      lhs.fontWeight == rhs.fontWeight,
      lhs.fontWeightValue == rhs.fontWeightValue
    else {
      return false
    }
    guard
      lhs.height == rhs.height,
      lhs.highlightColor == rhs.highlightColor,
      lhs.hintColor == rhs.hintColor
    else {
      return false
    }
    guard
      lhs.hintText == rhs.hintText,
      lhs.id == rhs.id,
      lhs.isEnabled == rhs.isEnabled
    else {
      return false
    }
    guard
      lhs.keyboardType == rhs.keyboardType,
      lhs.layoutProvider == rhs.layoutProvider,
      lhs.letterSpacing == rhs.letterSpacing
    else {
      return false
    }
    guard
      lhs.lineHeight == rhs.lineHeight,
      lhs.margins == rhs.margins,
      lhs.mask == rhs.mask
    else {
      return false
    }
    guard
      lhs.maxLength == rhs.maxLength,
      lhs.maxVisibleLines == rhs.maxVisibleLines,
      lhs.nativeInterface == rhs.nativeInterface
    else {
      return false
    }
    guard
      lhs.paddings == rhs.paddings,
      lhs.reuseId == rhs.reuseId,
      lhs.rowSpan == rhs.rowSpan
    else {
      return false
    }
    guard
      lhs.selectAllOnFocus == rhs.selectAllOnFocus,
      lhs.selectedActions == rhs.selectedActions,
      lhs.textAlignmentHorizontal == rhs.textAlignmentHorizontal
    else {
      return false
    }
    guard
      lhs.textAlignmentVertical == rhs.textAlignmentVertical,
      lhs.textColor == rhs.textColor,
      lhs.textVariable == rhs.textVariable
    else {
      return false
    }
    guard
      lhs.tooltips == rhs.tooltips,
      lhs.transform == rhs.transform,
      lhs.transitionChange == rhs.transitionChange
    else {
      return false
    }
    guard
      lhs.transitionIn == rhs.transitionIn,
      lhs.transitionOut == rhs.transitionOut,
      lhs.transitionTriggers == rhs.transitionTriggers
    else {
      return false
    }
    guard
      lhs.validators == rhs.validators,
      lhs.variables == rhs.variables,
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
    result["accessibility"] = accessibility?.toDictionary()
    result["alignment_horizontal"] = alignmentHorizontal?.toValidSerializationValue()
    result["alignment_vertical"] = alignmentVertical?.toValidSerializationValue()
    result["alpha"] = alpha.toValidSerializationValue()
    result["background"] = background?.map { $0.toDictionary() }
    result["border"] = border?.toDictionary()
    result["column_span"] = columnSpan?.toValidSerializationValue()
    result["disappear_actions"] = disappearActions?.map { $0.toDictionary() }
    result["extensions"] = extensions?.map { $0.toDictionary() }
    result["focus"] = focus?.toDictionary()
    result["font_family"] = fontFamily?.toValidSerializationValue()
    result["font_size"] = fontSize.toValidSerializationValue()
    result["font_size_unit"] = fontSizeUnit.toValidSerializationValue()
    result["font_weight"] = fontWeight.toValidSerializationValue()
    result["font_weight_value"] = fontWeightValue?.toValidSerializationValue()
    result["height"] = height.toDictionary()
    result["highlight_color"] = highlightColor?.toValidSerializationValue()
    result["hint_color"] = hintColor.toValidSerializationValue()
    result["hint_text"] = hintText?.toValidSerializationValue()
    result["id"] = id
    result["is_enabled"] = isEnabled.toValidSerializationValue()
    result["keyboard_type"] = keyboardType.toValidSerializationValue()
    result["layout_provider"] = layoutProvider?.toDictionary()
    result["letter_spacing"] = letterSpacing.toValidSerializationValue()
    result["line_height"] = lineHeight?.toValidSerializationValue()
    result["margins"] = margins?.toDictionary()
    result["mask"] = mask?.toDictionary()
    result["max_length"] = maxLength?.toValidSerializationValue()
    result["max_visible_lines"] = maxVisibleLines?.toValidSerializationValue()
    result["native_interface"] = nativeInterface?.toDictionary()
    result["paddings"] = paddings?.toDictionary()
    result["reuse_id"] = reuseId?.toValidSerializationValue()
    result["row_span"] = rowSpan?.toValidSerializationValue()
    result["select_all_on_focus"] = selectAllOnFocus.toValidSerializationValue()
    result["selected_actions"] = selectedActions?.map { $0.toDictionary() }
    result["text_alignment_horizontal"] = textAlignmentHorizontal.toValidSerializationValue()
    result["text_alignment_vertical"] = textAlignmentVertical.toValidSerializationValue()
    result["text_color"] = textColor.toValidSerializationValue()
    result["text_variable"] = textVariable
    result["tooltips"] = tooltips?.map { $0.toDictionary() }
    result["transform"] = transform?.toDictionary()
    result["transition_change"] = transitionChange?.toDictionary()
    result["transition_in"] = transitionIn?.toDictionary()
    result["transition_out"] = transitionOut?.toDictionary()
    result["transition_triggers"] = transitionTriggers?.map { $0.rawValue }
    result["validators"] = validators?.map { $0.toDictionary() }
    result["variables"] = variables?.map { $0.toDictionary() }
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
