import Foundation

public struct BlockTooltipParams: Equatable {
  public let id: String
  public let mode: BlockTooltip.Mode
  public let duration: TimeInterval
  public let closeByTapOutside: Bool
  let tapOutsideActions: [UserInterfaceAction]
  public let backgroundAccessibilityDescription: String?

  public init(
    id: String,
    mode: BlockTooltip.Mode,
    duration: TimeInterval,
    closeByTapOutside: Bool,
    tapOutsideActions: [UserInterfaceAction] = [],
    backgroundAccessibilityDescription: String? = nil
  ) {
    self.id = id
    self.mode = mode
    self.duration = duration
    self.closeByTapOutside = closeByTapOutside
    self.tapOutsideActions = tapOutsideActions
    self.backgroundAccessibilityDescription = backgroundAccessibilityDescription
  }
}
