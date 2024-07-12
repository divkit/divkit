import UIKit

import LayoutKit
import VGSL

extension SizeProviderBlock {
  static func makeBlockView() -> BlockView {
    SizeProviderBlockView()
  }

  func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is SizeProviderBlockView
  }

  func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    (view as! SizeProviderBlockView).configure(
      block: self,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
  }
}

private final class SizeProviderBlockView: BlockView {
  private var block: SizeProviderBlock!
  private var childView: BlockView!

  init() {
    super.init(frame: .zero)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  var effectiveBackgroundColor: UIColor? { childView.backgroundColor }

  func configure(
    block: SizeProviderBlock,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    self.block = block
    childView = block.child.reuse(
      childView,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      superview: self
    )
    setNeedsLayout()
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    childView.frame = bounds
    block.widthUpdater?(Int(bounds.width))
    block.heightUpdater?(Int(bounds.height))
  }
}

extension SizeProviderBlockView: VisibleBoundsTrackingContainer {
  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] {
    [childView]
  }
}
