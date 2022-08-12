import UIKit

import LayoutKit

extension UIViewController {
  func showAlert(
    message: String? = nil,
    actions: [UIAlertAction] = []
  ) {
    let alert = UIAlertController(title: nil, message: message, preferredStyle: .actionSheet)
    actions.forEach {
      alert.addAction($0)
    }
    alert.addAction(UIAlertAction(title: "Cancel", style: .cancel))
    present(alert, animated: true)
  }

  func showMenu(
    _ menu: Menu,
    actionPerformer: UIActionEventPerforming
  ) {
    let actions = menu.items.map { item in
      UIAlertAction(title: item.text, style: .default) { _ in
        let events = item.actions.map {
          UIActionEvent(uiAction: $0, originalSender: self)
        }
        actionPerformer.perform(uiActionEvents: events, from: self)
      }
    }
    showAlert(actions: actions)
  }
}
