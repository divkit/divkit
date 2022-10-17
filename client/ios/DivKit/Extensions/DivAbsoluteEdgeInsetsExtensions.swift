import CoreGraphics

import CommonCore

extension DivAbsoluteEdgeInsets {
  func makeEdgeInsets(with expressionResolver: ExpressionResolver) -> EdgeInsets {
    return EdgeInsets(
      top: CGFloat(resolveTop(expressionResolver)),
      left: CGFloat(resolveLeft(expressionResolver)),
      bottom: CGFloat(resolveBottom(expressionResolver)),
      right: CGFloat(resolveRight(expressionResolver))
    )
  }
}
