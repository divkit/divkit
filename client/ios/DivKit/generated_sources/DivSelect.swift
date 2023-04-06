// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivSelect: DivBase {
  public final class Option {
    public let text: Expression<String>?
    public let value: Expression<String>

    public func resolveText(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveStringBasedValue(expression: text, initializer: { $0 })
    }

    public func resolveValue(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveStringBasedValue(expression: value, initializer: { $0 })
    }

    static let textValidator: AnyValueValidator<String> =
      makeStringValidator(minLength: 1)

    init(
      text: Expression<String>? = nil,
      value: Expression<String>
    ) {
      self.text = text
      self.value = value
    }
  }

  public static let type: String = "select"
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
  public let hintColor: Expression<Color> // default value: #73000000
  public let hintText: Expression<String>? // at least 1 char
  public let id: String? // at least 1 char
  public let letterSpacing: Expression<Double> // default value: 0
  public let lineHeight: Expression<Int>? // constraint: number >= 0
  public let margins: DivEdgeInsets
  public let options: [Option] // at least 1 elements
  public let paddings: DivEdgeInsets
  public let rowSpan: Expression<Int>? // constraint: number >= 0
  public let selectedActions: [DivAction]? // at least 1 elements
  public let textColor: Expression<Color> // default value: #FF000000
  public let tooltips: [DivTooltip]? // at least 1 elements
  public let transform: DivTransform
  public let transitionChange: DivChangeTransition?
  public let transitionIn: DivAppearanceTransition?
  public let transitionOut: DivAppearanceTransition?
  public let transitionTriggers: [DivTransitionTrigger]? // at least 1 elements
  public let valueVariable: String // at least 1 char
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

  public func resolveHintColor(_ resolver: ExpressionResolver) -> Color {
    resolver.resolveStringBasedValue(expression: hintColor, initializer: Color.color(withHexString:)) ?? Color.colorWithARGBHexCode(0x73000000)
  }

  public func resolveHintText(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveStringBasedValue(expression: hintText, initializer: { $0 })
  }

  public func resolveLetterSpacing(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumericValue(expression: letterSpacing) ?? 0
  }

  public func resolveLineHeight(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: lineHeight)
  }

  public func resolveRowSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: rowSpan)
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

  static let hintColorValidator: AnyValueValidator<Color> =
    makeNoOpValueValidator()

  static let hintTextValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let idValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let lineHeightValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let marginsValidator: AnyValueValidator<DivEdgeInsets> =
    makeNoOpValueValidator()

  static let optionsValidator: AnyArrayValueValidator<DivSelect.Option> =
    makeArrayValidator(minItems: 1)

  static let paddingsValidator: AnyValueValidator<DivEdgeInsets> =
    makeNoOpValueValidator()

  static let rowSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let selectedActionsValidator: AnyArrayValueValidator<DivAction> =
    makeArrayValidator(minItems: 1)

  static let textColorValidator: AnyValueValidator<Color> =
    makeNoOpValueValidator()

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

  static let valueVariableValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

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
    hintColor: Expression<Color>? = nil,
    hintText: Expression<String>? = nil,
    id: String? = nil,
    letterSpacing: Expression<Double>? = nil,
    lineHeight: Expression<Int>? = nil,
    margins: DivEdgeInsets? = nil,
    options: [Option],
    paddings: DivEdgeInsets? = nil,
    rowSpan: Expression<Int>? = nil,
    selectedActions: [DivAction]? = nil,
    textColor: Expression<Color>? = nil,
    tooltips: [DivTooltip]? = nil,
    transform: DivTransform? = nil,
    transitionChange: DivChangeTransition? = nil,
    transitionIn: DivAppearanceTransition? = nil,
    transitionOut: DivAppearanceTransition? = nil,
    transitionTriggers: [DivTransitionTrigger]? = nil,
    valueVariable: String,
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
    self.hintColor = hintColor ?? .value(Color.colorWithARGBHexCode(0x73000000))
    self.hintText = hintText
    self.id = id
    self.letterSpacing = letterSpacing ?? .value(0)
    self.lineHeight = lineHeight
    self.margins = margins ?? DivEdgeInsets()
    self.options = options
    self.paddings = paddings ?? DivEdgeInsets()
    self.rowSpan = rowSpan
    self.selectedActions = selectedActions
    self.textColor = textColor ?? .value(Color.colorWithARGBHexCode(0xFF000000))
    self.tooltips = tooltips
    self.transform = transform ?? DivTransform()
    self.transitionChange = transitionChange
    self.transitionIn = transitionIn
    self.transitionOut = transitionOut
    self.transitionTriggers = transitionTriggers
    self.valueVariable = valueVariable
    self.visibility = visibility ?? .value(.visible)
    self.visibilityAction = visibilityAction
    self.visibilityActions = visibilityActions
    self.width = width ?? .divMatchParentSize(DivMatchParentSize())
  }
}

#if DEBUG
extension DivSelect: Equatable {
  public static func ==(lhs: DivSelect, rhs: DivSelect) -> Bool {
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
      lhs.hintColor == rhs.hintColor
    else {
      return false
    }
    guard
      lhs.hintText == rhs.hintText,
      lhs.id == rhs.id,
      lhs.letterSpacing == rhs.letterSpacing
    else {
      return false
    }
    guard
      lhs.lineHeight == rhs.lineHeight,
      lhs.margins == rhs.margins,
      lhs.options == rhs.options
    else {
      return false
    }
    guard
      lhs.paddings == rhs.paddings,
      lhs.rowSpan == rhs.rowSpan,
      lhs.selectedActions == rhs.selectedActions
    else {
      return false
    }
    guard
      lhs.textColor == rhs.textColor,
      lhs.tooltips == rhs.tooltips,
      lhs.transform == rhs.transform
    else {
      return false
    }
    guard
      lhs.transitionChange == rhs.transitionChange,
      lhs.transitionIn == rhs.transitionIn,
      lhs.transitionOut == rhs.transitionOut
    else {
      return false
    }
    guard
      lhs.transitionTriggers == rhs.transitionTriggers,
      lhs.valueVariable == rhs.valueVariable,
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

extension DivSelect: Serializable {
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
    result["hint_color"] = hintColor.toValidSerializationValue()
    result["hint_text"] = hintText?.toValidSerializationValue()
    result["id"] = id
    result["letter_spacing"] = letterSpacing.toValidSerializationValue()
    result["line_height"] = lineHeight?.toValidSerializationValue()
    result["margins"] = margins.toDictionary()
    result["options"] = options.map { $0.toDictionary() }
    result["paddings"] = paddings.toDictionary()
    result["row_span"] = rowSpan?.toValidSerializationValue()
    result["selected_actions"] = selectedActions?.map { $0.toDictionary() }
    result["text_color"] = textColor.toValidSerializationValue()
    result["tooltips"] = tooltips?.map { $0.toDictionary() }
    result["transform"] = transform.toDictionary()
    result["transition_change"] = transitionChange?.toDictionary()
    result["transition_in"] = transitionIn?.toDictionary()
    result["transition_out"] = transitionOut?.toDictionary()
    result["transition_triggers"] = transitionTriggers?.map { $0.rawValue }
    result["value_variable"] = valueVariable
    result["visibility"] = visibility.toValidSerializationValue()
    result["visibility_action"] = visibilityAction?.toDictionary()
    result["visibility_actions"] = visibilityActions?.map { $0.toDictionary() }
    result["width"] = width.toDictionary()
    return result
  }
}

#if DEBUG
extension DivSelect.Option: Equatable {
  public static func ==(lhs: DivSelect.Option, rhs: DivSelect.Option) -> Bool {
    guard
      lhs.text == rhs.text,
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension DivSelect.Option: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["text"] = text?.toValidSerializationValue()
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
