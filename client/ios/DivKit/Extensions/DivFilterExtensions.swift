import Base
import CoreGraphics

extension DivFilter {
  func makeImageEffect(with resolver: ExpressionResolver) -> ImageEffect {
    switch self {
    case .divBlur(let blur):
      return .blur(radius: CGFloat(blur.resolveRadius(resolver) ?? 0))
    }
  }
}
