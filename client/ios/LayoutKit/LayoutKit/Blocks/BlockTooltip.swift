import CoreGraphics
import Foundation
import VGSL

#if os(iOS)
public typealias TooltipViewFactory = () async -> VisibleBoundsTrackingView?
#else
public typealias TooltipViewFactory = () async -> ViewType?
#endif

public struct BlockTooltip: Equatable {
  public enum Position: String, CaseIterable {
    case left
    case topLeft
    case top
    case topRight
    case right
    case bottomRight
    case bottom
    case bottomLeft
    case center
  }

  public enum Mode: Equatable {
    case modal
    case nonModal
  }

  public let id: String
  public let block: Block
  public let duration: TimeInterval
  public let offset: CGPoint
  public let position: Position
  public let useLegacyWidth: Bool
  public let tooltipViewFactory: TooltipViewFactory?
  public let closeByTapOutside: Bool
  public let tapOutsideActions: [UserInterfaceAction]
  public let mode: Mode

  public init(
    id: String,
    block: Block,
    duration: TimeInterval,
    offset: CGPoint,
    position: BlockTooltip.Position,
    useLegacyWidth: Bool = true,
    tooltipViewFactory: TooltipViewFactory? = nil,
    closeByTapOutside: Bool = true,
    tapOutsideActions: [UserInterfaceAction] = [],
    mode: Mode = .modal
  ) {
    self.id = id
    self.block = block
    self.duration = duration
    self.offset = offset
    self.position = position
    self.useLegacyWidth = useLegacyWidth
    self.tooltipViewFactory = tooltipViewFactory
    self.closeByTapOutside = closeByTapOutside
    self.tapOutsideActions = tapOutsideActions
    self.mode = mode
  }

  public static func ==(lhs: BlockTooltip, rhs: BlockTooltip) -> Bool {
    lhs.id == rhs.id &&
      lhs.duration == rhs.duration &&
      lhs.offset == rhs.offset &&
      lhs.position == rhs.position &&
      lhs.useLegacyWidth == rhs.useLegacyWidth &&
      lhs.block.equals(rhs.block) &&
      lhs.closeByTapOutside == rhs.closeByTapOutside &&
      lhs.tapOutsideActions == rhs.tapOutsideActions &&
      lhs.mode == rhs.mode
  }
}
