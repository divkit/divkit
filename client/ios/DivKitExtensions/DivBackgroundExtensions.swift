import Foundation

import DivKit

extension DivBackground {
  func makeImageURL(with expressionResolver: ExpressionResolver) -> URL? {
    switch self {
    case let .divImageBackground(imageBackground):
      return imageBackground.resolveImageUrl(expressionResolver)
    case .divLinearGradient, .divRadialGradient, .divSolidBackground:
      return nil
    }
  }
}
