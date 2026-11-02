import Foundation
import VGSL

public struct AccessibilityCustomAction: Equatable {
  public let name: String
  public let actions: NonEmptyArray<UserInterfaceAction>

  public init(name: String, actions: NonEmptyArray<UserInterfaceAction>) {
    self.name = name
    self.actions = actions
  }
}
