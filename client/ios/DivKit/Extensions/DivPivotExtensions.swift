import CoreGraphics
import LayoutKit

extension DivPivot {
  func resolveAnchorValue(_ expressionResolver: ExpressionResolver) -> AnchorValue {
    switch self {
    case let .divPivotPercentage(value):
      .relative(value: value.resolveValue(expressionResolver) ?? 50)
    case let .divPivotFixed(value):
      .absolute(value: value.resolveValue(expressionResolver).flatMap(CGFloat.init) ?? 0)
    }
  }
}
