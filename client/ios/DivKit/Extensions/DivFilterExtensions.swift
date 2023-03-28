import BasePublic
import CoreGraphics

extension DivFilter {
  func makeImageEffect(with resolver: ExpressionResolver) -> ImageEffect {
    switch self {
    case let .divBlur(blur):
      return .blur(radius: CGFloat(blur.resolveRadius(resolver) ?? 0))
    }
  }
}
