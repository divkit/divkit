// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivText: DivBase, @unchecked Sendable {
  @frozen
  public enum Truncate: String, CaseIterable, Sendable {
    case none = "none"
    case start = "start"
    case end = "end"
    case middle = "middle"
  }

  public final class Ellipsis: Sendable {
    public let actions: [DivAction]?
    public let images: [Image]?
    public let ranges: [Range]?
    public let text: Expression<String>

    public func resolveText(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(text)
    }

    init(
      actions: [DivAction]? = nil,
      images: [Image]? = nil,
      ranges: [Range]? = nil,
      text: Expression<String>
    ) {
      self.actions = actions
      self.images = images
      self.ranges = ranges
      self.text = text
    }
  }

  public final class Image: Sendable {
    @frozen
    public enum IndexingDirection: String, CaseIterable, Sendable {
      case normal = "normal"
      case reversed = "reversed"
    }

    public final class Accessibility: Sendable {
      @frozen
      public enum Kind: String, CaseIterable, Sendable {
        case none = "none"
        case button = "button"
        case image = "image"
        case text = "text"
        case auto = "auto"
      }

      public let description: Expression<String>?
      public let type: Kind // default value: auto

      public func resolveDescription(_ resolver: ExpressionResolver) -> String? {
        resolver.resolveString(description)
      }

      init(
        description: Expression<String>? = nil,
        type: Kind? = nil
      ) {
        self.description = description
        self.type = type ?? .auto
      }
    }

    public let accessibility: Accessibility?
    public let alignmentVertical: Expression<DivTextAlignmentVertical> // default value: center
    public let height: DivFixedSize // default value: DivFixedSize(value: .value(20))
    public let indexingDirection: Expression<IndexingDirection> // default value: normal
    public let preloadRequired: Expression<Bool> // default value: false
    public let start: Expression<Int> // constraint: number >= 0
    public let tintColor: Expression<Color>?
    public let tintMode: Expression<DivBlendMode> // default value: source_in
    public let url: Expression<URL>
    public let width: DivFixedSize // default value: DivFixedSize(value: .value(20))

    public func resolveAlignmentVertical(_ resolver: ExpressionResolver) -> DivTextAlignmentVertical {
      resolver.resolveEnum(alignmentVertical) ?? DivTextAlignmentVertical.center
    }

    public func resolveIndexingDirection(_ resolver: ExpressionResolver) -> IndexingDirection {
      resolver.resolveEnum(indexingDirection) ?? IndexingDirection.normal
    }

    public func resolvePreloadRequired(_ resolver: ExpressionResolver) -> Bool {
      resolver.resolveNumeric(preloadRequired) ?? false
    }

    public func resolveStart(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumeric(start)
    }

    public func resolveTintColor(_ resolver: ExpressionResolver) -> Color? {
      resolver.resolveColor(tintColor)
    }

    public func resolveTintMode(_ resolver: ExpressionResolver) -> DivBlendMode {
      resolver.resolveEnum(tintMode) ?? DivBlendMode.sourceIn
    }

    public func resolveUrl(_ resolver: ExpressionResolver) -> URL? {
      resolver.resolveUrl(url)
    }

    static let startValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    init(
      accessibility: Accessibility? = nil,
      alignmentVertical: Expression<DivTextAlignmentVertical>? = nil,
      height: DivFixedSize? = nil,
      indexingDirection: Expression<IndexingDirection>? = nil,
      preloadRequired: Expression<Bool>? = nil,
      start: Expression<Int>,
      tintColor: Expression<Color>? = nil,
      tintMode: Expression<DivBlendMode>? = nil,
      url: Expression<URL>,
      width: DivFixedSize? = nil
    ) {
      self.accessibility = accessibility
      self.alignmentVertical = alignmentVertical ?? .value(.center)
      self.height = height ?? DivFixedSize(value: .value(20))
      self.indexingDirection = indexingDirection ?? .value(.normal)
      self.preloadRequired = preloadRequired ?? .value(false)
      self.start = start
      self.tintColor = tintColor
      self.tintMode = tintMode ?? .value(.sourceIn)
      self.url = url
      self.width = width ?? DivFixedSize(value: .value(20))
    }
  }

  public final class Range: @unchecked Sendable {
    public let actions: [DivAction]?
    public let alignmentVertical: Expression<DivTextAlignmentVertical>?
    public let background: DivTextRangeBackground?
    public let baselineOffset: Expression<Double> // default value: 0
    public let border: DivTextRangeBorder?
    public let end: Expression<Int>? // constraint: number > 0
    public let fontFamily: Expression<String>?
    public let fontFeatureSettings: Expression<String>?
    public let fontSize: Expression<Int>? // constraint: number >= 0
    public let fontSizeUnit: Expression<DivSizeUnit> // default value: sp
    public let fontVariationSettings: Expression<[String: Any]>?
    public let fontWeight: Expression<DivFontWeight>?
    public let fontWeightValue: Expression<Int>? // constraint: number > 0
    public let letterSpacing: Expression<Double>?
    public let lineHeight: Expression<Int>? // constraint: number >= 0
    public let mask: DivTextRangeMask?
    public let start: Expression<Int> // constraint: number >= 0; default value: 0
    public let strike: Expression<DivLineStyle>?
    public let textColor: Expression<Color>?
    public let textShadow: DivShadow?
    public let topOffset: Expression<Int>? // constraint: number >= 0
    public let underline: Expression<DivLineStyle>?

    public func resolveAlignmentVertical(_ resolver: ExpressionResolver) -> DivTextAlignmentVertical? {
      resolver.resolveEnum(alignmentVertical)
    }

    public func resolveBaselineOffset(_ resolver: ExpressionResolver) -> Double {
      resolver.resolveNumeric(baselineOffset) ?? 0
    }

    public func resolveEnd(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumeric(end)
    }

    public func resolveFontFamily(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(fontFamily)
    }

    public func resolveFontFeatureSettings(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(fontFeatureSettings)
    }

    public func resolveFontSize(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumeric(fontSize)
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

    public func resolveLetterSpacing(_ resolver: ExpressionResolver) -> Double? {
      resolver.resolveNumeric(letterSpacing)
    }

    public func resolveLineHeight(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumeric(lineHeight)
    }

    public func resolveStart(_ resolver: ExpressionResolver) -> Int {
      resolver.resolveNumeric(start) ?? 0
    }

    public func resolveStrike(_ resolver: ExpressionResolver) -> DivLineStyle? {
      resolver.resolveEnum(strike)
    }

    public func resolveTextColor(_ resolver: ExpressionResolver) -> Color? {
      resolver.resolveColor(textColor)
    }

    public func resolveTopOffset(_ resolver: ExpressionResolver) -> Int? {
      resolver.resolveNumeric(topOffset)
    }

    public func resolveUnderline(_ resolver: ExpressionResolver) -> DivLineStyle? {
      resolver.resolveEnum(underline)
    }

    static let endValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 > 0 })

    static let fontSizeValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    static let fontWeightValueValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 > 0 })

    static let lineHeightValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    static let startValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    static let topOffsetValidator: AnyValueValidator<Int> =
      makeValueValidator(valueValidator: { $0 >= 0 })

    init(
      actions: [DivAction]? = nil,
      alignmentVertical: Expression<DivTextAlignmentVertical>? = nil,
      background: DivTextRangeBackground? = nil,
      baselineOffset: Expression<Double>? = nil,
      border: DivTextRangeBorder? = nil,
      end: Expression<Int>? = nil,
      fontFamily: Expression<String>? = nil,
      fontFeatureSettings: Expression<String>? = nil,
      fontSize: Expression<Int>? = nil,
      fontSizeUnit: Expression<DivSizeUnit>? = nil,
      fontVariationSettings: Expression<[String: Any]>? = nil,
      fontWeight: Expression<DivFontWeight>? = nil,
      fontWeightValue: Expression<Int>? = nil,
      letterSpacing: Expression<Double>? = nil,
      lineHeight: Expression<Int>? = nil,
      mask: DivTextRangeMask? = nil,
      start: Expression<Int>? = nil,
      strike: Expression<DivLineStyle>? = nil,
      textColor: Expression<Color>? = nil,
      textShadow: DivShadow? = nil,
      topOffset: Expression<Int>? = nil,
      underline: Expression<DivLineStyle>? = nil
    ) {
      self.actions = actions
      self.alignmentVertical = alignmentVertical
      self.background = background
      self.baselineOffset = baselineOffset ?? .value(0)
      self.border = border
      self.end = end
      self.fontFamily = fontFamily
      self.fontFeatureSettings = fontFeatureSettings
      self.fontSize = fontSize
      self.fontSizeUnit = fontSizeUnit ?? .value(.sp)
      self.fontVariationSettings = fontVariationSettings
      self.fontWeight = fontWeight
      self.fontWeightValue = fontWeightValue
      self.letterSpacing = letterSpacing
      self.lineHeight = lineHeight
      self.mask = mask
      self.start = start ?? .value(0)
      self.strike = strike
      self.textColor = textColor
      self.textShadow = textShadow
      self.topOffset = topOffset
      self.underline = underline
    }
  }

  public static let type: String = "text"
  public let accessibility: DivAccessibility?
  public let action: DivAction?
  public let actionAnimation: DivAnimation // default value: DivAnimation(duration: .value(100), endValue: .value(0.6), name: .value(.fade), startValue: .value(1))
  public let actions: [DivAction]?
  public let alignmentHorizontal: Expression<DivAlignmentHorizontal>?
  public let alignmentVertical: Expression<DivAlignmentVertical>?
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let animators: [DivAnimator]?
  public let autoEllipsize: Expression<Bool>?
  public let background: [DivBackground]?
  public let border: DivBorder?
  public let captureFocusOnAction: Expression<Bool> // default value: true
  public let columnSpan: Expression<Int>? // constraint: number >= 0
  public let disappearActions: [DivDisappearAction]?
  public let doubletapActions: [DivAction]?
  public let ellipsis: Ellipsis?
  public let extensions: [DivExtension]?
  public let focus: DivFocus?
  public let focusedTextColor: Expression<Color>?
  public let fontFamily: Expression<String>?
  public let fontFeatureSettings: Expression<String>?
  public let fontSize: Expression<Int> // constraint: number >= 0; default value: 12
  public let fontSizeUnit: Expression<DivSizeUnit> // default value: sp
  public let fontVariationSettings: Expression<[String: Any]>?
  public let fontWeight: Expression<DivFontWeight> // default value: regular
  public let fontWeightValue: Expression<Int>? // constraint: number > 0
  public let functions: [DivFunction]?
  public let height: DivSize // default value: .divWrapContentSize(DivWrapContentSize())
  public let hoverEndActions: [DivAction]?
  public let hoverStartActions: [DivAction]?
  public let id: String?
  public let images: [Image]?
  public let layoutProvider: DivLayoutProvider?
  public let letterSpacing: Expression<Double> // default value: 0
  public let lineHeight: Expression<Int>? // constraint: number >= 0
  public let longtapActions: [DivAction]?
  public let margins: DivEdgeInsets?
  public let maxLines: Expression<Int>? // constraint: number >= 0
  public let minHiddenLines: Expression<Int>? // constraint: number >= 0
  public let paddings: DivEdgeInsets?
  public let pressEndActions: [DivAction]?
  public let pressStartActions: [DivAction]?
  public let ranges: [Range]?
  public let reuseId: Expression<String>?
  public let rowSpan: Expression<Int>? // constraint: number >= 0
  public let selectable: Expression<Bool> // default value: false
  public let selectedActions: [DivAction]?
  public let strike: Expression<DivLineStyle> // default value: none
  public let text: Expression<String>
  public let textAlignmentHorizontal: Expression<DivAlignmentHorizontal> // default value: start
  public let textAlignmentVertical: Expression<DivAlignmentVertical> // default value: top
  public let textColor: Expression<Color> // default value: #FF000000
  public let textGradient: DivTextGradient?
  public let textShadow: DivShadow?
  public let tightenWidth: Expression<Bool> // default value: false
  public let tooltips: [DivTooltip]?
  public let transform: DivTransform?
  public let transitionChange: DivChangeTransition?
  public let transitionIn: DivAppearanceTransition?
  public let transitionOut: DivAppearanceTransition?
  public let transitionTriggers: [DivTransitionTrigger]? // at least 1 elements
  public let truncate: Expression<Truncate> // default value: end
  public let underline: Expression<DivLineStyle> // default value: none
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

  public func resolveAutoEllipsize(_ resolver: ExpressionResolver) -> Bool? {
    resolver.resolveNumeric(autoEllipsize)
  }

  public func resolveCaptureFocusOnAction(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(captureFocusOnAction) ?? true
  }

  public func resolveColumnSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(columnSpan)
  }

  public func resolveFocusedTextColor(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveColor(focusedTextColor)
  }

  public func resolveFontFamily(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(fontFamily)
  }

  public func resolveFontFeatureSettings(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(fontFeatureSettings)
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

  public func resolveFontWeight(_ resolver: ExpressionResolver) -> DivFontWeight {
    resolver.resolveEnum(fontWeight) ?? DivFontWeight.regular
  }

  public func resolveFontWeightValue(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(fontWeightValue)
  }

  public func resolveLetterSpacing(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumeric(letterSpacing) ?? 0
  }

  public func resolveLineHeight(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(lineHeight)
  }

  public func resolveMaxLines(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(maxLines)
  }

  public func resolveMinHiddenLines(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(minHiddenLines)
  }

  public func resolveReuseId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(reuseId)
  }

  public func resolveRowSpan(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(rowSpan)
  }

  public func resolveSelectable(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(selectable) ?? false
  }

  public func resolveStrike(_ resolver: ExpressionResolver) -> DivLineStyle {
    resolver.resolveEnum(strike) ?? DivLineStyle.none
  }

  public func resolveText(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(text)
  }

  public func resolveTextAlignmentHorizontal(_ resolver: ExpressionResolver) -> DivAlignmentHorizontal {
    resolver.resolveEnum(textAlignmentHorizontal) ?? DivAlignmentHorizontal.start
  }

  public func resolveTextAlignmentVertical(_ resolver: ExpressionResolver) -> DivAlignmentVertical {
    resolver.resolveEnum(textAlignmentVertical) ?? DivAlignmentVertical.top
  }

  public func resolveTextColor(_ resolver: ExpressionResolver) -> Color {
    resolver.resolveColor(textColor) ?? Color.colorWithARGBHexCode(0xFF000000)
  }

  public func resolveTightenWidth(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(tightenWidth) ?? false
  }

  public func resolveTruncate(_ resolver: ExpressionResolver) -> Truncate {
    resolver.resolveEnum(truncate) ?? Truncate.end
  }

  public func resolveUnderline(_ resolver: ExpressionResolver) -> DivLineStyle {
    resolver.resolveEnum(underline) ?? DivLineStyle.none
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

  static let maxLinesValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let minHiddenLinesValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let rowSpanValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let transitionTriggersValidator: AnyArrayValueValidator<DivTransitionTrigger> =
    makeArrayValidator(minItems: 1)

  init(
    accessibility: DivAccessibility? = nil,
    action: DivAction? = nil,
    actionAnimation: DivAnimation? = nil,
    actions: [DivAction]? = nil,
    alignmentHorizontal: Expression<DivAlignmentHorizontal>? = nil,
    alignmentVertical: Expression<DivAlignmentVertical>? = nil,
    alpha: Expression<Double>? = nil,
    animators: [DivAnimator]? = nil,
    autoEllipsize: Expression<Bool>? = nil,
    background: [DivBackground]? = nil,
    border: DivBorder? = nil,
    captureFocusOnAction: Expression<Bool>? = nil,
    columnSpan: Expression<Int>? = nil,
    disappearActions: [DivDisappearAction]? = nil,
    doubletapActions: [DivAction]? = nil,
    ellipsis: Ellipsis? = nil,
    extensions: [DivExtension]? = nil,
    focus: DivFocus? = nil,
    focusedTextColor: Expression<Color>? = nil,
    fontFamily: Expression<String>? = nil,
    fontFeatureSettings: Expression<String>? = nil,
    fontSize: Expression<Int>? = nil,
    fontSizeUnit: Expression<DivSizeUnit>? = nil,
    fontVariationSettings: Expression<[String: Any]>? = nil,
    fontWeight: Expression<DivFontWeight>? = nil,
    fontWeightValue: Expression<Int>? = nil,
    functions: [DivFunction]? = nil,
    height: DivSize? = nil,
    hoverEndActions: [DivAction]? = nil,
    hoverStartActions: [DivAction]? = nil,
    id: String? = nil,
    images: [Image]? = nil,
    layoutProvider: DivLayoutProvider? = nil,
    letterSpacing: Expression<Double>? = nil,
    lineHeight: Expression<Int>? = nil,
    longtapActions: [DivAction]? = nil,
    margins: DivEdgeInsets? = nil,
    maxLines: Expression<Int>? = nil,
    minHiddenLines: Expression<Int>? = nil,
    paddings: DivEdgeInsets? = nil,
    pressEndActions: [DivAction]? = nil,
    pressStartActions: [DivAction]? = nil,
    ranges: [Range]? = nil,
    reuseId: Expression<String>? = nil,
    rowSpan: Expression<Int>? = nil,
    selectable: Expression<Bool>? = nil,
    selectedActions: [DivAction]? = nil,
    strike: Expression<DivLineStyle>? = nil,
    text: Expression<String>,
    textAlignmentHorizontal: Expression<DivAlignmentHorizontal>? = nil,
    textAlignmentVertical: Expression<DivAlignmentVertical>? = nil,
    textColor: Expression<Color>? = nil,
    textGradient: DivTextGradient? = nil,
    textShadow: DivShadow? = nil,
    tightenWidth: Expression<Bool>? = nil,
    tooltips: [DivTooltip]? = nil,
    transform: DivTransform? = nil,
    transitionChange: DivChangeTransition? = nil,
    transitionIn: DivAppearanceTransition? = nil,
    transitionOut: DivAppearanceTransition? = nil,
    transitionTriggers: [DivTransitionTrigger]? = nil,
    truncate: Expression<Truncate>? = nil,
    underline: Expression<DivLineStyle>? = nil,
    variableTriggers: [DivTrigger]? = nil,
    variables: [DivVariable]? = nil,
    visibility: Expression<DivVisibility>? = nil,
    visibilityAction: DivVisibilityAction? = nil,
    visibilityActions: [DivVisibilityAction]? = nil,
    width: DivSize? = nil
  ) {
    self.accessibility = accessibility
    self.action = action
    self.actionAnimation = actionAnimation ?? DivAnimation(duration: .value(100), endValue: .value(0.6), name: .value(.fade), startValue: .value(1))
    self.actions = actions
    self.alignmentHorizontal = alignmentHorizontal
    self.alignmentVertical = alignmentVertical
    self.alpha = alpha ?? .value(1.0)
    self.animators = animators
    self.autoEllipsize = autoEllipsize
    self.background = background
    self.border = border
    self.captureFocusOnAction = captureFocusOnAction ?? .value(true)
    self.columnSpan = columnSpan
    self.disappearActions = disappearActions
    self.doubletapActions = doubletapActions
    self.ellipsis = ellipsis
    self.extensions = extensions
    self.focus = focus
    self.focusedTextColor = focusedTextColor
    self.fontFamily = fontFamily
    self.fontFeatureSettings = fontFeatureSettings
    self.fontSize = fontSize ?? .value(12)
    self.fontSizeUnit = fontSizeUnit ?? .value(.sp)
    self.fontVariationSettings = fontVariationSettings
    self.fontWeight = fontWeight ?? .value(.regular)
    self.fontWeightValue = fontWeightValue
    self.functions = functions
    self.height = height ?? .divWrapContentSize(DivWrapContentSize())
    self.hoverEndActions = hoverEndActions
    self.hoverStartActions = hoverStartActions
    self.id = id
    self.images = images
    self.layoutProvider = layoutProvider
    self.letterSpacing = letterSpacing ?? .value(0)
    self.lineHeight = lineHeight
    self.longtapActions = longtapActions
    self.margins = margins
    self.maxLines = maxLines
    self.minHiddenLines = minHiddenLines
    self.paddings = paddings
    self.pressEndActions = pressEndActions
    self.pressStartActions = pressStartActions
    self.ranges = ranges
    self.reuseId = reuseId
    self.rowSpan = rowSpan
    self.selectable = selectable ?? .value(false)
    self.selectedActions = selectedActions
    self.strike = strike ?? .value(.none)
    self.text = text
    self.textAlignmentHorizontal = textAlignmentHorizontal ?? .value(.start)
    self.textAlignmentVertical = textAlignmentVertical ?? .value(.top)
    self.textColor = textColor ?? .value(Color.colorWithARGBHexCode(0xFF000000))
    self.textGradient = textGradient
    self.textShadow = textShadow
    self.tightenWidth = tightenWidth ?? .value(false)
    self.tooltips = tooltips
    self.transform = transform
    self.transitionChange = transitionChange
    self.transitionIn = transitionIn
    self.transitionOut = transitionOut
    self.transitionTriggers = transitionTriggers
    self.truncate = truncate ?? .value(.end)
    self.underline = underline ?? .value(.none)
    self.variableTriggers = variableTriggers
    self.variables = variables
    self.visibility = visibility ?? .value(.visible)
    self.visibilityAction = visibilityAction
    self.visibilityActions = visibilityActions
    self.width = width ?? .divMatchParentSize(DivMatchParentSize())
  }
}

#if DEBUG
// WARNING: this == is incomplete because of [String: Any] in class fields
extension DivText: Equatable {
  public static func ==(lhs: DivText, rhs: DivText) -> Bool {
    guard
      lhs.accessibility == rhs.accessibility,
      lhs.action == rhs.action,
      lhs.actionAnimation == rhs.actionAnimation
    else {
      return false
    }
    guard
      lhs.actions == rhs.actions,
      lhs.alignmentHorizontal == rhs.alignmentHorizontal,
      lhs.alignmentVertical == rhs.alignmentVertical
    else {
      return false
    }
    guard
      lhs.alpha == rhs.alpha,
      lhs.animators == rhs.animators,
      lhs.autoEllipsize == rhs.autoEllipsize
    else {
      return false
    }
    guard
      lhs.background == rhs.background,
      lhs.border == rhs.border,
      lhs.captureFocusOnAction == rhs.captureFocusOnAction
    else {
      return false
    }
    guard
      lhs.columnSpan == rhs.columnSpan,
      lhs.disappearActions == rhs.disappearActions,
      lhs.doubletapActions == rhs.doubletapActions
    else {
      return false
    }
    guard
      lhs.ellipsis == rhs.ellipsis,
      lhs.extensions == rhs.extensions,
      lhs.focus == rhs.focus
    else {
      return false
    }
    guard
      lhs.focusedTextColor == rhs.focusedTextColor,
      lhs.fontFamily == rhs.fontFamily,
      lhs.fontFeatureSettings == rhs.fontFeatureSettings
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
      lhs.hoverEndActions == rhs.hoverEndActions,
      lhs.hoverStartActions == rhs.hoverStartActions,
      lhs.id == rhs.id
    else {
      return false
    }
    guard
      lhs.images == rhs.images,
      lhs.layoutProvider == rhs.layoutProvider,
      lhs.letterSpacing == rhs.letterSpacing
    else {
      return false
    }
    guard
      lhs.lineHeight == rhs.lineHeight,
      lhs.longtapActions == rhs.longtapActions,
      lhs.margins == rhs.margins
    else {
      return false
    }
    guard
      lhs.maxLines == rhs.maxLines,
      lhs.minHiddenLines == rhs.minHiddenLines,
      lhs.paddings == rhs.paddings
    else {
      return false
    }
    guard
      lhs.pressEndActions == rhs.pressEndActions,
      lhs.pressStartActions == rhs.pressStartActions,
      lhs.ranges == rhs.ranges
    else {
      return false
    }
    guard
      lhs.reuseId == rhs.reuseId,
      lhs.rowSpan == rhs.rowSpan,
      lhs.selectable == rhs.selectable
    else {
      return false
    }
    guard
      lhs.selectedActions == rhs.selectedActions,
      lhs.strike == rhs.strike,
      lhs.text == rhs.text
    else {
      return false
    }
    guard
      lhs.textAlignmentHorizontal == rhs.textAlignmentHorizontal,
      lhs.textAlignmentVertical == rhs.textAlignmentVertical,
      lhs.textColor == rhs.textColor
    else {
      return false
    }
    guard
      lhs.textGradient == rhs.textGradient,
      lhs.textShadow == rhs.textShadow,
      lhs.tightenWidth == rhs.tightenWidth
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
      lhs.truncate == rhs.truncate,
      lhs.underline == rhs.underline,
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

extension DivText: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["accessibility"] = accessibility?.toDictionary()
    result["action"] = action?.toDictionary()
    result["action_animation"] = actionAnimation.toDictionary()
    result["actions"] = actions?.map { $0.toDictionary() }
    result["alignment_horizontal"] = alignmentHorizontal?.toValidSerializationValue()
    result["alignment_vertical"] = alignmentVertical?.toValidSerializationValue()
    result["alpha"] = alpha.toValidSerializationValue()
    result["animators"] = animators?.map { $0.toDictionary() }
    result["auto_ellipsize"] = autoEllipsize?.toValidSerializationValue()
    result["background"] = background?.map { $0.toDictionary() }
    result["border"] = border?.toDictionary()
    result["capture_focus_on_action"] = captureFocusOnAction.toValidSerializationValue()
    result["column_span"] = columnSpan?.toValidSerializationValue()
    result["disappear_actions"] = disappearActions?.map { $0.toDictionary() }
    result["doubletap_actions"] = doubletapActions?.map { $0.toDictionary() }
    result["ellipsis"] = ellipsis?.toDictionary()
    result["extensions"] = extensions?.map { $0.toDictionary() }
    result["focus"] = focus?.toDictionary()
    result["focused_text_color"] = focusedTextColor?.toValidSerializationValue()
    result["font_family"] = fontFamily?.toValidSerializationValue()
    result["font_feature_settings"] = fontFeatureSettings?.toValidSerializationValue()
    result["font_size"] = fontSize.toValidSerializationValue()
    result["font_size_unit"] = fontSizeUnit.toValidSerializationValue()
    result["font_variation_settings"] = fontVariationSettings?.toValidSerializationValue()
    result["font_weight"] = fontWeight.toValidSerializationValue()
    result["font_weight_value"] = fontWeightValue?.toValidSerializationValue()
    result["functions"] = functions?.map { $0.toDictionary() }
    result["height"] = height.toDictionary()
    result["hover_end_actions"] = hoverEndActions?.map { $0.toDictionary() }
    result["hover_start_actions"] = hoverStartActions?.map { $0.toDictionary() }
    result["id"] = id
    result["images"] = images?.map { $0.toDictionary() }
    result["layout_provider"] = layoutProvider?.toDictionary()
    result["letter_spacing"] = letterSpacing.toValidSerializationValue()
    result["line_height"] = lineHeight?.toValidSerializationValue()
    result["longtap_actions"] = longtapActions?.map { $0.toDictionary() }
    result["margins"] = margins?.toDictionary()
    result["max_lines"] = maxLines?.toValidSerializationValue()
    result["min_hidden_lines"] = minHiddenLines?.toValidSerializationValue()
    result["paddings"] = paddings?.toDictionary()
    result["press_end_actions"] = pressEndActions?.map { $0.toDictionary() }
    result["press_start_actions"] = pressStartActions?.map { $0.toDictionary() }
    result["ranges"] = ranges?.map { $0.toDictionary() }
    result["reuse_id"] = reuseId?.toValidSerializationValue()
    result["row_span"] = rowSpan?.toValidSerializationValue()
    result["selectable"] = selectable.toValidSerializationValue()
    result["selected_actions"] = selectedActions?.map { $0.toDictionary() }
    result["strike"] = strike.toValidSerializationValue()
    result["text"] = text.toValidSerializationValue()
    result["text_alignment_horizontal"] = textAlignmentHorizontal.toValidSerializationValue()
    result["text_alignment_vertical"] = textAlignmentVertical.toValidSerializationValue()
    result["text_color"] = textColor.toValidSerializationValue()
    result["text_gradient"] = textGradient?.toDictionary()
    result["text_shadow"] = textShadow?.toDictionary()
    result["tighten_width"] = tightenWidth.toValidSerializationValue()
    result["tooltips"] = tooltips?.map { $0.toDictionary() }
    result["transform"] = transform?.toDictionary()
    result["transition_change"] = transitionChange?.toDictionary()
    result["transition_in"] = transitionIn?.toDictionary()
    result["transition_out"] = transitionOut?.toDictionary()
    result["transition_triggers"] = transitionTriggers?.map { $0.rawValue }
    result["truncate"] = truncate.toValidSerializationValue()
    result["underline"] = underline.toValidSerializationValue()
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
extension DivText.Range: Equatable {
  public static func ==(lhs: DivText.Range, rhs: DivText.Range) -> Bool {
    guard
      lhs.actions == rhs.actions,
      lhs.alignmentVertical == rhs.alignmentVertical,
      lhs.background == rhs.background
    else {
      return false
    }
    guard
      lhs.baselineOffset == rhs.baselineOffset,
      lhs.border == rhs.border,
      lhs.end == rhs.end
    else {
      return false
    }
    guard
      lhs.fontFamily == rhs.fontFamily,
      lhs.fontFeatureSettings == rhs.fontFeatureSettings,
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
      lhs.letterSpacing == rhs.letterSpacing,
      lhs.lineHeight == rhs.lineHeight,
      lhs.mask == rhs.mask
    else {
      return false
    }
    guard
      lhs.start == rhs.start,
      lhs.strike == rhs.strike,
      lhs.textColor == rhs.textColor
    else {
      return false
    }
    guard
      lhs.textShadow == rhs.textShadow,
      lhs.topOffset == rhs.topOffset,
      lhs.underline == rhs.underline
    else {
      return false
    }
    return true
  }
}
#endif

#if DEBUG
extension DivText.Ellipsis: Equatable {
  public static func ==(lhs: DivText.Ellipsis, rhs: DivText.Ellipsis) -> Bool {
    guard
      lhs.actions == rhs.actions,
      lhs.images == rhs.images,
      lhs.ranges == rhs.ranges
    else {
      return false
    }
    guard
      lhs.text == rhs.text
    else {
      return false
    }
    return true
  }
}
#endif

#if DEBUG
extension DivText.Image.Accessibility: Equatable {
  public static func ==(lhs: DivText.Image.Accessibility, rhs: DivText.Image.Accessibility) -> Bool {
    guard
      lhs.description == rhs.description,
      lhs.type == rhs.type
    else {
      return false
    }
    return true
  }
}
#endif

#if DEBUG
extension DivText.Image: Equatable {
  public static func ==(lhs: DivText.Image, rhs: DivText.Image) -> Bool {
    guard
      lhs.accessibility == rhs.accessibility,
      lhs.alignmentVertical == rhs.alignmentVertical,
      lhs.height == rhs.height
    else {
      return false
    }
    guard
      lhs.indexingDirection == rhs.indexingDirection,
      lhs.preloadRequired == rhs.preloadRequired,
      lhs.start == rhs.start
    else {
      return false
    }
    guard
      lhs.tintColor == rhs.tintColor,
      lhs.tintMode == rhs.tintMode,
      lhs.url == rhs.url
    else {
      return false
    }
    guard
      lhs.width == rhs.width
    else {
      return false
    }
    return true
  }
}
#endif

extension DivText.Ellipsis: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["actions"] = actions?.map { $0.toDictionary() }
    result["images"] = images?.map { $0.toDictionary() }
    result["ranges"] = ranges?.map { $0.toDictionary() }
    result["text"] = text.toValidSerializationValue()
    return result
  }
}

extension DivText.Image.Accessibility: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["description"] = description?.toValidSerializationValue()
    result["type"] = type.rawValue
    return result
  }
}

extension DivText.Image: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["accessibility"] = accessibility?.toDictionary()
    result["alignment_vertical"] = alignmentVertical.toValidSerializationValue()
    result["height"] = height.toDictionary()
    result["indexing_direction"] = indexingDirection.toValidSerializationValue()
    result["preload_required"] = preloadRequired.toValidSerializationValue()
    result["start"] = start.toValidSerializationValue()
    result["tint_color"] = tintColor?.toValidSerializationValue()
    result["tint_mode"] = tintMode.toValidSerializationValue()
    result["url"] = url.toValidSerializationValue()
    result["width"] = width.toDictionary()
    return result
  }
}

extension DivText.Range: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["actions"] = actions?.map { $0.toDictionary() }
    result["alignment_vertical"] = alignmentVertical?.toValidSerializationValue()
    result["background"] = background?.toDictionary()
    result["baseline_offset"] = baselineOffset.toValidSerializationValue()
    result["border"] = border?.toDictionary()
    result["end"] = end?.toValidSerializationValue()
    result["font_family"] = fontFamily?.toValidSerializationValue()
    result["font_feature_settings"] = fontFeatureSettings?.toValidSerializationValue()
    result["font_size"] = fontSize?.toValidSerializationValue()
    result["font_size_unit"] = fontSizeUnit.toValidSerializationValue()
    result["font_variation_settings"] = fontVariationSettings?.toValidSerializationValue()
    result["font_weight"] = fontWeight?.toValidSerializationValue()
    result["font_weight_value"] = fontWeightValue?.toValidSerializationValue()
    result["letter_spacing"] = letterSpacing?.toValidSerializationValue()
    result["line_height"] = lineHeight?.toValidSerializationValue()
    result["mask"] = mask?.toDictionary()
    result["start"] = start.toValidSerializationValue()
    result["strike"] = strike?.toValidSerializationValue()
    result["text_color"] = textColor?.toValidSerializationValue()
    result["text_shadow"] = textShadow?.toDictionary()
    result["top_offset"] = topOffset?.toValidSerializationValue()
    result["underline"] = underline?.toValidSerializationValue()
    return result
  }
}
