import Combine
import DivKit
import DivKitExtensions
import LayoutKit
import UIKit

open class DivViewController: UIViewController {
  private let divKitComponents: DivKitComponents
  private let debugParams: DebugParams
  private var divView: DivView?

  private let scrollView = VisibilityTrackingScrollView()
  private var cancellables = Set<AnyCancellable>()

  private let identifier: String = "baseDivView"

  init(
    jsonPublisher: JsonPublisher,
    divKitComponents: DivKitComponents,
    debugParams: DebugParams
  ) {
    self.divKitComponents = divKitComponents
    self.debugParams = debugParams

    super.init(nibName: nil, bundle: nil)

    jsonPublisher
      .filter { !$0.isEmpty }
      .sink { [weak self] value in
        self?.divView = self?.createDivView()
        self?.setData(value)
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
    cancellables = []
  }

  open func onViewUpdated() {}

  private func createDivView() -> DivView {
    let divView = DivView(divKitComponents: divKitComponents)
    scrollView.divView = divView

    divView.setParentScrollView(scrollView)
    divView.accessibilityIdentifier = identifier

    return divView
  }

  private func setData(
    _ data: [String: Any]
  ) {
    Task {
      await divView?.setSource(
        DivViewSource(
          kind: .json(data),
          cardId: cardId
        ),
        debugParams: debugParams,
        shouldResetPreviousCardData: true
      )
    }
  }
}
