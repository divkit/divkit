import LayoutKit
import VGSLFundamentals

final class DebugErrorCollector: DivReporter {
  private let wrappedDivReporter: DivReporter

  var errorStorage: DivErrorsStorage

  private(set) var layoutErrors = [DivError]()
  let observableErrorCount = ObservableProperty<Int>(initialValue: 0)

  init(
    wrappedDivReporter: DivReporter,
    errorStorage: DivErrorsStorage
  ) {
    self.wrappedDivReporter = wrappedDivReporter
    self.errorStorage = errorStorage
  }

  func reportError(cardId: DivCardID, error: DivError) {
    wrappedDivReporter.reportError(cardId: cardId, error: error)
    guard !hasError(error) else { return }
    layoutErrors.append(error)
    observableErrorCount.value += 1
  }

  func reportAction(cardId: DivCardID, info: DivActionInfo) {
    wrappedDivReporter.reportAction(cardId: cardId, info: info)
  }

  func reportViewWasCreated(cardId: DivCardID) {
    wrappedDivReporter.reportViewWasCreated(cardId: cardId)
  }

  func reportBlockDidConfigure(path: UIElementPath) {
    wrappedDivReporter.reportBlockDidConfigure(path: path)
  }

  func reportBlockWillConfigure(path: UIElementPath) {
    wrappedDivReporter.reportBlockWillConfigure(path: path)
  }

  func reportViewDidLayout(path: UIElementPath) {
    wrappedDivReporter.reportViewDidLayout(path: path)
  }

  func reportViewWillLayout(path: UIElementPath) {
    wrappedDivReporter.reportViewWillLayout(path: path)
  }

  var totalErrorCount: Int {
    errorStorage.errors.count + layoutErrors.count
  }

  var errorList: [String] {
    errorStorage.errors.map(\.prettyMessage) + layoutErrors.map(\.prettyMessage)
  }

  var debugDescription: String {
    "Errors: \(errorList)"
  }

  private func hasError(_ error: DivError) -> Bool {
    layoutErrors.contains {
      $0.kind == error.kind
        && $0.message == error.message
        && $0.path == error.path
        && $0.level == error.level
    }
  }
}
