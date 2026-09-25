import DivKit
import UIKit
import VGSL

@MainActor
final class UITestCardViewController: UIViewController, UIScrollViewDelegate {
  private let divView: DivView
  private let scrollView = UIScrollView()
  private var loadingErrorLabel: UILabel?
  private var sizeChangedSubscription: Disposable?

  init(divKitComponents: DivKitComponents) {
    divView = DivView(divKitComponents: divKitComponents)
    super.init(nibName: nil, bundle: nil)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func viewDidLoad() {
    super.viewDidLoad()
    view.backgroundColor = .white
    view.addSubview(scrollView)
    scrollView.contentInsetAdjustmentBehavior = .never
    scrollView.delegate = self
    scrollView.addSubview(divView)
    sizeChangedSubscription = divView.addObserver { [weak self] _ in
      self?.view.setNeedsLayout()
    }
  }

  override func viewDidLayoutSubviews() {
    super.viewDidLayoutSubviews()
    scrollView.frame = view.safeAreaLayoutGuide.layoutFrame
    loadingErrorLabel?.frame = view.safeAreaLayoutGuide.layoutFrame
    let size = divView.cardSize?.sizeFor(parentViewSize: scrollView.bounds.size) ?? .zero
    divView.frame = CGRect(origin: .zero, size: size)
    scrollView.contentSize = size
    updateVisibleBounds()
  }

  func scrollViewDidScroll(_: UIScrollView) {
    updateVisibleBounds()
  }

  func load(_ data: DivData, cardId: DivCardID) async {
    loadViewIfNeeded()
    await divView.setSource(DivViewSource(kind: .divData(data), cardId: cardId))
    divView.setParentScrollView(scrollView)
    view.setNeedsLayout()
    view.layoutIfNeeded()
    scrollView.layoutIfNeeded()
    divView.accessibilityIdentifier = "rootDivView"
  }

  func showError(_ message: String) {
    loadViewIfNeeded()
    let label = UILabel()
    label.numberOfLines = 0
    label.text = message
    label.accessibilityIdentifier = "uiTestLoadError"
    view.addSubview(label)
    loadingErrorLabel = label
    view.setNeedsLayout()
    view.layoutIfNeeded()
  }

  private func updateVisibleBounds() {
    divView.onVisibleBoundsChanged(to: scrollView.bounds.intersection(divView.frame))
  }
}
