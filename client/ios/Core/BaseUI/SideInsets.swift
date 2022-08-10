// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics

public struct SideInsets: Equatable, ExpressibleByFloatLiteral, ExpressibleByIntegerLiteral {
  public let leading: CGFloat
  public let trailing: CGFloat

  public init(floatLiteral value: Double) {
    self.init(uniform: CGFloat(value))
  }

  public init(integerLiteral value: Int) {
    self.init(uniform: CGFloat(value))
  }

  public init(uniform value: CGFloat) {
    self.init(leading: value, trailing: value)
  }

  public init(leading: CGFloat, trailing: CGFloat) {
    self.leading = leading
    self.trailing = trailing
  }

  public static func +(lhs: SideInsets, rhs: SideInsets) -> SideInsets {
    SideInsets(leading: lhs.leading + rhs.leading, trailing: lhs.trailing + rhs.trailing)
  }

  public static let zero = SideInsets(leading: 0, trailing: 0)

  public var sum: CGFloat {
    leading + trailing
  }

  public func asArray() -> [CGFloat] {
    [leading, trailing]
  }
}
