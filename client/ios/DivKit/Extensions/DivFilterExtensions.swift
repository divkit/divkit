import BasePublic
import CoreGraphics

extension DivFilter {
  func makeImageEffect(with resolver: ExpressionResolver) -> ImageEffect? {
    switch self {
    case let .divBlur(blur):
      return .blur(radius: CGFloat(blur.resolveRadius(resolver) ?? 0))
    case .divFilterRtlMirror:
      DivKitLogger.error("rtl_mirror filter is not supported")
      return nil
    }
  }
}
