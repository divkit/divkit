import DivKit
import UIKit
import VGSL

@MainActor
final class UITestCardViewController: UIViewController, UIScrollViewDelegate {
  private var divView: DivView?
  private let scrollView = UIScrollView()
  private let loadingErrorLabel = UILabel()
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
  }

  override func viewDidLayoutSubviews() {
    super.viewDidLayoutSubviews()
    scrollView.frame = view.safeAreaLayoutGuide.layoutFrame
    loadingErrorLabel.frame = view.safeAreaLayoutGuide.layoutFrame
    guard let divView else { return }
    let size = divView.cardSize?.sizeFor(parentViewSize: scrollView.bounds.size) ?? .zero
    divView.frame = CGRect(origin: .zero, size: size)
    scrollView.contentSize = size
    updateVisibleBounds()
  }

  func scrollViewDidScroll(_: UIScrollView) {
    updateVisibleBounds()
  }

  func load(_ data: DivData, cardId: DivCardID, divKitComponents: DivKitComponents) async {
    loadViewIfNeeded()
    let divView = DivView(divKitComponents: divKitComponents)
    self.divView = divView
    scrollView.addSubview(divView)
    sizeChangedSubscription = divView.addObserver { [weak self] _ in
      self?.view.setNeedsLayout()
    }
    await divView.setSource(DivViewSource(kind: .divData(data), cardId: cardId))
    divView.setParentScrollView(scrollView)
    view.setNeedsLayout()
    view.layoutIfNeeded()
    scrollView.layoutIfNeeded()
    divView.accessibilityIdentifier = "rootDivView"
  }

  func showError(_ message: String) {
    loadViewIfNeeded()
    loadingErrorLabel.text = message
    view.addSubview(loadingErrorLabel)
    scrollView.removeFromSuperview()
    view.setNeedsLayout()
  }

  private func updateVisibleBounds() {
    guard let divView, scrollView.superview != nil else { return }
    divView.onVisibleBoundsChanged(to: scrollView.bounds.intersection(divView.frame))
  }
}
