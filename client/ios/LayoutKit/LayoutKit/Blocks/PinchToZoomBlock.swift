import CoreGraphics
import VGSL

/// The caller owns the observer; neither the block nor its view retains it.
/// The session ends after the return animation or immediately when interrupted.
/// A missing overlay is detected on the next layout pass or gesture event.
public protocol PinchToZoomObserver: AnyObject {
  @MainActor
  func pinchToZoomDidBegin(in view: BlockView, scale: CGFloat)

  @MainActor
  func pinchToZoomDidChange(in view: BlockView, scale: CGFloat)

  @MainActor
  func pinchToZoomDidEnd(in view: BlockView)
}

public final class PinchToZoomBlock: WrapperBlock, LayoutCachingDefaultImpl {
  public let child: Block

  private(set) weak var overlayView: ViewType?
  private(set) weak var zoomObserver: PinchToZoomObserver?

  public convenience init(child: Block, overlayView: ViewType) {
    self.init(child: child, overlayView: overlayView, zoomObserver: nil)
  }

  public init(
    child: Block,
    overlayView: ViewType,
    zoomObserver: PinchToZoomObserver?
  ) {
    self.child = child
    self.overlayView = overlayView
    self.zoomObserver = zoomObserver
  }

  private init(child: Block, overlayView: ViewType?, zoomObserver: PinchToZoomObserver?) {
    self.child = child
    self.overlayView = overlayView
    self.zoomObserver = zoomObserver
  }

  public func makeCopy(wrapping block: Block) -> PinchToZoomBlock {
    PinchToZoomBlock(child: block, overlayView: overlayView, zoomObserver: zoomObserver)
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
    lhs.child == rhs.child && lhs.zoomObserver === rhs.zoomObserver
  }
}

extension PinchToZoomBlock: CustomDebugStringConvertible {
  public var debugDescription: String { "PinchToZoomBlock child: \(child)" }
}
