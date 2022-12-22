import CoreGraphics
import Foundation

@frozen
public enum LayoutTrait: Equatable {
  /// Corresponding dimension is fixed and equal to associated value
  case fixed(CGFloat)

  /// Corresponding dimension is determined by block content
  case intrinsic(constrained: Bool, minSize: CGFloat, maxSize: CGFloat)

  /// Corresponding dimension of a parent block is filled by weighted blocks resized according to
  /// their weights
  case weighted(Weight)

  public struct Weight: RawRepresentable, ExpressibleByFloatLiteral, ExpressibleByIntegerLiteral,
    Equatable, Comparable {
    public let rawValue: CGFloat

    public init?(rawValue: CGFloat) {
      guard rawValue > 0, rawValue.isFinite else { return nil }
      self.rawValue = rawValue
    }

    public init(floatLiteral value: FloatLiteralType) {
      self.init(rawValue: CGFloat(value))!
    }

    public init(integerLiteral value: IntegerLiteralType) {
      self.init(rawValue: CGFloat(value))!
    }

    public static let `default`: Weight = 1

    public static func <(lhs: Weight, rhs: Weight) -> Bool {
      lhs.rawValue < rhs.rawValue
    }

    public static func /(lhs: Weight, rhs: Weight) -> Weight {
      Weight(rawValue: lhs.rawValue / rhs.rawValue)!
    }
  }

  /// Resizable block with default weight - custom case typically for the only resizable block in a
  /// parent block
  public static let resizable = LayoutTrait.weighted(.default)
  public static let intrinsic = LayoutTrait.intrinsic(
    constrained: false,
    minSize: 0,
    maxSize: .infinity
  )
}

extension LayoutTrait {
  public var isResizable: Bool {
    if case .weighted = self { return true }
    return false
  }

  public var fixedValue: CGFloat? {
    if case let .fixed(value) = self { return value }
    return nil
  }

  public var isConstrained: Bool {
    switch self {
    case let .intrinsic(constrained, _, _):
      return constrained
    case .fixed, .weighted:
      return false
    }
  }
}
