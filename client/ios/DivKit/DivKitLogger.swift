import Foundation

private var externalLogger: (DivErrorLevel, String) -> Void = { _,_  in }

public enum DivKitLogger {
  public static var isEnabled: Bool = false

  public static func error(_ message: String) {
    if isEnabled {
      print("[DivKit] [ERROR] \(message)")
      externalLogger(.error, message)
    }
  }

  static func failure(_ message: String) {
    if isEnabled {
      error(message)
      assertionFailure(message)
    }
  }

  public static func warning(_ message: String) {
    if isEnabled {
      print("[DivKit] [WARNING] \(message)")
      externalLogger(.warning, message)
    }
  }

  public static func setLogger(_ logger: @escaping (DivErrorLevel, String) -> Void) {
    Thread.assertIsMain()
    externalLogger = logger
  }
  public static func setLogger(_ logger: @escaping (String) -> Void) {
    setLogger({ logger($1) })
  }
}
