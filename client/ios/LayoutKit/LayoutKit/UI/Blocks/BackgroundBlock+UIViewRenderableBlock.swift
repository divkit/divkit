import UIKit

import CommonCorePublic

extension BackgroundBlock {
  public static func makeBlockView() -> BlockView {
    BackgroundBlockView()
  }

  func configureBlockView(
    _ view: BlockView,
    with layout: Layout?,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    let view = view as! BackgroundBlockView
    let model = BackgroundBlockView.Model(
      background: background,
      child: child,
      source: Variable { [weak self] in self },
      cornerRadius: cornerRadius,
      layout: layout
    )
    view.configure(
      model: model,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is BackgroundBlockView
  }
}

private final class BackgroundBlockView: BlockView, VisibleBoundsTrackingContainer {
  private var backgroundView: BlockView? {
    didSet {
      guard oldValue !== backgroundView else { return }
      oldValue?.removeFromSuperview()
      if let view = backgroundView {
        insertSubview(view, at: 0)
      }
    }
  }

  private var childView: BlockView? {
    didSet {
      guard oldValue !== childView else { return }
      oldValue?.removeFromSuperview()
      if let view = childView {
        addSubview(view)
      }
    }
  }

  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] {
    [childView, backgroundView].compactMap { $0 }
  }

  var effectiveBackgroundColor: UIColor? { backgroundView?.effectiveBackgroundColor }

  struct Model: ReferenceEquatable {
    let background: Background
    let child: Block
    let source: Variable<AnyObject?>
    let cornerRadius: CGFloat
    let layout: BackgroundBlock.Layout?
  }

  private var model: Model!
  private weak var observer: ElementStateObserver?

  func configure(
    model: Model,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    guard self.model != model || self.observer !== observer else { return }
    let oldModel = self.model

    self.model = model
    self.observer = observer

    if model.background != oldModel?.background {
      backgroundView = model.background.apply(
        to: backgroundView,
        observer: observer,
        overscrollDelegate: overscrollDelegate,
        renderingDelegate: renderingDelegate
      )
    }

    if model.child !== oldModel?.child {
      childView = model.child.apply(
        to: childView,
        observer: observer,
        overscrollDelegate: overscrollDelegate,
        renderingDelegate: renderingDelegate
      )
    }

    layer.masksToBounds = true

    setNeedsLayout()
  }

  override func layoutSubviews() {
    super.layoutSubviews()

    layer.cornerRadius = clamp(model.cornerRadius, min: 0, max: bounds.size.minDimension.half)

    backgroundView?.frame = model?.layout ?? bounds
    childView?.frame = model?.layout ?? bounds
  }

  override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
    let result = super.hitTest(point, with: event)
    return result === self ? nil : result
  }
}

extension UIViewRenderable {
  fileprivate func apply(
    to view: BlockView?,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) -> BlockView {
    if let view = view, canConfigureBlockView(view) {
      configureBlockView(
        view,
        observer: observer,
        overscrollDelegate: overscrollDelegate,
        renderingDelegate: renderingDelegate
      )
      return view
    } else {
      return makeBlockView(
        observer: observer,
        overscrollDelegate: overscrollDelegate,
        renderingDelegate: renderingDelegate
      )
    }
  }
}
