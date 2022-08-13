// Copyright 2018 Yandex LLC. All rights reserved.

import CoreGraphics

import CommonCore
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
      return .intrinsic(constrained: constrained)
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
