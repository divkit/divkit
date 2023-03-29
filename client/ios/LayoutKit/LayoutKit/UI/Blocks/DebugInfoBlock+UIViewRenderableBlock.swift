import UIKit

import CommonCorePublic

extension DebugInfoBlock {
  public static func makeBlockView() -> BlockView {
    DebugInfoBlockView()
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is DebugInfoBlockView
  }

  public func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    (view as! DebugInfoBlockView).configure(
      child: child,
      showDebugInfo: showDebugInfo,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
  }
}

private final class DebugInfoBlockView: BlockView {
  private var childView: BlockView!
  private var showDebugInfo: Action!

  var effectiveBackgroundColor: UIColor? {
    childView.backgroundColor
  }

  init() {
    super.init(frame: .zero)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  func configure(
    child: Block,
    showDebugInfo: @escaping Action,
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
    self.showDebugInfo = showDebugInfo
    setNeedsLayout()
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    self.childView.frame = bounds
  }

  private func showInfo() {
    showDebugInfo()
  }
}

extension DebugInfoBlockView: VisibleBoundsTrackingContainer {
  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] {
    [childView]
  }
}

extension DebugInfoBlockView: UIActionEventPerforming {
  public func perform(uiActionEvent event: LayoutKit.UIActionEvent, from _: AnyObject) {
    guard case let .url(url) = event.payload, url == DebugInfoBlock.showOverlayURL else {
      event.sendFrom(self)
      return
    }
    showInfo()
  }
}
