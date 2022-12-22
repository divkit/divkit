import Foundation

private var externalLogger: (String) -> Void = { _ in }

public enum DivKitLogger {
  public static var isEnabled: Bool = false

  public static func error(_ message: String) {
    if isEnabled {
      print("[DivKit] [ERROR] \(message)")
      externalLogger(message)
    }
  }

  static func failure(_ message: String) {
    if isEnabled {
      error(message)
      assertionFailure(message)
    }
  }

  static func warning(_ message: String) {
    if isEnabled {
      print("[DivKit] [WARNING] \(message)")
      externalLogger(message)
    }
  }

  public static func setLogger(_ logger: @escaping (String) -> Void) {
    Thread.assertIsMain()
    externalLogger = logger
  }
}
