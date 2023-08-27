import UIKit

import DivKit
import LayoutKit

public final class PinchToZoomExtensionHandler: DivExtensionHandler {
  private weak var overlayView: UIView?

  public let id = "pinch-to-zoom"

  public init(overlayView: UIView) {
    self.overlayView = overlayView
  }

  public func applyAfterBaseProperties(
    to block: Block,
    div _: DivBase,
    context: DivBlockModelingContext
  ) -> Block {
    guard let overlayView = overlayView else {
      context.addError(message: "PinchToZoomExtensionHandler.overlayView is nil")
      return EmptyBlock()
    }

    return PinchToZoomBlock(child: block, overlayView: overlayView)
  }
}
