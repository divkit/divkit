// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics
import CoreText
import Foundation

import BaseTinyPublic

private let DKParagraphStyleAttributeName = NSAttributedString.Key("DKParagraphStyle")
private let DKAllowHeightOverrunAttributeName = NSAttributedString.Key("DKAllowHeightOverrun")

private struct ParagraphStyle: Equatable {
  var lineSpacing: CGFloat?
  var paragraphSpacing: CGFloat?
  var alignment: TextAlignment?
  var headIndent: CGFloat?
  var tailIndent: CGFloat?
  var firstLineHeadIndent: CGFloat?
  var minimumLineHeight: CGFloat?
  var maximumLineHeight: CGFloat?
  var lineBreakMode: LineBreakMode?
  var baseWritingDirection: WritingDirection?
  var lineHeightMultiple: CGFloat?
  var paragraphSpacingBefore: CGFloat?
  var hyphenationFactor: Float?
  var tabStops: [TextTab]?
  var defaultTabInterval: CGFloat?
  var allowsDefaultTighteningForTruncation: Bool?

  init(
    lineSpacing: CGFloat? = nil,
    paragraphSpacing: CGFloat? = nil,
    alignment: TextAlignment? = nil,
    headIndent: CGFloat? = nil,
    tailIndent: CGFloat? = nil,
    firstLineHeadIndent: CGFloat? = nil,
    minimumLineHeight: CGFloat? = nil,
    maximumLineHeight: CGFloat? = nil,
    lineBreakMode: LineBreakMode? = nil,
    baseWritingDirection: WritingDirection? = nil,
    lineHeightMultiple: CGFloat? = nil,
    paragraphSpacingBefore: CGFloat? = nil,
    hyphenationFactor: Float? = nil,
    tabStops: [TextTab]? = nil,
    defaultTabInterval: CGFloat? = nil,
    allowsDefaultTighteningForTruncation: Bool? = nil
  ) {
    self.lineSpacing = lineSpacing
    self.paragraphSpacing = paragraphSpacing
    self.alignment = alignment
    self.headIndent = headIndent
    self.tailIndent = tailIndent
    self.firstLineHeadIndent = firstLineHeadIndent
    self.minimumLineHeight = minimumLineHeight
    self.maximumLineHeight = maximumLineHeight
    self.lineBreakMode = lineBreakMode
    self.baseWritingDirection = baseWritingDirection
    self.lineHeightMultiple = lineHeightMultiple
    self.paragraphSpacingBefore = paragraphSpacingBefore
    self.hyphenationFactor = hyphenationFactor
    self.tabStops = tabStops
    self.defaultTabInterval = defaultTabInterval
    self.allowsDefaultTighteningForTruncation = allowsDefaultTighteningForTruncation
  }

  init?(nsParagraphStyle style: NSParagraphStyle?) {
    guard let style = style else {
      return nil
    }

    let reference = NSParagraphStyle()

    self.lineSpacing = reference.lineSpacing != style.lineSpacing ? style.lineSpacing : nil
    self.paragraphSpacing = reference.paragraphSpacing != style.paragraphSpacing ? style
      .paragraphSpacing : nil
    self.alignment = reference.alignment != style.alignment ? style.alignment : nil
    self.headIndent = reference.headIndent != style.headIndent ? style.headIndent : nil
    self.tailIndent = reference.tailIndent != style.tailIndent ? style.tailIndent : nil
    self.firstLineHeadIndent = reference.firstLineHeadIndent != style.firstLineHeadIndent ? style
      .firstLineHeadIndent : nil
    self.minimumLineHeight = reference.minimumLineHeight != style.minimumLineHeight ? style
      .minimumLineHeight : nil
    self.maximumLineHeight = reference.maximumLineHeight != style.maximumLineHeight ? style
      .maximumLineHeight : nil
    self.lineBreakMode = reference.lineBreakMode != style.lineBreakMode ? style.lineBreakMode : nil
    self.baseWritingDirection = reference.baseWritingDirection != style.baseWritingDirection ? style
      .baseWritingDirection : nil
    self.lineHeightMultiple = reference.lineHeightMultiple != style.lineHeightMultiple ? style
      .lineHeightMultiple : nil
    self.paragraphSpacingBefore = reference.paragraphSpacingBefore != style
      .paragraphSpacingBefore ? style.paragraphSpacingBefore : nil
    self.hyphenationFactor = reference.hyphenationFactor != style.hyphenationFactor ? style
      .hyphenationFactor : nil
    self.tabStops = reference.tabStops != style.tabStops ? style.tabStops : nil
    self.defaultTabInterval = reference.defaultTabInterval != style.defaultTabInterval ? style
      .defaultTabInterval : nil
    self.allowsDefaultTighteningForTruncation = reference
      .allowsDefaultTighteningForTruncation != style.allowsDefaultTighteningForTruncation ? style
      .allowsDefaultTighteningForTruncation : nil
  }

  var nsParagraphStyle: NSParagraphStyle {
    let result = NSMutableParagraphStyle()

    setOptional(lineSpacing) { result.lineSpacing = $0 }
    setOptional(paragraphSpacing) { result.paragraphSpacing = $0 }
    setOptional(alignment) { result.alignment = $0 }
    setOptional(headIndent) { result.headIndent = $0 }
    setOptional(tailIndent) { result.tailIndent = $0 }
    setOptional(firstLineHeadIndent) { result.firstLineHeadIndent = $0 }
    setOptional(minimumLineHeight) { result.minimumLineHeight = $0 }
    setOptional(maximumLineHeight) { result.maximumLineHeight = $0 }
    setOptional(lineBreakMode) { result.lineBreakMode = $0 }
    setOptional(baseWritingDirection) { result.baseWritingDirection = $0 }
    setOptional(lineHeightMultiple) { result.lineHeightMultiple = $0 }
    setOptional(paragraphSpacingBefore) { result.paragraphSpacingBefore = $0 }
    setOptional(hyphenationFactor) { result.hyphenationFactor = $0 }
    setOptional(tabStops) { result.tabStops = $0 }
    setOptional(defaultTabInterval) { result.defaultTabInterval = $0 }
    setOptional(allowsDefaultTighteningForTruncation) {
      result.allowsDefaultTighteningForTruncation = $0
    }

    return result
  }

  static func +(lhs: ParagraphStyle, rhs: ParagraphStyle) -> ParagraphStyle {
    var result = rhs

    result.lineSpacing = rhs.lineSpacing ?? lhs.lineSpacing
    result.paragraphSpacing = rhs.paragraphSpacing ?? lhs.paragraphSpacing
    result.alignment = rhs.alignment ?? lhs.alignment
    result.headIndent = rhs.headIndent ?? lhs.headIndent
    result.tailIndent = rhs.tailIndent ?? lhs.tailIndent
    result.firstLineHeadIndent = rhs.firstLineHeadIndent ?? lhs.firstLineHeadIndent
    result.minimumLineHeight = rhs.minimumLineHeight ?? lhs.minimumLineHeight
    result.maximumLineHeight = rhs.maximumLineHeight ?? lhs.maximumLineHeight
    result.lineBreakMode = rhs.lineBreakMode ?? lhs.lineBreakMode
    result.baseWritingDirection = rhs.baseWritingDirection ?? lhs.baseWritingDirection
    result.lineHeightMultiple = rhs.lineHeightMultiple ?? lhs.lineHeightMultiple
    result.paragraphSpacingBefore = rhs.paragraphSpacingBefore ?? lhs.paragraphSpacingBefore
    result.hyphenationFactor = rhs.hyphenationFactor ?? lhs.hyphenationFactor
    result.tabStops = rhs.tabStops ?? lhs.tabStops
    result.defaultTabInterval = rhs.defaultTabInterval ?? lhs.defaultTabInterval
    result.allowsDefaultTighteningForTruncation = rhs.allowsDefaultTighteningForTruncation ?? lhs
      .allowsDefaultTighteningForTruncation

    return result
  }
}

private func setOptional<T>(_ value: T?, _ setter: (T) -> Void) {
  if let value = value {
    setter(value)
  }
}

extension Dictionary {
  fileprivate mutating func fastNonResettingSetValue(_ value: Value?, for key: Key) {
    setOptional(value) { self[key] = $0 }
  }
}

public struct Shadow: Equatable {
  public let offset: CGSize
  public let blurRadius: CGFloat
  public let color: Color?

  public init(offset: CGSize, blurRadius: CGFloat, color: Color?) {
    self.offset = offset
    self.blurRadius = blurRadius
    self.color = color
  }

  public var systemShadow: SystemShadow {
    let result = SystemShadow()
    result.shadowOffset = offset.withInvertedHeightForIOS14
    result.shadowBlurRadius = blurRadius
    result.shadowColor = color?.systemColor
    return result
  }
}

public struct Typo: Equatable, CustomDebugStringConvertible {
  private let font: Font?
  private let paragraphStyle: ParagraphStyle?
  private let kern: CGFloat?
  private let baselineOffset: CGFloat?
  private let strikethrough: UnderlineStyle?
  private let underline: UnderlineStyle?
  private let color: Color?
  private let strokeColor: Color?
  private let strokeWidth: CGFloat?
  private let shadow: Shadow?
  private let heightOverrunAllowed: Bool?
  private let uncategorizedAttributes: [NSAttributedString.Key: Any]?
  private let linkColor: Color?

  public var attributes: [NSAttributedString.Key: Any] {
    var result: [NSAttributedString.Key: Any] = uncategorizedAttributes ??
      .init(minimumCapacity: 11)
    result.fastNonResettingSetValue(font, for: .font)
    result.fastNonResettingSetValue(paragraphStyle?.nsParagraphStyle, for: .paragraphStyle)
    // setting kern to 0 alters rendering, despite the fact that it is default kern value
    if kern?.isApproximatelyEqualTo(0) == false {
      result.fastNonResettingSetValue(kern, for: .kern)
    }
    result.fastNonResettingSetValue(baselineOffset, for: .baselineOffset)
    result.fastNonResettingSetValue(strikethrough?.rawValue, for: .strikethroughStyle)
    result.fastNonResettingSetValue(underline?.rawValue, for: .underlineStyle)
    result.fastNonResettingSetValue(strokeColor?.systemColor, for: .strokeColor)
    result.fastNonResettingSetValue(strokeWidth, for: .strokeWidth)
    result.fastNonResettingSetValue(shadow?.systemShadow, for: .shadow)
    result.fastNonResettingSetValue(heightOverrunAllowed, for: DKAllowHeightOverrunAttributeName)

    if let linkColor = linkColor /* , result[ActionsAttribute.Key] != nil */ {
      result.fastNonResettingSetValue(linkColor.systemColor, for: .foregroundColor)
    } else {
      result.fastNonResettingSetValue(color?.systemColor, for: .foregroundColor)
    }

    return result
  }

  public static func +(lhs: Typo, rhs: Typo) -> Typo {
    Typo(
      font: rhs.font ?? lhs.font,
      paragraphStyle: lhs.paragraphStyle + rhs.paragraphStyle,
      kern: rhs.kern ?? lhs.kern,
      baselineOffset: rhs.baselineOffset ?? lhs.baselineOffset,
      strikethrough: rhs.strikethrough ?? lhs.strikethrough,
      underline: rhs.underline ?? lhs.underline,
      color: rhs.color ?? lhs.color,
      strokeColor: rhs.strokeColor ?? lhs.strokeColor,
      strokeWidth: rhs.strokeWidth ?? lhs.strokeWidth,
      shadow: rhs.shadow ?? lhs.shadow,
      heightOverrunAllowed: rhs.heightOverrunAllowed ?? lhs.heightOverrunAllowed,
      uncategorizedAttributes: lhs.uncategorizedAttributes + rhs.uncategorizedAttributes,
      linkColor: rhs.linkColor ?? lhs.linkColor
    )
  }

  public var leftAligned: Typo {
    self + Typo(alignment: .left)
  }

  public var centered: Typo {
    self + Typo(alignment: .center)
  }

  public var rightAligned: Typo {
    self + Typo(alignment: .right)
  }

  public func with(alignment: TextAlignment) -> Typo {
    self + Typo(alignment: alignment)
  }

  public func with(color: Color) -> Typo {
    self + Typo(color: color)
  }

  public func with(linkColor: Color) -> Typo {
    self + Typo(linkColor: linkColor)
  }

  public func with(strokeColor: Color, width: CGFloat) -> Typo {
    self + Typo(strokeColor: strokeColor, width: width)
  }

  public func kerned(_ kern: Kern) -> Typo {
    self + Typo(kern: kern)
  }

  public func kerned(_ kern: CGFloat) -> Typo {
    self + Typo(kern: kern)
  }

  public func with(height: FontLineHeight) -> Typo {
    self + Typo(height: height)
  }

  public func with(minimumLineHeight: FontLineHeight) -> Typo {
    self + Typo(minimumLineHeight: minimumLineHeight)
  }

  public func with(height: CGFloat) -> Typo {
    self + Typo(height: height)
  }

  public func with(minHeight: CGFloat, maxHeight: CGFloat) -> Typo {
    self + Typo(minHeight: minHeight, maxHeight: maxHeight)
  }

  public func with(baseline: CGFloat) -> Typo {
    self + Typo(baselineOffset: baseline)
  }

  public func with(lineSpacing: CGFloat) -> Typo {
    self + Typo(lineSpacing: lineSpacing)
  }

  public func with(lineHeightMultiple: CGFloat) -> Typo {
    self + Typo(lineHeightMultiple: lineHeightMultiple)
  }

  public func with(font: Font) -> Typo {
    self + Typo(font: font)
  }

  public func with(lineBreakMode: LineBreakMode) -> Typo {
    self + Typo(lineBreakMode: lineBreakMode)
  }

  public func with(fontWeight weight: FontWeight) -> Typo {
    let size = font!.pointSize
    return self + Typo(size: size, weight: weight)
  }

  public func with(writingDirection: WritingDirection) -> Typo {
    self + Typo(writingDirection: writingDirection)
  }

  public func hyphenated(_ hyphenationFactor: Float) -> Typo {
    self + Typo(hyphenationFactor: hyphenationFactor)
  }

  public func shaded(_ shadow: Shadow) -> Typo {
    self + Typo(shadow: shadow)
  }

  public func struckThrough(_ style: UnderlineStyle) -> Typo {
    self + Typo(strikethrough: style)
  }

  public func underlined(_ style: UnderlineStyle) -> Typo {
    self + Typo(underline: style)
  }

  public func scaled(by scale: CGFloat) -> Typo {
    guard scale != 1 else { return self }

    return Typo(
      font: font?.scaled(by: scale),
      paragraphStyle: paragraphStyle?.scaled(by: scale),
      kern: kern.map { $0 * scale },
      baselineOffset: baselineOffset.map { $0 * scale },
      strikethrough: strikethrough,
      underline: underline,
      color: color,
      strokeColor: strokeColor,
      strokeWidth: strokeWidth,
      shadow: shadow,
      heightOverrunAllowed: heightOverrunAllowed,
      uncategorizedAttributes: uncategorizedAttributes,
      linkColor: linkColor
    )
  }

  public var allowHeightOverrun: Typo {
    #if INTERNAL_BUILD
    return self + Typo(heightOverrunAllowed: true)
    #else
    return self
    #endif
  }

  #if INTERNAL_BUILD
  public var isHeightSufficient: Bool {
    guard let font = font,
          let paragraphStyle = paragraphStyle,
          let maxLineHeight = paragraphStyle.maximumLineHeight else {
      return true
    }
    let baselineOffset = self.baselineOffset ?? 0
    let allowOverrun = heightOverrunAllowed ?? false
    let heightSufficient = allowOverrun ||
      (font.defaultLineHeight() + baselineOffset <= maxLineHeight)
    return heightSufficient
  }
  #endif

  public func apply(to str: CFMutableAttributedString, at range: CFRange) {
    str.apply(font, at: range, name: kCTFontAttributeName)
    str.apply(paragraphStyle?.nsParagraphStyle, at: range, name: kCTParagraphStyleAttributeName)
    if kern?.isApproximatelyEqualTo(0) == false {
      // setting kern to 0 alters rendering, despite the fact that it is default kern value
      str.apply(kern.map { $0 as NSNumber }, at: range, name: kCTKernAttributeName)
    }
    str.apply(baselineOffset.map { $0 as NSNumber }, at: range, name: baselineOffsetCFKey)
    str.apply(
      strikethrough.map { $0.rawValue as NSNumber },
      at: range,
      name: strikethroughStyleCFKey
    )
    str.apply(
      underline.map { $0.rawValue as NSNumber },
      at: range,
      name: kCTUnderlineStyleAttributeName
    )
    str.apply(color?.cgColor, at: range, name: kCTForegroundColorAttributeName)
    str.apply(strokeColor?.cgColor, at: range, name: kCTStrokeColorAttributeName)
    str.apply(strokeWidth.map { $0 as NSNumber }, at: range, name: kCTStrokeWidthAttributeName)
    str.apply(shadow?.systemShadow, at: range, name: shadowAttributeCFKey)
    str.apply(heightOverrunAllowed.map { $0 as NSNumber }, at: range, name: allowHeightOverrunCFKey)
    uncategorizedAttributes?.forEach {
      str.apply($0.value as AnyObject, at: range, name: $0.key as CFString)
    }
  }

  public static func ==(lhs: Typo, rhs: Typo) -> Bool {
    lhs.font == rhs.font
      && lhs.paragraphStyle == rhs.paragraphStyle
      && lhs.kern == rhs.kern
      && lhs.baselineOffset == rhs.baselineOffset
      && lhs.strikethrough == rhs.strikethrough
      && lhs.underline == rhs.underline
      && lhs.color == rhs.color
      && lhs.strokeColor == rhs.strokeColor
      && lhs.strokeWidth == rhs.strokeWidth
      && lhs.shadow == rhs.shadow
      && lhs.heightOverrunAllowed == rhs.heightOverrunAllowed
      && lhs.uncategorizedAttributes as NSDictionary? == rhs
      .uncategorizedAttributes as NSDictionary?
  }

  public var debugDescription: String {
    var namedFields: [(String, String?)] = [
      ("Font", font?.prettyDescription),
      ("Kern", kern.map(String.init)),
      ("Baseline offset", baselineOffset.map(String.init)),
      ("Strikethrough", strikethrough?.prettyDescription),
      ("Underline", underline?.prettyDescription),
      ("Color", color.map(String.init(describing:))),
      ("Stroke width", strokeWidth.map(String.init)),
      ("Stroke color", strokeColor.map(String.init(describing:))),
      ("Shadow", shadow?.prettyDescription),
      ("Height overrun", heightOverrunAllowed?.description),
    ]
    if let paragraphStyleNamedFields = paragraphStyle?.namedFields {
      namedFields += paragraphStyleNamedFields
    }
    namedFields.append(("Uncategorized", uncategorizedAttributes.map(String.init)))

    guard namedFields.contains(where: { $0.1 != nil })
      || uncategorizedAttributes != nil
    else {
      return "None"
    }

    return namedFields.compactMap { name, description in
      description.map { name + ": " + $0 }
    }.joined(separator: "\n")
  }
}

extension Font {
  fileprivate var prettyDescription: String {
    "\(fontName) \(pointSize)pt Traits: \(fontDescriptor.symbolicTraits.rawValue)"
  }
}

extension Shadow {
  fileprivate var prettyDescription: String {
    let colorDescription = color?.debugDescription ?? "No color"
    return "\(colorDescription) Offset \(offset.width) x \(offset.height) Radius \(blurRadius)"
  }
}

extension UnderlineStyle {
  private enum NumberOfLines {
    case single
    case double
  }

  private enum LineStyle {
    case solid
    case dash
    case dot
    case dashDot
    case dashDotDot

    func description(forNumberOfLines numberOfLines: NumberOfLines) -> String {
      switch (self, numberOfLines) {
      case (.solid, .single):
        return "————————"
      case (.solid, .double):
        return " ̳ ̳ ̳ ̳ ̳ ̳ ̳ ̳"
      case (.dash, .single):
        return "--------"
      case (.dash, .double):
        return "‗‗‗‗‗‗‗‗"
      case (.dot, .single):
        return "......"
      case (.dot, .double):
        return "::::::"
      case (.dashDot, .single):
        return "-‧-‧-‧-‧-"
      case (.dashDot, .double):
        return "=﹕=﹕=﹕="
      case (.dashDotDot, .single):
        return "-‧‧-‧‧-‧‧-"
      case (.dashDotDot, .double):
        return "=﹕﹕=﹕﹕="
      }
    }
  }

  private var numberOfLines: NumberOfLines? {
    if contains(.double) {
      return .double
    } else if contains(.single) {
      return .single
    } else {
      return nil
    }
  }

  private var lineStyle: LineStyle {
    if contains(.patternDashDot) {
      return .dashDot
    } else if contains(.patternDot) {
      return .dot
    } else if contains(.patternDash) {
      return .dash
    }

    if contains(.patternDashDotDot) {
      return .dashDotDot
    }

    return .solid
  }

  fileprivate var prettyDescription: String {
    guard let numberOfLines = numberOfLines else {
      return "None"
    }

    var result = lineStyle.description(forNumberOfLines: numberOfLines)
    if contains(.thick) {
      result += "; thick"
    }
    if contains(.byWord) {
      result += "; by word"
    }

    return result
  }
}

extension TextAlignment {
  fileprivate var description: String {
    switch self {
    case .left: return "Left"
    case .center: return "Center"
    case .right: return "Right"
    case .justified: return "Justified"
    case .natural: return "Natural"
    @unknown default:
      return "unknown"
    }
  }
}

extension LineBreakMode {
  fileprivate var description: String {
    switch self {
    case .byCharWrapping: return "By Char"
    case .byClipping: return "Clip"
    case .byTruncatingHead: return "Truncating head"
    case .byTruncatingMiddle: return "Truncating middle"
    case .byTruncatingTail: return "Truncating tail"
    case .byWordWrapping: return "Word wrap"
    @unknown default:
      return "unknown"
    }
  }
}

extension ParagraphStyle {
  fileprivate var namedFields: [(String, String?)] {
    [
      ("Line spacing", lineSpacing.map(String.init)),
      ("Paragraph spacing", paragraphSpacing.map(String.init)),
      ("Alignment", alignment?.description),
      ("Head indent", headIndent.map(String.init)),
      ("Tail indent", tailIndent.map(String.init)),
      ("First line head indent", firstLineHeadIndent.map(String.init)),
      ("Min line height", minimumLineHeight.map(String.init)),
      ("Max line height", maximumLineHeight.map(String.init)),
      ("Line break", lineBreakMode?.description),
      ("Writing direction", baseWritingDirection.map(String.init(describing:))),
      ("Line height multipe", lineHeightMultiple.map(String.init)),
      ("Paragraph spacing before", paragraphSpacingBefore.map(String.init)),
      ("Hyphenation factor", hyphenationFactor.map(String.init(describing:))),
      ("Tab stops", tabStops.map(String.init)),
      ("Default tab interval", defaultTabInterval.map(String.init)),
      (
        "Allows default tightening for truncation",
        allowsDefaultTighteningForTruncation.map(String.init)
      ),
    ]
  }
}

extension CFMutableAttributedString {
  fileprivate func apply<T: AnyObject>(_ attribute: T?, at range: CFRange, name: CFString) {
    if let value = attribute {
      CFAttributedStringSetAttribute(self, range, name, value)
    }
  }
}

extension ParagraphStyle {
  fileprivate func scaled(by scale: CGFloat) -> ParagraphStyle {
    var result = self
    result.minimumLineHeight = minimumLineHeight.map { ceil($0 * scale) }
    result.maximumLineHeight = maximumLineHeight.map { ceil($0 * scale) }
    result.lineHeightMultiple = lineHeightMultiple.map { $0 * scale }
    result.lineSpacing = lineSpacing.map { $0 * scale }
    return result
  }
}

extension Typo {
  public init(
    font: Font? = nil,
    kern: CGFloat? = nil,
    baselineOffset: CGFloat? = nil,
    strikethrough: UnderlineStyle? = nil,
    underline: UnderlineStyle? = nil,
    color: Color? = nil,
    strokeStyle: (color: Color, width: CGFloat)? = nil,
    shadow: Shadow? = nil,
    linkColor: Color? = nil
  ) {
    self.font = font
    paragraphStyle = nil
    self.kern = kern
    self.baselineOffset = baselineOffset
    self.strikethrough = strikethrough
    self.underline = underline
    self.color = color
    strokeColor = strokeStyle?.color
    strokeWidth = strokeStyle?.width
    self.shadow = shadow
    self.linkColor = linkColor
    heightOverrunAllowed = nil
    uncategorizedAttributes = nil
  }

  public init(size: FontSize, weight: FontWeight, isMonospacedDigits: Bool = false) {
    self.init(font: Font.with(
      weight: weight,
      size: size,
      isMonospacedDigits: isMonospacedDigits
    ))
  }

  public init(size: CGFloat, weight: FontWeight, isMonospacedDigits: Bool = false) {
    self.init(font: Font.with(
      weight: weight,
      size: size,
      isMonospacedDigits: isMonospacedDigits
    ))
  }

  public init(size: CGFloat, family: String) {
    self.init(font: Font(name: family, size: size)!)
  }

  public init(height: FontLineHeight) {
    self.init(height: height.rawValue)
  }

  public init(height: CGFloat) {
    self.init(paragraphStyle: ParagraphStyle(minimumLineHeight: height, maximumLineHeight: height))
  }

  public init(minimumLineHeight: FontLineHeight) {
    self.init(minimumLineHeight: minimumLineHeight.rawValue)
  }

  public init(minimumLineHeight: CGFloat) {
    self.init(paragraphStyle: ParagraphStyle(minimumLineHeight: minimumLineHeight))
  }

  public init(alignment: TextAlignment) {
    self.init(paragraphStyle: ParagraphStyle(alignment: alignment))
  }

  public init(writingDirection: WritingDirection) {
    self.init(paragraphStyle: ParagraphStyle(baseWritingDirection: writingDirection))
  }

  public init(hyphenationFactor: Float) {
    self.init(paragraphStyle: ParagraphStyle(hyphenationFactor: hyphenationFactor))
  }

  public init(lineBreakMode: LineBreakMode) {
    self.init(paragraphStyle: ParagraphStyle(lineBreakMode: lineBreakMode))
  }

  public init(lineSpacing: CGFloat) {
    self.init(paragraphStyle: ParagraphStyle(lineSpacing: lineSpacing))
  }

  public init(lineHeightMultiple: CGFloat) {
    self.init(paragraphStyle: ParagraphStyle(lineHeightMultiple: lineHeightMultiple))
  }

  public init(minHeight: CGFloat, maxHeight: CGFloat) {
    self.init(paragraphStyle: ParagraphStyle(
      minimumLineHeight: minHeight,
      maximumLineHeight: maxHeight
    ))
  }

  public init(tabs: [TextTab], defaultInterval: CGFloat) {
    self.init(paragraphStyle: ParagraphStyle(tabStops: tabs, defaultTabInterval: defaultInterval))
  }

  public init(kern: Kern) {
    self.init(kern: kern.rawValue)
  }

  public init(strokeColor: Color, width: CGFloat) {
    self.init(strokeStyle: (strokeColor, width))
  }

  // warning: this is SLOW
  public init(attributedString: NSAttributedString) {
    if attributedString.length > 0 {
      self.init(attributes: attributedString.attributes(at: 0, effectiveRange: nil))
    } else {
      self.init()
    }
  }

  // warning: this is SLOW
  public init(attributes: [NSAttributedString.Key: Any]) {
    let uncategorizedAttributes: [NSAttributedString.Key: Any] = attributes.filter {
      !categorizedKeys.contains($0.key)
    }

    let nsParagraphStyle = attributes[.paragraphStyle] as? NSParagraphStyle
      ?? (attributes[kCTParagraphStyleAttributeName as NSAttributedString.Key] as? NSParagraphStyle)
    let systemShadow = attributes[.shadow] as? SystemShadow
    let shadow = systemShadow.map {
      Shadow(
        offset: $0.shadowOffset.withInvertedHeightForIOS14,
        blurRadius: $0.shadowBlurRadius,
        color: $0.color
      )
    }
    self.init(
      font: attributes[.font] as? Font,
      paragraphStyle: ParagraphStyle(nsParagraphStyle: nsParagraphStyle),
      kern: attributes[.kern] as? CGFloat ??
        attributes[kCTKernAttributeName as NSAttributedString.Key] as? CGFloat,
      baselineOffset: attributes[.baselineOffset] as? CGFloat,
      strikethrough: (attributes[.strikethroughStyle] as? Int).map(UnderlineStyle.init),
      underline: (attributes[.underlineStyle] as? Int).map(UnderlineStyle.init),
      color: RGBAColor.fromAttribute(attributes[.foregroundColor])
        ?? RGBAColor
        .fromAttribute(attributes[kCTForegroundColorAttributeName as NSAttributedString.Key]),
      strokeColor: RGBAColor.fromAttribute(attributes[.strokeColor])
        ?? RGBAColor
        .fromAttribute(attributes[kCTStrokeColorAttributeName as NSAttributedString.Key]),
      strokeWidth: attributes[.strokeWidth] as? CGFloat
        ?? attributes[kCTStrokeWidthAttributeName as NSAttributedString.Key] as? CGFloat,
      shadow: shadow,
      heightOverrunAllowed: attributes[DKAllowHeightOverrunAttributeName] as? Bool,
      uncategorizedAttributes: uncategorizedAttributes.isEmpty ? nil : uncategorizedAttributes,
      linkColor: nil
    )
  }
}

extension CGSize {
  fileprivate var withInvertedHeightForIOS14: CGSize {
    if #available(iOS 14, *) {
      return CGSize(width: width, height: -height)
    } else {
      return self
    }
  }
}

extension SystemShadow {
  fileprivate var color: Color? {
    #if os(iOS) || os(tvOS)
    if let color = safeCFCast(shadowColor as CFTypeRef) as CGColor? {
      return color.rgba
    } else if let color = shadowColor as? SystemColor {
      return color.rgba
    } else {
      return nil
    }
    #else // OSX
    return shadowColor?.rgba
    #endif
  }
}

extension RGBAColor {
  fileprivate static func fromAttribute(_ value: Any?) -> RGBAColor? {
    guard let value = value else {
      return nil
    }

    if let systemValue = value as? SystemColor {
      return systemValue.rgba
    }

    let cgColor: CGColor? = safeCFCast(value as CFTypeRef)
    return cgColor?.rgba
  }
}

extension Typo {
  fileprivate init(paragraphStyle: ParagraphStyle) {
    font = nil
    self.paragraphStyle = paragraphStyle
    kern = nil
    baselineOffset = nil
    strikethrough = nil
    underline = nil
    color = nil
    strokeColor = nil
    strokeWidth = nil
    shadow = nil
    heightOverrunAllowed = nil
    uncategorizedAttributes = nil
    linkColor = nil
  }

  fileprivate init(heightOverrunAllowed: Bool) {
    font = nil
    paragraphStyle = nil
    kern = nil
    baselineOffset = nil
    strikethrough = nil
    underline = nil
    color = nil
    strokeColor = nil
    strokeWidth = nil
    shadow = nil
    self.heightOverrunAllowed = heightOverrunAllowed
    uncategorizedAttributes = nil
    linkColor = nil
  }
}

extension Optional where Wrapped == ParagraphStyle {
  fileprivate static func +(lhs: ParagraphStyle?, rhs: ParagraphStyle?) -> ParagraphStyle? {
    switch (lhs, rhs) {
    case let (lhsPS?, rhsPS?): return lhsPS + rhsPS
    case let (result?, nil): return result
    case let (nil, result?): return result
    case (nil, nil): return nil
    }
  }
}

extension Optional where Wrapped: Collection {
  fileprivate static func +(lhs: Wrapped?, rhs: Wrapped?) -> Wrapped? {
    switch (lhs, rhs) {
    case let (lhsValue?, rhsValue?): return lhsValue + rhsValue
    case let (result?, nil): return result
    case let (nil, result?): return result
    case (nil, nil): return nil
    }
  }
}

private let categorizedKeys: Set<NSAttributedString.Key> = [
  .font,
  kCTFontAttributeName as NSAttributedString.Key,
  .paragraphStyle,
  kCTParagraphStyleAttributeName as NSAttributedString.Key,
  .kern,
  kCTKernAttributeName as NSAttributedString.Key,
  .baselineOffset,
  .strikethroughStyle,
  .underlineStyle,
  .foregroundColor,
  kCTForegroundColorAttributeName as NSAttributedString.Key,
  .strokeColor,
  kCTStrokeColorAttributeName as NSAttributedString.Key,
  .strokeWidth,
  kCTStrokeWidthAttributeName as NSAttributedString.Key,
  .shadow,
  DKAllowHeightOverrunAttributeName,
]

private let baselineOffsetCFKey = NSAttributedString.Key.baselineOffset as CFString
private let strikethroughStyleCFKey = NSAttributedString.Key.strikethroughStyle as CFString
private let allowHeightOverrunCFKey = DKAllowHeightOverrunAttributeName as CFString
private let shadowAttributeCFKey = NSAttributedString.Key.shadow as CFString
