#if os(iOS)
import Foundation
import UIKit
import VGSL

open class GenericCollectionViewCell: UICollectionViewCell, VisibleBoundsTrackingContainer {
  public private(set) var model: UIViewRenderable!

  public private(set) var view: BlockView? {
    didSet {
      oldValue?.removeFromSuperview()
      if let view {
        contentView.addSubview(view)
        setNeedsLayout()
      }
    }
  }

  public var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] { view.asArray() }

  public override func layoutSubviews() {
    super.layoutSubviews()
    view?.frame = contentView.bounds
  }

  public func configure(
    model: UIViewRenderable,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?,
    accessibilityElement: AccessibilityElement?
  ) {
    self.model = model

    if let view, model.canConfigureBlockView(view) {
      model.configureBlockViewWithReporting(
        view,
        observer: observer,
        overscrollDelegate: overscrollDelegate,
        renderingDelegate: renderingDelegate
      )
    } else {
      view = model.makeBlockView(
        observer: observer,
        overscrollDelegate: overscrollDelegate,
        renderingDelegate: renderingDelegate
      )
    }

    applyAccessibilityFromScratch(accessibilityElement)
  }

}
#endif
