import Foundation

public struct FocusViewState: ElementState, Equatable {
  public let isFocused: Bool

  public static let `default` = FocusViewState(isFocused: false)

  public init(isFocused: Bool) {
    self.isFocused = isFocused
  }
}
