import LayoutKit

public struct DebugParams {
  public let isDebugInfoEnabled: Bool
  public let showDebugInfo: (ViewType) -> Void
  public let errorCounterInsets: EdgeInsets

  public init(
    isDebugInfoEnabled: Bool = false,
    showDebugInfo: @escaping (ViewType) -> Void = { _ in },
    errorCounterInsets: EdgeInsets = .zero
  ) {
    self.isDebugInfoEnabled = isDebugInfoEnabled
    self.showDebugInfo = showDebugInfo
    self.errorCounterInsets = errorCounterInsets
  }
}
