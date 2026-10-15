#if os(iOS)
import DivKit
import LayoutKit
import UIKit

public final class PinchToZoomExtensionHandler: DivExtensionHandler {
  public let id = "pinch-to-zoom"

  private let overlayViewProvider: () -> UIView?
  private weak var zoomObserver: PinchToZoomObserver?

  public convenience init(overlayView: UIView) {
    self.init(overlayView: overlayView, zoomObserver: nil)
  }

  public init(overlayView: UIView, zoomObserver: PinchToZoomObserver?) {
    self.overlayViewProvider = { [weak overlayView] in overlayView }
    self.zoomObserver = zoomObserver
  }

  public convenience init(overlayViewProvider: @escaping () -> UIView?) {
    self.init(overlayViewProvider: overlayViewProvider, zoomObserver: nil)
  }

  public init(
    overlayViewProvider: @escaping () -> UIView?,
    zoomObserver: PinchToZoomObserver?
  ) {
    self.overlayViewProvider = overlayViewProvider
    self.zoomObserver = zoomObserver
  }

  public func applyAfterBaseProperties(
    to block: Block,
    div _: DivBase,
    context: DivBlockModelingContext
  ) -> Block {
    guard let overlayView = overlayViewProvider() else {
      context.addError(message: "PinchToZoomExtensionHandler.overlayView is nil")
      return EmptyBlock()
    }

    return PinchToZoomBlock(child: block, overlayView: overlayView, zoomObserver: zoomObserver)
  }
}
#endif
