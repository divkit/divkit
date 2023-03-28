// Copyright 2021 Yandex LLC. All rights reserved.

import Foundation

public struct AlertButton {
  public enum Trait: Int {
    case `default`
    case cancel
    case destructive
  }

  public enum Action {
    public typealias CustomAction = () -> Void
    public typealias SubmitInputAction = ([String]) -> Void
    case none
    case custom(CustomAction)
    case submitInput(SubmitInputAction)
  }

  public let title: String
  public let actionStyle: Trait
  public let isChecked: Bool
  public let isEnabled: Bool
  public let action: Action

  public init(
    title: String,
    actionStyle: Trait = .default,
    isChecked: Bool = false,
    isEnabled: Bool = true,
    action customAction: Action.CustomAction?
  ) {
    let action: Action
    if let customAction = customAction {
      action = .custom(customAction)
    } else {
      action = .none
    }
    self.init(
      title: title,
      actionStyle: actionStyle,
      isChecked: isChecked,
      isEnabled: isEnabled,
      action: action
    )
  }

  public init(
    title: String,
    actionStyle: Trait = .default,
    isChecked: Bool = false,
    isEnabled: Bool = true,
    action: @escaping Action.SubmitInputAction
  ) {
    self.init(
      title: title,
      actionStyle: actionStyle,
      isChecked: isChecked,
      isEnabled: isEnabled,
      action: .submitInput(action)
    )
  }

  public init(
    title: String,
    actionStyle: Trait = .default,
    isChecked: Bool = false,
    isEnabled: Bool = true,
    action: Action = .none
  ) {
    self.title = title
    self.actionStyle = actionStyle
    self.isChecked = isChecked
    self.isEnabled = isEnabled
    self.action = action
  }

  func copy(updateAction: (Action) -> Action) -> Self {
    Self(
      title: title,
      actionStyle: actionStyle,
      isChecked: isChecked,
      isEnabled: isEnabled,
      action: updateAction(action)
    )
  }

  static func hasEqualParams(_ lhs: [Self], _ rhs: [Self]) -> Bool {
    guard lhs.count == rhs.count else { return false }
    for (lhsButton, rhsButton) in zip(lhs, rhs) {
      if lhsButton.title != rhsButton.title ||
        lhsButton.actionStyle != rhsButton.actionStyle ||
        lhsButton.isEnabled != rhsButton.isEnabled ||
        lhsButton.isChecked != rhsButton.isChecked ||
        !lhsButton.action.hasEqualKind(to: rhsButton.action) {
        return false
      }
    }
    return true
  }
}

extension AlertButton.Action {
  fileprivate func hasEqualKind(to other: Self) -> Bool {
    switch (self, other) {
    case (.none, .none),
         (.custom, .custom),
         (.submitInput, .submitInput):
      return true
    case (.none, _),
         (.custom, _),
         (.submitInput, _):
      return false
    }
  }
}
