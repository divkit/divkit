import UIKit

import DivKit
import LayoutKit

final class DivHostViewController: UIViewController {
  private var divHostView: DivHostView!
  private var components: DivKitComponents!

  override func viewDidLoad() {
    super.viewDidLoad()
    components = DivKitComponents(
      urlHandler: DivUrlHandlerDelegate { UIApplication.shared.open($0) }
    )
    divHostView = DivHostView(components: components)

    if let cards = try? DivJson.loadCards() {
      view.addSubview(divHostView)
      divHostView.items = cards
    }
  }

  override func viewDidLayoutSubviews() {
    super.viewDidLayoutSubviews()
    divHostView.frame = view.bounds.inset(by: view.safeAreaInsets)
  }
}
