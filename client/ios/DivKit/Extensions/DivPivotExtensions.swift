import CoreGraphics

import LayoutKit

extension DivPivot {
  func makeAnchorValue(expressionResolver: ExpressionResolver) -> AnchorValue {
    switch self {
    case let .divPivotPercentage(value):
      return .relative(value: value.resolveValue(expressionResolver) ?? 50)
    case let .divPivotFixed(value):
      return .absolute(value: value.resolveValue(expressionResolver).flatMap(CGFloat.init) ?? 0)
    }
  }
}
