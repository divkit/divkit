// Copyright 2021 Yandex LLC. All rights reserved.

import SwiftUI

import BaseUIPublic

@frozen
public enum Theme: String {
  case dark
  case light
}

extension Theme {
  public var userInterfaceStyle: UserInterfaceStyle {
    switch self {
    case .light:
      return .light
    case .dark:
      return .dark
    }
  }

  @available(iOS 13, macOS 10.15, *)
  public var colorScheme: ColorScheme {
    switch self {
    case .light:
      return .light
    case .dark:
      return .dark
    }
  }
}

extension UserInterfaceStyle {
  public var theme: Theme {
    switch self {
    case .light:
      return .light
    case .dark:
      return .dark
    }
  }
}
