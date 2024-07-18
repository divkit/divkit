import CoreGraphics

import VGSL

extension DivAbsoluteEdgeInsets {
  func resolve(_ expressionResolver: ExpressionResolver) -> EdgeInsets {
    EdgeInsets(
      top: CGFloat(resolveTop(expressionResolver)),
      left: CGFloat(resolveLeft(expressionResolver)),
      bottom: CGFloat(resolveBottom(expressionResolver)),
      right: CGFloat(resolveRight(expressionResolver))
    )
  }
}
