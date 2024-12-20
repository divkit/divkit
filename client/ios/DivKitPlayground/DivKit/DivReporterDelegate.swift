import DivKit
import Foundation

final class DivReporterDelegate: DivReporter {
  private let reportError: (DivError) -> Void

  init(
    _ reportError: @escaping (DivError) -> Void
  ) {
    self.reportError = reportError
  }

  func reportError(cardId _: DivCardID, error: DivError) {
    reportError(error)
  }
}
