import CoreGraphics

import LayoutKit

extension DivTransform {
  public func makeAnchorPoint(expressionResolver: ExpressionResolver) -> AnchorPoint {
    AnchorPoint(
      x: pivotX.makeAnchorValue(expressionResolver: expressionResolver),
      y: pivotY.makeAnchorValue(expressionResolver: expressionResolver)
    )
  }
}
