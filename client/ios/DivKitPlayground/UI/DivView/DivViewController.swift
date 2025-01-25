import Combine
import DivKit
import DivKitExtensions
import LayoutKit
import UIKit

open class DivViewController: UIViewController {
  private let divKitComponents: DivKitComponents
  private let debugParams: DebugParams
  private let divView: DivView
  private let scrollView = VisibilityTrackingScrollView()
  private var cancellables = Set<AnyCancellable>()

  private let identifier: String = "baseDivView"

  init(
    jsonPublisher: JsonPublisher,
    divKitComponents: DivKitComponents,
    debugParams: DebugParams
  ) {
    self.divKitComponents = divKitComponents
    self.divView = DivView(divKitComponents: divKitComponents)
    self.debugParams = debugParams

    super.init(nibName: nil, bundle: nil)

    view.addSubview(divView)

    jsonPublisher
      .sink { [weak self] in
        guard !$0.isEmpty else { return }
        self?.divKitComponents.reset(cardId: cardId)
        self?.setData($0)
      }
      .store(in: &cancellables)
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

    setTestIdentifier()
  }

  public override func viewDidDisappear(_ animated: Bool) {
    super.viewDidDisappear(animated)
    cancellables = []
  }

  open func onViewUpdated() {}

  private func setData(_ data: [String: Any]) {
    Task {
      await divView.setSource(
        DivViewSource(
          kind: .json(data),
          cardId: cardId
        ),
        debugParams: debugParams,
        shouldResetPreviousCardData: true
      )
    }
  }

  private func setTestIdentifier() {
    divView.accessibilityIdentifier = identifier
  }
}
