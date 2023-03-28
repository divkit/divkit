import CoreGraphics

import CommonCorePublic
import LayoutKit

extension DivSize {
  func makeLayoutTrait(with expressionResolver: ExpressionResolver) -> LayoutTrait {
    switch self {
    case let .divFixedSize(size):
      return .fixed(
        size.resolveUnit(expressionResolver)
          .makeScaledValue(size.resolveValue(expressionResolver) ?? 0)
      )
    case let .divMatchParentSize(size):
      let weight = LayoutTrait.Weight(
        rawValue: CGFloat(size.resolveWeight(expressionResolver) ?? 1)
      )!
      return .weighted(weight)
    case let .divWrapContentSize(wrapContent):
      let constrained = wrapContent.resolveConstrained(expressionResolver) ?? false
      let minSize = wrapContent.minSize?.resolveValue(expressionResolver)
      let maxSize = wrapContent.maxSize?.resolveValue(expressionResolver)
      return .intrinsic(
        constrained: constrained,
        minSize: minSize.flatMap { CGFloat($0) } ?? 0,
        maxSize: maxSize.flatMap { CGFloat($0) } ?? .infinity
      )
    }
  }

  var isIntrinsic: Bool {
    switch self {
    case .divFixedSize, .divMatchParentSize:
      return false
    case .divWrapContentSize:
      return true
    }
  }
}

#if !DEBUG
extension DivSize: Equatable {
  public static func ==(lhs: DivSize, rhs: DivSize) -> Bool {
    switch (lhs, rhs) {
    case let (.divFixedSize(l), .divFixedSize(r)):
      return l == r
    case let (.divMatchParentSize(l), .divMatchParentSize(r)):
      return l == r
    case let (.divWrapContentSize(l), .divWrapContentSize(r)):
      return l == r
    default:
      return false
    }
  }
}

extension DivFixedSize: Equatable {
  public static func ==(lhs: DivFixedSize, rhs: DivFixedSize) -> Bool {
    guard
      lhs.unit == rhs.unit,
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}

extension DivMatchParentSize: Equatable {
  public static func ==(lhs: DivMatchParentSize, rhs: DivMatchParentSize) -> Bool {
    guard
      lhs.weight == rhs.weight
    else {
      return false
    }
    return true
  }
}

extension DivWrapContentSize: Equatable {
  public static func ==(lhs: DivWrapContentSize, rhs: DivWrapContentSize) -> Bool {
    guard
      lhs.constrained == rhs.constrained
    else {
      return false
    }
    return true
  }
}
#endif
