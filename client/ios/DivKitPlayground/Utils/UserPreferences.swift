import Foundation
import SwiftUI

enum UserPreferences {
  static let isQrScannerEnabledKey = "isQrScannerEnabled"
  static let lastUrlKey = "lastUrl"
  static let playgroundThemeKey = "playgroundTheme"

  static let isQrScannerEnabledDefault = {
  #if targetEnvironment(simulator)
    false
  #else
    true
  #endif
  }()

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
