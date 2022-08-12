// Copyright 2022 Yandex LLC. All rights reserved.

import CoreGraphics

import LayoutKit

extension DivPivot {
  public func makeAnchorValue(expressionResolver: ExpressionResolver) -> AnchorValue {
    switch self {
    case let .divPivotPercentage(value):
      return .relative(value: value.resolveValue(expressionResolver) ?? 50)
    case let .divPivotFixed(value):
      return .absolute(value: value.resolveValue(expressionResolver).flatMap(CGFloat.init) ?? 0)
    }
  }
}
