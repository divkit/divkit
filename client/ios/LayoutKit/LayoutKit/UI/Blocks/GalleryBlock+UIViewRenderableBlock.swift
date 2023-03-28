import Foundation
import UIKit

import CommonCorePublic

extension GalleryBlock {
  public static func makeBlockView() -> BlockView {
    GalleryView(frame: .zero)
  }

  public func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    let galleryView = view as! GalleryView
    galleryView.configure(
      model: model,
      state: state,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is GalleryView
  }
}
