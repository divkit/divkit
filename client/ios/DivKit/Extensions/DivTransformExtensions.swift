import LayoutKit

extension DivTransform {
  func makeAnchorPoint(expressionResolver: ExpressionResolver) -> LayoutKit.AnchorPoint {
    AnchorPoint(
      x: pivotX.makeAnchorValue(expressionResolver: expressionResolver),
      y: pivotY.makeAnchorValue(expressionResolver: expressionResolver)
    )
  }
}
