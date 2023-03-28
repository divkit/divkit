import UIKit

import CommonCorePublic

extension LayeredBlock {
  public static func makeBlockView() -> BlockView {
    LayeredBlockView()
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is LayeredBlockView
  }

  func configureBlockView(
    _ view: BlockView,
    with layout: Layout?,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    let view = view as! LayeredBlockView
    let model = LayeredBlockView.Model(
      block: self,
      layout: layout
    )
    view.configure(
      model: model,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
  }
}

private final class LayeredBlockView: BlockView, VisibleBoundsTrackingContainer {
  struct Model: ReferenceEquatable {
    let block: LayeredBlock
    let layout: LayeredBlock.Layout?
    var source: Variable<AnyObject?> {
      Variable { [weak block] in block }
    }
  }

  private weak var observer: ElementStateObserver?

  // layoutSubivews is called multiple times for same view size and model, so we optimize out
  // redundant calls
  private var modelAndLastLayoutSize: (model: Model?, lastLayoutSize: CGSize?)
  private var preventLayout = false

  private var blockViews: [BlockView] = []

  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] { blockViews }
  var effectiveBackgroundColor: UIColor? { blockViews.first?.effectiveBackgroundColor }

  func configure(
    model: Model,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    guard model != modelAndLastLayoutSize.model || observer !== self.observer else {
      return
    }

    modelAndLastLayoutSize = (model: model, lastLayoutSize: nil)
    self.observer = observer

    // Configuring views may lead to unpredictable side effects,
    // including view hierarchy layout.
    preventLayout = true
    blockViews = blockViews.reused(
      with: model.block.children.map(\.content),
      attachTo: self,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
    preventLayout = false

    setNeedsLayout()
  }

  override func layoutSubviews() {
    guard !preventLayout else { return }

    super.layoutSubviews()

    guard let model = modelAndLastLayoutSize.model,
          modelAndLastLayoutSize.lastLayoutSize != bounds.size else {
      return
    }

    let childrenFrames = model.layout ?? model.block.makeChildrenFrames(size: bounds.size)

    zip(blockViews, childrenFrames).forEach { view, frame in
      let currentTransform = view.transform
      view.transform = .identity
      view.frame = frame
      view.transform = currentTransform
    }

    modelAndLastLayoutSize = (model: model, lastLayoutSize: bounds.size)
  }

  override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
    let result = super.hitTest(point, with: event)
    return result == self ? nil : result
  }
}
