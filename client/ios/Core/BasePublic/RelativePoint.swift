// Copyright 2017 Yandex LLC. All rights reserved.

import CoreGraphics

import BaseUIPublic

public struct RelativePoint: Equatable {
  public var rawValue: CGPoint

  public init(rawValue: CGPoint) {
    self.rawValue = rawValue
  }
}

extension RelativePoint {
  public static let zero = RelativePoint(x: 0, y: 0)
  public static let topLeft = zero
  public static let topRight = RelativePoint(x: 1, y: 0)
  public static let bottomLeft = RelativePoint(x: 0, y: 1)
  public static let bottomRight = RelativePoint(x: 1, y: 1)
  public static let mid = RelativePoint(x: 0.5, y: 0.5)
  public static let midLeft = RelativePoint(x: 0, y: 0.5)
  public static let midRight = RelativePoint(x: 1, y: 0.5)
  public static let midTop = RelativePoint(x: 0.5, y: 0)
  public static let midBottom = RelativePoint(x: 0.5, y: 1)

  public var x: CGFloat {
    get { rawValue.x }
    set { rawValue.x = newValue }
  }

  public var y: CGFloat {
    get { rawValue.y }
    set { rawValue.y = newValue }
  }

  public init(x: CGFloat, y: CGFloat) {
    self.init(rawValue: CGPoint(x: x, y: y))
  }
}

extension RelativePoint {
  public var cgPoint: CGPoint {
    rawValue
  }

  public func absolutePosition(in rect: CGRect) -> CGPoint {
    CGPoint(
      x: rect.origin.x + x * rect.width,
      y: rect.origin.y + y * rect.height
    )
  }

  public func isApproximatelyEqualTo(_ other: RelativePoint) -> Bool {
    rawValue.isApproximatelyEqualTo(other.rawValue)
  }

  public static func +(lhs: RelativePoint, rhs: RelativePoint) -> RelativePoint {
    RelativePoint(rawValue: lhs.rawValue + rhs.rawValue)
  }

  public static func -(lhs: RelativePoint, rhs: RelativePoint) -> RelativePoint {
    RelativePoint(rawValue: lhs.rawValue - rhs.rawValue)
  }

  public func clampedToBounds() -> RelativePoint {
    let boundsRange: ClosedRange<CGFloat> = 0...1
    return RelativePoint(x: x.clamp(boundsRange), y: y.clamp(boundsRange))
  }
}

extension CGPoint {
  public var relative: RelativePoint {
    RelativePoint(rawValue: self)
  }
}
