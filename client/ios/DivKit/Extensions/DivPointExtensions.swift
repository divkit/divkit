import Foundation

extension DivPoint {
  func resolve(_ expressionResolver: ExpressionResolver) -> CGPoint {
    CGPoint(
      x: x.resolveUnit(expressionResolver).makeScaledValue(x.resolveValue(expressionResolver) ?? 0),
      y: y.resolveUnit(expressionResolver).makeScaledValue(y.resolveValue(expressionResolver) ?? 0)
    )
  }
}
