import Foundation
import UIKit

import BaseUIPublic
import LayoutKitInterface

extension Menu {
  public func makeActionSheetModel(
    sender: UIResponder,
    parentPath: UIElementPath,
    cancelTitle: String,
    userInterfaceStyle: UserInterfaceStyle
  ) -> ActionSheetModel? {
    let buttons: [AlertButton] = items.compactMap { item in
      AlertButton(
        title: item.text,
        action: {
          item.actions.forEach { $0.perform(sendingFrom: sender) }
        }
      )
    }
    guard !buttons.isEmpty else {
      assertionFailure("Menu must contain items")
      return nil
    }

    let cancelPath = parentPath + cancelID
    let cancelAction = { UserInterfaceAction(path: cancelPath).perform(sendingFrom: sender) }
    let cancelButton = AlertButton(
      title: cancelTitle,
      actionStyle: .cancel,
      action: cancelAction
    )
    return ActionSheetModel(
      buttons: buttons + [cancelButton],
      userInterfaceStyle: userInterfaceStyle
    )
  }
}

private let cancelID = "cancel"
