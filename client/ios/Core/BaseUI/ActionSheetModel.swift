// Copyright 2020 Yandex LLC. All rights reserved.

import Foundation

public struct ActionSheetModel {
  public let title: String?
  public let subtitle: String?
  public let buttons: [AlertButton]
  public let userInterfaceStyle: UserInterfaceStyle?
  public let accessibilityID: String?

  public init(
    title: String? = nil,
    subtitle: String? = nil,
    buttons: [AlertButton],
    userInterfaceStyle: UserInterfaceStyle? = nil,
    accessibilityID: String? = nil
  ) {
    self.title = title
    self.subtitle = subtitle
    self.buttons = buttons
    self.userInterfaceStyle = userInterfaceStyle
    self.accessibilityID = accessibilityID
  }

  public func hasSameComparablePayload(_ other: Self) -> Bool {
    title == other.title && subtitle == other.subtitle
      && userInterfaceStyle == other.userInterfaceStyle
      && AlertButton.hasEqualParams(buttons, other.buttons)
  }
}
