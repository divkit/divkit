import Foundation
import LayoutKit
import VGSL

extension DivShadow {
  func resolve(
    _ expressionResolver: ExpressionResolver,
    cornerRadii: CornerRadii = .zero
  ) -> BlockShadow {
    BlockShadow(
      cornerRadii: cornerRadii,
      blurRadius: CGFloat(resolveBlur(expressionResolver)),
      offset: offset.resolve(expressionResolver),
      opacity: Float(resolveAlpha(expressionResolver)),
      color: resolveColor(expressionResolver)
    )
  }
}
