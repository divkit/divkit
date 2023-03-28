import Foundation
import UIKit

import BaseUIPublic
import CommonCorePublic

open class GenericCollectionViewCell: UICollectionViewCell, VisibleBoundsTrackingContainer {
  public private(set) var model: UIViewRenderable!

  public var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] { view.asArray() }

  public private(set) var view: BlockView? {
    didSet {
      oldValue?.removeFromSuperview()
      if let view = view {
        contentView.addSubview(view)
        setNeedsLayout()
      }
    }
  }

  public func configure(
    model: UIViewRenderable,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?,
    accessibilityElement: AccessibilityElement?
  ) {
    self.model = model

    if let view = view, model.canConfigureBlockView(view) {
      model.configureBlockView(
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

    applyAccessibility(accessibilityElement)
  }

  public override func layoutSubviews() {
    super.layoutSubviews()
    view?.frame = contentView.bounds
  }
}
