import Foundation
import SwiftUI

enum UserPreferences {
  static let isQrScannerEnabledKey = "isQrScannerEnabled"
  static let lastUrlKey = "lastUrl"
  static let playgroundThemeKey = "playgroundTheme"
  static let showRenderingTimeKey = "showRenderingTime"

  static let isQrScannerEnabledDefault = {
  #if targetEnvironment(simulator)
    false
  #else
    true
  #endif
  }()

  static let showRenderingTimeDefault = false
  static var showRenderingTime: Bool {
    defaults.value(forKey: showRenderingTimeKey) as? Bool ?? showRenderingTimeDefault
  }

  static let playgroundThemeDefault = Theme.system

  static var playgroundTheme: Theme {
    let value =  Theme(rawValue: defaults.value(forKey: playgroundThemeKey) as? String ?? "") ?? playgroundThemeDefault
    switch value {
    case .system:
      return UIViewController().traitCollection.userInterfaceStyle == .light ? .light : .dark
    default:
      return value
    }
  }
}

private var defaults: UserDefaults {
  UserDefaults.standard
}
