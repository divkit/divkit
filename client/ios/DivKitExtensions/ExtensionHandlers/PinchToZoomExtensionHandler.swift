#if os(iOS)
import DivKit
import LayoutKit
import UIKit

public final class PinchToZoomExtensionHandler: DivExtensionHandler {
  public let id = "pinch-to-zoom"

  private weak var overlayView: UIView?

  public init(overlayView: UIView) {
    self.overlayView = overlayView
  }

  public func applyAfterBaseProperties(
    to block: Block,
    div _: DivBase,
    context: DivBlockModelingContext
  ) -> Block {
    guard let overlayView else {
      context.addError(message: "PinchToZoomExtensionHandler.overlayView is nil")
      return EmptyBlock()
    }

    return PinchToZoomBlock(child: block, overlayView: overlayView)
  }
}
#endif
