import VGSLFundamentals

public protocol LayoutReporterProvider {
  var layoutReporter: LayoutReporter? { get set }
}

extension LayoutReporterProvider {
  public var layoutReporter: LayoutReporter? {
    get { nil }
    set {}
  }
}

public struct LayoutReporter {
  let willLayoutSubviews: Action
  let didLayoutSubviews: Action
}
