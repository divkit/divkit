// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivSlider: DivBase {
  public final class TextStyle {
    public let fontSize: Expression<Int> // constraint: number >= 0
    public let fontSizeUnit: Expression<DivSizeUnit> // default value: sp
    public let fontWeight: Expression<DivFontWeight> // default value: regular
    public let offset: DivPoint?
    public let textColor: Expression<Color> // default value: #FF000000

    public func resolveFontSize(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumericValue(expression: fontSize)
    }

    public func resolveFontSizeUnit(_ resolver: ExpressionResolver) -> DivSizeUnit {
      resolver.resolveStringBasedValue(expression: fontSizeUnit, initializer: DivSizeUnit.init(rawValue:)) ?? DivSizeUnit.sp
    }

    public func resolveFontWeight(_ resolver: ExpressionResolver) -> DivFontWeight {
      resolver.resolveStringBasedValue(expression: fontWeight, initializer: DivFontWeight.init(rawValue:)) ?? DivFontWeight.regular
    }

    public func resolveTextColor(_ resolver: ExpressionResolver) -> Color {
      resolver.resolveStringBasedValue(expression: textColor, initializer: Color.color(withHexString:)) ?? Color.colorWithARGBHexCode(0xFF000000)
    }

    static let fontSizeValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    static let fontSizeUnitValidator: AnyValueValidator<DivSizeUnit> =
      makeNoOpValueValidator()

    static let fontWeightValidator: AnyValueValidator<DivFontWeight> =
      makeNoOpValueValidator()

    static let offsetValidator: AnyValueValidator<DivPoint> =
      makeNoOpValueValidator()

    static let textColorValidator: AnyValueValidator<Color> =
      makeNoOpValueValidator()

    init(
      fontSize: Expression<Int>,
      fontSizeUnit: Expression<DivSizeUnit>? = nil,
      fontWeight: Expression<DivFontWeight>? = nil,
      offset: DivPoint? = nil,
      textColor: Expression<Color>? = nil
    ) {
      self.fontSize = fontSize
      self.fontSizeUnit = fontSizeUnit ?? .value(.sp)
      self.fontWeight = fontWeight ?? .value(.regular)
      self.offset = offset
      self.textColor = textColor ?? .value(Color.colorWithARGBHexCode(0xFF000000))
    }
  }

  public static let type: String = "slider"
  public let accessibility: DivAccessibility
  public let alignmentHorizontal: Expression<DivAlignmentHorizontal>?
  public let alignmentVertical: Expression<DivAlignmentVertical>?
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let background: [DivBackground]? // at least 1 elements
  public let border: DivBorder
  public let columnSpan: Expression<Int>? // constraint: number >= 0
  public let extensions: [DivExtension]? // at least 1 elements
  public let focus: DivFocus?
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: String? // at least 1 char
  public let margins: DivEdgeInsets
  public let maxValue: Expression<Int> // default value: 100
  public let minValue: Expression<Int> // default value: 0
  public let paddings: DivEdgeInsets
  public let rowSpan: Expression<Int>? // constraint: number >= 0
  public let secondaryValueAccessibility: DivAccessibility
  public let selectedActions: [DivAction]? // at least 1 elements
  public let thumbSecondaryStyle: DivDrawable?
  public let thumbSecondaryTextStyle: TextStyle?
  public let thumbSecondaryValueVariable: String? // at least 1 char
  public let thumbStyle: DivDrawable
  public let thumbTextStyle: TextStyle?
  public let thumbValueVariable: String? // at least 1 char
  public let tickMarkActiveStyle: DivDrawable?
  public let tickMarkInactiveStyle: DivDrawable?
  public let tooltips: [DivTooltip]? // at least 1 elements
  public let trackActiveStyle: DivDrawable
  public let trackInactiveStyle: DivDrawable
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

  public func resolveMaxValue(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: maxValue) ?? 100
  }

  public func resolveMinValue(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: minValue) ?? 0
  }

  public func resolveRowSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: rowSpan)
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

  static let heightValidator: AnyValueValidator<DivSize> =
    makeNoOpValueValidator()

  static let idValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let marginsValidator: AnyValueValidator<DivEdgeInsets> =
    makeNoOpValueValidator()

  static let paddingsValidator: AnyValueValidator<DivEdgeInsets> =
    makeNoOpValueValidator()

  static let rowSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let secondaryValueAccessibilityValidator: AnyValueValidator<DivAccessibility> =
    makeNoOpValueValidator()

  static let selectedActionsValidator: AnyArrayValueValidator<DivAction> =
    makeArrayValidator(minItems: 1)

  static let thumbSecondaryStyleValidator: AnyValueValidator<DivDrawable> =
    makeNoOpValueValidator()

  static let thumbSecondaryTextStyleValidator: AnyValueValidator<DivSlider.TextStyle> =
    makeNoOpValueValidator()

  static let thumbSecondaryValueVariableValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let thumbTextStyleValidator: AnyValueValidator<DivSlider.TextStyle> =
    makeNoOpValueValidator()

  static let thumbValueVariableValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let tickMarkActiveStyleValidator: AnyValueValidator<DivDrawable> =
    makeNoOpValueValidator()

  static let tickMarkInactiveStyleValidator: AnyValueValidator<DivDrawable> =
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
    height: DivSize? = nil,
    id: String? = nil,
    margins: DivEdgeInsets? = nil,
    maxValue: Expression<Int>? = nil,
    minValue: Expression<Int>? = nil,
    paddings: DivEdgeInsets? = nil,
    rowSpan: Expression<Int>? = nil,
    secondaryValueAccessibility: DivAccessibility? = nil,
    selectedActions: [DivAction]? = nil,
    thumbSecondaryStyle: DivDrawable? = nil,
    thumbSecondaryTextStyle: TextStyle? = nil,
    thumbSecondaryValueVariable: String? = nil,
    thumbStyle: DivDrawable,
    thumbTextStyle: TextStyle? = nil,
    thumbValueVariable: String? = nil,
    tickMarkActiveStyle: DivDrawable? = nil,
    tickMarkInactiveStyle: DivDrawable? = nil,
    tooltips: [DivTooltip]? = nil,
    trackActiveStyle: DivDrawable,
    trackInactiveStyle: DivDrawable,
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
    self.height = height ?? .divWrapContentSize(DivWrapContentSize())
    self.id = id
    self.margins = margins ?? DivEdgeInsets()
    self.maxValue = maxValue ?? .value(100)
    self.minValue = minValue ?? .value(0)
    self.paddings = paddings ?? DivEdgeInsets()
    self.rowSpan = rowSpan
    self.secondaryValueAccessibility = secondaryValueAccessibility ?? DivAccessibility()
    self.selectedActions = selectedActions
    self.thumbSecondaryStyle = thumbSecondaryStyle
    self.thumbSecondaryTextStyle = thumbSecondaryTextStyle
    self.thumbSecondaryValueVariable = thumbSecondaryValueVariable
    self.thumbStyle = thumbStyle
    self.thumbTextStyle = thumbTextStyle
    self.thumbValueVariable = thumbValueVariable
    self.tickMarkActiveStyle = tickMarkActiveStyle
    self.tickMarkInactiveStyle = tickMarkInactiveStyle
    self.tooltips = tooltips
    self.trackActiveStyle = trackActiveStyle
    self.trackInactiveStyle = trackInactiveStyle
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
extension DivSlider: Equatable {
  public static func ==(lhs: DivSlider, rhs: DivSlider) -> Bool {
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
      lhs.height == rhs.height,
      lhs.id == rhs.id,
      lhs.margins == rhs.margins
    else {
      return false
    }
    guard
      lhs.maxValue == rhs.maxValue,
      lhs.minValue == rhs.minValue,
      lhs.paddings == rhs.paddings
    else {
      return false
    }
    guard
      lhs.rowSpan == rhs.rowSpan,
      lhs.secondaryValueAccessibility == rhs.secondaryValueAccessibility,
      lhs.selectedActions == rhs.selectedActions
    else {
      return false
    }
    guard
      lhs.thumbSecondaryStyle == rhs.thumbSecondaryStyle,
      lhs.thumbSecondaryTextStyle == rhs.thumbSecondaryTextStyle,
      lhs.thumbSecondaryValueVariable == rhs.thumbSecondaryValueVariable
    else {
      return false
    }
    guard
      lhs.thumbStyle == rhs.thumbStyle,
      lhs.thumbTextStyle == rhs.thumbTextStyle,
      lhs.thumbValueVariable == rhs.thumbValueVariable
    else {
      return false
    }
    guard
      lhs.tickMarkActiveStyle == rhs.tickMarkActiveStyle,
      lhs.tickMarkInactiveStyle == rhs.tickMarkInactiveStyle,
      lhs.tooltips == rhs.tooltips
    else {
      return false
    }
    guard
      lhs.trackActiveStyle == rhs.trackActiveStyle,
      lhs.trackInactiveStyle == rhs.trackInactiveStyle,
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

extension DivSlider: Serializable {
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
    result["height"] = height.toDictionary()
    result["id"] = id
    result["margins"] = margins.toDictionary()
    result["max_value"] = maxValue.toValidSerializationValue()
    result["min_value"] = minValue.toValidSerializationValue()
    result["paddings"] = paddings.toDictionary()
    result["row_span"] = rowSpan?.toValidSerializationValue()
    result["secondary_value_accessibility"] = secondaryValueAccessibility.toDictionary()
    result["selected_actions"] = selectedActions?.map { $0.toDictionary() }
    result["thumb_secondary_style"] = thumbSecondaryStyle?.toDictionary()
    result["thumb_secondary_text_style"] = thumbSecondaryTextStyle?.toDictionary()
    result["thumb_secondary_value_variable"] = thumbSecondaryValueVariable
    result["thumb_style"] = thumbStyle.toDictionary()
    result["thumb_text_style"] = thumbTextStyle?.toDictionary()
    result["thumb_value_variable"] = thumbValueVariable
    result["tick_mark_active_style"] = tickMarkActiveStyle?.toDictionary()
    result["tick_mark_inactive_style"] = tickMarkInactiveStyle?.toDictionary()
    result["tooltips"] = tooltips?.map { $0.toDictionary() }
    result["track_active_style"] = trackActiveStyle.toDictionary()
    result["track_inactive_style"] = trackInactiveStyle.toDictionary()
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
extension DivSlider.TextStyle: Equatable {
  public static func ==(lhs: DivSlider.TextStyle, rhs: DivSlider.TextStyle) -> Bool {
    guard
      lhs.fontSize == rhs.fontSize,
      lhs.fontSizeUnit == rhs.fontSizeUnit,
      lhs.fontWeight == rhs.fontWeight
    else {
      return false
    }
    guard
      lhs.offset == rhs.offset,
      lhs.textColor == rhs.textColor
    else {
      return false
    }
    return true
  }
}
#endif

extension DivSlider.TextStyle: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["font_size"] = fontSize.toValidSerializationValue()
    result["font_size_unit"] = fontSizeUnit.toValidSerializationValue()
    result["font_weight"] = fontWeight.toValidSerializationValue()
    result["offset"] = offset?.toDictionary()
    result["text_color"] = textColor.toValidSerializationValue()
    return result
  }
}
