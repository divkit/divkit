import UIKit
import DivKitKMP

final class ViewController: UIViewController {
  private let divkitFacade = DivKitKMPFacade(
    actionHandler: DivKitActionHandler(actionHandler: { _ in }),
    errorReporter: DivKitErrorReporter(reportError: { _, _ in })
  )

  private lazy var divView = divkitFacade.makeDivKitView(jsonString, cardId: "Sample")

  override func viewDidLoad() {
    super.viewDidLoad()

    view.addSubview(divView)
    divView.translatesAutoresizingMaskIntoConstraints = false

    NSLayoutConstraint.activate([
      divView.centerXAnchor.constraint(equalTo: view.centerXAnchor),
      divView.centerYAnchor.constraint(equalTo: view.centerYAnchor),
      divView.heightAnchor.constraint(equalToConstant: 50),
      divView.widthAnchor.constraint(equalToConstant: 50),
    ])
  }
}

final class DivKitActionHandler: DivKitKMPActionHandler {
  private let actionHandler: (String) -> Void

  init(actionHandler: @escaping (String) -> Void) {
    self.actionHandler = actionHandler
  }

  func handleAction(url: String) {
    actionHandler(url)
  }
}

final class DivKitErrorReporter: DivKitKMPErrorReporter {
  private let reportError: (String, String) -> Void

  init(reportError: @escaping (String, String) -> Void) {
    self.reportError = reportError
  }

  func report(cardId: String, message: String) {
    reportError(cardId, message)
  }
}

let jsonString = """
{
    "card": {
        "log_id": "div2_sample_card",
        "states": [
            {
                "state_id": 0,
                "div": {
                    "type": "text",
                    "text": "Hello, world!"
                }
            }
        ]
    }
}
"""
