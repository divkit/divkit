// Copyright 2019 Yandex LLC. All rights reserved.

import Foundation

public struct InvalidArgumentError: Error {
  public let name: String
  public let value: Any

  #if DEBUG
  public let callStack = Thread.callStackSymbols
  #endif

  public init(name: String, value: Any) {
    self.name = name
    self.value = value
  }
}
