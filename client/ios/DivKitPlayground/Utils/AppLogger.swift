import Foundation

enum AppLogger {
  static func info(_ message: String) {
    print("[DivKitPlayground] [INFO] \(message)")
  }

  static func error(_ message: String) {
    print("[DivKitPlayground] [ERROR] \(message)")
  }
}
