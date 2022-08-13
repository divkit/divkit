// Copyright 2022 Yandex LLC. All rights reserved.

import Foundation

private var externalLogger: (String) -> Void = { _ in }

public enum DivKitLogger {
  static func error(_ message: String) {
    #if INTERNAL_BUILD
    print("[DivKit] [ERROR] \(message)")
    externalLogger(message)
    #endif
  }

  static func failure(_ message: String) {
    error(message)
    assertionFailure(message)
  }

  public static func setLogger(_ logger: @escaping (String) -> Void) {
    Thread.assertIsMain()
    externalLogger = logger
  }
}
