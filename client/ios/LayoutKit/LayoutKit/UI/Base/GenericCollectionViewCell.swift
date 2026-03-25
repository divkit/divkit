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
    accessibilityElement _: AccessibilityElement? = nil
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

    // Do not apply accessibility on the cell itself — the inner BlockView
    // already carries its own accessibility properties.  Applying them on the
    // cell as well produced a redundant accessibility node because UIKit's
    // mandatory contentView sits between the cell and the BlockView, showing
    // up as an extra "Other" element in the accessibility tree.
    isAccessibilityElement = false
    contentView.isAccessibilityElement = false
  }
}
#endif
