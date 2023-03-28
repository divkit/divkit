import CoreGraphics

import CommonCorePublic

public final class PinchToZoomBlock: WrapperBlock, LayoutCachingDefaultImpl {
  public let child: Block
  public let overlayView: ViewType

  public init(child: Block, overlayView: ViewType) {
    self.child = child
    self.overlayView = overlayView
  }

  public func makeCopy(wrapping block: Block) -> PinchToZoomBlock {
    PinchToZoomBlock(child: block, overlayView: overlayView)
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? PinchToZoomBlock else {
      return false
    }
    return self == other
  }
}

extension PinchToZoomBlock: Equatable {
  public static func ==(lhs: PinchToZoomBlock, rhs: PinchToZoomBlock) -> Bool {
    lhs.child == rhs.child
  }
}

extension PinchToZoomBlock: CustomDebugStringConvertible {
  public var debugDescription: String { "PinchToZoomBlock child: \(child)" }
}
