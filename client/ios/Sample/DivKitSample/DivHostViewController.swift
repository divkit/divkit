import DivKit
import DivKitExtensions
import UIKit

final class DivHostViewController: UIViewController {
  lazy var divKitComponents = makeDivKitComponents()
  lazy var divView = DivView(divKitComponents: divKitComponents)

  override func viewDidLoad() {
    super.viewDidLoad()
    view.addSubview(divView)

    Task {
      await configureDivView()
    }
  }

  override func viewWillLayoutSubviews() {
    super.viewWillLayoutSubviews()
    divView.frame = view.bounds.inset(by: view.safeAreaInsets)
  }

  private func configureDivView() async {
    let url = Bundle.main.url(forResource: "Sample", withExtension: "json")!
    let data = try! Data(contentsOf: url)
    await divView.setSource(
      .init(kind: .data(data), cardId: "Sample"),
      debugParams: DebugParams(isDebugInfoEnabled: true)
    )
  }

  private func makeDivKitComponents() -> DivKitComponents {
    let extensionHandlers = [PinchToZoomExtensionHandler(overlayView: view)]
    let customBlockFactory = SampleDivCustomBlockFactory()
    let urlHandler = SampleDivActionHandler(hostViewController: self)
    return DivKitComponents(
      divCustomBlockFactory: customBlockFactory,
      extensionHandlers: extensionHandlers,
      urlHandler: urlHandler
    )
  }
}
