// Copyright 2017 Yandex LLC. All rights reserved.

import Foundation

extension Thread {
  public static func assertIsMain() {
    assert(isMainThread)
  }

  public static func assertIsNotMain() {
    #if INTERNAL_BUILD
    guard !isUnitTest else { return }
    assert(!isMainThread)
    #endif
  }
}

private let isUnitTest = ProcessInfo.processInfo.environment["XCTestConfigurationFilePath"] != nil
