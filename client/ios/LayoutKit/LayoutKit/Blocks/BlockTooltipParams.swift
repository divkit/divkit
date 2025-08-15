import Foundation

public struct BlockTooltipParams: Equatable {
  public let id: String
  public let mode: BlockTooltip.Mode
  public let duration: TimeInterval
  public let closeByTapOutside: Bool
  public let backgroundAccessibilityDescription: String?
  public let animationIn: [TransitioningAnimation]?
  public let animationOut: [TransitioningAnimation]?

  let tapOutsideActions: [UserInterfaceAction]

  public init(
    id: String,
    mode: BlockTooltip.Mode,
    duration: TimeInterval,
    closeByTapOutside: Bool,
    tapOutsideActions: [UserInterfaceAction] = [],
    backgroundAccessibilityDescription: String? = nil,
    animationIn: [TransitioningAnimation]? = nil,
    animationOut: [TransitioningAnimation]? = nil
  ) {
    self.id = id
    self.mode = mode
    self.duration = duration
    self.closeByTapOutside = closeByTapOutside
    self.tapOutsideActions = tapOutsideActions
    self.backgroundAccessibilityDescription = backgroundAccessibilityDescription
    self.animationIn = animationIn
    self.animationOut = animationOut
  }
}
