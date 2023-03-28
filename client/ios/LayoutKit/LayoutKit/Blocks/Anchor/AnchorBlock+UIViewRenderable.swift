import UIKit

import CommonCorePublic

extension AnchorBlock {
  public static func makeBlockView() -> BlockView {
    AnchorView()
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is AnchorView
  }

  public func configureBlockView(
    _ view: BlockView,
    with layout: Layout?,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    (view as! AnchorView).configure(
      model: .init(block: self, layout: layout),
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
  }
}

private final class AnchorView: BlockView, VisibleBoundsTrackingContainer {
  struct Model: Equatable {
    let block: AnchorBlock
    let layout: AnchorBlock.Layout?

    static func ==(lhs: Model, rhs: Model) -> Bool {
      lhs.block === rhs.block && lhs.layout == rhs.layout
    }
  }

  private var leadingView: BlockView?
  private var centerView: BlockView?
  private var trailingView: BlockView?
  private var modelAndLastLayoutSize: (model: Model?, lastLayoutSize: CGSize?)
  private var preventLayout = false
  private weak var observer: ElementStateObserver?

  private var block: AnchorBlock! {
    modelAndLastLayoutSize.model?.block
  }

  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] {
    [leadingView, centerView, trailingView].compactMap { $0 }
  }

  var effectiveBackgroundColor: UIColor? { backgroundColor }

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

    func reuse(_ block: Block?, view: BlockView?) -> BlockView? {
      guard let block = block else {
        view?.removeFromSuperview()
        return nil
      }

      return block.reuse(
        view,
        observer: observer,
        overscrollDelegate: overscrollDelegate,
        renderingDelegate: renderingDelegate,
        superview: self
      )
    }

    // Configuring views may lead to unpredictable side effects,
    // including view hierarchy layout.
    preventLayout = true
    leadingView = reuse(block.leading, view: leadingView)
    centerView = reuse(block.center, view: centerView)
    trailingView = reuse(block.trailing, view: trailingView)
    preventLayout = false

    setNeedsLayout()
  }

  override func layoutSubviews() {
    guard !preventLayout else { return }

    super.layoutSubviews()

    if let lastLayoutSize = modelAndLastLayoutSize.lastLayoutSize, bounds.size == lastLayoutSize {
      return
    }

    guard let model = modelAndLastLayoutSize.model else {
      return
    }

    let layout = model.layout ?? model.block.makeLayout(for: bounds.size)

    leadingView?.frame = layout.leadingFrame
    centerView?.frame = layout.centerFrame
    trailingView?.frame = layout.trailingFrame
  }

  override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
    let result = super.hitTest(point, with: event)
    return result === self ? nil : result
  }
}
