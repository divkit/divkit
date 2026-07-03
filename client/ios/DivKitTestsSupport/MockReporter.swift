@testable import DivKit

public final class MockReporter: DivReporter {
  public private(set) var lastCardId: DivCardID?
  public private(set) var lastActionInfo: DivActionInfo?
  public private(set) var lastError: DivError?

  public init() {}

  public func reportAction(cardId: DivCardID, info: DivActionInfo) {
    lastCardId = cardId
    lastActionInfo = info
  }

  public func reportError(cardId: DivCardID, error: DivError) {
    lastCardId = cardId
    lastError = error
  }
}
