#if os(iOS)
import DivKit
import LayoutKit
import UIKit

public final class PinchToZoomExtensionHandler: DivExtensionHandler {
  public let id = "pinch-to-zoom"

  private let overlayViewProvider: () -> UIView?

  public init(overlayView: UIView) {
    self.overlayViewProvider = { [weak overlayView] in overlayView }
  }

  public init(overlayViewProvider: @escaping () -> UIView?) {
    self.overlayViewProvider = overlayViewProvider
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

    return PinchToZoomBlock(child: block, overlayView: overlayView)
  }
}
#endif
