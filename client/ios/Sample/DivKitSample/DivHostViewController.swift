import UIKit

import DivKit

final class DivHostViewController: UIViewController {
  private var divHostView: DivHostView!
  private var components: DivKitComponents!

  override func viewDidLoad() {
    super.viewDidLoad()
    components = DivKitComponents(
      urlHandler: DivUrlHandlerDelegate { UIApplication.shared.open($0) }
    )
    let preloader = DivViewPreloader(divKitComponents: components)
    divHostView = DivHostView(components: components, preloader: preloader)

    if let cards = try? DivJson.loadCards() {
      view.addSubview(divHostView)
      Task {
        await preloader
          .setSources(cards.map { DivViewSource(kind: .divData($0), cardId: $0.cardId) })
        divHostView.items = cards.map(\.cardId)
      }
    }
  }

  override func viewDidLayoutSubviews() {
    super.viewDidLayoutSubviews()
    divHostView.frame = view.bounds.inset(by: view.safeAreaInsets)
  }
}
