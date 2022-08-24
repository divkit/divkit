import UIKit

import Serialization

class DivHostViewController: UIViewController {
  private let divHostView = DivHostView(
    urlOpener: { UIApplication.shared.open($0) }
  )

  override func viewDidLoad() {
    super.viewDidLoad()
    view.addSubview(divHostView)

    if let card = try? DivJson.loadCard()?.value {
      try? divHostView.setCard(card)
    }
  }

  override func viewDidLayoutSubviews() {
    super.viewDidLayoutSubviews()
    divHostView.frame = view.bounds.inset(by: view.safeAreaInsets)
  }
}
