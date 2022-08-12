// Copyright 2021 Yandex LLC. All rights reserved.

public struct DivFlagsInfo {
  public let isTextSelectingEnabled: Bool

  public init(isTextSelectingEnabled: Bool) {
    self.isTextSelectingEnabled = isTextSelectingEnabled
  }

  public static let `default` = DivFlagsInfo(isTextSelectingEnabled: false)
}
