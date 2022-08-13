// Copyright 2020 Yandex LLC. All rights reserved.

import Foundation

public struct AlertTextField: Equatable {
  public let placeholder: String?
  public let initialText: String?
  public let isSecureTextEntry: Bool

  public init(
    placeholder: String? = nil,
    initialText: String? = nil,
    isSecureTextEntry: Bool = false
  ) {
    self.placeholder = placeholder
    self.initialText = initialText
    self.isSecureTextEntry = isSecureTextEntry
  }
}
