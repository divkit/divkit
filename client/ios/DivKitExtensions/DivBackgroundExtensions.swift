import DivKit
import Foundation

extension DivBackground {
  func resolveImageURL(_ expressionResolver: ExpressionResolver) -> URL? {
    switch self {
    case let .divImageBackground(imageBackground):
      imageBackground.resolveImageUrl(expressionResolver)
    case let .divNinePatchBackground(ninePatchImage):
      ninePatchImage.resolveImageUrl(expressionResolver)
    case .divLinearGradient, .divRadialGradient, .divSolidBackground:
      nil
    }
  }
}
