import Foundation

extension DivAspect? {
  func resolveAspectRatio(_ expressionResolver: ExpressionResolver) -> CGFloat? {
    if let aspect = self, let ratio = aspect.resolveRatio(expressionResolver) {
      // AspectBlock has inverted ratio
      return 1 / ratio
    }
    return nil
  }
}
