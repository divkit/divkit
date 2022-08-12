// Copyright 2019 Yandex LLC. All rights reserved.

import Foundation
import UIKit

import CommonCore

public final class GenericCollectionReusableView: UICollectionReusableView {
  public private(set) var model: UIViewRenderable!

  private var view: BlockView? {
    didSet {
      oldValue?.removeFromSuperview()
      if let view = view {
        addSubview(view)
        setNeedsLayout()
      }
    }
  }

  public func configure(model: UIViewRenderable, observer: ElementStateObserver? = nil) {
    self.model = model

    if let view = view, model.canConfigureBlockView(view) {
      model.configureBlockView(
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
