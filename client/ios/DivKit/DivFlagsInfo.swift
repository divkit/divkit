// Copyright 2021 Yandex LLC. All rights reserved.

public struct DivFlagsInfo {
  public let isTextSelectingEnabled: Bool
  public let appendVariablesEnabled: Bool

  public init(
    isTextSelectingEnabled: Bool,
    appendVariablesEnabled: Bool
  ) {
    self.isTextSelectingEnabled = isTextSelectingEnabled
    self.appendVariablesEnabled = appendVariablesEnabled
  }

  public static let `default` = DivFlagsInfo(
    isTextSelectingEnabled: false,
    appendVariablesEnabled: true
  )
}
