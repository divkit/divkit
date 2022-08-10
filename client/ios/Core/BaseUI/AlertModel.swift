// Copyright 2020 Yandex LLC. All rights reserved.

import Foundation

public struct AlertModel {
  public let title: String?
  public let subtitle: String?
  public let buttons: [AlertButton]
  public let textFields: [AlertTextField]
  public let userInterfaceStyle: UserInterfaceStyle?
  public let accessibilityID: String?

  public init(
    title: String,
    subtitle: String? = nil,
    buttons: [AlertButton],
    textFields: [AlertTextField] = [],
    userInterfaceStyle: UserInterfaceStyle? = nil,
    accessibilityID: String? = nil
  ) {
    self.init(
      unsafeTitle: title,
      subtitle: subtitle,
      buttons: buttons,
      textFields: textFields,
      userInterfaceStyle: userInterfaceStyle,
      accessibilityID: accessibilityID
    )
  }

  public init<T: RawRepresentable>(
    title: String,
    subtitle: String? = nil,
    buttons: [AlertButton],
    textFields: [AlertTextField] = [],
    userInterfaceStyle: UserInterfaceStyle? = nil,
    accessibilityID: T
  ) where T.RawValue == String {
    self.init(
      unsafeTitle: title,
      subtitle: subtitle,
      buttons: buttons,
      textFields: textFields,
      userInterfaceStyle: userInterfaceStyle,
      accessibilityID: accessibilityID.rawValue
    )
  }

  public init(
    subtitle: String,
    buttons: [AlertButton],
    textFields: [AlertTextField] = [],
    userInterfaceStyle: UserInterfaceStyle? = nil,
    accessibilityID: String? = nil
  ) {
    self.init(
      unsafeTitle: nil,
      subtitle: subtitle,
      buttons: buttons,
      textFields: textFields,
      userInterfaceStyle: userInterfaceStyle,
      accessibilityID: accessibilityID
    )
  }

  public func copy(updateButtonActions: (AlertButton.Action) -> AlertButton.Action) -> Self {
    Self(
      unsafeTitle: title,
      subtitle: subtitle,
      buttons: buttons.map { $0.copy(updateAction: updateButtonActions) },
      textFields: textFields,
      userInterfaceStyle: userInterfaceStyle,
      accessibilityID: accessibilityID
    )
  }

  public func hasSameComparablePayload(_ other: Self) -> Bool {
    title == other.title && subtitle == other.subtitle
      && userInterfaceStyle == other.userInterfaceStyle
      && textFields == other.textFields
      && AlertButton.hasEqualParams(buttons, other.buttons)
  }

  private init(
    unsafeTitle title: String?,
    subtitle: String?,
    buttons: [AlertButton],
    textFields: [AlertTextField],
    userInterfaceStyle: UserInterfaceStyle?,
    accessibilityID: String? = nil
  ) {
    precondition(title != nil || subtitle != nil)
    self.title = title
    self.subtitle = subtitle
    self.buttons = buttons
    self.textFields = textFields
    self.userInterfaceStyle = userInterfaceStyle
    self.accessibilityID = accessibilityID
  }
}
