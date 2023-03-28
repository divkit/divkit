import CoreGraphics

import CommonCorePublic

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
  }

  public let id: String
  public let block: Block
  public let duration: Duration
  public let offset: CGPoint
  public let position: Position

  public init(
    id: String,
    block: Block,
    duration: Duration,
    offset: CGPoint,
    position: BlockTooltip.Position
  ) {
    self.id = id
    self.block = block
    self.duration = duration
    self.offset = offset
    self.position = position
  }

  public static func ==(lhs: BlockTooltip, rhs: BlockTooltip) -> Bool {
    lhs.id == rhs.id &&
      lhs.duration == rhs.duration &&
      lhs.offset == rhs.offset &&
      lhs.position == rhs.position &&
      lhs.block.equals(rhs.block)
  }
}
