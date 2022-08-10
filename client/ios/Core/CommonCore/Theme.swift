// Copyright 2021 Yandex LLC. All rights reserved.

import BaseUI
import CommonCoreTiny

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
