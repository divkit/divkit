#if os(iOS)
import LayoutKit
import UIKit
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
      renderingDelegate: renderingDelegate,
      childMarginsSize: childMarginSize
    )
  }
}

private final class SizeProviderBlockView: BlockView {
  var childMarginsSize = CGSize.zero

  private var block: SizeProviderBlock!
  private var childView: BlockView!

  var effectiveBackgroundColor: UIColor? { childView.backgroundColor }

  init() {
    super.init(frame: .zero)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    childView.frame = bounds
    block.widthUpdater?(Int(bounds.width - childMarginsSize.width))
    block.heightUpdater?(Int(bounds.height - childMarginsSize.height))
  }

  func configure(
    block: SizeProviderBlock,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?,
    childMarginsSize: CGSize
  ) {
    self.block = block
    self.childMarginsSize = childMarginsSize
    childView = block.child.reuse(
      childView,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      superview: self
    )
    setNeedsLayout()
  }
}

extension SizeProviderBlockView: VisibleBoundsTrackingContainer {
  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] {
    [childView]
  }
}
#endif
