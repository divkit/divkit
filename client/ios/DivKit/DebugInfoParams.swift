import LayoutKit

public struct DebugInfoParams {
  public let show: (ViewType) -> Void
  public let safeAreaInsets: EdgeInsets

  public init(
    show: @escaping (ViewType) -> Void,
    safeAreaInsets: EdgeInsets = .zero
  ) {
    self.show = show
    self.safeAreaInsets = safeAreaInsets
  }
}
