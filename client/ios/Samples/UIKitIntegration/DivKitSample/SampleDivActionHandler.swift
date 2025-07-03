import DivKit
import Foundation
import UIKit

final class SampleDivActionHandler: DivUrlHandler {
  weak var hostViewController: UIViewController?

  init(hostViewController: UIViewController) {
    self.hostViewController = hostViewController
  }

  func handle(_ url: URL, info _: DivActionInfo, sender _: AnyObject?) {
    guard let components = url.components, components.scheme == scheme else {
      return
    }

    switch components.host {
    case "toast":
      if let text = components.queryItems?.first?.name, let hostViewController {
        showAlert(on: hostViewController, message: text)
      }
    default:
      return
    }
  }
}

private let scheme = "sample-action"

private func showAlert(
  on viewController: UIViewController,
  message: String
) {
  let alert = UIAlertController(title: "Sample alert", message: message, preferredStyle: .alert)
  alert.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
  viewController.present(alert, animated: true, completion: nil)
}
