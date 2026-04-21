#if os(iOS)
import LayoutKit
import UIKit
import VGSL

extension RasterizeBlock {
  static func makeBlockView() -> BlockView { RasterizeView() }

  func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is RasterizeView
  }

  func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    (view as! RasterizeView).configure(
      child: child,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
  }
}

private final class RasterizeView: UIView, BlockViewProtocol, VisibleBoundsTrackingContainer {
  private var childView: BlockView?

  var effectiveBackgroundColor: UIColor? { childView?.effectiveBackgroundColor }

  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] {
    childView.asArray()
  }

  override init(frame: CGRect) {
    super.init(frame: frame)
    layer.shouldRasterize = true
    layer.rasterizationScale = UIScreen.main.scale
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) { fatalError("init(coder:) has not been implemented") }

  override func layoutSubviews() {
    super.layoutSubviews()
    childView?.frame = bounds
  }

  func configure(
    child: Block,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    childView = child.reuse(
      childView,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      superview: self
    )
    setNeedsLayout()
  }
}
#endif
