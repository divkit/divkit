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

  func resolvePreloadRequired(_ expressionResolver: ExpressionResolver) -> Bool {
    switch self {
    case let .divImageBackground(imageBackground):
      imageBackground.resolvePreloadRequired(expressionResolver)
    case .divNinePatchBackground, .divLinearGradient, .divRadialGradient, .divSolidBackground:
      false
    }
  }
}
