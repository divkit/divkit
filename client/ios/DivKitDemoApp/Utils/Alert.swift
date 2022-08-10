import UIKit

import LayoutKit

extension UIViewController {
  func showAlert(
    title: String? = nil,
    message: String? = nil
  ) {
    let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
    alert.addAction(UIAlertAction(title: "OK", style: .default))
    present(alert, animated: true)
  }

  func showMenu(
    _ menu: Menu,
    actionPerformer: UIActionEventPerforming
  ) {
    let alert = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
    menu.items.forEach { item in
      let action = UIAlertAction(title: item.text, style: .default) { _ in
        let events = item.actions.map {
          UIActionEvent(uiAction: $0, originalSender: self)
        }
        actionPerformer.perform(uiActionEvents: events, from: self)
      }
      alert.addAction(action)
    }
    present(alert, animated: true)
  }
}
