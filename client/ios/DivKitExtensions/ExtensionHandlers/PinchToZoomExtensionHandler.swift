import DivKit
import LayoutKit

public final class PinchToZoomExtensionHandler: DivExtensionHandler {
  private weak var overlayView: ViewType?

  public let id = "pinch-to-zoom"

  public init(overlayView: ViewType) {
    self.overlayView = overlayView
  }

  public func applyAfterBaseProperties(
    to block: Block,
    div _: DivBase,
    context: DivBlockModelingContext
  ) -> Block {
    guard let overlayView = overlayView else {
      context.addError(
        level: .error,
        message: "PinchToZoomExtensionHandler.overlayView is nil"
      )
      return EmptyBlock()
    }
    
    return PinchToZoomBlock(child: block, overlayView: overlayView)
  }
}
