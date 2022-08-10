public enum TabbedPagesHeightMode: Equatable {
  case byHighestPage
  case bySelectedPage

  public static let `default` = Self.byHighestPage
}
