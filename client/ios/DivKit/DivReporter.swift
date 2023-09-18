import Foundation

import LayoutKit

public protocol DivReporter {
  func reportError(cardId: DivCardID, error: DivError)
}

extension DivReporter {
  func asExpressionErrorTracker(cardId: DivCardID) -> ExpressionErrorTracker {
    {
      reportError(
        cardId: cardId,
        error: DivExpressionError($0, path: UIElementPath(cardId.rawValue))
      )
    }
  }
}

final class DefaultDivReporter: DivReporter {
  func reportError(cardId: DivCardID, error: DivError) {}
}
