// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivSelect: DivBase {
  public final class Option {
    public let text: Expression<String>?
    public let value: Expression<String>

    public func resolveText(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(text)
    }

    public func resolveValue(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(value)
    }

    init(
      text: Expression<String>? = nil,
      value: Expression<String>
    ) {
      self.text = text
      self.value = value
    }
  }

  public static let type: String = "select"
  public let accessibility: DivAccessibility?
  public let alignmentHorizontal: Expression<DivAlignmentHorizontal>?
  public let alignmentVertical: Expression<DivAlignmentVertical>?
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let animators: [DivAnimator]?
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
  public let functions: [DivFunction]?
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let hintColor: Expression<Color> // default value: #73000000
  public let hintText: Expression<String>?
  public let id: String?
  public let layoutProvider: DivLayoutProvider?
  public let letterSpacing: Expression<Double> // default value: 0
  public let lineHeight: Expression<Int>? // constraint: number >= 0
  public let margins: DivEdgeInsets?
  public let options: [Option] // at least 1 elements
  public let paddings: DivEdgeInsets?
  public let reuseId: Expression<String>?
  public let rowSpan: Expression<Int>? // constraint: number >= 0
  public let selectedActions: [DivAction]?
  public let textColor: Expression<Color> // default value: #FF000000
  public let tooltips: [DivTooltip]?
  public let transform: DivTransform?
  public let transitionChange: DivChangeTransition?
  public let transitionIn: DivAppearanceTransition?
  public let transitionOut: DivAppearanceTransition?
  public let transitionTriggers: [DivTransitionTrigger]? // at least 1 elements
  public let valueVariable: String
  public let variableTriggers: [DivTrigger]?
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

  public func resolveHintColor(_ resolver: ExpressionResolver) -> Color {
    resolver.resolveColor(hintColor) ?? Color.colorWithARGBHexCode(0x73000000)
  }

  public func resolveHintText(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(hintText)
  }

  public func resolveLetterSpacing(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumeric(letterSpacing) ?? 0
  }

  public func resolveLineHeight(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(lineHeight)
  }

  public func resolveReuseId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(reuseId)
  }

  public func resolveRowSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(rowSpan)
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

  static let optionsValidator: AnyArrayValueValidator<DivSelect.Option> =
    makeArrayValidator(minItems: 1)

  static let rowSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let transitionTriggersValidator: AnyArrayValueValidator<DivTransitionTrigger> =
    makeArrayValidator(minItems: 1)

  init(
    accessibility: DivAccessibility? = nil,
    alignmentHorizontal: Expression<DivAlignmentHorizontal>? = nil,
    alignmentVertical: Expression<DivAlignmentVertical>? = nil,
    alpha: Expression<Double>? = nil,
    animators: [DivAnimator]? = nil,
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
    functions: [DivFunction]? = nil,
    height: DivSize? = nil,
    hintColor: Expression<Color>? = nil,
    hintText: Expression<String>? = nil,
    id: String? = nil,
    layoutProvider: DivLayoutProvider? = nil,
    letterSpacing: Expression<Double>? = nil,
    lineHeight: Expression<Int>? = nil,
    margins: DivEdgeInsets? = nil,
    options: [Option],
    paddings: DivEdgeInsets? = nil,
    reuseId: Expression<String>? = nil,
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
    variableTriggers: [DivTrigger]? = nil,
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
    self.animators = animators
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
    self.functions = functions
    self.height = height ?? .divWrapContentSize(DivWrapContentSize())
    self.hintColor = hintColor ?? .value(Color.colorWithARGBHexCode(0x73000000))
    self.hintText = hintText
    self.id = id
    self.layoutProvider = layoutProvider
    self.letterSpacing = letterSpacing ?? .value(0)
    self.lineHeight = lineHeight
    self.margins = margins
    self.options = options
    self.paddings = paddings
    self.reuseId = reuseId
    self.rowSpan = rowSpan
    self.selectedActions = selectedActions
    self.textColor = textColor ?? .value(Color.colorWithARGBHexCode(0xFF000000))
    self.tooltips = tooltips
    self.transform = transform
    self.transitionChange = transitionChange
    self.transitionIn = transitionIn
    self.transitionOut = transitionOut
    self.transitionTriggers = transitionTriggers
    self.valueVariable = valueVariable
    self.variableTriggers = variableTriggers
    self.variables = variables
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
      lhs.animators == rhs.animators,
      lhs.background == rhs.background
    else {
      return false
    }
    guard
      lhs.border == rhs.border,
      lhs.columnSpan == rhs.columnSpan,
      lhs.disappearActions == rhs.disappearActions
    else {
      return false
    }
    guard
      lhs.extensions == rhs.extensions,
      lhs.focus == rhs.focus,
      lhs.fontFamily == rhs.fontFamily
    else {
      return false
    }
    guard
      lhs.fontSize == rhs.fontSize,
      lhs.fontSizeUnit == rhs.fontSizeUnit,
      lhs.fontWeight == rhs.fontWeight
    else {
      return false
    }
    guard
      lhs.fontWeightValue == rhs.fontWeightValue,
      lhs.functions == rhs.functions,
      lhs.height == rhs.height
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
      lhs.layoutProvider == rhs.layoutProvider,
      lhs.letterSpacing == rhs.letterSpacing,
      lhs.lineHeight == rhs.lineHeight
    else {
      return false
    }
    guard
      lhs.margins == rhs.margins,
      lhs.options == rhs.options,
      lhs.paddings == rhs.paddings
    else {
      return false
    }
    guard
      lhs.reuseId == rhs.reuseId,
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
      lhs.variableTriggers == rhs.variableTriggers
    else {
      return false
    }
    guard
      lhs.variables == rhs.variables,
      lhs.visibility == rhs.visibility,
      lhs.visibilityAction == rhs.visibilityAction
    else {
      return false
    }
    guard
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
    result["accessibility"] = accessibility?.toDictionary()
    result["alignment_horizontal"] = alignmentHorizontal?.toValidSerializationValue()
    result["alignment_vertical"] = alignmentVertical?.toValidSerializationValue()
    result["alpha"] = alpha.toValidSerializationValue()
    result["animators"] = animators?.map { $0.toDictionary() }
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
    result["functions"] = functions?.map { $0.toDictionary() }
    result["height"] = height.toDictionary()
    result["hint_color"] = hintColor.toValidSerializationValue()
    result["hint_text"] = hintText?.toValidSerializationValue()
    result["id"] = id
    result["layout_provider"] = layoutProvider?.toDictionary()
    result["letter_spacing"] = letterSpacing.toValidSerializationValue()
    result["line_height"] = lineHeight?.toValidSerializationValue()
    result["margins"] = margins?.toDictionary()
    result["options"] = options.map { $0.toDictionary() }
    result["paddings"] = paddings?.toDictionary()
    result["reuse_id"] = reuseId?.toValidSerializationValue()
    result["row_span"] = rowSpan?.toValidSerializationValue()
    result["selected_actions"] = selectedActions?.map { $0.toDictionary() }
    result["text_color"] = textColor.toValidSerializationValue()
    result["tooltips"] = tooltips?.map { $0.toDictionary() }
    result["transform"] = transform?.toDictionary()
    result["transition_change"] = transitionChange?.toDictionary()
    result["transition_in"] = transitionIn?.toDictionary()
    result["transition_out"] = transitionOut?.toDictionary()
    result["transition_triggers"] = transitionTriggers?.map { $0.rawValue }
    result["value_variable"] = valueVariable
    result["variable_triggers"] = variableTriggers?.map { $0.toDictionary() }
    result["variables"] = variables?.map { $0.toDictionary() }
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
