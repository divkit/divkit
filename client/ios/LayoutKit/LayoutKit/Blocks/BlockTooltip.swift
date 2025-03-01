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

  public let params: BlockTooltipParams

  public let block: Block
  public let offset: CGPoint
  public let position: Position
  public let useLegacyWidth: Bool
  public let tooltipViewFactory: TooltipViewFactory?

  public init(
    block: Block,
    params: BlockTooltipParams,
    offset: CGPoint,
    position: BlockTooltip.Position,
    useLegacyWidth: Bool = true,
    tooltipViewFactory: TooltipViewFactory? = nil
  ) {
    self.block = block
    self.offset = offset
    self.position = position
    self.useLegacyWidth = useLegacyWidth
    self.tooltipViewFactory = tooltipViewFactory
    self.params = params
  }

  public var id: String {
    params.id
  }

  public static func ==(lhs: BlockTooltip, rhs: BlockTooltip) -> Bool {
    lhs.params == rhs.params &&
      lhs.offset == rhs.offset &&
      lhs.position == rhs.position &&
      lhs.useLegacyWidth == rhs.useLegacyWidth &&
      lhs.block.equals(rhs.block)
  }
}
