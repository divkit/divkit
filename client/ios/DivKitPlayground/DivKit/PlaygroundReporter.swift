import DivKit

final class PlaygroundReporter: DivReporter {
  private let reportError: (DivError) -> Void

  init(
    reportError: @escaping (DivError) -> Void = { _ in }
  ) {
    self.reportError = reportError
  }

  func reportError(cardId _: DivCardID, error: DivError) {
    AppLogger.error(error.message)
    reportError(error)
  }

  func reportAction(cardId _: DivCardID, info: DivActionInfo) {
    AppLogger.info("Action (\(info.source)): logId = \(info.logId)")
  }
}
