import CoreGraphics

import BaseUIPublic
import CommonCorePublic

public struct TabTitleStyle: Equatable {
  public static let defaultTypo = Typo(size: .capsS, weight: .semibold).with(height: .capsS)
    .kerned(.capsS)
  public static let defaultPaddings = EdgeInsets(top: 4, left: 8, bottom: 4, right: 8)
  public static let defaultActiveTextColor = Color.colorWithHexCode(0x00_00_00_CC)
  public static let defaultBaseTextColor = Color.colorWithHexCode(0x00_00_00_80)
  public static let defaultActiveBackgroundColor = Color.colorWithHexCode(0xFF_DC_60_FF)

  public let typo: Typo
  public let inactiveTypo: Typo
  public let paddings: EdgeInsets
  public let cornerRadius: CornerRadii?
  public let baseTextColor: Color
  public let activeTextColor: Color
  public let activeBackgroundColor: Color
  public let inactiveBackgroundColor: Color
  public let itemSpacing: CGFloat?

  public init(
    typo: Typo = defaultTypo,
    inactiveTypo: Typo? = nil,
    paddings: EdgeInsets = defaultPaddings,
    cornerRadius: CornerRadii? = nil,
    baseTextColor: Color = defaultBaseTextColor,
    activeTextColor: Color = defaultActiveTextColor,
    activeBackgroundColor: Color = defaultActiveBackgroundColor,
    inactiveBackgroundColor: Color = .clear,
    itemSpacing: CGFloat? = nil
  ) {
    self.typo = typo
    self.inactiveTypo = inactiveTypo ?? typo
    self.paddings = paddings
    self.cornerRadius = cornerRadius
    self.baseTextColor = baseTextColor
    self.activeTextColor = activeTextColor
    self.activeBackgroundColor = activeBackgroundColor
    self.inactiveBackgroundColor = inactiveBackgroundColor
    self.itemSpacing = itemSpacing
  }

  public static func ==(lhs: TabTitleStyle, rhs: TabTitleStyle) -> Bool {
    lhs.typo.hasEqualDistributedAttributes(to: rhs.typo) &&
      lhs.inactiveTypo.hasEqualDistributedAttributes(to: rhs.inactiveTypo) &&
      lhs.paddings == rhs.paddings &&
      lhs.cornerRadius == rhs.cornerRadius &&
      lhs.baseTextColor == rhs.baseTextColor &&
      lhs.activeTextColor == rhs.activeTextColor &&
      lhs.activeBackgroundColor == rhs.activeBackgroundColor &&
      lhs.inactiveBackgroundColor == rhs.inactiveBackgroundColor &&
      lhs.itemSpacing == rhs.itemSpacing
  }
}

extension TabTitleStyle: CustomDebugStringConvertible {
  public var debugDescription: String {
    let descriptions = [
      describeNonDefault(typo, default: TabTitleStyle.defaultTypo, description: "Typo"),
      describeNonDefault(paddings, default: TabTitleStyle.defaultPaddings, description: "Paddings"),
      describeNonDefault(
        baseTextColor,
        default: TabTitleStyle.defaultBaseTextColor,
        description: "Text color"
      ),
      describeNonDefault(
        activeTextColor,
        default: TabTitleStyle.defaultActiveTextColor,
        description: "Active text color"
      ),
      describeNonDefault(
        activeBackgroundColor,
        default: TabTitleStyle.defaultActiveBackgroundColor,
        description: "Active background color"
      ),
    ].compactMap { $0?.indented() }

    guard !descriptions.isEmpty else { return "default" }
    return """
    {
    \(descriptions.joined(separator: ",\n"))
    }
    """
  }
}

extension Typo {
  fileprivate func hasEqualDistributedAttributes(to typo: Typo) -> Bool {
    let template = "a"
    let lhsString = template.with(typo: self)
    let rhsString = template.with(typo: typo)
    return lhsString.isEqual(rhsString)
  }
}

private func describeNonDefault<T: Equatable>(
  _ value: T,
  default: T,
  description: String
) -> String? {
  guard value != `default` else {
    return nil
  }

  return description + ": \(value)"
}
