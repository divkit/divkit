import Foundation
import UIKit
import VGSL

public final class GenericCollectionReusableView: UICollectionReusableView {
  public private(set) var model: UIViewRenderable!

  private var view: BlockView? {
    didSet {
      oldValue?.removeFrom(self)
      if let view {
        addSubview(view)
        setNeedsLayout()
      }
    }
  }

  public func configure(model: UIViewRenderable, observer: ElementStateObserver? = nil) {
    self.model = model

    if let view, model.canConfigureBlockView(view) {
      model.configureBlockViewWithReporting(
        view,
        observer: observer,
        overscrollDelegate: nil,
        renderingDelegate: nil
      )
    } else {
      view = model.makeBlockView(
        observer: observer,
        overscrollDelegate: nil,
        renderingDelegate: nil
      )
    }
  }

  public override func layoutSubviews() {
    super.layoutSubviews()
    view?.frame = bounds
  }
}
