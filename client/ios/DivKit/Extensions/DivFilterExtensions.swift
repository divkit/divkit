import CoreGraphics
import VGSL

extension DivFilter {
  func resolveEffect(_ expressionResolver: ExpressionResolver) -> ImageEffect? {
    switch self {
    case let .divBlur(blur):
      return .blur(radius: CGFloat(blur.resolveRadius(expressionResolver) ?? 0))
    case .divFilterRtlMirror:
      DivKitLogger.error("rtl_mirror filter is not supported")
      return nil
    }
  }
}
