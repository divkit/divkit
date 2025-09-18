#if os(iOS)
import LayoutKit
import UIKit
import VGSL

extension DebugBlock: UIViewRenderable {
  static func makeBlockView() -> any LayoutKit.BlockView {
    DebugBlockView()
  }

  func canConfigureBlockView(_ view: any LayoutKit.BlockView) -> Bool {
    view is DebugBlockView
  }

  func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    (view as? DebugBlockView)?.configure(
      child: child,
      errorCollector: errorCollector,
      showDebugInfo: showDebugInfo,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
  }
}

private final class DebugBlockView: BlockView, VisibleBoundsTrackingContainer {
  private var childView: BlockView?

  private var showDebugInfo: ((ViewType) -> Void)?
  private var errorCollector: DebugErrorCollector?
  private let disposePool = AutodisposePool()

  private let errorsButton: UIButton = {
    let button = UIButton(type: .custom)
    button.isHidden = true
    button.setTitle("0", for: .normal)
    button.setTitleColor(.white, for: .normal)
    button.backgroundColor = .red
    button.accessibilityIdentifier = "divLayoutErrorCounter"
    return button
  }()

  var effectiveBackgroundColor: UIColor? {
    childView?.backgroundColor
  }

  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] {
    childView.map { [$0] } ?? []
  }

  init() {
    super.init(frame: .zero)
    addSubview(errorsButton)
    errorsButton.addTarget(self, action: #selector(errorsButtonTapped), for: .touchUpInside)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    childView?.frame = bounds
    errorsButton.frame = CGRect(
      center: CGPoint(x: buttonSize / 2.0, y: bounds.midY),
      size: CGSize(squareDimension: buttonSize)
    )
    errorsButton.layer.cornerRadius = buttonSize / 2.0
  }

  override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
    let result = super.hitTest(point, with: event)
    return result === self ? nil : result
  }

  func configure(
    child: Block,
    errorCollector: DebugErrorCollector,
    showDebugInfo: @escaping (ViewType) -> Void,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    childView = child.reuse(
      childView,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      superview: self,
      subviewPosition: .index(0)
    )
    self.showDebugInfo = showDebugInfo
    if self.errorCollector !== errorCollector {
      self.errorCollector = errorCollector
      errorCollector.observableErrorCount.currentAndNewValues.addObserver { [weak self] _ in
        self?.updateCountLabel()
      }.dispose(in: disposePool)
    }
    setNeedsLayout()
  }

  @objc func errorsButtonTapped() {
    guard let showDebugInfo, let errorCollector, errorCollector.totalErrorCount > 0 else { return }
    showDebugInfo(ErrorListView(errors: errorCollector.errorList))
  }

  private func updateCountLabel() {
    let errorsCount = errorCollector?.totalErrorCount ?? 0
    let isHidden = errorsCount == 0
    errorsButton.isHidden = isHidden
    errorsButton.setTitle("\(min(maxCount, errorsCount))", for: .normal)
  }
}

private let showOverlayURL = URL(string: "debugInfo://show")!
private let maxCount = 9999
private let buttonSize = 50.0
#endif
