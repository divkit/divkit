import LayoutKit
import VGSL

import Foundation

extension DivShadow {
  func resolve(
    _ expressionResolver: ExpressionResolver,
    cornerRadii: CornerRadii = .zero
  ) -> BlockShadow {
    return BlockShadow(
      cornerRadii: cornerRadii,
      blurRadius: CGFloat(resolveBlur(expressionResolver)),
      offset: offset.resolve(expressionResolver),
      opacity: Float(resolveAlpha(expressionResolver)),
      color: resolveColor(expressionResolver)
    )
  }
}
