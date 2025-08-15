// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivSlider: DivBase, Sendable {
  public final class Range: Sendable {
    public let end: Expression<Int>?
    public let margins: DivEdgeInsets?
    public let start: Expression<Int>?
    public let trackActiveStyle: DivDrawable?
    public let trackInactiveStyle: DivDrawable?

    public func resolveEnd(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumeric(end)
    }

    public func resolveStart(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumeric(start)
    }

    init(
      end: Expression<Int>? = nil,
      margins: DivEdgeInsets? = nil,
      start: Expression<Int>? = nil,
      trackActiveStyle: DivDrawable? = nil,
      trackInactiveStyle: DivDrawable? = nil
    ) {
      self.end = end
      self.margins = margins
      self.start = start
      self.trackActiveStyle = trackActiveStyle
      self.trackInactiveStyle = trackInactiveStyle
    }
  }

  public final class TextStyle: @unchecked Sendable {
    public let fontFamily: Expression<String>?
    public let fontSize: Expression<Int> // constraint: number >= 0; default value: 12
    public let fontSizeUnit: Expression<DivSizeUnit> // default value: sp
    public let fontVariationSettings: Expression<[String: Any]>?
    public let fontWeight: Expression<DivFontWeight>?
    public let fontWeightValue: Expression<Int>? // constraint: number > 0
    public let letterSpacing: Expression<Double> // default value: 0
    public let offset: DivPoint?
    public let textColor: Expression<Color> // default value: #FF000000

    public func resolveFontFamily(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(fontFamily)
    }

    public func resolveFontSize(_ resolver: ExpressionResolver) -> Int {
      resolver.resolveNumeric(fontSize) ?? 12
    }

    public func resolveFontSizeUnit(_ resolver: ExpressionResolver) -> DivSizeUnit {
      resolver.resolveEnum(fontSizeUnit) ?? DivSizeUnit.sp
    }

    public func resolveFontVariationSettings(_ resolver: ExpressionResolver) -> [String: Any]? {
      resolver.resolveDict(fontVariationSettings)
    }

    public func resolveFontWeight(_ resolver: ExpressionResolver) -> DivFontWeight? {
      resolver.resolveEnum(fontWeight)
    }

    public func resolveFontWeightValue(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumeric(fontWeightValue)
    }

    public func resolveLetterSpacing(_ resolver: ExpressionResolver) -> Double {
      resolver.resolveNumeric(letterSpacing) ?? 0
    }

    public func resolveTextColor(_ resolver: ExpressionResolver) -> Color {
      resolver.resolveColor(textColor) ?? Color.colorWithARGBHexCode(0xFF000000)
    }

    static let fontSizeValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    static let fontWeightValueValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 > 0 })

    init(
      fontFamily: Expression<String>? = nil,
      fontSize: Expression<Int>? = nil,
      fontSizeUnit: Expression<DivSizeUnit>? = nil,
      fontVariationSettings: Expression<[String: Any]>? = nil,
      fontWeight: Expression<DivFontWeight>? = nil,
      fontWeightValue: Expression<Int>? = nil,
      letterSpacing: Expression<Double>? = nil,
      offset: DivPoint? = nil,
      textColor: Expression<Color>? = nil
    ) {
      self.fontFamily = fontFamily
      self.fontSize = fontSize ?? .value(12)
      self.fontSizeUnit = fontSizeUnit ?? .value(.sp)
      self.fontVariationSettings = fontVariationSettings
      self.fontWeight = fontWeight
      self.fontWeightValue = fontWeightValue
      self.letterSpacing = letterSpacing ?? .value(0)
      self.offset = offset
      self.textColor = textColor ?? .value(Color.colorWithARGBHexCode(0xFF000000))
    }
  }

  public static let type: String = "slider"
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
  public let functions: [DivFunction]?
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: String?
  public let isEnabled: Expression<Bool> // default value: true
  public let layoutProvider: DivLayoutProvider?
  public let margins: DivEdgeInsets?
  public let maxValue: Expression<Int> // default value: 100
  public let minValue: Expression<Int> // default value: 0
  public let paddings: DivEdgeInsets?
  public let ranges: [Range]?
  public let reuseId: Expression<String>?
  public let rowSpan: Expression<Int>? // constraint: number >= 0
  public let secondaryValueAccessibility: DivAccessibility?
  public let selectedActions: [DivAction]?
  public let thumbSecondaryStyle: DivDrawable?
  public let thumbSecondaryTextStyle: TextStyle?
  public let thumbSecondaryValueVariable: String?
  public let thumbStyle: DivDrawable
  public let thumbTextStyle: TextStyle?
  public let thumbValueVariable: String?
  public let tickMarkActiveStyle: DivDrawable?
  public let tickMarkInactiveStyle: DivDrawable?
  public let tooltips: [DivTooltip]?
  public let trackActiveStyle: DivDrawable
  public let trackInactiveStyle: DivDrawable
  public let transform: DivTransform?
  public let transitionChange: DivChangeTransition?
  public let transitionIn: DivAppearanceTransition?
  public let transitionOut: DivAppearanceTransition?
  public let transitionTriggers: [DivTransitionTrigger]? // at least 1 elements
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

  public func resolveIsEnabled(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(isEnabled) ?? true
  }

  public func resolveMaxValue(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(maxValue) ?? 100
  }

  public func resolveMinValue(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(minValue) ?? 0
  }

  public func resolveReuseId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(reuseId)
  }

  public func resolveRowSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(rowSpan)
  }

  public func resolveVisibility(_ resolver: ExpressionResolver) -> DivVisibility {
    resolver.resolveEnum(visibility) ?? DivVisibility.visible
  }

  static let alphaValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 >= 0.0 && $0 <= 1.0 })

  static let columnSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

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
    functions: [DivFunction]? = nil,
    height: DivSize? = nil,
    id: String? = nil,
    isEnabled: Expression<Bool>? = nil,
    layoutProvider: DivLayoutProvider? = nil,
    margins: DivEdgeInsets? = nil,
    maxValue: Expression<Int>? = nil,
    minValue: Expression<Int>? = nil,
    paddings: DivEdgeInsets? = nil,
    ranges: [Range]? = nil,
    reuseId: Expression<String>? = nil,
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
    self.functions = functions
    self.height = height ?? .divWrapContentSize(DivWrapContentSize())
    self.id = id
    self.isEnabled = isEnabled ?? .value(true)
    self.layoutProvider = layoutProvider
    self.margins = margins
    self.maxValue = maxValue ?? .value(100)
    self.minValue = minValue ?? .value(0)
    self.paddings = paddings
    self.ranges = ranges
    self.reuseId = reuseId
    self.rowSpan = rowSpan
    self.secondaryValueAccessibility = secondaryValueAccessibility
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
    self.transform = transform
    self.transitionChange = transitionChange
    self.transitionIn = transitionIn
    self.transitionOut = transitionOut
    self.transitionTriggers = transitionTriggers
    self.variableTriggers = variableTriggers
    self.variables = variables
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
      lhs.functions == rhs.functions
    else {
      return false
    }
    guard
      lhs.height == rhs.height,
      lhs.id == rhs.id,
      lhs.isEnabled == rhs.isEnabled
    else {
      return false
    }
    guard
      lhs.layoutProvider == rhs.layoutProvider,
      lhs.margins == rhs.margins,
      lhs.maxValue == rhs.maxValue
    else {
      return false
    }
    guard
      lhs.minValue == rhs.minValue,
      lhs.paddings == rhs.paddings,
      lhs.ranges == rhs.ranges
    else {
      return false
    }
    guard
      lhs.reuseId == rhs.reuseId,
      lhs.rowSpan == rhs.rowSpan,
      lhs.secondaryValueAccessibility == rhs.secondaryValueAccessibility
    else {
      return false
    }
    guard
      lhs.selectedActions == rhs.selectedActions,
      lhs.thumbSecondaryStyle == rhs.thumbSecondaryStyle,
      lhs.thumbSecondaryTextStyle == rhs.thumbSecondaryTextStyle
    else {
      return false
    }
    guard
      lhs.thumbSecondaryValueVariable == rhs.thumbSecondaryValueVariable,
      lhs.thumbStyle == rhs.thumbStyle,
      lhs.thumbTextStyle == rhs.thumbTextStyle
    else {
      return false
    }
    guard
      lhs.thumbValueVariable == rhs.thumbValueVariable,
      lhs.tickMarkActiveStyle == rhs.tickMarkActiveStyle,
      lhs.tickMarkInactiveStyle == rhs.tickMarkInactiveStyle
    else {
      return false
    }
    guard
      lhs.tooltips == rhs.tooltips,
      lhs.trackActiveStyle == rhs.trackActiveStyle,
      lhs.trackInactiveStyle == rhs.trackInactiveStyle
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

extension DivSlider: Serializable {
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
    result["functions"] = functions?.map { $0.toDictionary() }
    result["height"] = height.toDictionary()
    result["id"] = id
    result["is_enabled"] = isEnabled.toValidSerializationValue()
    result["layout_provider"] = layoutProvider?.toDictionary()
    result["margins"] = margins?.toDictionary()
    result["max_value"] = maxValue.toValidSerializationValue()
    result["min_value"] = minValue.toValidSerializationValue()
    result["paddings"] = paddings?.toDictionary()
    result["ranges"] = ranges?.map { $0.toDictionary() }
    result["reuse_id"] = reuseId?.toValidSerializationValue()
    result["row_span"] = rowSpan?.toValidSerializationValue()
    result["secondary_value_accessibility"] = secondaryValueAccessibility?.toDictionary()
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
    result["transform"] = transform?.toDictionary()
    result["transition_change"] = transitionChange?.toDictionary()
    result["transition_in"] = transitionIn?.toDictionary()
    result["transition_out"] = transitionOut?.toDictionary()
    result["transition_triggers"] = transitionTriggers?.map { $0.rawValue }
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
// WARNING: this == is incomplete because of [String: Any] in class fields
extension DivSlider.TextStyle: Equatable {
  public static func ==(lhs: DivSlider.TextStyle, rhs: DivSlider.TextStyle) -> Bool {
    guard
      lhs.fontFamily == rhs.fontFamily,
      lhs.fontSize == rhs.fontSize,
      lhs.fontSizeUnit == rhs.fontSizeUnit
    else {
      return false
    }
    guard
      lhs.fontWeight == rhs.fontWeight,
      lhs.fontWeightValue == rhs.fontWeightValue,
      lhs.letterSpacing == rhs.letterSpacing
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

#if DEBUG
extension DivSlider.Range: Equatable {
  public static func ==(lhs: DivSlider.Range, rhs: DivSlider.Range) -> Bool {
    guard
      lhs.end == rhs.end,
      lhs.margins == rhs.margins,
      lhs.start == rhs.start
    else {
      return false
    }
    guard
      lhs.trackActiveStyle == rhs.trackActiveStyle,
      lhs.trackInactiveStyle == rhs.trackInactiveStyle
    else {
      return false
    }
    return true
  }
}
#endif

extension DivSlider.Range: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["end"] = end?.toValidSerializationValue()
    result["margins"] = margins?.toDictionary()
    result["start"] = start?.toValidSerializationValue()
    result["track_active_style"] = trackActiveStyle?.toDictionary()
    result["track_inactive_style"] = trackInactiveStyle?.toDictionary()
    return result
  }
}

extension DivSlider.TextStyle: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["font_family"] = fontFamily?.toValidSerializationValue()
    result["font_size"] = fontSize.toValidSerializationValue()
    result["font_size_unit"] = fontSizeUnit.toValidSerializationValue()
    result["font_variation_settings"] = fontVariationSettings?.toValidSerializationValue()
    result["font_weight"] = fontWeight?.toValidSerializationValue()
    result["font_weight_value"] = fontWeightValue?.toValidSerializationValue()
    result["letter_spacing"] = letterSpacing.toValidSerializationValue()
    result["offset"] = offset?.toDictionary()
    result["text_color"] = textColor.toValidSerializationValue()
    return result
  }
}
