import Foundation

enum UserPreferences {
  static var lastUrl: String {
    get {
      UserDefaults.standard.string(forKey: "lastUrl") ?? ""
    }
    set {
      UserDefaults.standard.set(newValue, forKey: "lastUrl")
    }
  }
}
