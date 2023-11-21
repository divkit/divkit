import UIKit

import CommonCorePublic
import DivKit
import DivKitExtensions
import LayoutKit

open class DivViewController: UIViewController {
  private let divKitComponents: DivKitComponents
  private let debugParams: DebugParams
  private let divView: DivView
  private let disposePool = AutodisposePool()
  private let scrollView = VisibilityTrackingScrollView()

  init(
    jsonProvider: Signal<[String: Any]>,
    divKitComponents: DivKitComponents,
    debugParams: DebugParams
  ) {
    self.divKitComponents = divKitComponents
    self.divView = DivView(divKitComponents: divKitComponents)
    self.debugParams = debugParams

    super.init(nibName: nil, bundle: nil)

    view.addSubview(divView)

    jsonProvider.addObserver { [weak self] in
      self?.divKitComponents.reset(cardId: cardId)
      self?.setData($0)
    }.dispose(in: disposePool)
  }

  @available(*, unavailable)
  public required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  public override func loadView() {
    scrollView.backgroundColor = .white
    view = scrollView
    scrollView.divView = divView
    divView.setParentScrollView(scrollView)
  }

  public override func viewDidLoad() {
    super.viewDidLoad()
    let pinchToZoomExtensionHandler = PinchToZoomExtensionHandler(overlayView: view)
    divKitComponents.extensionHandlers = divKitComponents.extensionHandlers.filter {
      $0.id != pinchToZoomExtensionHandler.id
    }
    divKitComponents.extensionHandlers.append(pinchToZoomExtensionHandler)
  }

  public override func viewDidDisappear(_ animated: Bool) {
    super.viewDidDisappear(animated)
    disposePool.drain()
  }

  open func onViewUpdated() {}

  private func setData(_ data: [String: Any]) {
    divView.setSource(
      DivViewSource(
        kind: .json(data),
        cardId: cardId
      ),
      debugParams: debugParams,
      shouldResetPreviousCardData: true
    )
  }
}

private let cardId: DivCardID = "DivViewCard"
