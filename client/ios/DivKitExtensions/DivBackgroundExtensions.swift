import Foundation

import DivKit

extension DivBackground {
  func resolveImageURL(_ expressionResolver: ExpressionResolver) -> URL? {
    switch self {
    case let .divImageBackground(imageBackground):
      return imageBackground.resolveImageUrl(expressionResolver)
    case let .divNinePatchBackground(ninePatchImage):
      return ninePatchImage.resolveImageUrl(expressionResolver)
    case .divLinearGradient, .divRadialGradient, .divSolidBackground:
      return nil
    }
  }
}
