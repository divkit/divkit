import LayoutKit

extension DivTransform {
  func resolveAnchorPoint(_ expressionResolver: ExpressionResolver) -> AnchorPoint {
    AnchorPoint(
      x: pivotX.resolveAnchorValue(expressionResolver),
      y: pivotY.resolveAnchorValue(expressionResolver)
    )
  }
}
