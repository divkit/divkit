import CoreGraphics
import VGSL

#if os(iOS)
public typealias TooltipViewFactory = Variable<VisibleBoundsTrackingView?>
#else
public typealias TooltipViewFactory = Variable<ViewType?>
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

  public let id: String
  public let block: Block
  public let duration: Duration
  public let offset: CGPoint
  public let position: Position
  public let useLegacyWidth: Bool
  public let tooltipViewFactory: TooltipViewFactory?

  public init(
    id: String,
    block: Block,
    duration: Duration,
    offset: CGPoint,
    position: BlockTooltip.Position,
    useLegacyWidth: Bool = true,
    tooltipViewFactory: TooltipViewFactory? = nil
  ) {
    self.id = id
    self.block = block
    self.duration = duration
    self.offset = offset
    self.position = position
    self.useLegacyWidth = useLegacyWidth
    self.tooltipViewFactory = tooltipViewFactory
  }

  public static func ==(lhs: BlockTooltip, rhs: BlockTooltip) -> Bool {
    lhs.id == rhs.id &&
      lhs.duration == rhs.duration &&
      lhs.offset == rhs.offset &&
      lhs.position == rhs.position &&
      lhs.useLegacyWidth == rhs.useLegacyWidth &&
      lhs.block.equals(rhs.block)
  }
}
