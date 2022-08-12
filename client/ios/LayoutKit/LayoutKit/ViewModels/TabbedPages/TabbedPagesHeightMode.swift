// Copyright 2020 Yandex LLC. All rights reserved.

public enum TabbedPagesHeightMode: Equatable {
  case byHighestPage
  case bySelectedPage

  public static let `default` = Self.byHighestPage
}
