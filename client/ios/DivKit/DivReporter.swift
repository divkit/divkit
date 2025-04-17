import Foundation
import LayoutKit

/// The ``DivReporter`` protocol allows you to learn about actions and errors that occur in the
/// layout.
public protocol DivReporter {
  func reportError(cardId: DivCardID, error: DivError)
  func reportAction(cardId: DivCardID, info: DivActionInfo)

  @_spi(Performance)
  func reportViewWasCreated(cardId: DivCardID)
  @_spi(Performance)
  func reportBlockWillConfigure(path: UIElementPath)
  @_spi(Performance)
  func reportBlockDidConfigure(path: UIElementPath)
}

extension DivReporter {
  public func reportViewWasCreated(cardId _: DivCardID) {}
  public func reportBlockWillConfigure(path _: UIElementPath) {}
  public func reportBlockDidConfigure(path _: UIElementPath) {}
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
