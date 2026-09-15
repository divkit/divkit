#if os(iOS)
import Foundation
import UIKit
import VGSL

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
      layoutFactory: { [weak galleryView] model, size in
        galleryView?.reusingDefaultLayout(model: model, boundsSize: size)
          ?? GalleryViewLayout(model: model, boundsSize: size)
      },
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is GalleryView
  }
}
#endif
