import DivKit
import UIKit
import VGSL

@MainActor
final class UITestCardViewController: UIViewController, UIScrollViewDelegate {
  private var divView: DivView?
  private let scrollView = UIScrollView()
  private let loadingErrorLabel = UILabel()
  private let emptyView = UILabel()
  private var sizeChangedSubscription: Disposable?

  init() {
    super.init(nibName: nil, bundle: nil)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func viewDidLoad() {
    super.viewDidLoad()
    view.backgroundColor = .white
    loadingErrorLabel.numberOfLines = 0
    loadingErrorLabel.accessibilityIdentifier = "uiTestLoadError"
    view.addSubview(scrollView)
    scrollView.contentInsetAdjustmentBehavior = .never
    scrollView.delegate = self
    emptyView.text = "<empty view>"
    emptyView.isHidden = true
    scrollView.addSubview(emptyView)
  }

  override func viewDidLayoutSubviews() {
    super.viewDidLayoutSubviews()
    scrollView.frame = view.safeAreaLayoutGuide.layoutFrame
    loadingErrorLabel.frame = view.safeAreaLayoutGuide.layoutFrame
    guard let divView else { return }
    let size = divView.cardSize?.sizeFor(parentViewSize: scrollView.bounds.size) ?? .zero
    divView.frame = CGRect(origin: .zero, size: size)
    scrollView.contentSize = size
    updateEmptyView(for: divView)
    emptyView.frame = CGRect(origin: scrollView.bounds.origin, size: emptyView.intrinsicContentSize)
    updateVisibleBounds()
  }

  func scrollViewDidScroll(_: UIScrollView) {
    updateVisibleBounds()
  }

  func load(_ data: DivData, cardId: DivCardID, divKitComponents: DivKitComponents) async {
    loadViewIfNeeded()
    let divView = DivView(divKitComponents: divKitComponents)
    scrollView.addSubview(divView)
    sizeChangedSubscription = divView.addObserver { [weak self] _ in
      self?.view.setNeedsLayout()
    }
    await divView.setSource(DivViewSource(kind: .divData(data), cardId: cardId))
    divView.setParentScrollView(scrollView)
    self.divView = divView
    view.setNeedsLayout()
    view.layoutIfNeeded()
    scrollView.layoutIfNeeded()
  }

  func showError(_ message: String) {
    loadViewIfNeeded()
    loadingErrorLabel.text = message
    view.addSubview(loadingErrorLabel)
    scrollView.removeFromSuperview()
    view.setNeedsLayout()
  }

  private func updateEmptyView(for divView: DivView) {
    // Temporary snapshot placeholder until empty cards have a dedicated assertion.
    let isEmpty = divView.bounds.isEmpty
    divView.accessibilityIdentifier = isEmpty ? nil : "rootDivView"
    emptyView.accessibilityIdentifier = isEmpty ? "rootDivView" : nil
    emptyView.isHidden = !isEmpty
  }

  private func updateVisibleBounds() {
    guard let divView, scrollView.superview != nil else { return }
    divView.onVisibleBoundsChanged(to: scrollView.bounds.intersection(divView.frame))
  }
}
