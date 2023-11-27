import LayoutKit

extension DivTransform {
  func resolveAnchorPoint(_ expressionResolver: ExpressionResolver) -> LayoutKit.AnchorPoint {
    // must keep package name here due to conflict with CoreText.AnchorPoint
    LayoutKit.AnchorPoint(
      x: pivotX.resolveAnchorValue(expressionResolver),
      y: pivotY.resolveAnchorValue(expressionResolver)
    )
  }
}
