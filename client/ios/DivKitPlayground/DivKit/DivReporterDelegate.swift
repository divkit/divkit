import Foundation

import DivKit

final class DivReporterDelegate: DivReporter {
  private let reportError: (DivError) -> Void

  init(
    _ reportError: @escaping (DivError) -> Void
  ) {
    self.reportError = reportError
  }

  func reportError(cardId: DivCardID, error: DivError) {
    reportError(error)
  }
}
