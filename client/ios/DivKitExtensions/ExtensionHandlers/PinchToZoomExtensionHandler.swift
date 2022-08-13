// Copyright 2021 Yandex LLC. All rights reserved.

import DivKit
import LayoutKit

public final class PinchToZoomExtensionHandler: DivExtensionHandler {
  private let overlayView: ViewType

  public let id = "pinch-to-zoom"

  public init(overlayView: ViewType) {
    self.overlayView = overlayView
  }

  public func applyAfterBaseProperties(
    to block: Block,
    div _: DivBase,
    context _: DivBlockModelingContext
  ) -> Block {
    PinchToZoomBlock(child: block, overlayView: overlayView)
  }
}
