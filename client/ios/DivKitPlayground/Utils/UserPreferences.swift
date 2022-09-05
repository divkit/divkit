import Foundation
import SwiftUI

enum UserPreferences {
  static var lastUrl: String {
    get { defaults.string(forKey: lastUrlKey) ?? "" }
    set { defaults.set(newValue, forKey: lastUrlKey) }
  }

  static var isQrScannerEnabled: Bool {
    defaults.value(forKey: isQrScannerEnabledKey) as? Bool ?? isQrScannerEnabledDefault
  }

  static let isQrScannerEnabledBinding = boolBinding(key: isQrScannerEnabledKey, defaultValue: isQrScannerEnabledDefault)
}

private let isQrScannerEnabledDefault = {
#if targetEnvironment(simulator)
  false
#else
  true
#endif
}()

private func boolBinding(key: String, defaultValue: Bool) -> Binding<Bool> {
  Binding<Bool>(
    get: { defaults.value(forKey: key) as? Bool ?? defaultValue },
    set: { defaults.set($0, forKey: key) }
  )
}

private var defaults: UserDefaults {
  UserDefaults.standard
}

private let isQrScannerEnabledKey = "isQrScannerEnabled"
private let lastUrlKey = "lastUrl"
