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
    observer _: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    (view as? DebugBlockView)?.configure(
      errorCollector: errorCollector,
      showDebugInfo: showDebugInfo
    )
  }
}

private final class DebugBlockView: BlockView, VisibleBoundsTrackingContainer {
  let effectiveBackgroundColor: UIColor? = nil
  let visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] = []

  private var showDebugInfo: ((ViewType) -> Void)?
  private var errorCollector: DebugErrorCollector?
  private let disposePool = AutodisposePool()

  private let errorsLabel: UILabel = {
    let label = UILabel(frame: CGRect(origin: .zero, size: CGSize(squareDimension: 50.0)))
    label.text = "0"
    label.numberOfLines = 1
    label.font = .systemFont(ofSize: 14)
    label.textColor = .white
    label.textAlignment = .center
    label.backgroundColor = .red
    label.isHidden = true
    return label
  }()

  init() {
    super.init(frame: .zero)
    addSubview(errorsLabel)
    accessibilityIdentifier = "divLayoutErrorCounter"
    accessibilityTraits = .button
    clipsToBounds = true
    addGestureRecognizer(UITapGestureRecognizer(
      target: self,
      action: #selector(handleTapGesture(_:))
    ))
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  func configure(
    errorCollector: DebugErrorCollector,
    showDebugInfo: @escaping (ViewType) -> Void
  ) {
    self.showDebugInfo = showDebugInfo
    if self.errorCollector !== errorCollector {
      self.errorCollector = errorCollector
      errorCollector.observableErrorCount.currentAndNewValues.addObserver { [weak self] _ in
        self?.updateCountLabel()
      }.dispose(in: disposePool)
    }
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    errorsLabel.frame = bounds
    layer.cornerRadius = bounds.size.height / 2
  }

  private func updateCountLabel() {
    let errorsCount = errorCollector?.totalErrorCount ?? 0
    let isHidden = errorsCount == 0
    isUserInteractionEnabled = !isHidden
    errorsLabel.isHidden = isHidden
    errorsLabel.text = "\(min(maxCount, errorsCount))"
  }

  @objc func handleTapGesture(_: UITapGestureRecognizer) {
    guard let showDebugInfo, let errorCollector, errorCollector.totalErrorCount > 0 else { return }
    showDebugInfo(ErrorListView(errors: errorCollector.errorList))
  }
}

private let showOverlayURL = URL(string: "debugInfo://show")!
private let maxCount = 9999
