import Foundation
import SwiftUI

enum UserPreferences {
  static var lastUrl: String {
    get { defaults.string(forKey: lastUrlKey) ?? "" }
    set { defaults.set(newValue, forKey: lastUrlKey) }
  }

  static var isQrScannerEnabled: Bool {
    defaults.value(forKey: isQrScannerEnabledKey) as? Bool ?? true
  }

  static let isQrScannerEnabledBinding = boolBinding(key: isQrScannerEnabledKey)
}

private func boolBinding(key: String) -> Binding<Bool> {
  Binding<Bool>(
    get: { defaults.value(forKey: key) as? Bool ?? true },
    set: { defaults.set($0, forKey: key) }
  )
}

private var defaults: UserDefaults {
  UserDefaults.standard
}

private let isQrScannerEnabledKey = "isQrScannerEnabled"
private let lastUrlKey = "lastUrl"
