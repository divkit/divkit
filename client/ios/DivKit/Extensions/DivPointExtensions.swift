import Foundation

extension DivPoint {
  internal func cast(with expressionResolver: ExpressionResolver) -> CGPoint {
    CGPoint(
      x: x.resolveUnit(expressionResolver).makeScaledValue(x.resolveValue(expressionResolver) ?? 0),
      y: y.resolveUnit(expressionResolver).makeScaledValue(y.resolveValue(expressionResolver) ?? 0)
    )
  }
}
