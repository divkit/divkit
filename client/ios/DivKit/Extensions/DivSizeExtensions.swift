import CoreGraphics
import LayoutKit
import VGSL

extension DivSize {
  func resolveLayoutTrait(_ expressionResolver: ExpressionResolver) -> LayoutTrait {
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

  func resolveHeightLayoutTrait(
    _ expressionResolver: ExpressionResolver,
    aspectRatio: CGFloat?
  ) -> LayoutTrait {
    if aspectRatio != nil {
      return .resizable
    }
    return resolveLayoutTrait(expressionResolver)
  }

  var isIntrinsic: Bool {
    switch self {
    case .divFixedSize, .divMatchParentSize:
      false
    case .divWrapContentSize:
      true
    }
  }
}

#if !DEBUG
extension DivSize: Equatable {
  public static func ==(lhs: DivSize, rhs: DivSize) -> Bool {
    switch (lhs, rhs) {
    case let (.divFixedSize(l), .divFixedSize(r)):
      l == r
    case let (.divMatchParentSize(l), .divMatchParentSize(r)):
      l == r
    case let (.divWrapContentSize(l), .divWrapContentSize(r)):
      l == r
    default:
      false
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
