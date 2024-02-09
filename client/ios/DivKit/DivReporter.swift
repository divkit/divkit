import Foundation

import LayoutKit

/// The ``DivReporter`` protocol allows you to learn about actions and errors that occur in the
/// layout.
public protocol DivReporter {
  func reportError(cardId: DivCardID, error: DivError)
  func reportAction(cardId: DivCardID, info: DivActionInfo)
}

extension DivReporter {
  public func reportAction(cardId _: DivCardID, info _: DivActionInfo) {}

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
  func reportError(cardId _: DivCardID, error _: DivError) {}
}
